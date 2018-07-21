package hanium.niceshot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ConstraintLayout constraintLayout;
    private TextureView mCameraTextureView;
    private Preview mPreview;
    private ImageButton pictureBtn, timerBtn, biasBtn, modeBtn, flashBtn, galleryBtn, guideBtn;
    private TextView mTextview;
    final Animation in = new AlphaAnimation(0.0f, 1.0f);
    final Animation out = new AlphaAnimation(1.0f, 0.0f);
    static final int REQUEST_CAMERA = 1;
    private Context c;
    private boolean flash;
    private int bias;
    private int mode;
    private int time;
    private int width;
    private int heigth;
    private boolean guide;
    private File recentFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        permissionRequest();
        init();
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
                    recentFile = mPreview.takePicture(c, heigth, width, flash);
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
                timerBtn.setImageResource(R.drawable.timer3new);
            } else if(time == 3){
                time = 5;
                timerBtn.setImageResource(R.drawable.timer5new);
            } else if(time == 5){
                time = 10;
                timerBtn.setImageResource(R.drawable.timer10new);
            } else if(time == 10){
                time = 0;
                timerBtn.setImageResource(R.drawable.timer0);
            }

        } else if(view.getId() == R.id.flashBtn){
            if(!flash){
                flashBtn.setImageResource(R.drawable.flash);
            } else {
                flashBtn.setImageResource(R.drawable.noflash);
            }
            flash = !flash;

        } else if(view.getId() == R.id.biasBtn){
            if(bias == 1){
                bias = 3;
                biasBtn.setImageResource(R.drawable.bias3_4new);
                width = 1280;
                changeBias("3:4");
            } else if (bias == 3){
                bias = 9;
                biasBtn.setImageResource(R.drawable.bias9_16new);
                width = 1960;
                changeBias("9:16");

            } else if (bias == 9){
                bias = 1;
                biasBtn.setImageResource(R.drawable.bias1_1);
                width = 960;
                changeBias("1:1");

            }

        } else if(view.getId() == R.id.modeBtn){
            if(mode == 0) mode = 1;
            else mode = 0;
            mPreview.onPause();
            mPreview = new Preview(this, mCameraTextureView, mode);
            mPreview.openCamera();

        } else if(view.getId() == R.id.galleryBtn){
            if(recentFile != null)  refreshGallery(this, recentFile);
            Uri uri = Uri.parse("content://media/internal/images/media");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        } else if(view.getId() == R.id.guideBtn){
            if(guide){
                guideBtn.setImageResource(R.drawable.guideoff);
            } else {
                guideBtn.setImageResource(R.drawable.guideon);
            }
            guide = !guide;
        }
    }

    private void changeBias(String bias){
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        lp.dimensionRatio = bias;
        lp.topToTop = constraintLayout.getId();
        lp.bottomToBottom = constraintLayout.getId();
        lp.verticalBias = 0.5f;
        constraintLayout.getViewById(R.id.cameraTextureView).setLayoutParams(lp);
    }

    private void refreshGallery(Context c, File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        c.sendBroadcast(mediaScanIntent);
    }

    private void init(){
        flash = false;
        bias = 1;
        mode = 0;
        time = 0;
        guide = false;
        heigth = 1280;
        width = 960;

        mCameraTextureView = findViewById(R.id.cameraTextureView);
        mPreview = new Preview(this, mCameraTextureView, mode);
        constraintLayout = findViewById(R.id.macl);

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
    private void permissionRequest(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(c, "권한 허가", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(c, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                //.setRationaleMessage("카메라 및 저장소 권한이 필요합니다.")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.CAMERA)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}