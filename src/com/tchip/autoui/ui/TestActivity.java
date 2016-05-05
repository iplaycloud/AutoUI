package com.tchip.autoui.ui;

import com.tchip.autoui.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		Button btnTest = (Button) findViewById(R.id.btnTest);
		btnTest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(TestActivity.this, "Ripple it!",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		ImageButton btnTest2 = (ImageButton) findViewById(R.id.btnTest2);
		btnTest2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(TestActivity.this, "Ripple it!",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		ImageButton btnTest3 = (ImageButton) findViewById(R.id.btnTest3);
		btnTest3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(TestActivity.this, "Ripple it!",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

}
