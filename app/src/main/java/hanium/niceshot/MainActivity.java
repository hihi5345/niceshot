package hanium.niceshot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextureView mCameraTextureView;
    private Preview mPreview;
    private ImageButton pictureBtn, timerBtn, biasBtn, modeBtn, flashBtn;
    private Button galleryBtn, guideBtn;
    private TextView mTextview;
    final Animation in = new AlphaAnimation(0.0f, 1.0f);
    final Animation out = new AlphaAnimation(1.0f, 0.0f);

    Activity mainActivity = this;
    static final int REQUEST_CAMERA = 1;
    private Context c;
    private boolean flash;
    private int bias;
    private int mode;
    private int time;
    private void init(){
        flash = false;
        bias = 0;
        mode = 0;
        time = 0;

        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
        mPreview = new Preview(this, mCameraTextureView, mode);
        

        mTextview = findViewById(R.id.timerText);
        mTextview.bringToFront();

        in.setDuration(500);
        out.setDuration(500);

        c = this;
        pictureBtn = findViewById(R.id.pictureBtn);
        pictureBtn.bringToFront();
        pictureBtn.setOnClickListener(this);

        timerBtn = findViewById(R.id.timerBtn);
        timerBtn.bringToFront();
        timerBtn.setOnClickListener(this);

        flashBtn = findViewById(R.id.flashBtn);
        flashBtn.bringToFront();
        flashBtn.setOnClickListener(this);

        biasBtn = findViewById(R.id.biasBtn);
        biasBtn.bringToFront();
        biasBtn.setOnClickListener(this);

        modeBtn = findViewById(R.id.modeBtn);
        modeBtn.bringToFront();
        modeBtn.setOnClickListener(this);

        galleryBtn = findViewById(R.id.galleryBtn);
        galleryBtn.bringToFront();
        galleryBtn.setOnClickListener(this);

        guideBtn = findViewById(R.id.guideBtn);
        guideBtn.bringToFront();
        guideBtn.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
                            mPreview = new Preview(mainActivity, mCameraTextureView, mode);
                            Log.d("TAG","mPreview set");
                        } else {
                            Toast.makeText(this,"Should have camera permission to run", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.onResume();
        mPreview = new Preview(this, mCameraTextureView, mode);
        mPreview.onResume();
        mPreview.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.pictureBtn){
            int t = time * 1000;
            final int[] check = {time};
            Timer timer = new Timer();
            TimerTask tt = new TimerTask()
            {
                @Override
                public void run()
                {
                    mPreview.takePicture(c);
                }
            };
            timer.schedule(tt, t);
            new CountDownTimer(t, 1000) {

                public void onTick(long millisUntilFinished) {
                    mTextview.startAnimation(out);
                    mTextview.setText(String.valueOf(check[0]));
                    mTextview.startAnimation(in);
                    check[0]--;
                }

                public void onFinish() {
                    mTextview.setText("");
                }
            }.start();
        } else if(view.getId() == R.id.timerBtn){
            if(time == 0){
                time = 3;
                timerBtn.setImageResource(R.drawable.timer3);
            } else if(time == 3){
                time = 5;
                timerBtn.setImageResource(R.drawable.timer5);
            } else if(time == 5){
                time = 10;
                timerBtn.setImageResource(R.drawable.timer10);
            } else if(time == 10){
                time = 0;
                timerBtn.setImageResource(R.drawable.timer0);
            }
        } else if(view.getId() == R.id.flashBtn){
            if(!flash){
                try {
                    //flashBtn.setText("ON");
                    mPreview.turnOnFlashLight();
                } catch (@SuppressLint("NewApi") CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    //flashBtn.setText("OFF");
                    mPreview.turnOffFlashLight();
                } catch (@SuppressLint("NewApi") CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            flash = !flash;
            Log.v("TAG", String.valueOf(flash));

        } else if(view.getId() == R.id.biasBtn){

        } else if(view.getId() == R.id.modeBtn){
            if(mode == 0) mode = 1;
            else mode = 0;
            mPreview.onPause();
            mPreview = new Preview(this, mCameraTextureView, mode);
            mPreview.openCamera();
        } else if(view.getId() == R.id.galleryBtn){
            Uri uri = Uri.parse("content://media/internal/images/media");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);


        } else if(view.getId() == R.id.guideBtn){

        }
    }
}