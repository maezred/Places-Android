package net.moltendorf.places.model;

import android.util.Log;

import java.util.List;

/**
 * Place Data Object
 * <p/>
 * Holds cache data from database.
 */
public class Place {
	private static final String TAG = "Place";

	private int    id;
	private String name;

	private String phone, phoneRaw;

	private String description;

	private String hours;

	private boolean isFavorite;

	private List<Tag> tags;

	public Place(int id, String name, String phone, String description, String hours, boolean isFavorite, List<Tag> tags) {
		Log.d(TAG, "Place: Called.");

		this.id = id;
		this.name = name;
		this.description = description;
		this.hours = hours;

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

	public String getHours() {
		return hours;
	}

	public String getPhoneRaw() {
		return phoneRaw;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	void setIsFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * Tag Data Object
	 * <p/>
	 * Holds cache data about tags from the database.
	 */
	public static class Tag {
		int    id;
		String name;

		public Tag(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
