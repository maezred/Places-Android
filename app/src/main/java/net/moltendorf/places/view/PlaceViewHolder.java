package net.moltendorf.places.view;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import net.moltendorf.places.Place;
import net.moltendorf.places.R;

public class PlaceViewHolder extends PlacesListAdapter.ViewHolder {
	private static final String TAG = "PlaceViewHolder";

	public PlaceViewHolder(Context context, ViewGroup viewGroup) {
		super(context, viewGroup, R.layout.item_place);

		Log.d(TAG, "PlaceViewHolder: Called.");
	}

	@Override
	public void bindTo(Object object, int position) {
		// Set place's name.
		((TextView) itemView.findViewById(R.id.place_name)).setText(((Place) object).getName());
	}
}
