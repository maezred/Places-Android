package net.moltendorf.places.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by moltendorf on 16/3/24.
 */
public class ViewPool<T> {
	private static final String TAG = "ViewPool";

	private Context   context;
	private int       resource;
	private ViewGroup viewGroup;

	private Deque<T> pool = new LinkedList<>();

	public ViewPool(Context context, int resource, Class<? extends ViewGroup> parentClass) {
		this.context = context;
		this.resource = resource;

		try {
			viewGroup = parentClass.getConstructor(Context.class).newInstance(context);
		} catch (Exception exception) {
			Log.w(TAG, "ViewPool: Invalid parent class specified: ", exception);

			viewGroup = null;
		}
	}

	public T getView() {
		if (pool.size() > 0) {
			return pool.removeLast();
		}

		return createView();
	}

	public void returnView(T view) {
		pool.addLast(view);
	}

	public void returnViews(Collection<? extends T> view) {
		pool.addAll(view);
	}

	private T createView() {
		return (T) LayoutInflater.from(context).inflate(resource, viewGroup, false);
	}
}
