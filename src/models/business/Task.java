package models.business;

import models.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
	private String name;
	private String annotation;
	private int ID;
	private Status status;
	private LocalDateTime startTime;
	private long duration;


	public Task(String name, String annotation) {
		this.name = name;
		this.annotation = annotation;
	}

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

	public Task(int ID, String name, String annotation, Status status, LocalDateTime startTime, long duration) {
		this.ID = ID;
		this.name = name;
		this.annotation = annotation;
		this.status = status;
		this.startTime = startTime;
		this.duration = duration;
	}

	public Task(String name, String annotation, LocalDateTime startTime, long duration) {
		this.name = name;
		this.annotation = annotation;
		this.startTime = startTime;
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public String getAnnotation() {
		return annotation;
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
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy|HH:mm");
		String start = startTime == null ? null : startTime.format(formatter);
		String end = startTime == null ? null : getEndTime().format(formatter);
		StringBuilder builder = new StringBuilder();
		return builder.append(start).append("__").append(end).append("__").append(ID).append("__").append(name)
					  .append("__").append(status).append("__").append(annotation).append("\n").toString();
	}

	public LocalDateTime getEndTime() {
		return startTime == null ? null : startTime.plus(Duration.ofMinutes(duration));
	}


	public LocalDateTime getStartTime() {
		if (startTime == null) {
			return null;
		}
		return startTime;
	}

	public long getDuration() {
		return duration;
	}

}


