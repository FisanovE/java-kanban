package models.business;

import models.enums.Status;

public class SubTask extends Task {
	private final int epicID;

	public SubTask(int ID, String name, String annotation, Status status, int epicID) {
		super(ID, name, annotation, status);
		this.epicID = epicID;
	}

	public int getEpicID() {

		return epicID;
	}
}
