package com.jovial.jrpn;

import com.jovial.jrpn.R;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

//The "vanity plate"
public class fmAbout extends Activity {
    // I like to manually control the release date value

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText("Version:  " + BuildConfig.VERSION_NAME);
        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText("Built:  " + (new SimpleDateFormat()).format(
            new Date(BuildConfig.BUILD_TIMESTAMP)));
    }

    // let's blow this pop stand!
    public void Close(View v) {
		finish();
	}
}
