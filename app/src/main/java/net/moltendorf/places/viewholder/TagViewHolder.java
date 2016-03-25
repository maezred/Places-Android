package net.moltendorf.places.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.moltendorf.places.PlacesListAdapter;
import net.moltendorf.places.R;
import net.moltendorf.places.model.Place;

import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * View holder for tag rows in the tag activity.
 */
public class TagViewHolder extends PlacesListAdapter.ViewHolder {
	private Place.Tag tag;

	private Set<OnTagClickListener> onTagClickListeners = new LinkedHashSet<>();

	public TagViewHolder(Context context, ViewGroup viewGroup) {
		super(context, viewGroup, R.layout.item_tag);

		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callOnTagClicked();
			}
		});
	}

	@Override
	public void bindTo(Object object, int position) {
		tag = (Place.Tag) object;

		((TextView) itemView).setText(tag.getName());
	}

	public void addOnTagClickListener(OnTagClickListener listener) {
		onTagClickListeners.add(listener);
	}

	public void removeOnTagClickListener(OnTagClickListener listener) {
		onTagClickListeners.remove(listener);
	}

	public boolean callOnTagClicked() {
		for (OnTagClickListener listener : onTagClickListeners) {
			listener.onTagClicked(tag);
		}

		return !onTagClickListeners.isEmpty();
	}

	/**
	 * Event listener for when the user taps on a tag.
	 */
	public static abstract class OnTagClickListener implements EventListener {
		abstract public void onTagClicked(Place.Tag tag);
	}
}
