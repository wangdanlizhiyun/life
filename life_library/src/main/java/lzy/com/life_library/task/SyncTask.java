package lzy.com.life_library.task;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

public abstract class SyncTask<Params,Result> implements MessageQueue.IdleHandler {
    MessageQueue messageQueue = null;
    Handler mainHandler;
    Handler threadHandler;
    Result result;

    public SyncTask() {
        HandlerThread handlerThread = new HandlerThread("workThread");
        handlerThread.start();
        if ((messageQueue = getMessageQueue(handlerThread.getLooper())) != null) {
            messageQueue.addIdleHandler(this);
        }

        threadHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                    if (msg.obj instanceof Object[]){
                        Object[] objects = (Object[]) msg.obj;
                        if (objects != null && objects.length > 0){
                            ArrayList<Params> ts = new ArrayList<>();
                            for (Object object:objects
                                    ) {
                                ts.add((Params) object);
                            }
                            result = doOnbackground(ts);
                        }
                    }

            }
        };
        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                doOnUiThreadWhenAllBackgroudTaskIsOver(result);
            }
        };
    }
    public MessageQueue getMessageQueue(Looper looper){
        MessageQueue messageQueue = null;
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
        return messageQueue;
    }

    public abstract Result doOnbackground(List<Params> params);

    public abstract void doOnUiThreadWhenAllBackgroudTaskIsOver(Result result);

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
        mainHandler.removeCallbacksAndMessages(null);
        threadHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean queueIdle() {
        Message message = mainHandler.obtainMessage();
        message.obj = result;
        mainHandler.sendMessage(message);
        return true;
    }

    public void run(Params... params) {
        if (isRemoveOldTask()) {
            threadHandler.removeCallbacksAndMessages(null);
        }
        Message message = threadHandler.obtainMessage();
        message.obj = params;
        threadHandler.sendMessageDelayed(message, 0);
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
