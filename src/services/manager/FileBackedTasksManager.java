package services.manager;

import exceptions.ManagerSaveException;
import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;
import models.enums.TypesTasks;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTasksManager implements TasksManager {

	static File savedData;

	public FileBackedTasksManager(File savedData) {
		this.savedData = savedData;
	}

	public static void main(String[] args) throws ManagerSaveException, IOException {

//Тестирование по ТЗ ФП-6

//Заведите несколько разных задач, эпиков и подзадач.
		TasksManager taskManager = new FileBackedTasksManager(new File("resources/savedData.csv"));

		Task task1 = new Task("Уборка", "Сделать уборку в кухне");
		Task task2 = new Task("Покупки", "Купить продукты в магазине");

		Epic epic1 = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон");

		SubTask subTask1 = new SubTask("Купить компоненты", "Купить компоненты для бетона", 3);
		SubTask subTask2 = new SubTask("Залить бетон", "Залить бетон из купленных компонентов", 3);
		SubTask subTask3 = new SubTask("Начать отдыхать", "Забросить всё и начать отдыхать", 3);

		Epic epic2 = new Epic("Отдохнуть", "Отдохнуть от дел праведных");

		taskManager.addNewTask(task1);
		taskManager.addNewTask(task2);
		taskManager.addNewEpic(epic1);
		taskManager.addNewEpic(epic2);
		taskManager.addNewSubTask(subTask1);
		taskManager.addNewSubTask(subTask2);
		taskManager.addNewSubTask(subTask3);

//Запросите некоторые из них, чтобы заполнилась история просмотра.

		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(3));
		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(4));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(1));
		//System.out.println("Просмотр задачи: " + taskManager.getTaskByID(2));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(6));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(7));

		System.out.println("Последние задачи: " + taskManager.getHistory());

		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(4));
		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(3));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(1));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(7));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(5));

		System.out.println("Последние задачи: " + taskManager.getHistory());

		System.out.println("Удалена задача 2");
		taskManager.removeTaskByID(2);
		System.out.println("Последние задачи: " + taskManager.getHistory());

		System.out.println("Удален эпик 1");
		taskManager.removeEpicByID(3);
		System.out.println("Последние задачи: " + taskManager.getHistory());

//Создайте новый FileBackedTasksManager менеджер из этого же файла.
//роверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи, которые были в старом, есть в
// новом менеджере.
		FileBackedTasksManager fileBackedTasksManager = loadFromFile(new File("resources/savedData.csv"));
	}

	public void save() throws ManagerSaveException {
		try {
			FileWriter fw = new FileWriter(savedData);
			BufferedWriter writer = new BufferedWriter(fw);
			writer.write("id,type,name,status,description,epic" + "\n");
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
			writer.close();
		} catch (IOException e) {
			System.out.println("Произошла ошибка во время записи файла.");
		}
	}

	public static FileBackedTasksManager loadFromFile(File file) throws IOException {
		FileBackedTasksManager manager = new FileBackedTasksManager(file);
		String value = Files.readString(file.toPath());
		String[] allData = value.split(" \n");
		String[] tasksOfString = allData[0].split("\n");
		List<Integer> tasksID = historyFromString(allData[1]);
		Collections.reverse(tasksID);
		Map<Integer, Task> tasksMap = new HashMap<>();
		//List<Task> tasks = new ArrayList<>();
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
		}
		for (Integer key : tasksID) {
			manager.historyManager.add(tasksMap.get(key));
		}
		manager.save();
		return manager;
	}

	public String toString(Task task) {
		String result = "";
		if (task instanceof SubTask) {
			SubTask subTask = (SubTask) task;
			result = subTask.getID() + "," + TypesTasks.SUBTASK + "," + subTask.getName() + "," + subTask.getStatus() + "," + subTask.getAnnotation() + "," + subTask.getEpicID() + "\n";
		} else if (task instanceof Epic) {
			result = task.getID() + "," + TypesTasks.EPIC + "," + task.getName() + "," + task.getStatus() + "," + task.getAnnotation() + "\n";
		} else {
			result = task.getID() + "," + TypesTasks.TASK + "," + task.getName() + "," + task.getStatus() + "," + task.getAnnotation() + "\n";
		}
		return result;
	}

	public static Task fromString(String value) {
		String[] split = value.split(",");
		if (split[1].equals("SUBTASK")) {
			return new SubTask(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]), Integer.parseInt(split[5]));
		} else if (split[1].equals("EPIC")) {
			return new Epic(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]));
		}
		return new Task(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]));
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
