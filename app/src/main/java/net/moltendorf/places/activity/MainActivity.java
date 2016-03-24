package net.moltendorf.places.activity;

import android.os.Bundle;
import android.util.Log;

import net.moltendorf.places.R;

/**
 * Main screen; displays information about the overall location.
 */
public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreateContentView() {
		setContentView(R.layout.activity_search);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");
	}
}
