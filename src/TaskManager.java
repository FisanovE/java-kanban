import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
	public int Counter = 1;

	HashMap<Integer, Task> taskStorage = new HashMap<>();
	HashMap<Integer, Epic> epicStorage = new HashMap<>();
	HashMap<Integer, SubTask> subTaskStorage = new HashMap<>();

	public int addNewTask(Task task) {
		task.setID(Counter);
		taskStorage.put(task.getID(), task);
		Counter += 1;
		return task.getID();
	}

	public int addNewEpicTask(Epic epic) {
		epic.setID(Counter);
		epic.setStatus("NEW");
		epicStorage.put(epic.getID(), epic);
		Counter += 1;
		return epic.getID();
	}

	public int addNewSubTask(SubTask sub) {
		sub.setID(Counter);
		subTaskStorage.put(sub.getID(), sub);
		Counter += 1;
		return sub.getID();
	}

	public void updateTask(Task task) {
		taskStorage.put(task.getID(), task);
	}

	public void updateEpic(Epic epic) {
		ArrayList<String> statusValuesList = new ArrayList<>();
		for (SubTask subTask : subTaskStorage.values()) {
			if (subTask.getEpicID() == epic.getID()) {
				statusValuesList.add(subTask.getStatus());
			}
			if (statusValuesList.isEmpty() || !statusValuesList.contains("IN_PROGRESS") && !statusValuesList.contains("DONE")) {
				epic.setStatus("NEW");
			} else if (!statusValuesList.contains("IN_PROGRESS") && !statusValuesList.contains("NEW")) {
				epic.setStatus("DONE");
			} else {
				epic.setStatus("IN_PROGRESS");
			}
		}
		epicStorage.put(epic.getID(), epic);
	}

	public void updateSubTask(SubTask sub) {
		int key = sub.getID();
		subTaskStorage.put(key, sub);
		updateEpic(epicStorage.get(sub.getEpicID()));
	}

	public Task getTaskByID(int ID) {
		return taskStorage.get(ID);
	}

	public Epic getEpicByID(int ID) {
		return epicStorage.get(ID);
	}

	public SubTask getSubTaskByID(int ID) {
		return subTaskStorage.get(ID);
	}

	public HashMap<Integer, Task> getAllTasks() {
		return taskStorage;
	}

	public HashMap<Integer, Epic> getAllEpics() {
		return epicStorage;
	}

	public HashMap<Integer, SubTask> getAllSubTasks() {
		return subTaskStorage;
	}

	public HashMap<Integer, SubTask> getAllSubTasksByEpic(int ID) {
		HashMap<Integer, SubTask> storage = new HashMap<>();
		for (SubTask subTask : subTaskStorage.values()) {
			if (subTask.getEpicID() == ID) {
				storage.put(subTask.getID(), subTask);
			}
		}
		return storage;
	}

	public void removeTaskByID(int ID) {
		taskStorage.remove(ID);
	}

	public void removeEpicByID(int ID) {
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

		public void removeSubTaskByID ( int ID){
			subTaskStorage.remove(ID);
		}

		public void removeAllTasks () {
			taskStorage.clear();
		}

		public void removeAllEpics () {
			epicStorage.clear();
			subTaskStorage.clear();
		}

		public void removeAllSubTasks ( int ID){
			subTaskStorage.clear();
		}
	}