package lzy.com.life_library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

/**
 * Created by 李志云 10/11/18 16:40
 */
public class TimeUtil {
    public static long sLongOffset = 0;
    public static long sSohu;
    private static SharedPreferences sSharedPreferences;
    private static SharedPreferences.Editor sEditor;

    /**
     * 获取当前网络时间
     * @return
     */
    public static long getCurrentWebTime(){
        return System.currentTimeMillis() - sLongOffset;
    }

    /**
     * 开启同步时间，启动界面执行一次
     */
    public static void syncTime(Context context){
        sSharedPreferences = context.getSharedPreferences(TimeUtil.class.getName(),Context.MODE_MULTI_PROCESS);
        sEditor = sSharedPreferences.edit();
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(TimeWork.class,2,TimeUnit.HOURS).setConstraints(constraints).build();
        WorkManager.getInstance().enqueue(workRequest);
    }

    public static long getDelayTime(long totalTime){
        if (TimeUtil.getSynTime() <= 0L){
            return -1;
        }
        if (totalTime <= 0) {
            return -1;
        }
        long offSet = getCurrentWebTime() - getSynTime();
        return totalTime - offSet % totalTime;
    }

    public static void setSynTime(long value){
        synchronized (TimeUtil.class){
            sEditor.putLong("synTime",value);
            sEditor.commit();
        }
    }

    public synchronized static long getSynTime(){
        synchronized (TimeUtil.class){
            long value = sSharedPreferences.getLong("synTime",0);
            if (value > 0){
                value = 1;
            }else {
                value = 0;
            }
            return value;
        }
    }

    public static long getNetworkTime(String webUrl) {
        try {
            URL url = new URL(webUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            return conn.getDate();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static class TimeWork extends Worker{
        public TimeWork() {
        }

        @Override
        public Result doWork() {
            String webUrl9 = "http://www.sohu.com";// sohu
            sSohu = getNetworkTime(webUrl9);
            long systemTime = System.currentTimeMillis();
            if (sSohu <= 0){
                return Result.RETRY;
            }else {
                sLongOffset = systemTime - sSohu;
            }
            return Result.SUCCESS;
        }
    }

}
