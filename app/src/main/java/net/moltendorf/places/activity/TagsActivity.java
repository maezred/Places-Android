package net.moltendorf.places.activity;

import android.os.Bundle;
import android.util.Log;

import net.moltendorf.places.R;

public class TagsActivity extends BaseActivity {
	private static final String TAG = "TagsActivity";

	@Override
	protected void onCreateContentView() {
		setContentView(R.layout.activity_tags);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");
	}
}
