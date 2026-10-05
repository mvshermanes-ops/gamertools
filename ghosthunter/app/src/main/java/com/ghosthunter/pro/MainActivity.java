package com.ghosthunter.pro;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.hardware.*;
import android.media.*;
import android.view.*;
import java.util.*;

public class MainActivity extends Activity implements SensorEventListener {
    SensorManager sm; Sensor mag; GhostView v;
    float x,y,z,total,prev,rate; long last;
    AudioRecord rec; volatile boolean audioOn=false; volatile int level=0;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(5,8,13));
        getWindow().setNavigationBarColor(Color.rgb(5,8,13));
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        mag=sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        v=new GhostView(); setContentView(v);
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},7);
        if(mag!=null) sm.registerListener(this,mag,SensorManager.SENSOR_DELAY_GAME);
        startAudio();
    }

    void startAudio(){
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)return;
        try{
            int min=AudioRecord.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
            rec=new AudioRecord(MediaRecorder.AudioSource.MIC,44100,AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,Math.max(min,4096));
            rec.startRecording(); audioOn=true;
            new Thread(()->{
                short[] b=new short[1024];
                while(audioOn){
                    int n=rec.read(b,0,b.length); long sum=0;
                    for(int i=0;i<n;i++) sum+=(long)b[i]*b[i];
                    level=n>0?(int)Math.min(100,Math.sqrt(sum/(double)n)/327.68):0;
                    v.postInvalidate();
                }
            }).start();
        }catch(Exception e){audioOn=false;}
    }

    @Override public void onSensorChanged(SensorEvent e){
        if(e.sensor.getType()!=Sensor.TYPE_MAGNETIC_FIELD)return;
        x=e.values[0]; y=e.values[1]; z=e.values[2];
        total=(float)Math.sqrt(x*x+y*y+z*z);
        if(last>0){double dt=(e.timestamp-last)/1e9; if(dt>0)rate=(float)(Math.abs(total-prev)/dt);}
        prev=total; last=e.timestamp; v.postInvalidate();
    }
    @Override public void onAccuracyChanged(Sensor s,int a){}
    @Override protected void onDestroy(){
        super.onDestroy(); if(sm!=null)sm.unregisterListener(this);
        audioOn=false; if(rec!=null){try{rec.stop();}catch(Exception e){} rec.release();}
    }

    class GhostView extends View {
        Paint p=new Paint(3); int tab=0;
        int bg=Color.rgb(5,8,13), card=Color.rgb(10,16,25), white=Color.rgb(225,232,242),
            muted=Color.rgb(135,151,175), green=Color.rgb(0,230,160), purple=Color.rgb(145,85,255),
            cyan=Color.rgb(50,190,255);
        GhostView(){super(MainActivity.this);setBackgroundColor(bg);}
        void t(Canvas c,String s,float x,float y,float size,int col){p.setStyle(Paint.Style.FILL);p.setTextSize(size);p.setColor(col);c.drawText(s,x,y,p);}
        void box(Canvas c,float l,float top,float r,float bot){p.setStyle(Paint.Style.FILL);p.setColor(card);c.drawRoundRect(l,top,r,bot,24,24,p);}
        @Override protected void onDraw(Canvas c){
            float w=getWidth(),h=getHeight();
            t(c,"GHOST HUNTER",32,48,27,white); t(c,"REAL SENSORS · REAL DATA",34,70,10,muted); t(c,"⚙",w-54,52,25,white);
            String[] a={"EMF","REM","TALKBOX","RADAR","CAMERA"}; float tw=w/5f;
            for(int i=0;i<5;i++){float cx=i*tw+tw/2;int col=i==tab?green:muted;t(c,a[i],cx-p.measureText(a[i])/2,105,13,col);if(i==tab){p.setColor(green);c.drawRect(i*tw,119,(i+1)*tw,121,p);}}
            if(tab==0) emf(c,w); else if(tab==1) rem(c,w); else if(tab==2) talk(c,w); else if(tab==3) radar(c,w); else camera(c,w);
            t(c,"⌂",w*.15f,h-35,24,tab==0?green:muted);t(c,"☷",w*.38f,h-35,24,muted);t(c,"□",w*.62f,h-35,24,muted);t(c,"⋮",w*.85f,h-35,24,muted);
            t(c,"Home",w*.10f,h-14,11,tab==0?green:muted);t(c,"History",w*.33f,h-14,11,muted);t(c,"Recordings",w*.55f,h-14,11,muted);t(c,"More",w*.82f,h-14,11,muted);
        }
        void emf(Canvas c,float w){
            box(c,18,145,w-18,510);t(c,"✦  EMF",36,181,22,white);
            t(c,mag!=null?"● Sensor Active":"● Sensor unavailable",w-205,181,12,mag!=null?green:Color.RED);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(18);p.setColor(Color.rgb(30,43,60));c.drawArc(55,205,265,415,140,260,false,p);
            p.setColor(green);c.drawArc(55,205,265,415,140,Math.min(250,total/2f),false,p);p.setStyle(Paint.Style.FILL);
            t(c,String.format(Locale.US,"%.2f",total),105,325,44,white);t(c,"µT",151,350,16,muted);t(c,"Magnetic Field",102,382,13,muted);
            t(c,String.format(Locale.US,"X  %.2f µT",x),295,235,15,white);t(c,String.format(Locale.US,"Y  %.2f µT",y),295,270,15,white);t(c,String.format(Locale.US,"Z  %.2f µT",z),295,305,15,white);
            t(c,"LIVE MAGNETIC FIELD",295,350,11,muted);t(c,"Physical sensor reading",295,373,12,white);
            box(c,18,530,w-18,595);t(c,"♢  Alert Threshold",36,567,15,muted);t(c,"2.0 µT  ›",w-105,567,13,white);
            t(c,"Readings are physical sensor data; an anomaly is not proof of paranormal activity.",28,620,10,muted);
        }
        void rem(Canvas c,float w){
            box(c,18,145,w-18,430);t(c,"◎  REM DETECTOR",36,181,22,white);t(c,"● MONITORING",w-150,181,12,green);
            t(c,String.format(Locale.US,"%.3f µT/s",rate),45,250,40,white);t(c,"MAGNETIC CHANGE RATE",47,274,11,muted);
            t(c,"Sensitivity",47,330,13,muted);t(c,"MEDIUM   ›",w-125,330,13,white);
            t(c,"Monitors rapid changes in the phone's magnetic-field sensor.",30,390,11,muted);
            t(c,"Sources can include electronics, movement and the environment.",30,465,11,muted);
        }
        void talk(Canvas c,float w){
            box(c,18,145,w-18,455);t(c,"♩  TALKBOX",36,181,22,white);t(c,audioOn?"● MIC ACTIVE":"● MIC OFF",w-145,181,12,audioOn?green:Color.RED);
            t(c,"AUDIO LEVEL",42,230,11,muted);t(c,String.format(Locale.US,"%d %%",level),42,275,42,white);
            p.setColor(cyan);for(int i=0;i<36;i++){float bh=4+level*(0.2f+0.8f*(float)Math.abs(Math.sin(i*1.7)));c.drawRoundRect(40+i*8,390-bh,45+i*8,390,3,3,p);}
            t(c,"LIVE MICROPHONE ANALYSIS",42,425,11,muted);t(c,"Microphone input is real-time audio data.",30,490,11,muted);
        }
        void radar(Canvas c,float w){
            box(c,18,145,w-18,560);t(c,"◎  RADAR",36,181,22,white);float cx=w/2,cy=350;
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);p.setColor(Color.rgb(40,70,80));
            for(int rr=70;rr<220;rr+=50)c.drawCircle(cx,cy,rr,p);p.setColor(green);c.drawLine(cx-220,cy,cx+220,cy,p);c.drawLine(cx,cy-220,cx,cy+220,p);p.setStyle(Paint.Style.FILL);
            float ang=(float)(System.currentTimeMillis()/1500.0);float d=Math.min(210,rate*100);c.drawCircle(cx+(float)Math.cos(ang)*d,cy+(float)Math.sin(ang)*d,7,p);
            t(c,"LIVE SENSOR RADAR",cx-70,610,11,muted);
        }
        void camera(Canvas c,float w){
            box(c,18,145,w-18,560);t(c,"▣  CAMERA",36,181,22,white);t(c,"CAMERA OVERLAY",36,225,12,muted);
            t(c,String.format(Locale.US,"EMF   %.2f µT",total),36,270,18,green);t(c,String.format(Locale.US,"REM   %.3f µT/s",rate),36,305,18,purple);
            t(c,"Camera preview + sensor overlay is planned for the next build.",36,360,12,muted);
        }
        @Override public boolean onTouchEvent(MotionEvent e){
            if(e.getAction()==MotionEvent.ACTION_UP && e.getY()<135){int n=(int)(e.getX()/(getWidth()/5f));if(n>=0&&n<5){tab=n;invalidate();}}return true;
        }
    }
}
