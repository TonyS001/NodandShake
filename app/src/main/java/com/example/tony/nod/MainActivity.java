package com.example.tony.nod;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor AccelerSensor;
    private Sensor GyroSensor;
    private TextView nod;
    private TextView evaluate;
    private Button clearBtn;
    private double nodcount = 0;
    private long PreTime=0;
    private long TStart=0;
    private int charge=0;
    private int state=0;

    private Vector templateData_x = new Vector(300,5);
    private Vector templateData_y = new Vector(300,5);
    private Vector templateData_z = new Vector(300,5);

    private Vector compareData_x = new Vector(300,5);
    private Vector compareData_y = new Vector(300,5);
    private Vector compareData_z = new Vector(300,5);

    private float weight_x;
    private float weight_y;
    private float weight_z;
    private float d_x;
    private float d_y;
    private float d_z;
    private float dij=0;
    private float DTW=0;

    float [] value_a;
    float [] value_g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        nod = (TextView)findViewById(R.id.nod);
        evaluate=(TextView)findViewById(R.id.dtw) ;
        clearBtn=(Button)findViewById(R.id.clear);
        clearBtn.setOnClickListener(new myOnClickListener());

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        AccelerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        GyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(sensorEventListener,AccelerSensor,400000);
        sensorManager.registerListener(sensorEventListener, GyroSensor,400000);
    }

    private class myOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            nodcount=0;
            nod.setText("点头次数为"+nodcount);
            evaluate.setText("停止扫码");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    private void dtw(){
        //DTW=0;
        D();
        weight();
        DTW = weight_x * d_x * d_x + weight_y * d_y * d_y + weight_z * d_z * d_z;
        DTW=(float)(Math.sqrt((double)DTW));
    }

    private void D(){
        int len_t=templateData_x.size();
        int len_c=compareData_x.size();
        float[][]d=new float[len_c][len_t];
        dij=0;
        d[0][0]=((float)templateData_x.get(0)-(float)compareData_x.get(0))*
                ((float)templateData_x.get(0)-(float)compareData_x.get(0));
        for (int i=1;i<len_c;++i){
            dij=((float)templateData_x.get(0)-(float)compareData_x.get(i))*
                    ((float)templateData_x.get(0)-(float)compareData_x.get(i));
            dij=dij+d[i-1][0];
            d[i][0]=dij;

        }
        for (int i=1;i<len_t;++i){
            dij=((float)templateData_x.get(i)-(float)compareData_x.get(0))*
                    ((float)templateData_x.get(i)-(float)compareData_x.get(0));
            dij=dij+d[0][i-1];
            d[0][i]=dij;

            for (int j=1;j<len_c;++j){
                dij=((float)templateData_x.get(i)-(float)compareData_x.get(j))*
                        ((float)templateData_x.get(i)-(float)compareData_x.get(j));
                float min=Math.min(d[j-1][i-1],d[j-1][i]);
                min=Math.min(min,d[j][i-1]);
                dij=dij+min;
                d[j][i]=dij;
            }
        }
        d_x=d[len_c-1][len_t-1];

        d[0][0]=((float)templateData_y.get(0)-(float)compareData_y.get(0))*
                ((float)templateData_y.get(0)-(float)compareData_y.get(0));
        for (int i=1;i<len_c;++i){
            dij=((float)templateData_y.get(0)-(float)compareData_y.get(i))*
                    ((float)templateData_y.get(0)-(float)compareData_y.get(i));
            dij=dij+d[i-1][0];
            d[i][0]=dij;
        }
        for (int i=1;i<len_t;++i){
            dij=((float)templateData_y.get(i)-(float)compareData_y.get(0))*
                    ((float)templateData_y.get(i)-(float)compareData_y.get(0));
            dij=dij+d[0][i-1];
            d[0][i]=dij;
            for (int j=1;j<len_c;++j){
                dij=((float)templateData_y.get(i)-(float)compareData_y.get(j))*
                        ((float)templateData_y.get(i)-(float)compareData_y.get(j));
                float min=Math.min(d[j-1][i-1],d[j-1][i]);
                min=Math.min(min,d[j][i-1]);
                dij=dij+min;
                d[j][i]=dij;
            }
        }
        d_y=d[len_c-1][len_t-1];

        d[0][0]=((float)templateData_z.get(0)-(float)compareData_z.get(0))*
                ((float)templateData_z.get(0)-(float)compareData_z.get(0));
        for (int i=1;i<len_c;++i){
            dij=((float)templateData_z.get(0)-(float)compareData_z.get(i))*
                    ((float)templateData_z.get(0)-(float)compareData_z.get(i));
            dij=dij+d[i-1][0];
            d[i][0]=dij;
        }
        for (int i=1;i<len_t;++i){
            dij=((float)templateData_z.get(i)-(float)compareData_z.get(0))*
                    ((float)templateData_z.get(i)-(float)compareData_z.get(0));
            dij=dij+d[0][i-1];
            d[0][i]=dij;
            for (int j=1;j<len_c;++j){
                dij=((float)templateData_z.get(i)-(float)compareData_z.get(j))*
                        ((float)templateData_z.get(i)-(float)compareData_z.get(j));
                float min=Math.min(d[j-1][i-1],d[j-1][i]);
                min=Math.min(min,d[j][i-1]);
                dij=dij+min;
                d[j][i]=dij;
            }
        }
        d_z=d[len_c-1][len_t-1];
    }

    private void weight(){
        int len=templateData_x.size();
        float sum_a=0,sum_b=0,sum_c=0;
        for (int i=0;i<len;i++){
            sum_a+=(float)templateData_x.get(i);
            sum_b+=(float)templateData_y.get(i);
            sum_c+=(float)templateData_z.get(i);
        }
        sum_a=sum_a/len;
        sum_b=sum_b/len;
        sum_c=sum_c/len;

        float std_a=0,std_b=0,std_c=0;
        for (int i=0;i<len;i++){
            std_a+=((float)templateData_x.get(i)-sum_a)*((float)templateData_x.get(i)-sum_a);
            std_b+=((float)templateData_y.get(i)-sum_b)*((float)templateData_y.get(i)-sum_b);
            std_c+=((float)templateData_z.get(i)-sum_c)*((float)templateData_z.get(i)-sum_c);
        }
        std_a=std_a/len;
        std_b=std_b/len;
        std_c=std_c/len;

        double sa=Math.sqrt((double)std_a);
        double sb=Math.sqrt((double)std_b);
        double sc=Math.sqrt((double)std_c);

        weight_x=(float)(sa/(sa+sb+sc));
        weight_y=(float)(sb/(sa+sb+sc));
        weight_z=(float)(sc/(sa+sb+sc));
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:{
                    value_a = sensorEvent.values;

                    if (charge==0){
                        if(value_a[2]>1.3){
                            charge=1;
                            TStart = SystemClock.elapsedRealtime();
                        }
                    }

                    if (charge==1){
                        if (value_a[0]>2){
                            charge=2;
                            state=0;
                            compareData_x.clear();
                            compareData_y.clear();
                            compareData_z.clear();
                        }else if(Math.abs(value_a[2])<0.2) {
                            PreTime = SystemClock.elapsedRealtime();
                            //nod.setText(String.valueOf(PreTime-TStart));
                            if ((PreTime-TStart)>130){
                                if (nodcount>0){
                                    dtw();
                                    evaluate.setText(String.valueOf(DTW));
                                    if (DTW<10){
                                        nodcount++;

                                        state = 1;
                                        if (DTW < 4) {
                                            templateData_x.clear();
                                            templateData_y.clear();
                                            templateData_z.clear();
                                            for (int i=0;i<compareData_x.size();++i){
                                                templateData_x.add(compareData_x.get(i));
                                                templateData_y.add(compareData_y.get(i));
                                                templateData_z.add(compareData_z.get(i));
                                            }
                                        }
                                    }
                                }else {
                                    nodcount++;
                                    state = 1;
                                    for (int i=0;i<compareData_x.size();++i){
                                        templateData_x.add(compareData_x.get(i));
                                        templateData_y.add(compareData_y.get(i));
                                        templateData_z.add(compareData_z.get(i));
                                    }
                                }
                            }
                            charge=0;
                            compareData_x.clear();
                            compareData_y.clear();
                            compareData_z.clear();
                        }
                    }

                    if (charge==2){
                        if(Math.abs(value_a[2])<0.2){
                            charge=0;
                        }
                    }
                    break;
                }

                case Sensor.TYPE_GYROSCOPE:{
                    value_g = sensorEvent.values;
                    if (charge==0){
                        if (Math.abs(value_g[1])>2){
                            state=0;
                        }
                    }
                    if (charge==1){
                        if (Math.abs(value_g[1])>2){
                            charge=2;
                            state=0;
                            compareData_x.clear();
                            compareData_y.clear();
                            compareData_z.clear();
                        }else{
                            compareData_x.add(value_g[0]);
                            compareData_y.add(value_g[1]);
                            compareData_z.add(value_g[2]);
                        }
                    }
                    break;
                }
            }

            nod.setText("点头次数为"+nodcount);
            //if (state==1){
                //evaluate.setText("开始扫码");
            //}else{
                //evaluate.setText("停止扫码");
            //}
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}
