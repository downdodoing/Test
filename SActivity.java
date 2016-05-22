package com.example.changepage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SActivity extends Activity {

	private Button bnt1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second_activity);

		bnt1 = (Button) findViewById(R.id.button1);
		bnt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SActivity.this, FActivity.class);
				startActivity(intent);
			}
		});
	}
}
