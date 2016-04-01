package net.moltendorf.places;

import android.database.Cursor;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

public class CursorList<T> extends AbstractList {
	private static final String TAG = "CursorList";

	private Cursor               cursor;
	private Factory<? extends T> factory;
	private int                  count;
	private Map<Integer, T>      cache;

	public CursorList(Cursor cursor, Factory<? extends T> factory) {
		this.cursor = cursor;
		this.factory = factory;

		count = cursor.getCount();
		cache = new HashMap<>(count);
	}

	@Override
	public T get(int location) {
		if (location < 0 || location >= count) {
			return null;
		}

		T item = null;

		if (cache.size() > location) {
			item = cache.get(location);
		}

		if (item == null) {
			cursor.moveToPosition(location);
			item = factory.getInstance(cursor);
			cache.put(location, item);
		}

		return item;
	}

	@Override
	public int size() {
		return count;
	}

	public void replace(CursorList<T> cursorList) {

	}

	public interface Factory<T> {
		T getInstance(Cursor cursor);
	}
}
