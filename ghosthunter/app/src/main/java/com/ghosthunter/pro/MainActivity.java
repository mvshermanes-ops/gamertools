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
    SensorManager sm;
    Sensor mag;
    Sensor rotationSensor;
    GhostView v;
    float x,y,z,total,prev,rate;
    float smoothRate=0f;
    float baselineX,baselineY,baselineZ,baselineTotal;
    float worldX,worldY,worldZ;
    float baselineWorldX,baselineWorldY,baselineWorldZ;
    boolean baselineReady=false;
    boolean rotationReady=false;
    int sensorAccuracy=SensorManager.SENSOR_STATUS_ACCURACY_UNRELIABLE;
    float[] rotationMatrix=new float[9];
    float[] rotationVectorMatrix=new float[9];
    float headingDeg=0f;
    int baselineSamples=0;
    long last;

    final ArrayDeque<RadarPoint> radarTrail = new ArrayDeque<>();
    int radarSensitivity = 2;
    static class RadarPoint { float x,y,alpha; RadarPoint(float x,float y,float alpha){this.x=x;this.y=y;this.alpha=alpha;} }

    AudioRecord rec;
    volatile boolean audioOn=false;
    volatile int level=0;
    Thread audioThread;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(5,8,13));
        getWindow().setNavigationBarColor(Color.rgb(5,8,13));

        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        mag=sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        rotationSensor=sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        v=new GhostView();
        setContentView(v);

        if(mag!=null) sm.registerListener(this,mag,SensorManager.SENSOR_DELAY_GAME);
        if(rotationSensor!=null) sm.registerListener(this,rotationSensor,SensorManager.SENSOR_DELAY_GAME);

        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){
            startAudio();
        }else{
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},7);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] results){
        super.onRequestPermissionsResult(requestCode,permissions,results);
        if(requestCode==7 && results.length>0 && results[0]==PackageManager.PERMISSION_GRANTED){
            startAudio();
        }
        if(v!=null) v.postInvalidate();
    }

    void startAudio(){
        if(audioOn || checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED) return;
        try{
            int sr=44100;
            int min=AudioRecord.getMinBufferSize(sr,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
            if(min<=0) min=4096;
            int buffer=Math.max(min*2,8192);

            rec=new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sr,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffer
            );

            if(rec.getState()!=AudioRecord.STATE_INITIALIZED){
                rec.release();
                rec=null;
                return;
            }

            rec.startRecording();
            if(rec.getRecordingState()!=AudioRecord.RECORDSTATE_RECORDING){
                rec.release();
                rec=null;
                return;
            }

            audioOn=true;
            audioThread=new Thread(()->{
                short[] samples=new short[2048];
                while(audioOn && rec!=null){
                    int n=rec.read(samples,0,samples.length,AudioRecord.READ_BLOCKING);
                    if(n>0){
                        double sum=0;
                        for(int i=0;i<n;i++){
                            double s=samples[i]/32768.0;
                            sum+=s*s;
                        }
                        double rms=Math.sqrt(sum/n);
                        double db=20.0*Math.log10(Math.max(rms,0.00001));
                        int mapped=(int)Math.round((db+60.0)*100.0/60.0);
                        level=Math.max(0,Math.min(100,mapped));
                    }else{
                        level=0;
                    }
                    if(v!=null) v.postInvalidate();
                }
            },"GhostHunterAudio");
            audioThread.start();
        }catch(Exception e){
            audioOn=false;
            level=0;
        }
    }

    void stopAudio(){
        audioOn=false;
        if(rec!=null){
            try{rec.stop();}catch(Exception ignored){}
            try{rec.release();}catch(Exception ignored){}
            rec=null;
        }
        audioThread=null;
    }

    @Override public void onSensorChanged(SensorEvent e){
        if(e.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rotationVectorMatrix,e.values);
            System.arraycopy(rotationVectorMatrix,0,rotationMatrix,0,9);
            float[] orientation=new float[3];
            SensorManager.getOrientation(rotationMatrix,orientation);
            headingDeg=(float)Math.toDegrees(orientation[0]);
            if(headingDeg<0) headingDeg+=360f;
            rotationReady=true;
            if(v!=null) v.postInvalidate();
            return;
        }

        if(e.sensor.getType()!=Sensor.TYPE_MAGNETIC_FIELD)return;

        x=e.values[0]; y=e.values[1]; z=e.values[2];
        total=(float)Math.sqrt(x*x+y*y+z*z);
        sensorAccuracy=e.accuracy;

        if(rotationReady){
            worldX=rotationMatrix[0]*x+rotationMatrix[1]*y+rotationMatrix[2]*z;
            worldY=rotationMatrix[3]*x+rotationMatrix[4]*y+rotationMatrix[5]*z;
            worldZ=rotationMatrix[6]*x+rotationMatrix[7]*y+rotationMatrix[8]*z;
        }else{
            worldX=x; worldY=y; worldZ=z;
        }

        if(!baselineReady){
            baselineSamples++;
            float n=baselineSamples;
            if(baselineSamples==1){
                baselineX=x; baselineY=y; baselineZ=z; baselineTotal=total;
                baselineWorldX=worldX; baselineWorldY=worldY; baselineWorldZ=worldZ;
            }else{
                baselineX+=(x-baselineX)/n;
                baselineY+=(y-baselineY)/n;
                baselineZ+=(z-baselineZ)/n;
                baselineTotal+=(total-baselineTotal)/n;
                baselineWorldX+=(worldX-baselineWorldX)/n;
                baselineWorldY+=(worldY-baselineWorldY)/n;
                baselineWorldZ+=(worldZ-baselineWorldZ)/n;
            }
            if(baselineSamples>=60) baselineReady=true;
        }

        if(last>0){
            double dt=(e.timestamp-last)/1e9;
            if(dt>0){
                rate=(float)(Math.abs(total-prev)/dt);
                float filtered=Math.min(rate,50f);
                smoothRate=smoothRate*0.90f+filtered*0.10f;
            }
        }
        prev=total;
        last=e.timestamp;
        if(v!=null) v.postInvalidate();
    }

    void recalibrate(){
        baselineReady=false;
        baselineSamples=0;
        radarTrail.clear();
        smoothRate=0f;
        rate=0f;
        if(v!=null) v.invalidate();
    }

    @Override public void onAccuracyChanged(Sensor s,int a){
        if(s!=null && s.getType()==Sensor.TYPE_MAGNETIC_FIELD) sensorAccuracy=a;
    }

    @Override protected void onDestroy(){
        super.onDestroy();
        if(sm!=null) sm.unregisterListener(this);
        stopAudio();
    }

    class GhostView extends View {
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        int tab=0;
        final float DESIGN_W=360f, DESIGN_H=760f;
        float scale=1f;

        int bg=Color.rgb(5,8,13), card=Color.rgb(10,16,25), white=Color.rgb(225,232,242),
            muted=Color.rgb(135,151,175), green=Color.rgb(0,230,160),
            purple=Color.rgb(145,85,255), cyan=Color.rgb(50,190,255);

        GhostView(){
            super(MainActivity.this);
            setBackgroundColor(bg);
            setFocusable(true);
        }

        float sx(){ return getWidth()/DESIGN_W; }
        float sy(){ return getHeight()/DESIGN_H; }

        void t(Canvas c,String s,float x,float y,float size,int col){
            p.setStyle(Paint.Style.FILL);
            p.setTextSize(size);
            p.setColor(col);
            c.drawText(s,x,y,p);
        }

        void box(Canvas c,float l,float top,float r,float bot){
            p.setStyle(Paint.Style.FILL);
            p.setColor(card);
            c.drawRoundRect(l,top,r,bot,18,18,p);
        }

        @Override protected void onDraw(Canvas c){
            super.onDraw(c);

            scale=Math.min(sx(),sy());
            float offsetX=(getWidth()-DESIGN_W*scale)/2f;
            float offsetY=(getHeight()-DESIGN_H*scale)/2f;

            c.save();
            c.translate(offsetX,offsetY);
            c.scale(scale,scale);

            float w=DESIGN_W, h=DESIGN_H;

            t(c,"GHOST HUNTER",18,30,21,white);
            t(c,"REAL SENSORS · REAL DATA",20,47,9,muted);
            t(c,"⚙",w-35,32,21,white);

            String[] a={"EMF","REM","TALKBOX","RADAR","CAMERA"};
            float tw=w/5f;
            for(int i=0;i<5;i++){
                p.setTextSize(12);
                float cx=i*tw+tw/2f;
                float lw=p.measureText(a[i]);
                int col=i==tab?green:muted;
                t(c,a[i],cx-lw/2f,76,12,col);
                if(i==tab){
                    p.setColor(green);
                    c.drawRect(i*tw,86,(i+1)*tw,88,p);
                }
            }

            if(tab==0) emf(c,w);
            else if(tab==1) rem(c,w);
            else if(tab==2) talk(c,w);
            else if(tab==3) radar(c,w);
            else camera(c,w);

            t(c,"⌂",w*.15f-8,h-28,23,tab==0?green:muted);
            t(c,"☷",w*.38f-8,h-28,23,muted);
            t(c,"□",w*.62f-8,h-28,23,muted);
            t(c,"⋮",w*.85f-8,h-28,23,muted);
            t(c,"Home",w*.10f,h-8,10,tab==0?green:muted);
            t(c,"History",w*.33f,h-8,10,muted);
            t(c,"Recordings",w*.55f,h-8,10,muted);
            t(c,"More",w*.82f,h-8,10,muted);

            c.restore();

            if(tab==3) postInvalidateDelayed(16);
        }

        void emf(Canvas c,float w){
            box(c,10,105,w-10,445);
            t(c,"✦  EMF",22,131,19,white);
            t(c,mag!=null?"● SENSOR ACTIVE":"● SENSOR UNAVAILABLE",w-150,131,9,mag!=null?green:Color.RED);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(13);
            p.setColor(Color.rgb(30,43,60));
            c.drawArc(30,150,210,330,140,260,false,p);
            p.setColor(green);
            c.drawArc(30,150,210,330,140,Math.min(250,total/2f),false,p);
            p.setStyle(Paint.Style.FILL);

            t(c,String.format(Locale.US,"%.2f",total),73,245,38,white);
            t(c,"µT",113,267,14,muted);
            t(c,"Magnetic Field",69,294,12,muted);

            t(c,String.format(Locale.US,"X  %.2f µT",x),238,180,12,white);
            t(c,String.format(Locale.US,"Y  %.2f µT",y),238,211,12,white);
            t(c,String.format(Locale.US,"Z  %.2f µT",z),238,242,12,white);
            t(c,"LIVE MAGNETIC FIELD",238,280,9,muted);
            t(c,"Physical sensor reading",238,299,10,white);

            box(c,10,463,w-10,518);
            t(c,"♢  Alert Threshold",22,497,13,muted);
            t(c,"2.0 µT  ›",w-86,497,11,white);
            t(c,"Physical magnetic-field data. An anomaly is not proof of paranormal activity.",15,542,8,muted);
        }

        void rem(Canvas c,float w){
            box(c,10,105,w-10,370);
            t(c,"◎  REM DETECTOR",22,131,19,white);
            t(c,"● MONITORING",w-112,131,9,green);
            t(c,String.format(Locale.US,"%.3f µT/s",rate),25,197,34,white);
            t(c,"MAGNETIC CHANGE RATE",27,220,9,muted);
            t(c,"Sensitivity",27,267,12,muted);
            t(c,"MEDIUM   ›",w-92,267,11,white);
            t(c,"Monitors rapid changes in the magnetic-field sensor.",17,330,9,muted);
            t(c,"Movement, electronics and the environment can cause changes.",17,347,9,muted);
        }

        void talk(Canvas c,float w){
            box(c,10,105,w-10,405);
            t(c,"♩  TALKBOX",22,131,19,white);
            boolean micGranted=checkSelfPermission(Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED;
            t(c,audioOn?"● MIC ACTIVE":(micGranted?"● MIC READY":"● MIC PERMISSION NEEDED"),w-145,131,9,audioOn?green:(micGranted?cyan:Color.RED));

            t(c,"AUDIO LEVEL",25,177,9,muted);
            t(c,String.format(Locale.US,"%d %%",level),25,220,36,white);

            p.setColor(cyan);
            float baseY=345;
            float barW=(w-50)/40f;
            for(int i=0;i<40;i++){
                float wave=(float)(0.25+0.75*Math.abs(Math.sin(i*0.72)));
                float bh=5+level*0.90f*wave;
                float left=25+i*barW;
                c.drawRoundRect(left,baseY-bh,left+Math.max(3,barW-2),baseY,2,2,p);
            }

            t(c,"LIVE MICROPHONE ANALYSIS",25,375,9,muted);
            t(c,"Speak or make a sound near the phone to test the level.",15,432,9,muted);
        }

        void radar(Canvas c,float w){
            box(c,10,105,w-10,500);
            t(c,"◎  RADAR 2.0",22,131,19,white);
            t(c,rotationReady?"ORIENTATION LOCKED":"ORIENTATION SENSOR N/A",w-145,131,8,rotationReady?green:Color.RED);

            float cx=w/2f, cy=292f;
            float maxR=Math.min(124f,w/2f-28f);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1.5f);
            p.setColor(Color.rgb(40,70,80));
            for(int i=1;i<=4;i++) c.drawCircle(cx,cy,maxR*i/4f,p);
            p.setColor(Color.rgb(25,55,65));
            c.drawLine(cx-maxR,cy,cx+maxR,cy,p);
            c.drawLine(cx,cy-maxR,cx,cy+maxR,p);

            // Cardinal labels: the radar is world/compass-relative, not phone-relative.
            t(c,"N",cx-4,cy-maxR-8,10,muted);
            t(c,"S",cx-4,cy+maxR+17,10,muted);
            t(c,"W",cx-maxR-17,cy+4,10,muted);
            t(c,"E",cx+maxR+8,cy+4,10,muted);

            double seconds=System.nanoTime()/1_000_000_000.0;
            float sweep=(float)((seconds*0.95)%(Math.PI*2));
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(2.0f);
            p.setColor(green);
            c.drawLine(cx,cy,cx+(float)Math.cos(sweep)*maxR,cy+(float)Math.sin(sweep)*maxR,p);

            float dx=baselineReady?(worldX-baselineWorldX):0f;
            float dy=baselineReady?(worldY-baselineWorldY):0f;
            float horizontalDelta=(float)Math.sqrt(dx*dx+dy*dy);

            // Low-pass the vector itself so the target moves gradually instead of jumping.
            radarTargetX=radarTargetX*0.82f+dx*0.18f;
            radarTargetY=radarTargetY*0.82f+dy*0.18f;

            float sensitivityScale=radarSensitivity==1?16f:(radarSensitivity==2?9f:5f);
            float targetRadius=Math.min(maxR,(float)Math.sqrt(radarTargetX*radarTargetX+radarTargetY*radarTargetY)*sensitivityScale);
            float dirLen=(float)Math.sqrt(radarTargetX*radarTargetX+radarTargetY*radarTargetY);
            float dirX=dirLen>0.20f?radarTargetX/dirLen:0f;
            float dirY=dirLen>0.20f?radarTargetY/dirLen:0f;

            // Android world X/Y are fixed to the environment, so rotating the phone
            // no longer rotates this dot with the handset.
            float targetX=cx+dirX*targetRadius;
            float targetY=cy-dirY*targetRadius;

            if(baselineReady && targetRadius>3f && horizontalDelta>0.35f){
                if(radarTrail.size()>24) radarTrail.removeFirst();
                radarTrail.addLast(new RadarPoint(targetX,targetY,1f));
            }
            for(RadarPoint rp:radarTrail){
                rp.alpha*=0.90f;
                p.setStyle(Paint.Style.FILL);
                p.setColor(Color.argb((int)(Math.max(0.08f,rp.alpha)*115),0,230,160));
                c.drawCircle(rp.x,rp.y,3.2f,p);
            }
            radarTrail.removeIf(rp->rp.alpha<0.10f);

            p.setStyle(Paint.Style.FILL);
            p.setColor(green);
            c.drawCircle(targetX,targetY,baselineReady?6f:4f,p);

            String acc;
            switch(sensorAccuracy){
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH: acc="HIGH"; break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM: acc="MEDIUM"; break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW: acc="LOW"; break;
                default: acc="UNRELIABLE";
            }

            t(c,baselineReady?"BASELINE LOCKED":"CALIBRATING 60 SAMPLES",22,430,9,baselineReady?green:muted);
            t(c,String.format(Locale.US,"FIELD  %.2f µT",total),22,450,10,white);
            t(c,String.format(Locale.US,"Δ FIELD  %.2f µT",horizontalDelta),22,468,10,white);
            t(c,String.format(Locale.US,"RATE  %.3f µT/s",smoothRate),22,486,10,muted);

            t(c,String.format(Locale.US,"HEADING  %03d°",(int)headingDeg),w-105,430,9,white);
            t(c,"SENSOR  "+acc,w-105,448,8,muted);
            t(c,"SENSITIVITY",w-105,466,8,muted);
            String sens=radarSensitivity==1?"LOW":(radarSensitivity==2?"MEDIUM":"HIGH");
            t(c,sens,w-105,484,9,white);
            t(c,"TAP CALIBRATE / SENSITIVITY",w-165,506,7,muted);
        }

        void camera(Canvas c,float w){
            box(c,10,105,w-10,500);
            t(c,"▣  CAMERA",22,131,19,white);
            t(c,"CAMERA OVERLAY",22,166,10,muted);
            t(c,String.format(Locale.US,"EMF   %.2f µT",x*0+total),22,205,16,green);
            t(c,String.format(Locale.US,"REM   %.3f µT/s",rate),22,235,16,purple);
            t(c,"Live camera preview will be connected next.",22,280,10,muted);
        }

        @Override public boolean onTouchEvent(MotionEvent e){
            if(e.getAction()==MotionEvent.ACTION_UP){
                float offsetX=(getWidth()-DESIGN_W*scale)/2f;
                float offsetY=(getHeight()-DESIGN_H*scale)/2f;
                float xPos=(e.getX()-offsetX)/scale;
                float yPos=(e.getY()-offsetY)/scale;

                if(yPos<100){
                    int n=(int)(xPos/(DESIGN_W/5f));
                    if(n>=0 && n<5){
                        tab=n;
                        invalidate();
                    }
                }else if(tab==3 && yPos>=410 && yPos<=515){
                    if(yPos>=410 && yPos<=440 && xPos<180){
                        recalibrate();
                    }else if(xPos>=220){
                        radarSensitivity++;
                        if(radarSensitivity>3) radarSensitivity=1;
                        invalidate();
                    }else if(xPos<180){
                        recalibrate();
                    }
                }
            }
            return true;
        }
    }
}
