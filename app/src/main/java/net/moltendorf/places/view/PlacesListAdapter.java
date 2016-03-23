package net.moltendorf.places.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.moltendorf.places.Place;
import net.moltendorf.places.R;

import java.util.HashMap;
import java.util.Map;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.ViewHolder> {
	private static final String TAG = "PlacesListAdapter";

	private Context             context;
	private Map<Integer, Place> objects;

	private Map<Class<?>, Integer> viewTypeLookup;
	private Class<?>[]             viewHolderLookup; // We already guarantee it extends ViewHolder.

	public PlacesListAdapter(Context context, Map<Class<?>, Class<? extends ViewHolder>> relations, Map<Integer, Place> objects) {
		Log.d(TAG, "PlacesListAdapter: Called.");

		this.context = context;
		this.objects = objects;

		// Create lookups.
		int size = relations.size();
		viewTypeLookup = new HashMap<>(size);
		viewHolderLookup = new Class<?>[size];

		int i = 0;
		for (Map.Entry<Class<?>, Class<? extends ViewHolder>> entry : relations.entrySet()) {
			viewTypeLookup.put(entry.getKey(), i);
			viewHolderLookup[i] = entry.getValue();

			i++;
		}
	}

	@Override
	public int getItemViewType(int position) {
		// Todo: add more helpful exception when no class to resource relationship exists.
		return viewTypeLookup.get(objects.values().toArray()[position].getClass());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		try {
			// Todo: better type safety guarantees using more generics so we don't crash here as often?
			return viewHolderLookup[viewType]
				.asSubclass(ViewHolder.class)
				.getConstructor(Context.class, ViewGroup.class)
				.newInstance(context, parent);
		} catch (final Exception exception) {
			throw new IllegalStateException("Could not construct view holder.", exception);
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.bindTo(objects.values().toArray()[position], position);
	}

	@Override
	public int getItemCount() {
		return objects.size();
	}

	abstract public static class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(Context context, ViewGroup viewGroup) {
			super(LayoutInflater.from(context).inflate(R.layout.item_place, viewGroup, false));
		}

		abstract public void bindTo(Object object, int position);
	}
}
