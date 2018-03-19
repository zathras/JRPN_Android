package com.emmetgray.wrpn;

import com.emmetgray.wrpn.R;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

//The "vanity plate"
public class fmAbout extends Activity {
	// I like to manually control the release date value
	String RELEASE_DATE = "11 Jan 2016";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		TextView tvDate = (TextView) findViewById(R.id.tvDate);

		tvDate.setText(tvDate.getText() + RELEASE_DATE);

		TextView tvEmail = (TextView) findViewById(R.id.tvEmail);
		tvEmail.setText(fmMain.prop.getProperty("Email"));

		TextView tvHomeURL = (TextView) findViewById(R.id.tvHomeURL);
		tvHomeURL.setText(fmMain.prop.getProperty("HomeURL"));

		TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
		// v6.0.1 - 29 Mar 12
		try {
			tvVersion
					.setText(tvVersion.getText()
							+ getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	// let's blow this pop stand!
	public void Close(View v) {
		finish();
	}
}
