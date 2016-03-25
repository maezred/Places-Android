package net.moltendorf.places.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import net.moltendorf.places.Place;
import net.moltendorf.places.PlacesQueryHelper;
import net.moltendorf.places.R;
import net.moltendorf.places.view.PlaceViewHolder;
import net.moltendorf.places.view.PlacesListAdapter;
import net.moltendorf.places.view.PlacesListView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Search screen; displays results from a query entered anywhere in the application.
 */
public class SearchActivity extends BaseActivity {
	private static final String TAG = "SearchActivity";

	public static final String ACTION_TAG_ID_SEARCH   = "net.moltendorf.places.ACTION_TAG_ID_SEARCH";
	public static final String ACTION_FAVORITE_SEARCH = "net.moltendorf.places.ACTION_FAVORITE_SEARCH";

	public static final String EXTRA_TAG_ID = "tagId";

	private static final int EXTRA_TAG_ID_DEFAULT = -1;

	Map<Integer, Place> placeNames = new LinkedHashMap<>();

	private PlacesListAdapter placesAdapter;
	private PlacesListView    placesListView;

	private PlacesQueryHelper queryHelper;

	@Override
	protected void onCreateContentView() {
		setContentView(R.layout.activity_search);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		queryHelper = PlacesQueryHelper.getInstance(this);

		createViewReferences();
		createPlacesAdapter();

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void createViewReferences() {
		placesListView = (PlacesListView) findViewById(R.id.search_places_list);
		placesListView.setLayoutManager(new LinearLayoutManager(this));
	}

	private void createPlacesAdapter() {
		Map<Class<?>, Class<? extends PlacesListAdapter.ViewHolder>> relations;
		relations = new HashMap<Class<?>, Class<? extends PlacesListAdapter.ViewHolder>>(1) {{
			put(Place.class, PlaceViewHolder.class);
		}};

		placesAdapter = new PlacesListAdapter(this, relations, placeNames);
		placesAdapter.addEventListener(new PlaceViewHolder.OnOpenDetailsListener() {
			@Override
			public void onOpenDetails(Place place) {
				Intent intent = new Intent(DetailActivity.ACTION_SHOW_PLACE_BY_ID);
				intent.putExtra(DetailActivity.EXTRA_PLACE_ID, place.getId());

				startActivity(intent);
			}
		});

		placesListView.setAdapter(placesAdapter);
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();

		action:
		{
			if (action != null) {
				switch (action) {
					case Intent.ACTION_SEARCH:
						String query = intent.getStringExtra(SearchManager.QUERY);
						Log.i(TAG, "handleIntent: Performing search for \"" + query + "\".");

						swapAdapterDataSet(queryHelper.searchPlaces(query));
						break action;

					case ACTION_TAG_ID_SEARCH:
						int tagId = intent.getIntExtra(EXTRA_TAG_ID, EXTRA_TAG_ID_DEFAULT);
						Log.i(TAG, "handleIntent: Looking up places with tag_id \"" + tagId + "\".");

						swapAdapterDataSet(queryHelper.getPlacesByTagId(tagId));
						break action;

					case ACTION_FAVORITE_SEARCH:
						Log.i(TAG, "handleIntent: Looking up places that are favorited.");

						swapAdapterDataSet(queryHelper.getPlacesByFavorite());
						break action;
				}
			}

			Log.i(TAG, "handleIntent: Getting all places.");
			swapAdapterDataSet(queryHelper.getPlaces());
		}

		Log.i(TAG, "handleIntent: Found " + placeNames.size() + " results.");
	}

	private void swapAdapterDataSet(Map<Integer, Place> newPlaceNames) {
		placeNames.clear();
		placeNames.putAll(newPlaceNames);

		placesAdapter.notifyDataSetChanged();
	}
}
