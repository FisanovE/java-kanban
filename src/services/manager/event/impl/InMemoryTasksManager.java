package services.manager.event.impl;

import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;
import services.manager.event.HistoryManager;
import services.manager.utils.Managers;
import services.manager.event.TasksManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static services.manager.utils.DateUtils.formatter2;

public class InMemoryTasksManager implements TasksManager {
	private int counter = 1;
	private final Map<Integer, Task> taskStorage = new HashMap<>();
	private final Map<Integer, Epic> epicStorage = new HashMap<>();
	private final Map<Integer, SubTask> subTaskStorage = new HashMap<>();

	private final Comparator<Task> comparator = new Comparator<>() {
		@Override
		public int compare(Task task1, Task task2) {

			if (task1.getStartTime() != null && task2.getStartTime() != null) {
				if (task1.getStartTime().isAfter(task2.getStartTime())) {
					return 1;
				} else if (task1.getStartTime().isBefore(task2.getStartTime())) {
					return -1;
				} else {
					return 0;
				}
			} else if (task1.getStartTime() != null) {
				return -1;
			} else if (task2.getStartTime() != null) {
				return 1;
			} else if (task1.getID() != task2.getID()) {
				return task1.getID() - task2.getID();
			} else {
				return 0;
			}

		}
	};

	private final TreeSet<Task> tasksSortedByTime = new TreeSet<>(comparator);
	private final Set<Task> set = new TreeSet<>(tasksSortedByTime);

	protected final HistoryManager historyManager = Managers.getDefaultHistory();


	@Override
	public int addNewTask(Task task) {
		if (!taskStorage.isEmpty()) {
			for (Task sortedTask : taskStorage.values()) {
				if (task.getStartTime() != null && sortedTask.getStartTime() != null) {
					if (task.getStartTime().isAfter(sortedTask.getStartTime()) && task.getStartTime()
																					  .isBefore(sortedTask.getEndTime())) {
						System.out.println("Задача \"" + task.getName() + "\" не создана. В промежуток времени с " + sortedTask.getStartTime()
																															   .format(formatter2) + " по " + sortedTask.getEndTime()
																																										.format(formatter2) + " уже есть другая задача.");
						return 0;
					}
				}

			}
		}

		if (task.getID() == 0) {
			task.setID(counter);
		} else {
			task.setID(task.getID());
		}
		task.setStatus(Status.NEW);

		taskStorage.put(task.getID(), task);
		tasksSortedByTime.remove(task);
		tasksSortedByTime.add(task);
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
		if (epic.getStartTime() != null) {
			epic.setEndTime(epic.getStartTime().plus(Duration.ofMinutes(epic.getDuration())));
		}

		epicStorage.put(epic.getID(), epic);
		counter += 1;
		return epic.getID();
	}

	@Override
	public int addNewSubTask(SubTask sub) {
		if (!subTaskStorage.isEmpty()) {
			for (Task sortedTask : subTaskStorage.values()) {
				if (sub.getStartTime() != null && sortedTask.getStartTime() != null) {
					if (sub.getStartTime().isAfter(sortedTask.getStartTime()) && sub.getStartTime()
																					.isBefore(sortedTask.getEndTime())) {
						System.out.println("Подзадача \"" + sub.getName() + "\" не создана. В промежуток времени с " + sortedTask.getStartTime()
																																 .format(formatter2) + " по " + sortedTask.getEndTime()
																																										  .format(formatter2) + " уже есть другая подзадача.");
						return 0;
					}
				}

			}
		}
		if (sub.getID() == 0) {
			sub.setID(counter);
		} else {
			sub.setID(sub.getID());
		}
		sub.setStatus(Status.NEW);
		if (epicStorage.containsKey(sub.getEpicID())) {
			subTaskStorage.put(sub.getID(), sub);
			tasksSortedByTime.remove(sub);
			tasksSortedByTime.add(sub);
			counter += 1;
			updateEpic(epicStorage.get(sub.getEpicID()));
		} else {
			System.out.println("Эпик с этим номером ID отсутствует.");
		}

		return sub.getID();
	}

	@Override
	public void updateTask(Task task) {
		if (!taskStorage.isEmpty()) {
			for (Task sortedTask : taskStorage.values()) {
				if (task.getStartTime() != null && sortedTask.getStartTime() != null) {
					if (task.getStartTime().isAfter(sortedTask.getStartTime()) && task.getStartTime()
																					  .isBefore(sortedTask.getEndTime())) {
						System.out.println("Задача \"" + task.getName() + "\" не создана. В промежуток времени с " + sortedTask.getStartTime()
																															   .format(formatter2) + " по " + sortedTask.getEndTime()
																																										.format(formatter2) + " уже есть другая задача.");
						return;
					}
				}

			}
		}
		if (taskStorage.containsKey(task.getID())) {
			tasksSortedByTime.remove(taskStorage.get(task.getID()));
			tasksSortedByTime.add(task);
			taskStorage.put(task.getID(), task);
		} else {
			System.out.println("Задача с этим номером ID отсутствует.");
		}
	}

