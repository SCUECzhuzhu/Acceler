package com.example.demo3_2;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {  
      
        private static final String TAG = MainActivity.class.getSimpleName();
        public static final String PREFERENCES = "LoginSession";
        private SensorManager mSensorManager;  
        private Sensor mSensor; 
         
        private TextView textviewX;  
        private TextView textviewY;  
        private TextView textviewZ;    
        private TextView textviewT;
        private Toast toast;
        private String currentTime;
        private long lasttimestamp = 0;        
        private int mX, mY;
        private int mZ = 9;        
 
        private Calendar mCalendar;
        private int times; // 移动的次数
        private String name = "yang";
        
        @Override  
        protected void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);  
            setContentView(R.layout.activity_main);
            
            
// 			启动线程
// 			MyThread myThread = new MyThread();
//          new Thread(myThread).start();
 
            
            // 通过DateFormat获取系统的时间
            currentTime = DateFormat.format("yyyy/MM/dd  hh:mm:ss", new Date()).toString();
            currentTime = "通过DateFormat获取的时间:\n" + currentTime;            
            
            
//          handler.postDelayed(runnable, 10000); // 打开定时器，执行操作,第二个参数的单位也是毫秒
//          handler.removeCallbacksAndMessages(this); // 关闭定时器处理
            
            
            textviewT = (TextView) findViewById(R.id.textView5);     
            textviewX = (TextView) findViewById(R.id.textView1);  
            textviewY = (TextView) findViewById(R.id.textView3);  
            textviewZ = (TextView) findViewById(R.id.textView4);   
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 加速度传感器  
            if (null == mSensorManager) {  
                Log.d(TAG, "This deveice not support SensorManager");  
            }  
              
            mSensorManager.registerListener(this, mSensor,  
                    SensorManager.SENSOR_DELAY_NORMAL);	// 参数三:检测的精准度
            
            /*  name = intent.getExtras().getString("name"); */
            
            //获取当前用户的用户名
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            name = sharedPreferences.getString("name","");
            sendToServerTimesEvery10Minutes();
        }
        
        
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {           
//			@Override
//            public void run() {                                
//            	Toast.makeText(MainActivity.this, "这是一定时间间隔!", Toast.LENGTH_SHORT).show();     
//              handler.postDelayed(this, 100000); // 第二个参数是延时时长,其的单位是毫秒
//            }            
//        };
		
        
        @Override
        public void onDestroy() {
        	super.onDestroy();
        	if (mSensorManager != null) {
        		mSensorManager.unregisterListener(this);
        	}
        }
              
        @Override  
        public void onAccuracyChanged(Sensor sensor, int accuracy) {  
      
        }  
        
//      传感器原理就是通过每次得到的x,y,z三轴的值,和下一次的值作比较,
//             它们每个差值中绝对值最大的,如果超过某一个阈值(自己定义)，并且这种状态持续了x秒(自己定义)，
//             我们就视为手机处于（颠簸）移动状态，当然这种判断肯定是不科学的，
//             有时候也会产生误判，比较理想的场景就是：携带手机坐在公交上或是开车。(所以此种情况要注意！)
        
        @Override  
        public void onSensorChanged(SensorEvent event) {  
            if (event.sensor == null) {  
                return;  
            }  
      
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {  
                int x = (int) event.values[0];  
                int y = (int) event.values[1];  
                int z = (int) event.values[2];                
                mCalendar = Calendar.getInstance();  
                long stamp = mCalendar.getTimeInMillis() / 1000; 
//              long second = mCalendar.get(Calendar.SECOND);
                
                textviewT.setText(currentTime);                               
                textviewX.setText(String.valueOf(x));  
                textviewY.setText(String.valueOf(y));  
                textviewZ.setText(String.valueOf(z));
                                              
                int pX = Math.abs(mX - x);  
                int pY = Math.abs(mY - y);  
                int pZ = Math.abs(mZ - z);
                
//              Log.d(TAG, "pX:" + pX + "  pY:" + pY + "  pZ:" + pZ + "    stamp:"  
//                        + stamp + "  second:" + second);
                
                int maxValue = getMaxValue(pX, pY, pZ);                               
                if (maxValue > 2 && stamp - lasttimestamp > 3) {
                	times++;
                    lasttimestamp = stamp;
                    
                    Log.d(TAG, "This mobile is moving...");
                    
                    Toast.makeText(getApplicationContext(), "检测到手机正在移动...", Toast.LENGTH_SHORT).show();
                    
                    toast = Toast.makeText(getApplicationContext(),
                    	     "这是第" + times + "次移动!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();                   
                }                      
                mX = x;  
                mY = y;  
                mZ = z;  
            }  
        }  
      
        /**
         * 获取其中的一个最大值            
         */  
        public int getMaxValue(int px, int py, int pz) {  
            int max = 0;  
            if (px > py && px > pz) {  
                max = px;  
            } else if (py > px && py > pz) {  
                max = py;  
            } else if (pz > px && pz > py) {  
                max = pz;  
            }  
      
            return max;  
        }
               
        // 每十分钟上传一次每十分钟移动的次数
        protected void sendToServerTimesEvery10Minutes() {
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    // 每次(10minutes)需要执行的代码
                	
                    NetWork.postFrequency(name, times + " ");
                    times = 0;
                    Log.d("xxx", name);                  
                }
            };
            
            // 10minutes = 10 * 60 * 1000 ms,测试时候为每10秒
            // timer.schedule(timerTask, 10 * 60 * 1000);
            
            timer.schedule(timerTask, 1000, 1 * 60 * 1000);
            
            // 上传后times归0,重新计数.
        }
 
}  
