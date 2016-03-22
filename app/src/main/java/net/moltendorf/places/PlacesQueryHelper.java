package net.moltendorf.places;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation for places_db queries.
 */
public class PlacesQueryHelper extends SQLiteAssetHelper {
	private static final String TAG = "PlacesQueryHelper";

	private static final String DB_NAME    = "places_db";
	private static final int    DB_VERSION = 3;

	// Tables.
	private static final String
		TBL_PLACES       = "places",
		TBL_PLACETAGS    = "placetags",
		TBL_TAGS         = "tags",
		TBL_TAGSPELLINGS = "tagspellings";

	// Places table columns.
	private static final String
		COL_PLACES_ID   = "place_id", // Also used in placetags table.
		COL_PLACES_NAME = "place_name";

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
		COL_PLACES_ID + ", " + COL_PLACES_NAME + " " +
		"FROM " + TBL_PLACES + " " +
		"ORDER BY " + COL_PLACES_NAME;

	/**
	 * Fetch all tags' id and name columns from the tags table that are attached to a place.
	 */
	private static final String SQL_GET_ALL_TAGS_BY_PLACES_ID = "SELECT " +
		"t." + COL_TAGS_ID + ", t." + COL_TAGS_NAME + " " +
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
	 * Cache of all places in object form.
	 */
	private Map<Integer, Place> places = new LinkedHashMap<>();

	/**
	 * Constructor
	 */
	private PlacesQueryHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

		// Upgrade database via overwrite.
		setForcedUpgrade();

		SQLiteDatabase db = getReadableDatabase();

		// Fetch all the places.
		Cursor cursor = db.rawQuery(SQL_GET_ALL_PLACES, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(cursor.getColumnIndex(COL_PLACES_ID));

			// Fetch all the tags for this place.
			Cursor tagCursor = db.rawQuery(SQL_GET_ALL_TAGS_BY_PLACES_ID, new String[]{
				Integer.toString(id)
			});

			tagCursor.moveToFirst();

			Map<Integer, String> tags = new LinkedHashMap<>();

			// Dump all the tags into a map.
			while (!tagCursor.isAfterLast()) {
				tags.put(
					tagCursor.getInt(tagCursor.getColumnIndex(COL_TAGS_ID)),
					tagCursor.getString(tagCursor.getColumnIndex(COL_TAGS_NAME)).intern() // Save memory.
				);

				tagCursor.moveToNext();
			}

			tagCursor.close();

			Place place = new Place(
				cursor.getInt(cursor.getColumnIndex(COL_PLACES_ID)),
				cursor.getString(cursor.getColumnIndex(COL_PLACES_NAME)),
				tags
			);

			// Put the place into the places map.
			places.put(
				place.getId(),
				place
			);

			cursor.moveToNext();
		}

		cursor.close();

		Log.d(TAG, "PlacesQueryHelper: Loaded " + places.size() + " places.");
	}

	/**
	 * @return All places without querying database.
	 */
	public Map<Integer, Place> getPlaces() {
		return places;
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
		cursor.moveToFirst();

		Map<Integer, Place> foundPlaces = new LinkedHashMap<>();

		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(cursor.getColumnIndex(COL_PLACES_ID));

			// Put a reference to our place in the new map containing the search results.
			foundPlaces.put(
				id,
				places.get(id)
			);

			cursor.moveToNext();
		}

		cursor.close();

		return foundPlaces;
	}
}
