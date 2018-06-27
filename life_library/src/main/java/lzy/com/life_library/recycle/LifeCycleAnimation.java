package lzy.com.life_library.recycle;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.AnimRes;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import lzy.com.life_library.listener.LifeCycleListener;
import lzy.com.life_library.utils.LifeUtil;

public class LifeCycleAnimation {
    Animation animation;
    public void setAnimationListener(Animation.AnimationListener listener) {
        if (animation == null)return;
        animation.setAnimationListener(listener);
    }

    public Animation getAnimation() {
        return animation;
    }

    public LifeCycleAnimation(Activity activity, @AnimRes int id){
        animation = AnimationUtils.loadAnimation(activity,id);
        LifeUtil.addLifeCycle(activity, new LifeCycleListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onStop() {
            }

            @Override
            public void onDestory() {
                animation.cancel();
            }
        });

    }
    public LifeCycleAnimation(Fragment fragment, @AnimRes int id){
        animation = AnimationUtils.loadAnimation(fragment.getActivity(),id);
        LifeUtil.addLifeCycle(fragment, new LifeCycleListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onStop() {

            }

            @Override
            public void onDestory() {
                animation.cancel();
            }
        });
    }
}
