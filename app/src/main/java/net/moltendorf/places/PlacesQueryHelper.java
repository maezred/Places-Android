package net.moltendorf.places;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation for places_db queries.
 */
public class PlacesQueryHelper extends SQLiteAssetHelper {
	private static final String TAG = "PlacesQueryHelper";

	private static final String DB_NAME    = "places_db";
	private static final int    DB_VERSION = 7;

	// Tables.
	private static final String
		TBL_PLACES       = "places",
		TBL_PLACETAGS    = "placetags",
		TBL_TAGS         = "tags",
		TBL_TAGSPELLINGS = "tagspellings";

	// Places table columns.
	private static final String
		COL_PLACES_ID          = "place_id", // Also used in placetags table.
		COL_PLACES_NAME        = "place_name",
		COL_PLACES_PHONE       = "place_phone",
		COL_PLACES_DESCRIPTION = "place_description",
		COL_PLACES_HOURS       = "place_hours",
		COL_PLACES_IS_FAVORITE = "place_is_favorite";

	// Tags table columns.
	private static final String
		COL_TAGS_ID   = "tag_id", // Also used in placetags and tagspelling tables.
		COL_TAGS_NAME = "tag_name";

	// Tag spellings table columns.
	private static final String
		COL_TAGSPELLINGS_SPELLING = "tagspelling_spelling";

	/**
	 * Fetch all rows' id and name columns from the places table.
	 */
	private static final String SQL_GET_ALL_PLACES = "SELECT " +
		COL_PLACES_ID + ", " + COL_PLACES_NAME + ", " + COL_PLACES_PHONE + ", " + COL_PLACES_DESCRIPTION + ", " + COL_PLACES_HOURS + ", " + COL_PLACES_IS_FAVORITE + " " +
		"FROM " + TBL_PLACES + " " +
		"ORDER BY " + COL_PLACES_NAME;

	/**
	 * Fetch all rows' id and name columns from the places table that have a tag.
	 */
	private static final String SQL_GET_ALL_PLACES_BY_TAG_ID = "SELECT " +
		"p." + COL_PLACES_ID + ", p." + COL_PLACES_NAME + " " +
		"FROM " + TBL_PLACES + " p, " + TBL_PLACETAGS + " pt " +
		"WHERE p." + COL_PLACES_ID + " = pt." + COL_PLACES_ID + " " +
		"AND pt." + COL_TAGS_ID + " = ? " +
		"ORDER BY " + COL_PLACES_NAME;

	/**
	 * Fetch all rows' id and name columns from the places table that are favorited.
	 */
	private static final String SQL_GET_ALL_PLACES_BY_FAVORITE = "SELECT " +
		COL_PLACES_ID + ", " + COL_PLACES_NAME + " " +
		"FROM " + TBL_PLACES + " " +
		"WHERE " + COL_PLACES_IS_FAVORITE + " = 1";

	/**
	 * Fetch all rows' id and name columns from the tags table.
	 */
	private static final String SQL_GET_ALL_TAGS = "SELECT " +
		COL_TAGS_ID + ", " + COL_TAGS_NAME + " " +
		"FROM " + TBL_TAGS + " " +
		"ORDER BY " + COL_TAGS_NAME;

	/**
	 * Fetch all tags' id and name columns from the tags table that are attached to a place.
	 */
	private static final String SQL_GET_ALL_TAGS_BY_PLACE_ID = "SELECT " +
		"t." + COL_TAGS_ID + " " +
		"FROM " + TBL_TAGS + " t, " + TBL_PLACETAGS + " pt " +
		"WHERE t." + COL_TAGS_ID + " = pt." + COL_TAGS_ID + " " +
		"AND pt." + COL_PLACES_ID + " = ? " +
		"ORDER BY " + COL_TAGS_NAME;

