package services.manager;

import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
	private int counter = 1;
	private HashMap<Integer, Task> taskStorage = new HashMap<>();
	private HashMap<Integer, Epic> epicStorage = new HashMap<>();
	private HashMap<Integer, SubTask> subTaskStorage = new HashMap<>();

	HistoryManager historyManager = Managers.getDefaultHistory();

	@Override
	public int addNewTask(Task task) {
		task.setID(counter);
		taskStorage.put(task.getID(), task);
		counter += 1;
		return task.getID();
	}

	@Override
	public int addNewEpicTask(Epic epic) {
		epic.setID(counter);
		epic.setStatus(Status.NEW);
		epicStorage.put(epic.getID(), epic);
		counter += 1;
		return epic.getID();
	}

	@Override
	public int addNewSubTask(SubTask sub) {
		sub.setID(counter);
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
		ArrayList<Status> statusValuesList = new ArrayList<>();
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
		if (!taskStorage.containsKey(ID)) {
			System.out.println("Задача с этим номером ID отсутствует");
		}
		historyManager.add(taskStorage.get(ID));
		return taskStorage.get(ID);
	}

	@Override
	public Epic getEpicByID(int ID) {
		if (!epicStorage.containsKey(ID)) {
			System.out.println("Эпик с этим номером ID отсутствует");
		}

		historyManager.add(epicStorage.get(ID));
		return epicStorage.get(ID);
	}

	@Override
	public SubTask getSubTaskByID(int ID) {
		if (!subTaskStorage.containsKey(ID)) {
			System.out.println("Подзадача с этим номером ID отсутствует");
		}
		historyManager.add(subTaskStorage.get(ID));
		return subTaskStorage.get(ID);
	}

	@Override
	public ArrayList<Task> getAllTasks() {
		return new ArrayList<Task>(taskStorage.values());
	}

	@Override
	public ArrayList<Epic> getAllEpics() {
		return new ArrayList<Epic>(epicStorage.values());
	}

	@Override
	public ArrayList<SubTask> getAllSubTasks() {
		return new ArrayList<SubTask>(subTaskStorage.values());
	}

	@Override
	public ArrayList<SubTask> getAllSubTasksByEpic(int ID) {
		if (!epicStorage.containsKey(ID)) {
			System.out.println("Эпик с этим номером ID отсутствует");
		}
		HashMap<Integer, SubTask> storage = new HashMap<>();
		for (SubTask subTask : subTaskStorage.values()) {
			if (subTask.getEpicID() == ID) {
				storage.put(subTask.getID(), subTask);
			}
		}
		return new ArrayList<SubTask>(storage.values());
	}

	@Override
	public void removeTaskByID(int ID) {
		if (!taskStorage.containsKey(ID)) {
			System.out.println("Задача с этим номером ID отсутствует");
		}
		taskStorage.remove(ID);
	}

	@Override
	public void removeEpicByID(int ID) {
		if (!epicStorage.containsKey(ID)) {
			System.out.println("Задача с этим номером ID отсутствует");
		}
		ArrayList<Integer> subTaskIDList = new ArrayList<>();
		for (SubTask subTask : subTaskStorage.values()) {
			if (subTask.getEpicID() == ID) {
				subTaskIDList.add(subTask.getID());
			}
		}
		epicStorage.remove(ID);
		for (int key : subTaskIDList) {
			subTaskStorage.remove(key);
		}
	}

	@Override
	public void removeSubTaskByID(int ID) {
		if (!subTaskStorage.containsKey(ID)) {
			System.out.println("Задача с этим номером ID отсутствует");
		}
		subTaskStorage.remove(ID);
	}

	@Override
	public void removeAllTasks() {
		taskStorage.clear();
	}

	@Override
	public void removeAllEpics() {
		epicStorage.clear();
		subTaskStorage.clear();
	}

	@Override
	public void removeAllSubTasks(int ID) {
		subTaskStorage.clear();
	}


}