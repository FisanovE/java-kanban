package models.business;

import models.enums.Status;

public class SubTask extends Task {
	private final int epicID;

	public SubTask(String name, String annotation, int epicID) {
		super(name, annotation);
		this.epicID = epicID;
	}

	public int getEpicID() {

		return epicID;
	}
}
