package lzy.com.life_library.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.LruCache;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lzy.com.life_library.entity.PermissionRequest;
import lzy.com.life_library.fragment.EmptyFragment;
import lzy.com.life_library.listener.LifeCycleListener;
import lzy.com.life_library.listener.ActivityLifecycleCallbacksAdapter;
import lzy.com.life_library.listener.AppFourgroundOrBackgroundChangeListener;
import lzy.com.life_library.listener.PermissionDeniedListener;
import lzy.com.life_library.listener.ResultListener;

public final class LifeUtil {
    public static void startActivityForResult(Intent intent, ResultListener resultListener) {
        startActivityForResult(getActivity(),intent,resultListener);
    }

    private static void startActivityForResult(@NonNull Activity activity, Intent intent, ResultListener resultListener) {
        LifeUtil.assertMainThread();
        if (activity != null){
            getFragment(activity).startActivityForResult(intent, resultListener);
        }
    }
    static LruCache<String,PermissionRequestBuilder> permissionRequestBuilderLruCache;
    public static PermissionRequestBuilder permission(String... permissions) {
        if (permissionRequestBuilderLruCache == null){
            permissionRequestBuilderLruCache = new LruCache<>(1);
        }
        PermissionRequestBuilder permissionRequestBuilder = permissionRequestBuilderLruCache.get("permissionRequestBuilderLruCache");
        if (permissionRequestBuilder == null){
            permissionRequestBuilder = new PermissionRequestBuilder();
            permissionRequestBuilderLruCache.put("permissionRequestBuilderLruCache",permissionRequestBuilder);
        }
        return permissionRequestBuilder.permission(permissions);
    }

    private static void requestPermission(@NonNull final Activity activity, final PermissionRequest permissionRequest) {
        if (activity != null) {
            if (isOnBackgroundThread()){
                new Handler(Looper.getMainLooper()).post(new Runnable() {
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

    private static void requestPermission(PermissionRequest permissionRequest) {
        requestPermission(getActivity(), permissionRequest);
    }

    public static class PermissionRequestBuilder {
        ArrayList<String> mPermissions;
        PermissionDeniedListener mPermissionDeniedListener;
        Runnable mRunnable;

        LruCache<String ,PermissionRequest> mPermissionRequestLruCache;

        public PermissionRequestBuilder() {
            mPermissions = new ArrayList<>(5);
            mPermissionRequestLruCache = new LruCache<>(1);
        }

        public PermissionRequestBuilder permission(String... permissions) {
            for (String permission : permissions
                    ) {
                if (!TextUtils.isEmpty(permission)) {
                    mPermissions.add(permission);
                }
            }
            return this;
        }

        public PermissionRequestBuilder deny(PermissionDeniedListener listener) {
            mPermissionDeniedListener = listener;
            return this;
        }


        public void run(Runnable runnable) {
            mRunnable = runnable;
            PermissionRequest permissionRequest = mPermissionRequestLruCache.get("permissionRequest");
            if (permissionRequest == null){
                permissionRequest = new PermissionRequest();
                mPermissionRequestLruCache.put("permissionRequest",permissionRequest);
            }
            permissionRequest.setPermissions(mPermissions.toArray(new String[]{}));
            permissionRequest.setPermissionDeniedListener(mPermissionDeniedListener);
            permissionRequest.setRunnable(mRunnable);
            requestPermission(permissionRequest);
        }
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
    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {
            @Override
            public void onActivityStarted(Activity activity) {
                super.onActivityStarted(activity);
                mWeakReferenceActivity = new WeakReference<Activity>(activity);
                mActivityCounts++;
                if (mActivityCounts == 1){
                    if (mAppFourgroundOrBackgroundChangeListener != null){
                        mAppFourgroundOrBackgroundChangeListener.change(false);
                    }
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                super.onActivityStopped(activity);
                mActivityCounts--;
                if (mActivityCounts == 0){
                    if (mAppFourgroundOrBackgroundChangeListener != null){
                        mAppFourgroundOrBackgroundChangeListener.change(true);
                    }
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                super.onActivityDestroyed(activity);
                InputMethodManagerUtil.fixInputMethodManagerLeak(activity);
            }
        });
    }
    public static void init(Activity activity) {
        init(activity.getApplication());
    }
    private static AppFourgroundOrBackgroundChangeListener mAppFourgroundOrBackgroundChangeListener;

    public static void setAppFourgroundOrBackgroundChangeListener(AppFourgroundOrBackgroundChangeListener mAppFourgroundOrBackgroundChangeListener) {
        LifeUtil.mAppFourgroundOrBackgroundChangeListener = mAppFourgroundOrBackgroundChangeListener;
    }

    public static void addLifeCycle(final Activity activity,final LifeCycleListener lifeCycleListener){
        if (isOnBackgroundThread()){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    getFragment(fragment).addLifeCycleListener(lifeCycleListener);
                }
            });
        }else {
            getFragment(fragment).addLifeCycleListener(lifeCycleListener);
        }
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
        compatibleFragment(fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fragment.getChildFragmentManager()
                    .beginTransaction()
                    .add(emptyFragment, TAG)
                    .commitAllowingStateLoss();
        }

        return emptyFragment;
    }
    private static void compatibleFragment(Fragment fragment) {
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(fragment, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
