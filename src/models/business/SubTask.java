package models.business;

import models.enums.Status;

public class SubTask extends Task {
	private int epicID;

	public SubTask(String name, String annotation, int epicID) {
		super(name, annotation);
		this.epicID = epicID;
	}

	public SubTask(int ID, String name, String annotation, Status status, int epicID) {
		super(ID, name, annotation);
		this.epicID = epicID;
	}

	public int getEpicID() {

		return epicID;
	}
}
