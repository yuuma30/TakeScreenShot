package com.reon.yuuma.takescreenshot;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.nio.ByteBuffer;

/**
 * Created by y on 2018/01/02.
 */

public class TakeService extends Service implements View.OnClickListener {
    private String TAG = "TakeService";
    // ウィンドウマネージャーのデータを格納
    WindowManager mWindowManager;
    private int mDpScale;
    private View mView;
    private View mTakeDetailView;
    private ImageReader mImageReader; // スクリーンショット用
    private int mWidth;
    private int mHeight;
    private VirtualDisplay mVirtualDisplay;
    private Image mImage;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        // dipを取得
        mDpScale = (int)getResources().getDisplayMetrics().density;
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        // 右上に配置
        setTakeButton(Gravity.TOP | Gravity.RIGHT);
        mWidth = MainActivity.mWidth;
        mHeight = MainActivity.mHeight;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public void onClick(View v){
        Log.d(TAG, "onClick");
        switch(v.getId()){
            case R.id.detailButton:
                Log.d(TAG, "detailButton()");
                onClickDetailButton();
                break;
            case R.id.closeButton:
                Log.d(TAG, "closeButton()");
                onClickCloseButton();
                break;
            case R.id.takeButton:
                onClickTakeButton();
                break;
            default:
        }
    }

    private void setTakeButton(int gravity) {
        // inflaterの作成
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        mWindowManager = (WindowManager)getApplicationContext().getSystemService((Context.WINDOW_SERVICE));
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SECURE,
                PixelFormat.TRANSLUCENT
        );

        //配置
        params.gravity = gravity;

        // レイアウトファイルからInflateするViewを作成
        mView = layoutInflater.inflate(R.layout.service_takebutton, null);

        Button detailButton = (Button)mView.findViewById(R.id.detailButton);
        // ButtonにClickListenerを設定
        detailButton.setOnClickListener(this);
        // Viewを画面上に追加
        mWindowManager.addView(mView, params);
    }
    /**
     * 詳細ボタン押下
     */
    private void onClickDetailButton(){
        Log.d(TAG, "onClickDetailButton()");
        // レイアウトファイルからInflateするViewを作成
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        mTakeDetailView = layoutInflater.inflate(R.layout.service_takedetail, null);

        mWindowManager = (WindowManager)getApplicationContext().getSystemService((Context.WINDOW_SERVICE));
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SECURE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;

        // Viewを画面上に追加
        mWindowManager.addView(mTakeDetailView, params);
        Button button = (Button)mTakeDetailView.findViewById(R.id.closeButton);
        button.setOnClickListener(this);
        button = (Button)mTakeDetailView.findViewById(R.id.takeButton);
        button.setOnClickListener(this);
    }

    /**
     * 閉じるボタン押下
     */
    private void onClickCloseButton(){
        Log.d(TAG,"onClickCloseButton()");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        mWindowManager.removeView(mTakeDetailView);

    }

    /**
     * 写真を撮るボタン
     */
    private void onClickTakeButton() {
        Log.d(TAG,"onClickTakeButton()");
        try {
            Bitmap screenshot = getScreenShot();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private Bitmap getScreenShot(){
        if(MainActivity.mImageReader == null){
            Log.d(TAG,"mImageReader:null");
        }
        // ImagerReaderから画面を取り出す
        Image image = MainActivity.mImageReader.acquireLatestImage();

        if(image == null) {
            Log.d(TAG, "image:null");
            image = mImage;
        }
        mImage = image;
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();

        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * mWidth;

        // バッファからBitmapを作成
        Bitmap bitmap =Bitmap.createBitmap(
                mWidth + rowPadding / pixelStride, mHeight,
                Bitmap.Config.RGB_565);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();
        return bitmap;
    }

    @Override
    public void onDestroy(){
        if(mVirtualDisplay!=null){
            Log.d(TAG, "release VirtualDisplay");
            mVirtualDisplay.release();
        }
        super.onDestroy();
    }
}
