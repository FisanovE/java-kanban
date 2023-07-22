package services.manager.structure;

import models.business.Task;

import java.util.List;

public interface HistoryManager {

	void add(Task task);

	void removeTaskFromHistory(int id);

	List<Task> getHistory();
}
