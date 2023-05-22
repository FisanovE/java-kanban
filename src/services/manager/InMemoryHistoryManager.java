package services.manager;

import models.business.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
	private final List<Task> historyStorage = new ArrayList<>();

	@Override
	public void add(Task task) {
		if (task != null) {
			int numberOfLastTasks = 10;
			if (historyStorage.size() == numberOfLastTasks) {
				historyStorage.remove(0);
			}
			historyStorage.add(task);
		}
	}

	@Override
	public List<Task> getHistory() {
		return historyStorage;
	}
}
