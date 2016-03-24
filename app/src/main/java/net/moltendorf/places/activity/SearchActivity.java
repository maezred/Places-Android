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

	Map<Integer, Place> placeNames = new LinkedHashMap<>();

	private PlacesListAdapter placesAdapter;
	private PlacesListView    placesListView;

	private PlacesQueryHelper queryHelper = PlacesQueryHelper.getInstance(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		setContentView(R.layout.activity_search);

		createViewReferences();
		createPlacesAdapter();

		Intent intent = getIntent();

		handleIntent(intent);

		if (!intent.getAction().equals(Intent.ACTION_SEARCH)) {
			populatePlacesListView();
		}
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
				Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
				intent.putExtra(DetailActivity.EXTRA_PLACE_ID, place.getId());

				startActivity(intent);
			}
		});

		placesListView.setAdapter(placesAdapter);
	}

	private void populatePlacesListView() {
		Map<Integer, Place> newPlaceNames = queryHelper.getPlaces();

		placeNames.clear();
		placeNames.putAll(newPlaceNames);

		placesAdapter.notifyDataSetChanged();
	}

	private void handleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i(TAG, "handleIntent: Performing search for \"" + query + "\".");

			Map<Integer, Place> newPlaceNames = queryHelper.searchPlaces(query);

			placeNames.clear();
			placeNames.putAll(newPlaceNames);

			placesAdapter.notifyDataSetChanged();

			Log.i(TAG, "handleIntent: Found " + placeNames.size() + " results.");
		}
	}
}
