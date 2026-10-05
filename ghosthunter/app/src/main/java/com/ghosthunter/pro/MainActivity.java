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
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); int tab=0; float d;
        int bg=Color.rgb(5,8,13), card=Color.rgb(10,16,25), white=Color.rgb(225,232,242),
            muted=Color.rgb(135,151,175), green=Color.rgb(0,230,160), purple=Color.rgb(145,85,255),
            cyan=Color.rgb(50,190,255);

        GhostView(){super(MainActivity.this); d=getResources().getDisplayMetrics().density; setBackgroundColor(bg);}
        void t(Canvas c,String s,float x,float y,float size,int col){p.setStyle(Paint.Style.FILL);p.setTextSize(size);p.setColor(col);c.drawText(s,x,y,p);}
        void box(Canvas c,float l,float top,float r,float bot){p.setStyle(Paint.Style.FILL);p.setColor(card);c.drawRoundRect(l,top,r,bot,24,24,p);}

        @Override protected void onDraw(Canvas c){
            super.onDraw(c); c.save(); c.scale(d,d);
            float w=getWidth()/d, h=getHeight()/d;
            t(c,"GHOST HUNTER",24,34,24,white); t(c,"REAL SENSORS · REAL DATA",26,53,10,muted); t(c,"⚙",w-42,37,24,white);
            String[] a={"EMF","REM","TALKBOX","RADAR","CAMERA"}; float tw=w/5f;
            for(int i=0;i<5;i++){p.setTextSize(13);float cx=i*tw+tw/2f;float lw=p.measureText(a[i]);int col=i==tab?green:muted;t(c,a[i],cx-lw/2f,82,13,col);if(i==tab){p.setColor(green);c.drawRect(i*tw,94,(i+1)*tw,96,p);}}
            if(tab==0) emf(c,w); else if(tab==1) rem(c,w); else if(tab==2) talk(c,w); else if(tab==3) radar(c,w); else camera(c,w);
            t(c,"⌂",w*.15f-8,h-31,24,tab==0?green:muted);t(c,"☷",w*.38f-8,h-31,24,muted);t(c,"□",w*.62f-8,h-31,24,muted);t(c,"⋮",w*.85f-8,h-31,24,muted);
            t(c,"Home",w*.10f,h-10,11,tab==0?green:muted);t(c,"History",w*.33f,h-10,11,muted);t(c,"Recordings",w*.55f,h-10,11,muted);t(c,"More",w*.82f,h-10,11,muted); c.restore();
        }

        void emf(Canvas c,float w){
            box(c,14,116,w-14,465);t(c,"✦  EMF",30,145,21,white);t(c,mag!=null?"● Sensor Active":"● Sensor unavailable",w-185,145,11,mag!=null?green:Color.RED);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(16);p.setColor(Color.rgb(30,43,60));c.drawArc(40,165,245,365,140,260,false,p);p.setColor(green);c.drawArc(40,165,245,365,140,Math.min(250,total/2f),false,p);p.setStyle(Paint.Style.FILL);
            t(c,String.format(Locale.US,"%.2f",total),84,270,42,white);t(c,"µT",132,294,16,muted);t(c,"Magnetic Field",80,323,13,muted);
            t(c,String.format(Locale.US,"X  %.2f µT",x),270,195,14,white);t(c,String.format(Locale.US,"Y  %.2f µT",y),270,230,14,white);t(c,String.format(Locale.US,"Z  %.2f µT",z),270,265,14,white);
            t(c,"LIVE MAGNETIC FIELD",270,310,11,muted);t(c,"Physical sensor reading",270,332,12,white);
            box(c,14,485,w-14,550);t(c,"♢  Alert Threshold",30,522,15,muted);t(c,"2.0 µT  ›",w-92,522,13,white);
            t(c,"Readings are physical sensor data; an anomaly is not proof of paranormal activity.",22,574,9,muted);
        }
        void rem(Canvas c,float w){
            box(c,14,116,w-14,385);t(c,"◎  REM DETECTOR",30,145,21,white);t(c,"● MONITORING",w-125,145,11,green);
            t(c,String.format(Locale.US,"%.3f µT/s",rate),34,215,38,white);t(c,"MAGNETIC CHANGE RATE",36,239,11,muted);t(c,"Sensitivity",36,292,13,muted);t(c,"MEDIUM   ›",w-110,292,13,white);
            t(c,"Monitors rapid changes in the phone's magnetic-field sensor.",22,350,10,muted);t(c,"Sources can include electronics, movement and the environment.",22,370,10,muted);
        }
        void talk(Canvas c,float w){
            box(c,14,116,w-14,410);t(c,"♩  TALKBOX",30,145,21,white);t(c,audioOn?"● MIC ACTIVE":"● MIC OFF",w-125,145,11,audioOn?green:Color.RED);
            t(c,"AUDIO LEVEL",34,193,11,muted);t(c,String.format(Locale.US,"%d %%",level),34,235,40,white);p.setColor(cyan);
            float barW=Math.max(5,(w-70)/36f);for(int i=0;i<36;i++){float bh=4+level*(0.18f+0.82f*(float)Math.abs(Math.sin(i*1.7)));float left=35+i*barW;c.drawRoundRect(left,350-bh,left+Math.max(3,barW-2),350,3,3,p);}
            t(c,"LIVE MICROPHONE ANALYSIS",34,382,11,muted);t(c,"Microphone input is real-time audio data.",22,445,10,muted);
        }
        void radar(Canvas c,float w){
            box(c,14,116,w-14,515);t(c,"◎  RADAR",30,145,21,white);float cx=w/2,cy=320,maxR=Math.min(190,w/2-35);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);p.setColor(Color.rgb(40,70,80));for(float rr=maxR/4;rr<=maxR;rr+=maxR/4)c.drawCircle(cx,cy,rr,p);
            p.setColor(green);c.drawLine(cx-maxR,cy,cx+maxR,cy,p);c.drawLine(cx,cy-maxR,cx,cy+maxR,p);p.setStyle(Paint.Style.FILL);
            float ang=(float)(System.currentTimeMillis()/1500.0);float d2=Math.min(maxR,rate*100);c.drawCircle(cx+(float)Math.cos(ang)*d2,cy+(float)Math.sin(ang)*d2,7,p);t(c,"LIVE SENSOR RADAR",cx-63,540,11,muted);
        }
        void camera(Canvas c,float w){
            box(c,14,116,w-14,515);t(c,"▣  CAMERA",30,145,21,white);t(c,"CAMERA OVERLAY",30,185,12,muted);
            t(c,String.format(Locale.US,"EMF   %.2f µT",total),30,225,18,green);t(c,String.format(Locale.US,"REM   %.3f µT/s",rate),30,260,18,purple);t(c,"Camera preview + sensor overlay is planned for the next build.",30,315,10,muted);
        }
        @Override public boolean onTouchEvent(MotionEvent e){
            if(e.getAction()==MotionEvent.ACTION_UP){float y=e.getY()/d,xPos=e.getX()/d;if(y<108){int n=(int)(xPos/(getWidth()/d/5f));if(n>=0&&n<5){tab=n;invalidate();}}}return true;
        }
    }
}
