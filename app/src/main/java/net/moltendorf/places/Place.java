package net.moltendorf.places;

import android.util.Log;

import java.util.Map;

public class Place {
	private static final String TAG = "Place";

	private int                  id;
	private String               name;
	private Map<Integer, String> tags;

	public Place(int id, String name, Map<Integer, String> tags) {
		Log.d(TAG, "Place: Called.");

		this.id = id;
		this.name = name;
		this.tags = tags;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
