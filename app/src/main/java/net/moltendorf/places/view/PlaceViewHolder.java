package net.moltendorf.places.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.moltendorf.places.Place;
import net.moltendorf.places.R;

import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.Set;

public class PlaceViewHolder extends PlacesListAdapter.ViewHolder {
	private static final String TAG = "PlaceViewHolder";

	private Place place;

	private Set<OnOpenDetailsListener> onOpenDetailsListeners = new LinkedHashSet<>();

	public PlaceViewHolder(Context context, ViewGroup viewGroup) {
		super(context, viewGroup, R.layout.item_place);

		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callOnOpenDetails();
			}
		});

		Log.d(TAG, "PlaceViewHolder: Called.");
	}

	@Override
	public void bindTo(Object object, int position) {
		// Set place's name.
		((TextView) itemView.findViewById(R.id.place_name)).setText(((Place) object).getName());
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

	public static abstract class OnOpenDetailsListener implements EventListener {
		abstract public void onOpenDetails(Place place);
	}
}