	@Override
	public void updateEpic(Epic epic) {

		List<Status> statusValuesList = new ArrayList<>();
		LocalDateTime start = null;
		LocalDateTime end = null;
		long duration = 0;
		for (SubTask subTask : subTaskStorage.values()) {
			if (subTask.getEpicID() == epic.getID()) {
				statusValuesList.add(subTask.getStatus());
				duration += subTask.getDuration();

				if (start == null) {
					start = subTask.getStartTime();
				} else if (start.isAfter(subTask.getStartTime())) {
					start = subTask.getStartTime();
				}

				if (end == null) {
					end = subTask.getEndTime();
				} else if (end.isBefore(subTask.getEndTime())) {
					end = subTask.getEndTime();
				}
			}
			if (statusValuesList.isEmpty() || !statusValuesList.contains(Status.IN_PROGRESS) && !statusValuesList.contains(Status.DONE)) {
				epic.setStatus(Status.NEW);
			} else if (!statusValuesList.contains(Status.IN_PROGRESS) && !statusValuesList.contains(Status.NEW)) {
				epic.setStatus(Status.DONE);
			} else {
				epic.setStatus(Status.IN_PROGRESS);
			}
		}


		if (epicStorage.containsKey(epic.getID())) {
			epic.setStartTime(start);
			epic.setEndTime(end);
			epic.setDuration(duration);
			epicStorage.put(epic.getID(), epic);
		} else {
			System.out.println("Эпик с этим номером ID отсутствует.");
		}
	}

	@Override
	public void updateSubTask(SubTask sub) {
		if (!subTaskStorage.isEmpty()) {
			for (Task sortedTask : subTaskStorage.values()) {
				if (sub.getStartTime() != null && sortedTask.getStartTime() != null) {
					if (sub.getStartTime().isAfter(sortedTask.getStartTime()) && sub.getStartTime()
																					.isBefore(sortedTask.getEndTime())) {
						System.out.println("Подзадача \"" + sub.getName() + "\" не создана. В промежуток времени с " + sortedTask.getStartTime()
																																 .format(formatter2) + " по " + sortedTask.getEndTime()
																																										  .format(formatter2) + " уже есть другая подзадача.");
						return;
					}
				}

			}
		}
		int key = sub.getID();
		if (subTaskStorage.containsKey(sub.getID())) {
			tasksSortedByTime.remove(subTaskStorage.get(sub.getID()));
			tasksSortedByTime.add(sub);
			subTaskStorage.put(key, sub);
			updateEpic(epicStorage.get(sub.getEpicID()));
		} else {
			System.out.println("Подзадача с этим номером ID отсутствует.");
		}

	}

	@Override
	public Task getTaskByID(int ID) {
		if (taskStorage.containsKey(ID)) {
			historyManager.add(taskStorage.get(ID));
		} else {
			System.out.println("Задача с этим номером ID отсутствует.");
		}
		return taskStorage.get(ID);
	}

	@Override
	public Epic getEpicByID(int ID) {
		if (epicStorage.containsKey(ID)) {
			historyManager.add(epicStorage.get(ID));
		} else {
			System.out.println("Эпик с этим номером ID отсутствует.");
		}
		return epicStorage.get(ID);
	}

	@Override
	public SubTask getSubTaskByID(int ID) {
		if (subTaskStorage.containsKey(ID)) {
			historyManager.add(subTaskStorage.get(ID));
		} else {
			System.out.println("Подзадача с этим номером ID отсутствует.");
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
			System.out.println("Эпик с этим номером ID отсутствует.");
		}
		return new ArrayList<>(storage.values());
	}

	@Override
	public void removeTaskByID(int ID) {
		if (!taskStorage.containsKey(ID)) {
			System.out.println("Задача с этим номером ID отсутствует.");
			return;
		}
		tasksSortedByTime.remove(taskStorage.get(ID));
		taskStorage.remove(ID);
		historyManager.removeTaskFromHistory(ID);
	}

	@Override
	public void removeEpicByID(int ID) {
		if (!epicStorage.containsKey(ID)) {
			System.out.println("Эпик с этим номером ID отсутствует.");
			return;
		}
		List<Integer> subTaskIDList = new ArrayList<>();
		for (SubTask subTask : subTaskStorage.values()) {
			if (subTask.getEpicID() == ID) {
				subTaskIDList.add(subTask.getID());
			}
		}

		epicStorage.remove(ID);
		historyManager.removeTaskFromHistory(ID);

		for (int key : subTaskIDList) {
			SubTask subTask = subTaskStorage.get(key);
			tasksSortedByTime.remove(subTask);
			removeSubTaskByID(key);

		}
	}

	@Override
	public void removeSubTaskByID(int ID) {
		if (!subTaskStorage.containsKey(ID)) {
			System.out.println("Подзадача с этим номером ID отсутствует.");
			return;
		}

		subTaskStorage.remove(ID);
		historyManager.removeTaskFromHistory(ID);

	}

	@Override
	public void removeAllTasks() {
		for (Task task : taskStorage.values()) {
			historyManager.removeTaskFromHistory(task.getID());
			tasksSortedByTime.remove(task);
		}
		taskStorage.clear();

	}

	@Override
	public void removeAllEpics() {
		for (Epic epic : epicStorage.values()) {
			historyManager.removeTaskFromHistory(epic.getID());
			//tasksSortedByTime.remove(epic);
		}

		for (SubTask subTask : subTaskStorage.values()) {
			historyManager.removeTaskFromHistory(subTask.getID());
			tasksSortedByTime.remove(subTask);
		}

		epicStorage.clear();
		subTaskStorage.clear();
	}

	@Override
	public void removeAllSubTasks(int ID) {
		for (SubTask subTask : subTaskStorage.values()) {
			historyManager.removeTaskFromHistory(subTask.getID());
			tasksSortedByTime.remove(subTask);
		}
		subTaskStorage.clear();
	}

	public List<Task> getHistory() {
		return new ArrayList<>(historyManager.getHistory());
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getCounter() {
		return counter;
	}

	public TreeSet<Task> getPrioritizedTasks() {
		return tasksSortedByTime;
	}

}

