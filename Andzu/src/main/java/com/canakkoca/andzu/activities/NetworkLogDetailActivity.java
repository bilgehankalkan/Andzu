package com.canakkoca.andzu.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canakkoca.andzu.R;
import com.canakkoca.andzu.base.BaseActivity;
import com.canakkoca.andzu.base.NetworkLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by can.akkoca on 4/13/2017.
 */
public class NetworkLogDetailActivity extends BaseActivity {

    TextView date, url, code, latency, headers, postData, response;
    Button shareInfo, copyResponse;
    ImageView status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networklogdetail);

        date = findViewById(R.id.date);
        url = findViewById(R.id.url);
        code = findViewById(R.id.code);
        latency = findViewById(R.id.latency);
        headers = findViewById(R.id.headers);
        postData = findViewById(R.id.postData);
        response = findViewById(R.id.response);
        shareInfo = findViewById(R.id.shareInfo);
        copyResponse = findViewById(R.id.button_copy_response);
        status = findViewById(R.id.code_img);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final NetworkLog networkLog = (NetworkLog) getIntent().getSerializableExtra("networkLog");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        date.setText(dateFormat.format(new Date(networkLog.getDate())));
        url.setText("[" + networkLog.getRequestType() + "] " + networkLog.getUrl());
        code.setText(networkLog.getResponseCode());
        latency.setText(String.format("%sms", networkLog.getDuration().intValue()));
        headers.setText(networkLog.getHeaders());
        postData.setText(networkLog.getPostData());

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(networkLog.getResponseData());
            response.setText(gson.toJson(je));
        } catch (Exception e) {
            response.setText(networkLog.getResponseData());
        }
        response.setTextSize(15);
        postData.setText(networkLog.getPostData());

        if (networkLog.getResponseCode().startsWith("2")) {
            status.setBackgroundColor(Color.GREEN);
            code.setTextColor(Color.GREEN);
        } else if (networkLog.getResponseCode().startsWith("4")) {
            status.setBackgroundColor(Color.parseColor("#ffa500"));
            code.setTextColor(Color.parseColor("#ffa500"));
        } else if (networkLog.getResponseCode().startsWith("5")) {
            status.setBackgroundColor(Color.RED);
            code.setTextColor(Color.RED);
        }

        shareInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, networkLog.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share with"));
            }
        });
        copyResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", response.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Response copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
