package net.moltendorf.places;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
	private static final String TAG = "SearchActivity";

	List<String> placeNames = new ArrayList<>();

	private ArrayAdapter<String> placesAdapter;
	private ListView             placesListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		createViewReferences();
		populatePlacesListView();

		handleIntent(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_search, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView    searchView    = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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
		placesListView = (ListView) findViewById(R.id.search_places_list);
	}

	private void populatePlacesListView() {
		Map<Integer, Place> places        = PlacesQueryHelper.getInstance(this).getPlaces();
		List<String>        newPlaceNames = new ArrayList<>();

		for (Place place : places.values()) {
			newPlaceNames.add(place.getName());
		}

		placeNames.clear();
		placeNames.addAll(newPlaceNames);

		placesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeNames);
		placesListView.setAdapter(placesAdapter);
	}

	private void handleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i(TAG, "handleIntent: Performing search for \"" + query + "\".");

			Map<Integer, Place> places = PlacesQueryHelper.getInstance(this).searchPlaces(query);
			List<String> newPlaceNames = new ArrayList<>();

			for (Place place : places.values()) {
				newPlaceNames.add(place.getName());
			}

			placeNames.clear();
			placeNames.addAll(newPlaceNames);

			placesAdapter.notifyDataSetChanged();

			Log.i(TAG, "handleIntent: Found " + placeNames.size() + " results.");
		}
	}
}
