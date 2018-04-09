package lzy.com.life_library.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import lzy.com.life_library.entity.PermissionRequest;
import lzy.com.life_library.listener.LifeCycleListener;
import lzy.com.life_library.listener.ResultListener;
import lzy.com.life_library.utils.LifeUtil;

/**
 * Created by jack on 2017/12/27.
 */

public class EmptyFragment extends Fragment {
    public static final int REQUEST_CODE = 0xffff - 13;
    PermissionRequest mPermissionRequest;

    ArrayList<LifeCycleListener> mLifeCycleListeners = new ArrayList<>();


    ResultListener mResultListener;
    Intent mIntent;


    public EmptyFragment() {
    }

    public void addLifeCycleListener(LifeCycleListener lifeCycleListener) {
        if (lifeCycleListener != null){
            this.mLifeCycleListeners.add(lifeCycleListener);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onStart() {
        super.onStart();
        for (LifeCycleListener lifeCycleListener:mLifeCycleListeners
             ) {
            lifeCycleListener.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (LifeCycleListener lifeCycleListener:mLifeCycleListeners
                ) {
            lifeCycleListener.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (LifeCycleListener lifeCycleListener:mLifeCycleListeners
                ) {
            lifeCycleListener.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        for (LifeCycleListener lifeCycleListener:mLifeCycleListeners
                ) {
            lifeCycleListener.onStop();
        }
    }

    @Override
    public void onDestroy() {
        for (LifeCycleListener lifeCycleListener:mLifeCycleListeners
                ) {
            lifeCycleListener.onDestory();
        }
        mLifeCycleListeners.clear();
        super.onDestroy();
    }

    public void doRequestPermissions(Context context, PermissionRequest permissionRequest) {
        this.mPermissionRequest = permissionRequest;
        if (mIsAttached) {
            doRequest(context);
        }
    }

    void doRequest(Context context) {
        if (!LifeUtil.isNeedCheck(context)) {
            if (mPermissionRequest.getRunnable() != null) {
                mPermissionRequest.getRunnable().run();
                mPermissionRequest = null;
            }
            return;
        }else {
            List<String> deniedPermissions = LifeUtil.findDeniedPermissions(context, mPermissionRequest.getPermissions());
            if (deniedPermissions.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(deniedPermissions.toArray(new String[]{}), REQUEST_CODE);
                }
                mPermissionRequest.hasRequested = true;
            } else {
                if (mPermissionRequest.getRunnable() != null) {
                    mPermissionRequest.getRunnable().run();
                    mPermissionRequest = null;
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                List<String> deniedPermissions = LifeUtil.findDeniedPermissions(getActivity(), mPermissionRequest.getPermissions());
                if (deniedPermissions.size() > 0) {
                    if (mPermissionRequest.getPermissionDeniedListener() != null) {
                        mPermissionRequest.getPermissionDeniedListener().onDenied(deniedPermissions);
                    }
                } else {
                    if (mPermissionRequest.getRunnable() != null) {
                        mPermissionRequest.getRunnable().run();
                    }
                }
                break;
        }
    }

    Boolean mIsAttached = false;

    @Override
    public void onDetach() {
        super.onDetach();
        mIsAttached = false;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mIsAttached = true;
        if (mPermissionRequest != null && !mPermissionRequest.hasRequested) {
            doRequest(activity);
        }
        if (mIntent != null) {
            doStartActivity();
        }
    }

    public void startActivityForResult(Intent intent, @NonNull ResultListener resultListener) {
        this.mResultListener = resultListener;
        this.mIntent = intent;
        if (mIsAttached) {
            doStartActivity();
        }
    }

    void doStartActivity() {
        startActivityForResult(mIntent, REQUEST_CODE);
        mIntent = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mResultListener == null) return;
        if (requestCode != REQUEST_CODE) return;
        if (data == null){
            if (mResultListener.getResultCancelListener() != null){
                mResultListener.getResultCancelListener().onResultCancel(new Intent());
            }
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (mResultListener.getResultOkListener() != null){
                mResultListener.getResultOkListener().onResultOk(data);
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (mResultListener.getResultCancelListener() != null){
                mResultListener.getResultCancelListener().onResultCancel(data);
            }
        } else if (resultCode == Activity.RESULT_FIRST_USER) {
            if (mResultListener.getResultFirstUserListener() != null){
                mResultListener.getResultFirstUserListener().onResultFirstUser(data);
            }
        }
    }

}
