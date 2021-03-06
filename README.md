# 该library用于解决跟组件生命周期相关的问题。
 1。封装动态权限请求(兼容小米等脑残手机，支持子线程调用,不需要requestcode和注解)
 
 2。封装startactivityforreuslt（不需要requestcode和注解）
 
 3。初始化顺便解决InputMethodManager的内存泄露bug
 
 4。可以获取activity/fragment生命周期，用于封装跟生命周期相关的功能模块。如braintree这个支付平台的2.0版本
 
 5。SyncTask封装一个同步任务类。当短时间内提交多个异步任务时，等待他们执行任务后回掉到ui线程
    * run方法触发执行一个异步任务，
    * doOnbackground定义具体异步任务，
    * doOnUiThreadWhenAllBackgroudTaskIsOver ui线程回掉，
    * isRemoveOldTask是否放弃还没有开始的旧任务，默认false表示全部执行
    * release释放回掉对象
    * with(activity/fragmet)绑定所在组件，destroy时自动release，其他情况自行处理释放
 
 
#使用
maven { url 'https://jitpack.io' }
compile 'com.github.wangdanlizhiyun:life:1.3.1'

 
 
 在application里初始化
    ```
        LifeUtil.init(this);
    ```
  权限
  
  ```
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
  ```
  
    
  startActivityForResult
  
   ```
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
   ```
   
   前后台切换监听
   ```
   LifeUtil.setAppFourgroundOrBackgroundChangeListener(new AppFourgroundOrBackgroundChangeListener() {
               @Override
               public void change(Boolean isToBackground) {
   
               }
           });
   ```
   生命周期获取(可以子线程使用)
   
   ```LifeUtil.addLifeCycle(activity, new LifeCycleListener())
   LifeUtil.addLifeCycle(fragment, new LifeCycleListener())
   ```
   SyncTask使用示例
   
   ```
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
  ```
  监听app退到后台多久
  ```
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
   ```