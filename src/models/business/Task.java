package models.business;

import models.enums.Status;

public class Task {
	private final String name;
	private final String annotation;
	private int ID;
	private Status status;

	public Task(int ID, String name, String annotation) {
		this.ID = ID;
		this.name = name;
		this.annotation = annotation;
	}

	public Task(int ID, String name, String annotation, Status status) {
		this.ID = ID;
		this.name = name;
		this.annotation = annotation;
		this.status = status;
	}

	public int getID() {
		return ID;
	}

	public Status getStatus() {

		return status;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "__" + "__" + ID + "__" + name + "__" + status + "__" + annotation + "\n";
	}
}


