package com.canakkoca.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.canakkoca.andzu.base.AndzuApp;
import com.canakkoca.andzu.utils.Logger;
import com.canakkoca.andzu.utils.LoggingInterceptor;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 2084);
        } else {
            AndzuApp.getAndzuApp().initAndzu();
        }

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("message", "Your message")
                .build();

        final Request request = new Request.Builder()
                .url("https://gist.githubusercontent.com/RIAEvangelist/63a5618119170384386e/" +
                        "raw/600df29651d9e4fbffc3af98580a36bcc86e71c3/dominos-sample-request.json")
                .build();


        new AsyncTask<Void,Integer,Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    Response response = client.newCall(request).execute();
                    Log.d("","");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();

        Logger.inf("info");

        Logger.err("error",Logger.HI_PRI);

        Logger.d("debug");

        Logger.v("verbose",Logger.LOW_PRI);

        Logger.w("warn");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2084) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                AndzuApp.getAndzuApp().initAndzu();
            } else { //Permission is not available
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