	/**
	 * Fetch all places' id and name columns from the places table that contain a term in their name
	 * or attached tags.
	 */
	private static final String SQL_SEARCH_PLACES_BY_NAME_OR_TAG = "SELECT " +
		"p." + COL_PLACES_ID + " AS " + COL_PLACES_ID + ", p." + COL_PLACES_NAME + " " +
		"FROM " + TBL_PLACES + " p, " + TBL_TAGS + " t, " + TBL_PLACETAGS + " pt, " + TBL_TAGSPELLINGS + " ts " +
		"WHERE p." + COL_PLACES_ID + " = pt." + COL_PLACES_ID + " " +
		"AND pt." + COL_TAGS_ID + " = t." + COL_TAGS_ID + " " +
		"AND t." + COL_TAGS_ID + " = ts." + COL_TAGS_ID + " " +
		"AND (p." + COL_PLACES_NAME + " LIKE ? " +
		"OR t." + COL_TAGS_NAME + " LIKE ? " +
		"OR ts." + COL_TAGSPELLINGS_SPELLING + " LIKE ?)";

	/**
	 * Fetch tag id column from the tags table that matches the id.
	 */
	private static final String SQL_GET_TAG_ID_BY_TAG_NAME = "SELECT " +
		COL_TAGS_ID + " " +
		"FROM " + TBL_TAGS + " " +
		"WHERE " + COL_TAGS_NAME + " = ?";

	/**
	 * Update is favorite column in places table that matches the id.
	 */
	private static final String SQL_SET_FAVORITE_BY_PLACE_ID = "UPDATE " +
		TBL_PLACES + " " +
		"SET " + COL_PLACES_IS_FAVORITE + " = ? " +
		"WHERE " + COL_PLACES_ID + " = ?";

	private static PlacesQueryHelper instance;

	/**
	 * Get single instance of PlacesQueryHelper.
	 *
	 * @param context Used to create the instance if one does not already exist.
	 * @return instance
	 */
	public static PlacesQueryHelper getInstance(Context context) {
		if (instance != null) {
			return instance;
		}

		return instance = new PlacesQueryHelper(context);
	}

	/**
	 * Cache of all tags in object form.
	 */
	private Map<Integer, Tag> tags;

	/**
	 * Cache of all places in object form.
	 */
	private Map<Integer, Place> places;

	/**
	 * Constructor
	 */
	private PlacesQueryHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

		Log.d(TAG, "PlacesQueryHelper: Called.");

