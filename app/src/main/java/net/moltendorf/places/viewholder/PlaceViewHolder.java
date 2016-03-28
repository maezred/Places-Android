package net.moltendorf.places.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.moltendorf.places.PlacesListAdapter;
import net.moltendorf.places.model.Place;
import net.moltendorf.places.R;
import net.moltendorf.places.ViewPool;

import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * View holder for place rows in the search activity.
 */
public class PlaceViewHolder extends PlacesListAdapter.ViewHolder {
	private static final String TAG = "PlaceViewHolder";

	private static ViewPool<TextView> tagPool;

	private List<TextView> tagsInUse = new LinkedList<>();

	private Place place;

	private Set<OnOpenDetailsListener>    onOpenDetailsListeners    = new LinkedHashSet<>();
	private Set<OnFavoriteChangeListener> onFavoriteChangeListeners = new LinkedHashSet<>();
	private Set<OnPhoneClickListener>     onPhoneClickListeners     = new LinkedHashSet<>();

	public PlaceViewHolder(Context context, ViewGroup viewGroup) {
		super(context, viewGroup, R.layout.item_place);

		if (tagPool == null) {
			tagPool = new ViewPool<>(context, R.layout.tag, LinearLayout.class);
		}

		itemView.findViewById(R.id.place_details).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: place_details");

				callOnOpenDetails();
			}
		});

		((CheckBox) itemView.findViewById(R.id.place_favorite)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG, "onCheckedChanged: place_favorite");

				callOnFavoriteChanged(isChecked);
			}
		});

		itemView.findViewById(R.id.place_phone).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (place.getPhone() == null) {
					itemView.findViewById(R.id.place_details).performClick();
				} else {
					Log.d(TAG, "onClick: place_phone");

					callOnPhoneClicked();
				}
			}
		});

		Log.d(TAG, "PlaceViewHolder: Called.");
	}

	@Override
	public void bindTo(Object object, int position) {
		place = (Place) object;

		// Get subviews.
		TextView
			nameView = (TextView) itemView.findViewById(R.id.place_name),
			phoneView = (TextView) itemView.findViewById(R.id.place_phone);

		CheckBox     favoriteCheckBox = (CheckBox) itemView.findViewById(R.id.place_favorite);
		LinearLayout tagsLayout       = (LinearLayout) itemView.findViewById(R.id.place_tags);

		// Clear existing content.
		tagsLayout.removeAllViewsInLayout();
		tagPool.returnViews(tagsInUse);
		tagsInUse.clear();

		// Populate views.
		nameView.setText(place.getName());
		favoriteCheckBox.setChecked(place.isFavorite());

		String phone      = place.getPhone();
		int    phoneColor = R.color.primary_text;

		if (phone == null) {
			phone = "No number listed";
			phoneColor = R.color.secondary_text;
		}

		phoneView.setText(phone);
		phoneView.setTextColor(context.getResources().getColor(phoneColor));

		for (Place.Tag tag : place.getTags()) {
			TextView tagView = tagPool.getView();
			tagView.setText(tag.getName());

			tagsInUse.add(tagView);
			tagsLayout.addView(tagView);
		}
	}

	public void addOnOpenDetailsListener(OnOpenDetailsListener listener) {
		onOpenDetailsListeners.add(listener);
	}

	public void removeOnOpenDetailsListener(OnOpenDetailsListener listener) {
		onOpenDetailsListeners.remove(listener);
	}

	public boolean callOnOpenDetails() {
		for (OnOpenDetailsListener listener : onOpenDetailsListeners) {
			listener.onOpenDetails(place);
		}

		return !onOpenDetailsListeners.isEmpty();
	}

	public void addOnFavoriteChangeListener(OnFavoriteChangeListener listener) {
		onFavoriteChangeListeners.add(listener);
	}

	public void removeOnFavoriteChangeListener(OnFavoriteChangeListener listener) {
		onFavoriteChangeListeners.remove(listener);
	}

	public boolean callOnFavoriteChanged(boolean isFavorite) {
		for (OnFavoriteChangeListener listener : onFavoriteChangeListeners) {
			listener.onFavoriteChanged(place, isFavorite);
		}

		return !onFavoriteChangeListeners.isEmpty();
	}

	public void addOnPhoneClickListener(OnPhoneClickListener listener) {
		onPhoneClickListeners.add(listener);
	}

	public void removeOnPhoneClickListener(OnPhoneClickListener listener) {
		onPhoneClickListeners.remove(listener);
	}

	public boolean callOnPhoneClicked() {
		call:
		{
			for (OnPhoneClickListener listener : onPhoneClickListeners) {
				if (listener.onPhoneClicked(place)) {
					break call;
				}
			}

			String phone = place.getPhoneRaw();

			if (phone != null) {
				Intent callNumber = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
				context.startActivity(callNumber);
			}
		}

		return !onPhoneClickListeners.isEmpty();
	}

	/**
	 * Event listener for when the user taps on a place in an ambiguous position.
	 */
	public static abstract class OnOpenDetailsListener implements EventListener {
		abstract public void onOpenDetails(Place place);
	}

	/**
	 * Event listener for when the user toggles a place as a favorite.
	 */
	public static abstract class OnFavoriteChangeListener implements EventListener {
		abstract public void onFavoriteChanged(Place place, boolean isFavorite);
	}

	/**
	 * Event listener for when the user taps on a phone number.
	 */
	public static abstract class OnPhoneClickListener implements EventListener {
		abstract public boolean onPhoneClicked(Place place);
	}
}
