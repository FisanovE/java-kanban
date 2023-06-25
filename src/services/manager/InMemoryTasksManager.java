package services.manager;

import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTasksManager implements TasksManager {
	private int counter = 1;
	private final Map<Integer, Task> taskStorage = new HashMap<>();
	private final Map<Integer, Epic> epicStorage = new HashMap<>();
	private final Map<Integer, SubTask> subTaskStorage = new HashMap<>();

	public HistoryManager historyManager = Managers.getDefaultHistory();

	@Override
	public int addNewTask(Task task) {
		if (task.getID() == 0) {
			task.setID(counter);
		} else {
			task.setID(task.getID());
		}
		task.setStatus(Status.NEW);
		taskStorage.put(task.getID(), task);
		counter += 1;
		return task.getID();
	}

	@Override
	public int addNewEpic(Epic epic) {
		if (epic.getID() == 0) {
			epic.setID(counter);
		} else {
			epic.setID(epic.getID());
		}
		epic.setStatus(Status.NEW);
		epicStorage.put(epic.getID(), epic);
		counter += 1;
		return epic.getID();
	}

	@Override
	public int addNewSubTask(SubTask sub) {
		if (sub.getID() == 0) {
			sub.setID(counter);
		} else {
			sub.setID(sub.getID());
		}
		sub.setStatus(Status.NEW);
		subTaskStorage.put(sub.getID(), sub);
		counter += 1;
		return sub.getID();
	}

	@Override
	public void updateTask(Task task) {
		taskStorage.put(task.getID(), task);
	}

	@Override
	public void updateEpic(Epic epic) {
		List<Status> statusValuesList = new ArrayList<>();
		for (SubTask subTask : subTaskStorage.values()) {
			if (subTask.getEpicID() == epic.getID()) {
				statusValuesList.add(subTask.getStatus());
			}
			if (statusValuesList.isEmpty() || !statusValuesList.contains(Status.IN_PROGRESS) && !statusValuesList.contains(Status.DONE)) {
				epic.setStatus(Status.NEW);
			} else if (!statusValuesList.contains(Status.IN_PROGRESS) && !statusValuesList.contains(Status.NEW)) {
				epic.setStatus(Status.DONE);
			} else {
				epic.setStatus(Status.IN_PROGRESS);
			}
		}
		epicStorage.put(epic.getID(), epic);
	}

	@Override
	public void updateSubTask(SubTask sub) {
		int key = sub.getID();
		subTaskStorage.put(key, sub);
		updateEpic(epicStorage.get(sub.getEpicID()));
	}

	@Override
	public Task getTaskByID(int ID) {
		if (taskStorage.containsKey(ID)) {
			historyManager.add(taskStorage.get(ID));
		} else {
			System.out.println("Задача с этим номером ID отсутствует");
		}
		return taskStorage.get(ID);
	}

	@Override
	public Epic getEpicByID(int ID) {
		if (epicStorage.containsKey(ID)) {
			historyManager.add(epicStorage.get(ID));
		} else {
			System.out.println("Эпик с этим номером ID отсутствует");
		}
		return epicStorage.get(ID);
	}

	@Override
	public SubTask getSubTaskByID(int ID) {
		if (subTaskStorage.containsKey(ID)) {
			historyManager.add(subTaskStorage.get(ID));
		} else {
			System.out.println("Подзадача с этим номером ID отсутствует");
		}
		return subTaskStorage.get(ID);
	}

	@Override
	public List<Task> getAllTasks() {
		return new ArrayList<>(taskStorage.values());
	}

	@Override
	public List<Epic> getAllEpics() {
		return new ArrayList<>(epicStorage.values());
	}

	@Override
	public List<SubTask> getAllSubTasks() {
		return new ArrayList<>(subTaskStorage.values());
	}

	@Override
	public List<SubTask> getAllSubTasksByEpic(int ID) {
		HashMap<Integer, SubTask> storage = new HashMap<>();
		if (epicStorage.containsKey(ID)) {
			for (SubTask subTask : subTaskStorage.values()) {
				if (subTask.getEpicID() == ID) {
					storage.put(subTask.getID(), subTask);
				}
			}
		} else {
			System.out.println("Эпик с этим номером ID отсутствует");
		}
		return new ArrayList<>(storage.values());
	}

	@Override
	public void removeTaskByID(int ID) {
		if (!taskStorage.containsKey(ID)) {
			System.out.println("Задача с этим номером ID отсутствует");
			return;
		}
		taskStorage.remove(ID);
		historyManager.remove(ID);
	}

	@Override
	public void removeEpicByID(int ID) {
		if (!epicStorage.containsKey(ID)) {
			System.out.println("Эпик с этим номером ID отсутствует");
			return;
		}
		List<Integer> subTaskIDList = new ArrayList<>();
		for (SubTask subTask : subTaskStorage.values()) {
			if (subTask.getEpicID() == ID) {
				subTaskIDList.add(subTask.getID());
			}
		}
		epicStorage.remove(ID);
		historyManager.remove(ID);
		for (int key : subTaskIDList) {
			removeSubTaskByID(key);
		}
	}

	@Override
	public void removeSubTaskByID(int ID) {
		if (!subTaskStorage.containsKey(ID)) {
			System.out.println("Подзадача с этим номером ID отсутствует");
			return;
		}
		subTaskStorage.remove(ID);
		historyManager.remove(ID);
	}

	@Override
	public void removeAllTasks() {
		for (Task task : taskStorage.values()) {
			historyManager.remove(task.getID());
		}
		taskStorage.clear();

	}

	@Override
	public void removeAllEpics() {
		for (Epic epic : epicStorage.values()) {
			historyManager.remove(epic.getID());
		}

		for (SubTask subTask : subTaskStorage.values()) {
			historyManager.remove(subTask.getID());
		}

		epicStorage.clear();
		subTaskStorage.clear();
	}

	@Override
	public void removeAllSubTasks(int ID) {
		for (SubTask subTask : subTaskStorage.values()) {
			historyManager.remove(subTask.getID());
		}
		subTaskStorage.clear();
	}

	public List<Task> getHistory() {
		return new ArrayList<>(historyManager.getHistory());
	}
}