package net.moltendorf.places.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import net.moltendorf.places.BaseActivity;
import net.moltendorf.places.PlacesListAdapter;
import net.moltendorf.places.R;
import net.moltendorf.places.model.Place;
import net.moltendorf.places.model.QueryHelper;
import net.moltendorf.places.viewholder.PlaceViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Search Screen
 * <p/>
 * Displays results from a passed in search.
 * - Advanced tag search (default).
 * - Specific tag (id) search.
 * - Favorites search.
 */
public class SearchActivity extends BaseActivity {
	private static final String TAG = "SearchActivity";

	public static final String ACTION_TAG_ID_SEARCH   = "net.moltendorf.places.ACTION_TAG_ID_SEARCH";
	public static final String ACTION_FAVORITE_SEARCH = "net.moltendorf.places.ACTION_FAVORITE_SEARCH";

	public static final String EXTRA_TAG_ID = "tagId";

	private static final int EXTRA_TAG_ID_DEFAULT = -1;

	Collection placesAdapterData = new ArrayList<>();

	private PlacesListAdapter placesAdapter;
	private RecyclerView      placesListView;

	private QueryHelper queryHelper;

	@Override
	protected void onCreateContentView() {
		setContentView(R.layout.activity_search);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		queryHelper = QueryHelper.getInstance(this);

		createViewReferences();
		createPlacesAdapter();

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void createViewReferences() {
		placesListView = (RecyclerView) findViewById(R.id.search_places_list);
		placesListView.setLayoutManager(new LinearLayoutManager(this));
	}

	private void createPlacesAdapter() {
		Map<Class<?>, Class<? extends PlacesListAdapter.ViewHolder>> relations;
		relations = new HashMap<Class<?>, Class<? extends PlacesListAdapter.ViewHolder>>(1) {{
			put(Place.class, PlaceViewHolder.class);
		}};

		placesAdapter = new PlacesListAdapter(this, relations, placesAdapterData);

		placesAdapter.addEventListener(new PlaceViewHolder.OnOpenDetailsListener() {
			@Override
			public void onOpenDetails(Place place) {
				Intent searchIntent = new Intent(SearchActivity.this, DetailActivity.class);
				searchIntent.setAction(DetailActivity.ACTION_SHOW_PLACE_BY_ID);
				searchIntent.putExtra(DetailActivity.EXTRA_PLACE_ID, place.getId());

				startActivity(searchIntent);
			}
		});

		placesAdapter.addEventListener(new PlaceViewHolder.OnFavoriteChangeListener() {
			@Override
			public void onFavoriteChanged(Place place, boolean isFavorite) {
				queryHelper.setPlaceIsFavorite(place, isFavorite);
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

						swapAdapterDataSet(queryHelper.searchPlaces(query).values());
						break action;

					case ACTION_TAG_ID_SEARCH:
						int tagId = intent.getIntExtra(EXTRA_TAG_ID, EXTRA_TAG_ID_DEFAULT);
						Log.i(TAG, "handleIntent: Looking up places with tag_id \"" + tagId + "\".");

						swapAdapterDataSet(queryHelper.getPlacesByTagId(tagId).values());
						break action;

					case ACTION_FAVORITE_SEARCH:
						Log.i(TAG, "handleIntent: Looking up places that are favorited.");

						swapAdapterDataSet(queryHelper.getPlacesByFavorite().values());
						break action;
				}
			}

			Log.i(TAG, "handleIntent: Getting all places.");
			swapAdapterDataSet(queryHelper.getPlaces().values());
		}

		Log.i(TAG, "handleIntent: Found " + placesAdapterData.size() + " results.");
	}

	private void swapAdapterDataSet(Collection newAdapterData) {
		placesAdapterData.clear();
		placesAdapterData.addAll(newAdapterData);

		placesAdapter.notifyDataSetChanged();
	}
}
