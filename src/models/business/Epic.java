package models.business;

import models.enums.Status;

public class Epic extends Task {
	public Epic(String name, String annotation) {
		super(name, annotation);
	}

	public Epic(int ID, String name, String annotation, Status status) {
		super(ID, name, annotation, status);
	}
}
