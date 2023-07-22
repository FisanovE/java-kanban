package services.manager.structure;

import services.manager.work.InMemoryHistoryManager;
import services.manager.work.InMemoryTasksManager;

public class Managers {

	public static TasksManager getDefault() {
		return new InMemoryTasksManager();
	}

	public static HistoryManager getDefaultHistory() {
		return new InMemoryHistoryManager();
	}
}
