package lzy.com.life;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;

import java.lang.reflect.Field;

import lzy.com.life_library.listener.LifeCycleListener;
import lzy.com.life_library.utils.LifeUtil;

/**
 * Created by lizhiyun on 2018/2/14.
 * 封装一个同步任务类。当短时间内提交多个异步任务时，等待他们执行任务后回掉到ui线程
 * run方法触发执行一个异步任务，
 * doOnbackground定义具体异步任务，
 * doOnUiThreadWhenAllBackgroudTaskIsOver ui线程回掉，
 * isRemoveOldTask是否放弃还没有开始的旧任务，默认false表示全部执行
 * release释放回掉对象
 * with(activity/fragmet)绑定所在组件，destroy时自动release，其他情况自行处理释放
 *
 */

public abstract class SyncTask implements MessageQueue.IdleHandler {
    MessageQueue messageQueue = null;
    Handler mainHandler;
    Handler handler;

    public SyncTask() {
        HandlerThread handlerThread = new HandlerThread("DataModel");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            messageQueue = looper.getQueue();
        } else {
            try {
                Field mQueue = Looper.class.getDeclaredField("mQueue");
                mQueue.setAccessible(true);
                messageQueue = (MessageQueue) mQueue.get(looper);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (messageQueue != null) {
            messageQueue.addIdleHandler(this);
        }

        handler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                doOnbackground();

            }
        };
        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                doOnUiThreadWhenAllBackgroudTaskIsOver();
            }
        };
    }

    public abstract void doOnbackground();

    public abstract void doOnUiThreadWhenAllBackgroudTaskIsOver();

    public SyncTask with(Activity activity) {
        LifeUtil.addLifeCycle(activity, new InnerLifeCycleListener());
        return this;
    }
    public SyncTask with(Fragment fragment) {
        LifeUtil.addLifeCycle(fragment, new InnerLifeCycleListener());
        return this;
    }

    private class InnerLifeCycleListener implements LifeCycleListener {

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
            release();
        }
    }

    /**
     * 释放
     */
    public void release() {
        messageQueue.removeIdleHandler(this);
    }

    @Override
    public boolean queueIdle() {
        mainHandler.sendEmptyMessage(0);
        return true;
    }

    public void run() {
        if (isRemoveOldTask()) {
            handler.removeCallbacksAndMessages(null);
        }
        handler.sendEmptyMessageDelayed(0, 0);
    }

    /**
     * 是否放弃还没有开始的旧任务
     *
     * @return
     */
    public Boolean isRemoveOldTask() {
        return false;
    }
}
