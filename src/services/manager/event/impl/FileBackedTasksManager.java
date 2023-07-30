package services.manager.event.impl;

import services.manager.exceptions.ManagerSaveException;
import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;
import models.enums.TypesTasks;
import services.manager.event.HistoryManager;
import services.manager.event.TasksManager;
import services.manager.utils.DateUtils;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTasksManager implements TasksManager {

	static String path;


	public FileBackedTasksManager(String path) {
		this.path = path;
	}

	public static void main(String[] args) throws ManagerSaveException{

//Тестирование по ТЗ ФП-6

//Заведите несколько разных задач, эпиков и подзадач.
		TasksManager taskManager = new FileBackedTasksManager("resources/savedData.csv");


		Task task1 = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000"
				, DateUtils.formatter), 60L);

		Task taskUpdate = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00" + ".000000000", DateUtils.formatter), 60L);

		Epic epicUpdate = new Epic(12, "Выучить алфавит", "Выучить несколько букв алфавита", Status.IN_PROGRESS,
				LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", DateUtils.formatter), 120L);

		Task task0 = new Task("БухнУть!", "Злоупотребить спиртным", LocalDateTime.parse("2023-07-15T08:30:00.000000000", DateUtils.formatter), 20L);

		Task task2 = new Task("Покупки", "Купить продукты в магазине");

		Epic epic1 = new Epic("Выучить алфавит", "Выучить несколько букв алфавита");

		SubTask subTask1 = new SubTask("Выучить А", "Выучить букву А", LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", DateUtils.formatter), 120L, 3);

		SubTask subTask2 = new SubTask("Выучить Б", "Выучить букву Б", LocalDateTime.parse("2023-07-15T11:40:00.000000000", DateUtils.formatter), 360L, 3);

		SubTask subTask3 = new SubTask("Выучить В", "Выучить букву В", LocalDateTime.parse("2023-07" + "-15T17:40:00.000000000", DateUtils.formatter), 600L, 3);

		Epic epic2 = new Epic("Отдохнуть", "Отдохнуть от дел праведных");

		System.out.println();
		taskManager.addNewTask(task1);
		taskManager.addNewTask(task0);
		taskManager.addNewTask(task2);
		taskManager.addNewEpic(epic1);
		taskManager.addNewEpic(epic2);
		taskManager.addNewSubTask(subTask1);
		taskManager.addNewSubTask(subTask2);
		taskManager.addNewSubTask(subTask3);
		System.out.println();

//Запросите некоторые из них, чтобы заполнилась история просмотра.


		System.out.print("Просмотр задачи: " + taskManager.getEpicByID(3));
		System.out.print("Просмотр задачи: " + taskManager.getEpicByID(4));
		System.out.print("Просмотр задачи: " + taskManager.getTaskByID(1));
		System.out.print("Просмотр задачи: " + taskManager.getTaskByID(2));
		System.out.print("Просмотр задачи: " + taskManager.getSubTaskByID(6));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(7));

		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач:\n" + taskManager.getHistory());
		System.out.println("Задачи по приоритету: \n" + taskManager.getPrioritizedTasks());

		System.out.print("Просмотр задачи: " + taskManager.getEpicByID(4));
		System.out.print("Просмотр задачи: " + taskManager.getEpicByID(3));
		System.out.print("Просмотр задачи: " + taskManager.getTaskByID(1));
		System.out.print("Просмотр задачи: " + taskManager.getSubTaskByID(7));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(5));

		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач: \n" + taskManager.getHistory());
		System.out.println("Задачи по приоритету: \n" + taskManager.getPrioritizedTasks());

		System.out.println("Удалена задача 2");
		taskManager.removeTaskByID(2);
		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач: \n" + taskManager.getHistory());
		System.out.println("Задачи по приоритету: \n" + taskManager.getPrioritizedTasks());

		System.out.println("Удален эпик 3");
		taskManager.removeEpicByID(3);
		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач: \n" + taskManager.getHistory());
		System.out.println("Задачи по приоритету: \n" + taskManager.getPrioritizedTasks());

		//taskManager.updateEpic(epicUpdate);


