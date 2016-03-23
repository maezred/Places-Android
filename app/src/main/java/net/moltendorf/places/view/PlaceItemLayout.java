package net.moltendorf.places.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class PlaceItemLayout extends LinearLayout {
	private static final String TAG = "ItemPlaceLayout";

	public PlaceItemLayout(Context context) {
		super(context);

		Log.d(TAG, "ItemPlaceLayout: Called.");
	}

	public PlaceItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		Log.d(TAG, "ItemPlaceLayout: Called.");
	}

	public PlaceItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		Log.d(TAG, "ItemPlaceLayout: Called.");
	}
}
