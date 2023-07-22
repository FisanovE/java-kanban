package models.business;

import models.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Epic extends Task {
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private long duration;

	public Epic(String name, String annotation) {
		super(name, annotation);
	}

	public Epic(String name, String annotation, LocalDateTime startTime, long duration) {
		super(name, annotation, startTime, duration);

	}

	public Epic(int ID, String name, String annotation, Status status, LocalDateTime startTime, long duration) {
		super(ID, name, annotation, status);
		this.startTime = startTime;
		this.duration = duration;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}


	public LocalDateTime getEndTime() {
		return endTime;
	}

	public LocalDateTime getStartTime() {

		return startTime;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override

	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy|HH:mm");
		String start = startTime == null ? null : startTime.format(formatter);
		String end = startTime == null ? null : getEndTime().format(formatter);
		StringBuilder builder = new StringBuilder();
		return builder.append(getClass().getSimpleName()).append(": ID=[").append(getID()).append("] startTime=[").append(start).append("] endTime=[").append(end)
					  .append("] name=[").append(getName()).append("] status=[").append(getStatus())
					  .append("] annotation=[").append(getAnnotation()).append("]").append("\n").toString();
	}
}

