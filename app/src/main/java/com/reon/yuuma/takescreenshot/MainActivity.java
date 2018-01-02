package com.reon.yuuma.takescreenshot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    public static int OVERLAY_PERMISSION_REQ_CODE = 1000;
    public static int SS_PERMISSION_REQ_CODE = 999;
    public static int mWidth;
    public static int mHeight;
    private VirtualDisplay mVirtualDisplay;
    private Image mImage;
    public static ImageReader mImageReader; // スクリーンショット用
    private String TAG = "TakeService";

    private MediaProjectionManager mMediaProjectionManager;
    public static MediaProjection mMediaProjection;
    private Button mButtonStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //権限チェック
        if (Build.VERSION.SDK_INT >= 23) {
            // API23以上
            checkPermission();
        }
        if (Build.VERSION.SDK_INT >= 21){
            checkCameraPermission();
        }


        mButtonStart = (Button)findViewById(R.id.startbutton);
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplication(), TakeService.class);
                //常駐サービス開始
                startService(intent);
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void checkCameraPermission(){
        // カメラのパーミッション取得
        mMediaProjectionManager = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent, SS_PERMISSION_REQ_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityForResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // 権限許可されていない
            }
        } else if(requestCode == SS_PERMISSION_REQ_CODE) {
            if (resultCode != RESULT_OK) {
                //パーミッション無し
                return;
            }
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    DisplayMetrics mertics = getResources().getDisplayMetrics();
                    mWidth = mertics.widthPixels;
                    mHeight = mertics.heightPixels;
                    int density = mertics.densityDpi;

                    Log.d(TAG, "setup VirtualDisplay");
                    if(MainActivity.mMediaProjection == null){
                        Log.d(TAG,"mMediaProjection:null");
                    }
                    mImageReader = ImageReader.newInstance(mWidth, mHeight, ImageFormat.RGB_565, 2);
                    mVirtualDisplay = MainActivity.mMediaProjection.createVirtualDisplay("Capturing Display",
                            mWidth, mHeight, density,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                            mImageReader.getSurface(), null, null);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

    }


}
