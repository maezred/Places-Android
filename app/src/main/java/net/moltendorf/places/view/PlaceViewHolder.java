package net.moltendorf.places.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.moltendorf.places.Place;
import net.moltendorf.places.R;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PlaceViewHolder extends PlacesListAdapter.ViewHolder {
	private static final String TAG = "PlaceViewHolder";

	private Place place;

	private Set<OnOpenDetailsListener>    onOpenDetailsListeners    = new LinkedHashSet<>();
	private Set<OnFavoriteChangeListener> onFavoriteChangeListeners = new LinkedHashSet<>();
	private Set<OnPhoneClickListener>     onPhoneClickListeners     = new LinkedHashSet<>();

	private int            tagViewPoolIndex = -1;
	private List<TextView> tagViewPool      = new ArrayList<>();

	public PlaceViewHolder(Context context, ViewGroup viewGroup) {
		super(context, viewGroup, R.layout.item_place);

		itemView.findViewById(R.id.place_details).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: place_details");

				callOnOpenDetails();
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
		resetTagsLayout();

		// Populate views.
		nameView.setText(place.getName());
		favoriteCheckBox.setChecked(place.isFavorite());

		String phone      = place.getPhone();
		int    phoneColor = android.R.color.black;

		if (phone == null) {
			phone = "No number listed";
			phoneColor = android.R.color.darker_gray;
		}

		phoneView.setText(phone);
		phoneView.setTextColor(context.getResources().getColor(phoneColor));

		for (String tag : place.getTags().values()) {
			TextView tagView = getTagView();
			tagView.setText(tag);

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

	private void resetTagsLayout() {
		((LinearLayout) itemView.findViewById(R.id.place_tags)).removeAllViewsInLayout();

		tagViewPoolIndex = -1;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private TextView getTagView() {
		++tagViewPoolIndex;

		if (tagViewPoolIndex == tagViewPool.size()) {
			TextView textView = new TextView(context);

			Resources resources = context.getResources();
			DisplayMetrics metrics = resources.getDisplayMetrics();
			int marginPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, metrics);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
			);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				layoutParams.setMarginStart(marginPixels / 2);
				layoutParams.setMarginEnd(marginPixels / 2);
			} else {
				layoutParams.setMargins(marginPixels, 0, marginPixels, 0);
			}

			textView.setTextColor(resources.getColor(R.color.colorDarkGray));
			textView.setBackgroundColor(resources.getColor(R.color.colorGray));
			textView.setLayoutParams(layoutParams);
			textView.setPadding(marginPixels, marginPixels / 2, marginPixels, marginPixels * 2 / 3);

			tagViewPool.add(textView);

			return textView;
		}

		return tagViewPool.get(tagViewPoolIndex);
	}

	public static abstract class OnOpenDetailsListener implements EventListener {
		abstract public void onOpenDetails(Place place);
	}

	public static abstract class OnFavoriteChangeListener implements EventListener {
		abstract public void onFavoriteChanged(Place place, boolean isFavorite);
	}

	public static abstract class OnPhoneClickListener implements EventListener {
		abstract public boolean onPhoneClicked(Place place);
	}
}