		populateAllTags();
		populateAllPlaces();
	}

	/**
	 * @return All places without querying database.
	 */
	public Map<Integer, Place> getPlaces() {
		return places;
	}

	public Map<Integer, Place> getPlacesByTagId(int tagId) {
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.rawQuery(SQL_GET_ALL_PLACES_BY_TAG_ID, new String[]{
			Integer.toString(tagId)
		});

		Map<Integer, Place> foundPlaces = createPlacesMapFromCursor(cursor);

		cursor.close();

		return foundPlaces;
	}

	/**
	 * @return Place by favorite lookup.
	 */
	public Map<Integer, Place> getPlacesByFavorite() {
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.rawQuery(SQL_GET_ALL_PLACES_BY_FAVORITE, null);

		Map<Integer, Place> foundPlaces = createPlacesMapFromCursor(cursor);

		cursor.close();

		return foundPlaces;
	}

	/**
	 * @param placeId Place to fetch.
	 * @return Place by key lookup without querying database.
	 */
	public Place getPlaceById(int placeId) {
		return places.get(placeId);
	}

	/**
	 * @param query Search query.
	 * @return Map containing all places found.
	 */
	public Map<Integer, Place> searchPlaces(String query) {
		// Clean up the query string. Replace newlines with spaces.
		// Remove consecutive, leading, and trailing spaces.
		query = query.replaceAll("(?:\\r|\\r?\\n)+", " ").replaceAll("(?:\\s{2,})", " ").trim();

		// Convert query string into an array.
		String[] searchTerms = query.split(" ");

		// Make all queries ignore context.
		for (int i = 0; i < searchTerms.length; ++i) {
			searchTerms[i] = "%" + searchTerms[i] + "%";
		}

		String[] searchArray = new String[searchTerms.length * 3];

		// Due to lack of named variables, we have to repeat each query three times.
		for (int i = 0; i < searchTerms.length; ++i) {
			int j = i * 3;

			searchArray[j] = searchTerms[i];
			searchArray[j + 1] = searchTerms[i];
			searchArray[j + 2] = searchTerms[i];
		}

		// Create a copy of each SQL statement for the number of query terms we have.
		String[] SQLArray = new String[searchTerms.length];
		Arrays.fill(SQLArray, SQL_SEARCH_PLACES_BY_NAME_OR_TAG);

		// Join them all together into a single SQL statement.
		String SQLQuery = TextUtils.join(" INTERSECT ", SQLArray) + " " +
			"ORDER BY p." + COL_PLACES_NAME;

		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.rawQuery(SQLQuery, searchArray);

		Map<Integer, Place> foundPlaces = createPlacesMapFromCursor(cursor);

		cursor.close();

		return foundPlaces;
	}

	public Map<Integer, Tag> getAllTags() {
		return tags;
	}

	public Tag getTagByName(String name) {
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.rawQuery(SQL_GET_TAG_ID_BY_TAG_NAME, new String[]{name});
		cursor.moveToFirst();

		if (cursor.isAfterLast()) {
			return null;
		}

		Tag result = tags.get(cursor.getInt(cursor.getColumnIndex(COL_TAGS_ID)));

		cursor.close();

		return result;
	}

	public void setPlaceIsFavorite(Place place, boolean isFavorite) {
		if (place.isFavorite() != isFavorite) {
			// Update local model.
			place.setIsFavorite(isFavorite);

			// Update database.
			SQLiteDatabase db = getWritableDatabase();

			db.execSQL(SQL_SET_FAVORITE_BY_PLACE_ID, new String[]{
				isFavorite ? "1" : "0",
				Integer.toString(place.getId())
			});

			String status = isFavorite ? " added to " : " removed from ";
			Log.i(TAG, "setPlaceIsFavorite: Place " + place.getName() + status + "favorites.");
		}
	}

	private void populateAllTags() {
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.rawQuery(SQL_GET_ALL_TAGS, null);
		cursor.moveToFirst();

		tags = new LinkedHashMap<>(cursor.getCount());

		while (!cursor.isAfterLast()) {
			Tag tag = new Tag(
				cursor.getInt(cursor.getColumnIndex(COL_TAGS_ID)),
				cursor.getString(cursor.getColumnIndex(COL_TAGS_NAME))
			);

			tags.put(tag.getId(), tag);

			cursor.moveToNext();
		}

		cursor.close();

		Log.i(TAG, "populateAllTags: Loaded " + tags.size() + " tags.");
	}

	private void populateAllPlaces() {
		SQLiteDatabase db = getReadableDatabase();

		// Fetch all the places.
		Cursor cursor = db.rawQuery(SQL_GET_ALL_PLACES, null);
		cursor.moveToFirst();

		places = new LinkedHashMap<>(cursor.getCount());

		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(cursor.getColumnIndex(COL_PLACES_ID));

			// Fetch all the tags for this place.
			Cursor tagCursor = db.rawQuery(SQL_GET_ALL_TAGS_BY_PLACE_ID, new String[]{
				Integer.toString(id)
			});

			tagCursor.moveToFirst();

			List<Tag> placeTags = new ArrayList<>(cursor.getCount());

			// Dump all the tags into a map.
			while (!tagCursor.isAfterLast()) {
				placeTags.add(tags.get(tagCursor.getInt(tagCursor.getColumnIndex(COL_TAGS_ID))));
				tagCursor.moveToNext();
			}

			tagCursor.close();

			Place place = new Place(
				cursor.getInt(cursor.getColumnIndex(COL_PLACES_ID)),
				cursor.getString(cursor.getColumnIndex(COL_PLACES_NAME)),
				cursor.getString(cursor.getColumnIndex(COL_PLACES_PHONE)),
				cursor.getString(cursor.getColumnIndex(COL_PLACES_DESCRIPTION)),
				cursor.getString(cursor.getColumnIndex(COL_PLACES_HOURS)),
				cursor.getInt(cursor.getColumnIndex(COL_PLACES_IS_FAVORITE)) > 0,
				placeTags
			);

			// Put the place into the places map.
			places.put(place.getId(), place);

			cursor.moveToNext();
		}

		cursor.close();

		Log.i(TAG, "populateAllPlaces: Loaded " + places.size() + " places.");
	}

	private Map<Integer, Place> createPlacesMapFromCursor(Cursor cursor) {
		Map<Integer, Place> foundPlaces = new LinkedHashMap<>(cursor.getCount());

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(cursor.getColumnIndex(COL_PLACES_ID));

			// Put a reference to our place in the new map containing the search results.
			foundPlaces.put(
				id,
				places.get(id)
			);

			cursor.moveToNext();
		}

		return foundPlaces;
	}
}
