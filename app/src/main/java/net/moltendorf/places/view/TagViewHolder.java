package net.moltendorf.places.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.moltendorf.places.R;
import net.moltendorf.places.Tag;

import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.Set;

public class TagViewHolder extends PlacesListAdapter.ViewHolder {
	private Tag tag;

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
		tag = (Tag) object;

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

	public static abstract class OnTagClickListener implements EventListener {
		abstract public void onTagClicked(Tag tag);
	}
}