//Создайте новый FileBackedTasksManager менеджер из этого же файла.
//роверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи, которые были в старом, есть в
// новом менеджере.
		FileBackedTasksManager fileBackedTasksManager = ((FileBackedTasksManager) taskManager).load(new File(path));
	}

	public void save() {
		try (FileWriter fw = new FileWriter(new File(path)); BufferedWriter writer = new BufferedWriter(fw)) {
			writer.write("id,type,name,status,description,startTime,duration,epic" + "\n");
			for (Task task : super.getAllTasks()) {
				writer.write(toString(task));
			}
			for (Epic epic : super.getAllEpics()) {
				writer.write(toString(epic));
			}
			for (SubTask subTask : super.getAllSubTasks()) {
				writer.write(toString(subTask));
			}
			writer.write(" " + "\n");
			writer.write(historyToString(super.historyManager));
		} catch (IOException e) {
			throw new ManagerSaveException("Произошла ошибка при записи файла");
		}
	}

	public static FileBackedTasksManager load(File file) {
		try {


		FileBackedTasksManager manager = new FileBackedTasksManager("resources/savedData.csv");
		String value = Files.readString(file.toPath());
		if (value.isEmpty()) {
			return manager;
		}
		String[] allData = value.split(" \n");
		String[] tasksOfString = allData[0].split("\n");
		Map<Integer, Task> tasksMap = new HashMap<>();
		int counterRestore = 0;
		for (String task : tasksOfString) {
			if (task.contains("description")) {
				continue;
			}
			Task sortedTask = manager.fromString(task);
			if (sortedTask instanceof Epic) {
				manager.addNewEpic((Epic) sortedTask);
			} else if (sortedTask instanceof SubTask) {
				manager.addNewSubTask((SubTask) sortedTask);
			} else {
				manager.addNewTask(sortedTask);
			}
			tasksMap.put(sortedTask.getID(), sortedTask);
			if (sortedTask.getID() > counterRestore) {
				counterRestore = sortedTask.getID();
			}
		}
		if (allData.length > 1) {
			List<Integer> tasksID = historyFromString(allData[1]);
			Collections.reverse(tasksID);
			for (Integer ID : tasksID) {
				manager.historyManager.add(tasksMap.get(ID));
			}
		}

		manager.setCounter(counterRestore + 1);
		manager.save();
		return manager;
		} catch (IOException e) {
			throw new ManagerSaveException("Произошла ошибка при записи файла");
		}
	}

	public String toString(Task task) {
		StringBuilder builder = new StringBuilder();
		if (task instanceof SubTask) {
			SubTask subTask = (SubTask) task;
			builder.append(subTask.getID()).append(",").append(TypesTasks.SUBTASK).append(",").append(subTask.getName())
				   .append(",").append(subTask.getStatus()).append(",").append(subTask.getAnnotation()).append(",")
				   .append(subTask.getStartTime()).append(",").append(subTask.getDuration()).append(",")
				   .append(subTask.getEpicID()).append("\n");
		} else if (task instanceof Epic) {
			builder.append(task.getID()).append(",").append(TypesTasks.EPIC).append(",").append(task.getName())
				   .append(",").append(task.getStatus()).append(",").append(task.getAnnotation()).append(",")
				   .append(task.getStartTime()).append(",").append(task.getDuration()).append(",").append("\n");
		} else {
			builder.append(task.getID()).append(",").append(TypesTasks.TASK).append(",").append(task.getName())
				   .append(",").append(task.getStatus()).append(",").append(task.getAnnotation()).append(",")
				   .append(task.getStartTime()).append(",").append(task.getDuration()).append(",").append("\n");
		}
		return builder.toString();
	}

	public static Task fromString(String value) {
		String[] split = value.split(",");
		if (split[1].equals("SUBTASK")) {
			LocalDateTime time = split[5].equals("null") ? null : LocalDateTime.parse((split[5]));
			return new SubTask(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]), time, Long.parseLong(split[6]), Integer.parseInt(split[7]));
		} else if (split[1].equals("EPIC")) {
			LocalDateTime time = split[5].equals("null") ? null : LocalDateTime.parse((split[5]));
			return new Epic(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]), time, Long.parseLong(split[6]));
		}
		LocalDateTime time = split[5].equals("null") ? null : LocalDateTime.parse((split[5]));
		return new Task(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]), time, Long.parseLong(split[6]));
	}

	public static String historyToString(HistoryManager manager) {
		List<Task> list = new ArrayList<>(manager.getHistory());
		String result = "";
		for (Task task : list) {
			result = result + task.getID() + ",";
		}
		return result;
	}

	public static List<Integer> historyFromString(String value) {
		String[] split = value.split(",");
		List<Integer> tasksID = new ArrayList<>();
		for (String ID : split) {
			tasksID.add(Integer.parseInt(ID));
		}
		return tasksID;
	}

	@Override
	public Task getTaskByID(int ID) {
		Task task = super.getTaskByID(ID);
		save();
		return task;
	}

	@Override
	public Epic getEpicByID(int ID) {
		Epic epic = super.getEpicByID(ID);
		save();
		return epic;
	}

	@Override
	public SubTask getSubTaskByID(int ID) {
		SubTask subTask = super.getSubTaskByID(ID);
		save();
		return subTask;
	}

	@Override
	public int addNewTask(Task task) {
		int ID = super.addNewTask(task);
		save();
		return ID;
	}

	@Override
	public int addNewEpic(Epic epic) {
		int ID = super.addNewEpic(epic);
		save();
		return ID;
	}

	@Override
	public int addNewSubTask(SubTask sub) {
		int ID = super.addNewSubTask(sub);
		save();
		return ID;
	}

	@Override
	public void updateTask(Task task) {
		super.updateTask(task);
		save();
	}

	@Override
	public void updateEpic(Epic epic) {
		super.updateEpic(epic);
		save();
	}

	@Override
	public void updateSubTask(SubTask sub) {
		super.updateSubTask(sub);
		save();
	}

	@Override
	public void removeTaskByID(int ID) {
		super.removeTaskByID(ID);
		save();
	}

	@Override
	public void removeEpicByID(int ID) {
		super.removeEpicByID(ID);
		save();
	}

	@Override
	public void removeSubTaskByID(int ID) {
		super.removeSubTaskByID(ID);
		save();
	}

	@Override
	public void removeAllTasks() {
		super.removeAllTasks();
		save();
	}

	@Override
	public void removeAllEpics() {
		super.removeAllEpics();
		save();
	}

	@Override
	public void removeAllSubTasks(int ID) {
		super.removeAllSubTasks(ID);
		save();
	}
}
