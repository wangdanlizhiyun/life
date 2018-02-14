# 该library用于解决跟组件生命周期相关的问题。
 1。封装动态权限请求(兼容小米等脑残手机，支持子线程调用)
 2。封装startactivityforreuslt
 3。初始化顺便解决InputMethodManager的内存泄露bug
 4。可以获取activity/fragment生命周期，用于封装跟生命周期相关的功能模块。如braintree这个支付平台的2.0版本
 
 
#使用
maven { url 'https://jitpack.io' }

implementation 'com.github.wangdanlizhiyun:permissionAndForResult:8c582910d4'
 
 
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
  #TODO:
  
  
  