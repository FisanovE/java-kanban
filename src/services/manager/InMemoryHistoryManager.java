package services.manager;

import models.business.Task;

import java.util.Arrays;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
	private int counterHistory = 0;
	private static List<Task> historyStorage = Arrays.asList(null, null, null, null, null, null, null, null, null, null);

	@Override
	public void add(Task task) {
		if (counterHistory == 10) {
			counterHistory = 0;
			historyStorage.set(counterHistory, task);
			counterHistory++;
		} else {
			historyStorage.set(counterHistory, task);
			counterHistory++;
		}
	}

	@Override
	public List<Task> getHistory() {
		return historyStorage;
	}
}
