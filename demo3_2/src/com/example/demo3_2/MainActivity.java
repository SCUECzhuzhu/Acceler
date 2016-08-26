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
        private int times; // �ƶ��Ĵ���
        private String name = "yang";
        
        @Override  
        protected void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);  
            setContentView(R.layout.activity_main);
            
            
// 			�����߳�
// 			MyThread myThread = new MyThread();
//          new Thread(myThread).start();
 
            
            // ͨ��DateFormat��ȡϵͳ��ʱ��
            currentTime = DateFormat.format("yyyy/MM/dd  hh:mm:ss", new Date()).toString();
            currentTime = "ͨ��DateFormat��ȡ��ʱ��:\n" + currentTime;            
            
            
//          handler.postDelayed(runnable, 10000); // �򿪶�ʱ����ִ�в���,�ڶ��������ĵ�λҲ�Ǻ���
//          handler.removeCallbacksAndMessages(this); // �رն�ʱ������
            
            
            textviewT = (TextView) findViewById(R.id.textView5);     
            textviewX = (TextView) findViewById(R.id.textView1);  
            textviewY = (TextView) findViewById(R.id.textView3);  
            textviewZ = (TextView) findViewById(R.id.textView4);   
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // ���ٶȴ�����  
            if (null == mSensorManager) {  
                Log.d(TAG, "This deveice not support SensorManager");  
            }  
              
            mSensorManager.registerListener(this, mSensor,  
                    SensorManager.SENSOR_DELAY_NORMAL);	// ������:���ľ�׼��
            
            /*  name = intent.getExtras().getString("name"); */
            
            //��ȡ��ǰ�û����û���
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            name = sharedPreferences.getString("name","");
            sendToServerTimesEvery10Minutes();
        }
        
        
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {           
//			@Override
//            public void run() {                                
//            	Toast.makeText(MainActivity.this, "����һ��ʱ����!", Toast.LENGTH_SHORT).show();     
//              handler.postDelayed(this, 100000); // �ڶ�����������ʱʱ��,��ĵ�λ�Ǻ���
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
        
//      ������ԭ�����ͨ��ÿ�εõ���x,y,z�����ֵ,����һ�ε�ֵ���Ƚ�,
//             ����ÿ����ֵ�о���ֵ����,�������ĳһ����ֵ(�Լ�����)����������״̬������x��(�Լ�����)��
//             ���Ǿ���Ϊ�ֻ����ڣ��������ƶ�״̬����Ȼ�����жϿ϶��ǲ���ѧ�ģ�
//             ��ʱ��Ҳ��������У��Ƚ�����ĳ������ǣ�Я���ֻ����ڹ����ϻ��ǿ�����(���Դ������Ҫע�⣡)
        
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
                    
                    Toast.makeText(getApplicationContext(), "��⵽�ֻ������ƶ�...", Toast.LENGTH_SHORT).show();
                    
                    toast = Toast.makeText(getApplicationContext(),
                    	     "���ǵ�" + times + "���ƶ�!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();                   
                }                      
                mX = x;  
                mY = y;  
                mZ = z;  
            }  
        }  
      
        /**
         * ��ȡ���е�һ�����ֵ            
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
               
        // ÿʮ�����ϴ�һ��ÿʮ�����ƶ��Ĵ���
        protected void sendToServerTimesEvery10Minutes() {
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    // ÿ��(10minutes)��Ҫִ�еĴ���
                	
                    NetWork.postFrequency(name, times + " ");
                    times = 0;
                    Log.d("xxx", name);                  
                }
            };
            
            // 10minutes = 10 * 60 * 1000 ms,����ʱ��Ϊÿ10��
            // timer.schedule(timerTask, 10 * 60 * 1000);
            
            timer.schedule(timerTask, 1000, 1 * 60 * 1000);
            
            // �ϴ���times��0,���¼���.
        }
 
}  
