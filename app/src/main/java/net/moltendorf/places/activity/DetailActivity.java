package net.moltendorf.places.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.moltendorf.places.Place;
import net.moltendorf.places.PlacesQueryHelper;
import net.moltendorf.places.R;

import java.util.Map;

public class DetailActivity extends BaseActivity {
	private static final String TAG = "DetailActivity";

	public static final String EXTRA_PLACE_ID = "placeId";

	private static final int EXTRA_PLACE_ID_DEFAULT = -1;

	private Place place;

	private TextView placeName, placePhone;
	private CheckBox     placeFavorite;
	private LinearLayout placeTags;

	private PlacesQueryHelper queryHelper;

	@Override
	protected void onCreateContentView() {
		setContentView(R.layout.activity_detail);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		queryHelper = PlacesQueryHelper.getInstance(this);

		createViewReferences();

		handleIntent(getIntent());

		populateViews();
	}

	private void createViewReferences() {
		placeName = (TextView) findViewById(R.id.detail_place_name);
		placePhone = (TextView) findViewById(R.id.detail_place_phone);
		placeFavorite = (CheckBox) findViewById(R.id.detail_place_favorite);
		placeTags = (LinearLayout) findViewById(R.id.detail_place_tags);
	}

	private void handleIntent(Intent intent) {
		int placeId = intent.getIntExtra(EXTRA_PLACE_ID, EXTRA_PLACE_ID_DEFAULT);

		if (placeId == EXTRA_PLACE_ID_DEFAULT) {
			throw new IllegalStateException("Intent must contain a valid " + EXTRA_PLACE_ID);
		}

		place = queryHelper.getPlaceById(placeId);
	}

	private void populateViews() {
		placeName.setText(place.getName());
		placePhone.setText(place.getPhone());
		placeFavorite.setChecked(place.isFavorite());

		placeTags.removeAllViewsInLayout();

		for (final Map.Entry<Integer, String> entry : place.getTags().entrySet()) {
			TextView tag = (TextView) LayoutInflater.from(this).inflate(R.layout.tag, placeTags, false);

			tag.setText(entry.getValue());
			tag.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(DetailActivity.this, SearchActivity.class);
					intent.setAction(SearchActivity.ACTION_TAG_ID_SEARCH);
					intent.putExtra(SearchActivity.EXTRA_TAG_ID, entry.getKey());

					startActivity(intent);
				}
			});

			placeTags.addView(tag);
		}
	}
}
