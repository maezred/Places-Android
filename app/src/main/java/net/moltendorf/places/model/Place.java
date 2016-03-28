package net.moltendorf.places.model;

import android.database.Cursor;
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

	public Place(Cursor cursor, List<Tag> tags) {
		Log.d(TAG, "Place: Called.");

		id = cursor.getInt(cursor.getColumnIndex(QueryHelper.COL_PLACES_ID));
		name = cursor.getString(cursor.getColumnIndex(QueryHelper.COL_PLACES_NAME));
		phoneRaw = cursor.getString(cursor.getColumnIndex(QueryHelper.COL_PLACES_PHONE));
		description = cursor.getString(cursor.getColumnIndex(QueryHelper.COL_PLACES_DESCRIPTION));
		hours = cursor.getString(cursor.getColumnIndex(QueryHelper.COL_PLACES_HOURS));
		isFavorite = cursor.getInt(cursor.getColumnIndex(QueryHelper.COL_PLACES_IS_FAVORITE)) > 0;

		phone = phoneRaw == null || phoneRaw.length() != 10 ?
			phoneRaw :
			String.format("(%s) %s-%s", phoneRaw.substring(0, 3), phoneRaw.substring(3, 6), phoneRaw.substring(6));

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

		public Tag(Cursor cursor) {
			id = cursor.getInt(cursor.getColumnIndex(QueryHelper.COL_TAGS_ID));
			name = cursor.getString(cursor.getColumnIndex(QueryHelper.COL_TAGS_NAME));
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
