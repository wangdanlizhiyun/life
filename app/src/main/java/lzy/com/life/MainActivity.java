package lzy.com.life;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import lzy.com.life_library.entity.PermissionType;
import lzy.com.life_library.listener.AppGotoBackgroundSomeTimeListener;
import lzy.com.life_library.listener.PermissionDeniedListener;
import lzy.com.life_library.listener.ResultCancelListener;
import lzy.com.life_library.listener.ResultOkListener;
import lzy.com.life_library.task.SyncTask;
import lzy.com.life_library.utils.LifeUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LifeUtil.permission(PermissionType.READ_EXTERNAL_STORAGE, PermissionType.WRITE_EXTERNAL_STORAGE, PermissionType.CALL_PHONE)
                                .deny(new PermissionDeniedListener() {
                                    @Override
                                    public void onDenied(List<String> perms) {
                                        Toast.makeText(MainActivity.this, "denied", Toast.LENGTH_SHORT).show();
                                    }
                                }).run(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "granted and run", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).start();

            }
        });
        findViewById(R.id.get_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LifeUtil.resultOk(new ResultOkListener() {
                    @Override
                    public void onResultOk(Intent intent) {
                        int id = intent.getIntExtra("id", 0);
                        Toast.makeText(MainActivity.this, "获取id=" + id, Toast.LENGTH_SHORT).show();
                    }
                }).resultCancel(new ResultCancelListener() {
                    @Override
                    public void onResultCancel(Intent intent) {
                        int id = intent.getIntExtra("id", 0);
                        Toast.makeText(MainActivity.this, "取消 但是返回id=" + id, Toast.LENGTH_SHORT).show();
                    }
                }).startActivityForResult(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

        syncTask = new SyncTask<Boolean,String>(){
            @Override
            public String doOnbackground(List<Boolean> booleans) {
                Log.e("test","点赞开始"+booleans.get(0));
                try {
                    Thread.sleep(3_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("test","点赞结束"+booleans.get(0));
                return "返回点赞结果:"+booleans.get(0);
            }

            @Override
            public void doOnUiThreadWhenAllBackgroudTaskIsOver(String s) {
                Log.e("test","获取点赞结果，修改ui");
                if (s.endsWith("true")){

                }else {

                }
            }

            @Override
            public Boolean isRemoveOldTask() {
                return true;
            }
        }.with(this);
        //快速多次点击模拟点赞。
        findViewById(R.id.sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //客户端预测点赞或去赞成功
                mIsZan = !mIsZan;
                syncTask.run(mIsZan);
            }
        });
        LifeUtil.addAppGotoBackgroundSomeTimeListener(new AppGotoBackgroundSomeTimeListener() {
            @Override
            public long delayTime() {
                return 30_000;
            }

            @Override
            public void gotoBackground() {
                Log.e("test","app 退到后台30秒了");
            }
        });

    }
    Boolean mIsZan = false;
    SyncTask syncTask;
}