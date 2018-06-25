package lzy.com.life_library.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.LruCache;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import lzy.com.life_library.entity.PermissionRequest;
import lzy.com.life_library.entity.PermissionRequestBuilder;
import lzy.com.life_library.fragment.EmptyFragment;
import lzy.com.life_library.listener.ActivityLifecycleCallbacksAdapter;
import lzy.com.life_library.listener.AppFourgroundOrBackgroundChangeListener;
import lzy.com.life_library.listener.AppGotoBackgroundSomeTimeListener;
import lzy.com.life_library.listener.LifeCycleListener;
import lzy.com.life_library.listener.ResultCancelListener;
import lzy.com.life_library.listener.ResultFirstUserListener;
import lzy.com.life_library.listener.ResultListener;
import lzy.com.life_library.listener.ResultOkListener;

public final class LifeUtil {
    public static ResultListenerBuilder resultOk(ResultOkListener resultOkListener){
        ResultListenerBuilder resultListenerBuilder = new ResultListenerBuilder();
        resultListenerBuilder.resultOk(resultOkListener);
        return resultListenerBuilder;
    }

    protected static void startActivityForResult(Intent intent, ResultListener resultListener) {
        startActivityForResult(getActivity(),intent,resultListener);
    }

    private static void startActivityForResult(@NonNull Activity activity, Intent intent, ResultListener resultListener) {
        LifeUtil.assertMainThread();
        if (activity != null){
            getFragment(activity).startActivityForResult(intent, resultListener);
        }
    }
    public static class ResultListenerBuilder {
        ResultOkListener resultOkListener;
        ResultCancelListener resultCancelListener;
        ResultFirstUserListener resultFirstUserListener;
        public ResultListenerBuilder resultOk(ResultOkListener resultOkListener){
            this.resultOkListener = resultOkListener;
            return this;
        }
        public ResultListenerBuilder resultCancel(ResultCancelListener resultCancelListener){
            this.resultCancelListener = resultCancelListener;
            return this;
        }
        public ResultListenerBuilder resultFirstUser(ResultFirstUserListener resultFirstUserListener){
            this.resultFirstUserListener = resultFirstUserListener;
            return this;
        }

        private ResultListener build(){
            ResultListener resultListener = new ResultListener();
            resultListener.setResultOkListener(resultOkListener);
            resultListener.setResultCancelListener(resultCancelListener);
            resultListener.setResultFirstUserListener(resultFirstUserListener);
            return resultListener;
        }

        public void startActivityForResult(Intent intent){
            LifeUtil.startActivityForResult(intent,build());
        }
    }
    static LruCache<String,PermissionRequestBuilder> sPermissionRequestBuilderLruCache;
    public static PermissionRequestBuilder permission(String... permissions) {
        if (sPermissionRequestBuilderLruCache == null){
            sPermissionRequestBuilderLruCache = new LruCache<>(1);
        }
        PermissionRequestBuilder permissionRequestBuilder = sPermissionRequestBuilderLruCache.get("permissionRequestBuilderLruCache");
        if (permissionRequestBuilder == null){
            permissionRequestBuilder = new PermissionRequestBuilder();
            sPermissionRequestBuilderLruCache.put("permissionRequestBuilderLruCache",permissionRequestBuilder);
        }
        return permissionRequestBuilder.permission(permissions);
    }

