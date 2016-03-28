package net.moltendorf.places;

import android.database.Cursor;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class CursorList<T> extends AbstractList {
	private Cursor     cursor;
	private Factory<T> factory;
	private int        count;
	private List<T>    cache;

	public CursorList(Cursor cursor, Factory<T> factory) {
		this.cursor = cursor;
		this.factory = factory;

		count = cursor.getCount();
		cache = new ArrayList<>(count);
	}

	@Override
	public T get(int location) {
		if (location < 0 || location >= count) {
			return null;
		}

		T item = cache.get(location);

		if (item == null) {
			cursor.move(location);
			item = factory.getInstance(cursor);
			cache.set(location, item);
		}

		return item;
	}

	@Override
	public int size() {
		return count;
	}

	public void replace(CursorList<T> cursorList) {

	}

	public abstract static class Factory<T> {
		abstract public T getInstance(Cursor cursor);
	}
}
