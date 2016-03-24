package net.moltendorf.places.activity;

import android.os.Bundle;
import android.util.Log;

import net.moltendorf.places.R;

public class DetailActivity extends BaseActivity {
	private static final String TAG = "DetailActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		setContentView(R.layout.activity_detail);
	}
}
