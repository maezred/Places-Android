package net.moltendorf.places.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
public class SearchActivity extends AppCompatActivity {
	private static final String TAG = "SearchActivity";

	Map<Integer, Place> placeNames = new LinkedHashMap<>();

	private PlacesListAdapter placesAdapter;
	private PlacesListView    placesListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		setContentView(R.layout.activity_search);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		createViewReferences();
		createPlacesAdapter();

		Intent intent = getIntent();

		handleIntent(intent);

		if (!intent.getAction().equals(Intent.ACTION_SEARCH)) {
			populatePlacesListView();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_search, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView    searchView    = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setQueryHint(getResources().getString(R.string.action_search_hint));

		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_search:
				return true;
		}

		return super.onOptionsItemSelected(item);
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
		placesListView.setAdapter(placesAdapter);
	}

	private void populatePlacesListView() {
		Map<Integer, Place> newPlaceNames = PlacesQueryHelper.getInstance(this).getPlaces();

		placeNames.clear();
		placeNames.putAll(newPlaceNames);

		placesAdapter.notifyDataSetChanged();
	}

	private void handleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i(TAG, "handleIntent: Performing search for \"" + query + "\".");

			Map<Integer, Place> newPlaceNames = PlacesQueryHelper.getInstance(this).searchPlaces(query);

			placeNames.clear();
			placeNames.putAll(newPlaceNames);

			placesAdapter.notifyDataSetChanged();

			Log.i(TAG, "handleIntent: Found " + placeNames.size() + " results.");
		}
	}
}
