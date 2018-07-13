package hanium.niceshot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextureView mCameraTextureView;
    private Preview mPreview;
    private Button pictureBtn;
    Activity mainActivity = this;
    static final int REQUEST_CAMERA = 1;
    private Context c;
    private void init(){

        mCameraTextureView = (TextureView) findViewById(R.id.cameraTextureView);
        mPreview = new Preview(this, mCameraTextureView);
        c = this;
        pictureBtn = findViewById(R.id.pictureBtn);
        pictureBtn.bringToFront();
        pictureBtn.setOnClickListener(this);

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
                            mPreview = new Preview(mainActivity, mCameraTextureView);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.pictureBtn){
            mPreview.takePicture(c);
        }
    }
}