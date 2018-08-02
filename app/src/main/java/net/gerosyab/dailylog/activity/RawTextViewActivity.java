package net.gerosyab.dailylog.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.StaticData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RawTextViewActivity extends AppCompatActivity {
    long rawTextType;
    public static final long INFORMATION = 0;
    public static final long LICENSE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_text_view);
        TextView textView = (TextView) findViewById(R.id.text);

        Intent intent = getIntent();
        rawTextType = intent.getLongExtra(StaticData.RAW_TEXT_INTENT_EXTRA, INFORMATION);

        ActionBar ab = getSupportActionBar();

        if(rawTextType == INFORMATION) {
            ab.setTitle("Daily Log - Information");
            textView.setTextSize(18);
        }
        else if(rawTextType == LICENSE) {
            ab.setTitle("Daily Log - License");
            textView.setTextSize(12);
        }

        textView.setText(readTxt());
    }

    private String readTxt() {
        String data = null;
        InputStream inputStream = null;
        if(rawTextType == INFORMATION) inputStream = getResources().openRawResource(R.raw.infomation);
        else if(rawTextType == LICENSE) inputStream = getResources().openRawResource(R.raw.license);

        if(inputStream != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int i;
            try {
                i = inputStream.read();
                while (i != -1) {
                    byteArrayOutputStream.write(i);
                    i = inputStream.read();
                }

                data = new String(byteArrayOutputStream.toByteArray(),"MS949");
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
        else {
            return "";
        }

    }
}
