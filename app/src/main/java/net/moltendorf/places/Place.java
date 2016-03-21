package net.moltendorf.places;

import java.util.Map;

/**
 * Created by moltendorf on 16/3/21.
 */
public class Place {
	private int                  id;
	private String               name;
	private Map<Integer, String> tags;

	public Place(int id, String name, Map<Integer, String> tags) {
		this.id = id;
		this.name = name;
		this.tags = tags;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
