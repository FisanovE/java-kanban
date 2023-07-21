package models.business;

import models.enums.Status;

import java.time.LocalDateTime;

public class SubTask extends Task {
	private final int epicID;

	public SubTask(String name, String annotation, int epicID) {
		super(name, annotation);
		this.epicID = epicID;
	}

	public SubTask(String name, String annotation, LocalDateTime startTime, long duration, int epicID) {
		super(name, annotation, startTime, duration);
		this.epicID = epicID;
	}

	public SubTask(int ID, String name, String annotation, Status status, int epicID) {
		super(ID, name, annotation, status);
		this.epicID = epicID;
	}

	public SubTask(int ID, String name, String annotation, Status status, LocalDateTime startTime, long duration, int epicID) {
		super(ID, name, annotation, status, startTime, duration);
		this.epicID = epicID;
	}

	public int getEpicID() {

		return epicID;
	}
}
