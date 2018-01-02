package com.reon.yuuma.takescreenshot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    public static int OVERLAY_PERMISSION_REQ_CODE = 1000;

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
        if (!Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityForResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // 権限許可されていない
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

    }


}