    private static void requestPermission(@NonNull final Activity activity, final PermissionRequest permissionRequest) {
        if (activity != null) {
            if (isOnBackgroundThread()){
                getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        getFragment(activity).doRequestPermissions(activity, permissionRequest);
                    }
                });
            }else {
                getFragment(activity).doRequestPermissions(activity, permissionRequest);
            }
        }
    }

    public static void requestPermission(PermissionRequest permissionRequest) {
        requestPermission(getActivity(), permissionRequest);
    }



    public static boolean isNeedCheck(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M;
    }

    public static List<String> findDeniedPermissions(@NonNull Context context, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (!CheckPermissionUtil.checkHasPermisson(context, value)) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    private static final String TAG = "EmptyFragment_Tag";
    private static WeakReference<Activity> mWeakReferenceActivity;

    private static int mActivityCounts = 0;
    private static int mActivityCanSeeCounts = 0;
    static AtomicBoolean mIsAppInited = new AtomicBoolean(false);
    public static void init(Application application) {
        if (mIsAppInited.compareAndSet(false,true)){
            application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    super.onActivityCreated(activity, savedInstanceState);
                    if (mActivityCounts == 0){
                        mWeakReferenceActivity = new WeakReference<Activity>(activity);
                    }
                    mActivityCounts++;
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    super.onActivityStarted(activity);
                    mWeakReferenceActivity = new WeakReference<Activity>(activity);
                    mActivityCanSeeCounts++;
                    if (mActivityCanSeeCounts == 1){
                        if (mAppFourgroundOrBackgroundChangeListener != null){
                            mAppFourgroundOrBackgroundChangeListener.change(false);
                        }
                        getMainHandler().removeMessages(WHAT_GotoBackgroundSomeTime);
                    }
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    super.onActivityStopped(activity);
                    mActivityCanSeeCounts--;
                    if (mActivityCanSeeCounts == 0){
                        if (mAppFourgroundOrBackgroundChangeListener != null){
                            mAppFourgroundOrBackgroundChangeListener.change(true);
                        }
                        Iterator<Map.Entry<Integer,AppGotoBackgroundSomeTimeListener>> entryIterator = mAppGotoBackgroundSomeTimeListeners.entrySet().iterator();
                        while (entryIterator.hasNext()){
                            AppGotoBackgroundSomeTimeListener listener = entryIterator.next().getValue();
                            Message message = getMainHandler().obtainMessage();
                            message.what = WHAT_GotoBackgroundSomeTime;
                            message.arg1 = listener.hashCode();
                            getMainHandler().sendMessageDelayed(message,listener.delayTime());
                        }
                    }
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    super.onActivityDestroyed(activity);
                    InputMethodManagerUtil.fixInputMethodManagerLeak(activity);
                    mActivityCounts--;
                }
            });
        }
    }
    public static void init(Activity activity) {
        init(activity.getApplication());
    }
    private static AppFourgroundOrBackgroundChangeListener mAppFourgroundOrBackgroundChangeListener;

    public static void setAppFourgroundOrBackgroundChangeListener(AppFourgroundOrBackgroundChangeListener mAppFourgroundOrBackgroundChangeListener) {
        LifeUtil.mAppFourgroundOrBackgroundChangeListener = mAppFourgroundOrBackgroundChangeListener;
    }
    static ConcurrentHashMap<Integer,AppGotoBackgroundSomeTimeListener> mAppGotoBackgroundSomeTimeListeners = new ConcurrentHashMap<>();
    private static final int WHAT_GotoBackgroundSomeTime = 1;
    public static void addAppGotoBackgroundSomeTimeListener(AppGotoBackgroundSomeTimeListener listener){
        mAppGotoBackgroundSomeTimeListeners.put(listener.hashCode(),listener);
    }
    public static void releaseAppGotoBackgroundSomeTimeListener(AppGotoBackgroundSomeTimeListener listener){
        mAppGotoBackgroundSomeTimeListeners.remove(listener.hashCode());
    }

    public static void addLifeCycle(final Activity activity,final LifeCycleListener lifeCycleListener){
        if (isOnBackgroundThread()){
            getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    getFragment(activity).addLifeCycleListener(lifeCycleListener);
                }
            });
        }else {
            getFragment(activity).addLifeCycleListener(lifeCycleListener);
        }
    }
    public static void addLifeCycle(final Fragment fragment, final LifeCycleListener lifeCycleListener){
        if (isOnBackgroundThread()){
            getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    getFragment(fragment).addLifeCycleListener(lifeCycleListener);
                }
            });
        }else {
            getFragment(fragment).addLifeCycleListener(lifeCycleListener);
        }
    }
    static final Handler mMainHandler;
    static {
        mMainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case WHAT_GotoBackgroundSomeTime:
                        AppGotoBackgroundSomeTimeListener appGotoBackgroundSomeTimeListener = mAppGotoBackgroundSomeTimeListeners.get(msg.arg1);
                        if (appGotoBackgroundSomeTimeListener != null){
                            appGotoBackgroundSomeTimeListener.gotoBackground();
                        }
                        break;
                }
            }
        };
    }
    private static Handler getMainHandler(){
        return mMainHandler;
    }

    public static EmptyFragment getFragment(Activity activity) {
        EmptyFragment fragment = findFragment(activity);
        if (fragment == null) {
            fragment = new EmptyFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }
    public static EmptyFragment getFragment(Fragment fragment) {
        EmptyFragment emptyFragment = findFragment(fragment);
        if (emptyFragment == null) {
            emptyFragment = new EmptyFragment();
            FragmentManager fragmentManager = fragment.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(emptyFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return emptyFragment;
    }

    private static EmptyFragment findFragment(Activity activity) {
        return (EmptyFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }
    private static EmptyFragment findFragment(Fragment fragment) {
        return (EmptyFragment) fragment.getFragmentManager().findFragmentByTag(TAG);
    }

    public static Activity getActivity() {
        if (mWeakReferenceActivity != null){
            return mWeakReferenceActivity.get();
        }
        return null;
    }


  public static void assertMainThread() {
    if (!isOnMainThread()) {
      throw new IllegalArgumentException("You must call this method on the main thread");
    }
  }
  public static void assertBackgroundThread() {
    if (!isOnBackgroundThread()) {
      throw new IllegalArgumentException("You must call this method on a background thread");
    }
  }

  public static boolean isOnMainThread() {
    return Looper.myLooper() == Looper.getMainLooper();
  }

  public static boolean isOnBackgroundThread() {
    return !isOnMainThread();
  }

}
