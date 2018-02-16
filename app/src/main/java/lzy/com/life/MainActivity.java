package lzy.com.life;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import lzy.com.life_library.entity.PermissionType;
import lzy.com.life_library.listener.LifeCycleListener;
import lzy.com.life_library.listener.PermissionDeniedListener;
import lzy.com.life_library.listener.ResultListenerAdapter;
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
                LifeUtil.startActivityForResult(new Intent(MainActivity.this, SecondActivity.class), new ResultListenerAdapter() {
                    @Override
                    public void onResultOk(Intent intent) {
                        super.onResultOk(intent);
                        int id = intent.getIntExtra("id", 0);
                        Toast.makeText(MainActivity.this, "获取id=" + id, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResultCancel(Intent intent) {
                        super.onResultCancel(intent);
                        int id = intent.getIntExtra("id", 0);
                        Toast.makeText(MainActivity.this, "取消 但是返回id=" + id, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        syncTask = new SyncTask(){

            @Override
            public void doOnbackground() {
                Log.e("test","异步任务开始");
                try {
                    Thread.sleep(3_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("test","异步任务结束");
            }

            @Override
            public void doOnUiThreadWhenAllBackgroudTaskIsOver() {
                Log.e("test","doOnUiThreadWhenAllBackgroudTaskIsOver");
            }

            @Override
            public Boolean isRemoveOldTask() {
                return true;
            }
        }.with(this);
        findViewById(R.id.sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncTask.run();
            }
        });

    }
    SyncTask syncTask;
}