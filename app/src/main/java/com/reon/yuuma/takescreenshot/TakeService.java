package com.reon.yuuma.takescreenshot;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by y on 2018/01/02.
 */

public class TakeService extends Service implements View.OnClickListener {
    private String TAG = "TakeService";
    // ウィンドウマネージャーのデータを格納
    WindowManager mWindowManager;
    private int mDpScale;
    private View mView;


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
                onClickloseButton();
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
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
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
        mView = layoutInflater.inflate(R.layout.service_takedetail, null);

        mWindowManager = (WindowManager)getApplicationContext().getSystemService((Context.WINDOW_SERVICE));
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;

        // Viewを画面上に追加
        mWindowManager.addView(mView, params);
        Button closeButton = (Button)mView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);
    }

    /**
     * 閉じるボタン押下
     */
    private void onClickloseButton(){

    }
}
