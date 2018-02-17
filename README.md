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
compile 'com.github.wangdanlizhiyun:life:v1.0.3'

 
 
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
        LifeUtil.startActivityForResult(new Intent(MainActivity.this,SecondActivity.class),new ResultListenerAdapter(){
                            @Override
                            public void onResultOk(Intent intent) {
                                super.onResultOk(intent);
                                int id = intent.getIntExtra("id",0);
                                Toast.makeText(MainActivity.this,"获取id="+id,Toast.LENGTH_SHORT).show();
                            }
        
                            @Override
                            public void onResultCancel(Intent intent) {
                                super.onResultCancel(intent);
                                int id = intent.getIntExtra("id",0);
                                Toast.makeText(MainActivity.this,"取消 但是返回id="+id,Toast.LENGTH_SHORT).show();
                            }
                        });
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
   SyncTask syncTask = new SyncTask(){
      
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
              
              syncTask.run();
  ```