package net.moltendorf.places.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import net.moltendorf.places.R;

public class DetailActivity extends BaseActivity {
	private static final String TAG = "DetailActivity";

	public static final String EXTRA_PLACE_ID = "placeId";

	private static final int EXTRA_PLACE_ID_DEFAULT = 0;

	private int placeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		setContentView(R.layout.activity_detail);

		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		if (intent != null) {
			placeId = intent.getIntExtra(EXTRA_PLACE_ID, EXTRA_PLACE_ID_DEFAULT);
		}
	}
}
