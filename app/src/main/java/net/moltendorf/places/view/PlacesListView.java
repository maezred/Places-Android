package net.moltendorf.places.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

public class PlacesListView extends RecyclerView {
	private static final String TAG = "PlacesListView";

	public PlacesListView(Context context) {
		super(context);

		Log.d(TAG, "PlacesListView: Called.");
	}

	public PlacesListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		Log.d(TAG, "PlacesListView: Called.");
	}

	public PlacesListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		Log.d(TAG, "PlacesListView: Called.");
	}
}
