package net.moltendorf.places.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.moltendorf.places.BaseActivity;
import net.moltendorf.places.model.QueryHelper;
import net.moltendorf.places.model.Place;
import net.moltendorf.places.R;

/**
 * Main Screen
 * <p/>
 * Provides an overview of possible searches.
 */
public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";

	private Button
		allPlacesButton,
		myFavoritesButton,
		dineInButton,
		confectionsButton,
		allTagsButton;

	private QueryHelper queryHelper;

	@Override
	protected void onCreateContentView() {
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		queryHelper = QueryHelper.getInstance(this);

		createViewReferences();
		createViewListeners();
	}

	private void createViewReferences() {
		allPlacesButton = (Button) findViewById(R.id.main_all_places);
		myFavoritesButton = (Button) findViewById(R.id.main_my_favorites);
		dineInButton = (Button) findViewById(R.id.main_dine_in);
		confectionsButton = (Button) findViewById(R.id.main_confections);
		allTagsButton = (Button) findViewById(R.id.main_all_tags);
	}

	private void createViewListeners() {
		searchButtonListener(allPlacesButton, null, null, null);
		searchButtonListener(myFavoritesButton, SearchActivity.ACTION_FAVORITE_SEARCH, null, null);
		searchButtonListener(dineInButton, SearchActivity.ACTION_TAG_ID_SEARCH, SearchActivity.EXTRA_TAG_ID, queryHelper.getTagByName("dining"));
		searchButtonListener(confectionsButton, SearchActivity.ACTION_TAG_ID_SEARCH, SearchActivity.EXTRA_TAG_ID, queryHelper.getTagByName("confection"));

		allTagsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent tagsIntent = new Intent(MainActivity.this, TagsActivity.class);
				startActivity(tagsIntent);
			}
		});
	}

	private void searchButtonListener(Button button, final String action, final String extra, final Place.Tag tag) {
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);

				if (action != null) {
					searchIntent.setAction(action);
				}

				if (extra != null && tag != null) {
					searchIntent.putExtra(extra, tag.getId());
				}

				startActivity(searchIntent);
			}
		});
	}
}
