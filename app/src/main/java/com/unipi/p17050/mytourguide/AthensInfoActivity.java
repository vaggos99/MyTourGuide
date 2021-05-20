package com.unipi.p17050.mytourguide;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AthensInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athens_info);
        getSupportActionBar().setTitle(getString(R.string.about_athens));
        String htmlText = "<h2>"+getString(R.string.info_about_athens_header)+"</h2><br>\n" + "<p>"+getString(R.string.athens_info) +"</p><br>" + "<p>"+getString(R.string.athens_info1) +"</p><br>"
                +"<p>"+getString(R.string.athens_info2)+"</p>";
        TextView textView= findViewById(R.id.athens_infoTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));
    }
}