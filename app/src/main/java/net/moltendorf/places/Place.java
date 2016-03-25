package net.moltendorf.places;

import android.util.Log;

import java.util.List;

public class Place {
	private static final String TAG = "Place";

	private int    id;
	private String name;

	private String phone, phoneRaw;

	private String description;

	private boolean isFavorite;

	private List<Tag> tags;

	public Place(int id, String name, String phone, String description, boolean isFavorite, List<Tag> tags) {
		Log.d(TAG, "Place: Called.");

		this.id = id;
		this.name = name;
		this.description = description;

		phoneRaw = phone;
		this.phone = phone == null || phone.length() != 10 ?
			phone :
			String.format("(%s) %s-%s", phone.substring(0, 3), phone.substring(3, 6), phone.substring(6));

		this.isFavorite = isFavorite;
		this.tags = tags;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getDescription() {
		return description;
	}

	public String getPhoneRaw() {
		return phoneRaw;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public List<Tag> getTags() {
		return tags;
	}
}
