package net.moltendorf.places.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import net.moltendorf.places.BaseActivity;
import net.moltendorf.places.PlacesListAdapter;
import net.moltendorf.places.R;
import net.moltendorf.places.model.Place;
import net.moltendorf.places.model.QueryHelper;
import net.moltendorf.places.viewholder.TagViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Tags Screen
 * <p/>
 * Displays all tags available to search with.
 */
public class TagsActivity extends BaseActivity {
	private static final String TAG = "TagsActivity";

	PlacesListAdapter tagsAdapter;
	RecyclerView      tagsListView;

	private QueryHelper queryHelper;

	@Override
	protected void onCreateContentView() {
		setContentView(R.layout.activity_tags);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		queryHelper = QueryHelper.getInstance(this);

		createViewReferences();
		createTagsAdapter();
	}

	private void createViewReferences() {
		tagsListView = (RecyclerView) findViewById(R.id.tags_tag_list);
		tagsListView.setLayoutManager(new LinearLayoutManager(this));
	}

	private void createTagsAdapter() {
		Map<Class<?>, PlacesListAdapter.Factory> factories;
		factories = new HashMap<Class<?>, PlacesListAdapter.Factory>() {{
			put(Place.Tag.class, new PlacesListAdapter.Factory() {
				@Override
				public PlacesListAdapter.ViewHolder createViewHolder(Context context, ViewGroup parent) {
					return new TagViewHolder(context, parent);
				}
			});
		}};

		tagsAdapter = new PlacesListAdapter(this, factories, new ArrayList<>(queryHelper.getAllTags().values()));
		tagsAdapter.addEventListener(new TagViewHolder.OnTagClickListener() {
			@Override
			public void onTagClicked(Place.Tag tag) {
				Intent searchIntent = new Intent(TagsActivity.this, SearchActivity.class);
				searchIntent.setAction(SearchActivity.ACTION_TAG_ID_SEARCH);
				searchIntent.putExtra(SearchActivity.EXTRA_TAG_ID, tag.getId());

				startActivity(searchIntent);
			}
		});

		tagsListView.setAdapter(tagsAdapter);
	}
}
