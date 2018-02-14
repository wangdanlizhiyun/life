package lzy.com.life;

import android.app.Application;

import lzy.com.life_library.listener.AppFourgroundOrBackgroundChangeListener;
import lzy.com.life_library.utils.LifeUtil;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LifeUtil.init(this);
        LifeUtil.setAppFourgroundOrBackgroundChangeListener(new AppFourgroundOrBackgroundChangeListener() {
            @Override
            public void change(Boolean isToBackground) {

            }
        });
    }
}
