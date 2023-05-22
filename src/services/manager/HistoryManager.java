package services.manager;

import models.business.Task;

import java.util.List;

public interface HistoryManager {

	void add(Task task);

	List<Task> getHistory();
}
