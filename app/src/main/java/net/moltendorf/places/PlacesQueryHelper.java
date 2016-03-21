package net.moltendorf.places;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by moltendorf on 16/3/21.
 */
public class PlacesQueryHelper extends SQLiteAssetHelper {
	private static final String TAG = "PlacesQueryHelper";

	private static final String DB_NAME    = "places_db";
	private static final int    DB_VERSION = 2;

	private static final String TBL_PLACES       = "places";
	private static final String TBL_PLACETAGS    = "placetags";
	private static final String TBL_TAGS         = "tags";
	private static final String TBL_TAGSPELLINGS = "tagspellings";

	private static final String COL_PLACES_ID   = "place_id";
	private static final String COL_PLACES_NAME = "place_name";

	private static final String COL_TAGS_ID   = "tag_id";
	private static final String COL_TAGS_NAME = "tag_name";

	private static final String COL_TAGSPELLINGS_SPELLING = "tagspelling_spelling";

	private static final String SQL_GET_ALL_PLACES = "SELECT " +
		COL_PLACES_ID + ", " + COL_PLACES_NAME + " " +
		"FROM " + TBL_PLACES + " " +
		"ORDER BY " + COL_PLACES_NAME;

	private static final String SQL_GET_ALL_TAGS_BY_PLACES_ID = "SELECT " +
		"t." + COL_TAGS_ID + ", t." + COL_TAGS_NAME + " " +
		"FROM " + TBL_TAGS + " t, " + TBL_PLACETAGS + " pt " +
		"WHERE t." + COL_TAGS_ID + " = pt." + COL_TAGS_ID + " " +
		"AND pt." + COL_PLACES_ID + " = ? " +
		"ORDER BY " + COL_TAGS_NAME;

	private static final String SQL_SEARCH_PLACES_BY_NAME_OR_TAG = "SELECT " +
		"p." + COL_PLACES_ID + " " +
		"FROM " + TBL_PLACES + " p, " + TBL_TAGS + " t, " + TBL_PLACETAGS + " pt, " + TBL_TAGSPELLINGS + " ts " +
		"WHERE p." + COL_PLACES_ID + " = pt." + COL_PLACES_ID + " " +
		"AND pt." + COL_TAGS_ID + " = t." + COL_TAGS_ID + " " +
		"AND t." + COL_TAGS_ID + " = ts." + COL_TAGS_ID + " " +
		"AND (p." + COL_PLACES_NAME + " LIKE ? " +
		"OR t." + COL_TAGS_NAME + " LIKE ? " +
		"OR ts." + COL_TAGSPELLINGS_SPELLING + " LIKE ?) " +
		"GROUP BY p." + COL_PLACES_ID + " " +
		"ORDER BY p." + COL_PLACES_NAME;

	private static PlacesQueryHelper instance;

	public static PlacesQueryHelper getInstance(Context context) {
		if (instance != null) {
			return instance;
		}

		return instance = new PlacesQueryHelper(context);
	}

	private Map<Integer, Place> places = new LinkedHashMap<>();

	private PlacesQueryHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

		setForcedUpgrade();

		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.rawQuery(SQL_GET_ALL_PLACES, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(cursor.getColumnIndex(COL_PLACES_ID));

			Cursor tagCursor = db.rawQuery(SQL_GET_ALL_TAGS_BY_PLACES_ID, new String[]{
				Integer.toString(id)
			});

			tagCursor.moveToFirst();

			Map<Integer, String> tags = new LinkedHashMap<>();

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

			places.put(
				place.getId(),
				place
			);

			cursor.moveToNext();
		}

		cursor.close();

		Log.d(TAG, "PlacesQueryHelper: Loaded " + places.size() + " places.");
	}

	public Map<Integer, Place> getPlaces() {
		return places;
	}

	public Map<Integer, Place> searchPlaces(String query) {
		query = "%" + query + "%";

		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.rawQuery(SQL_SEARCH_PLACES_BY_NAME_OR_TAG, new String[]{
			query,
			query,
			query
		});

		cursor.moveToFirst();

		Map<Integer, Place> foundPlaces = new LinkedHashMap<>();

		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(cursor.getColumnIndex(COL_PLACES_ID));

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
