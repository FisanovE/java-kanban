import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;
import services.manager.structure.Managers;
import services.manager.structure.TasksManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
	public static void main(String[] args) {

//Тестирование по ТЗ ФП-5

		TasksManager taskManager = Managers.getDefault();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]");

//Создайте 2 задачи, один эпик с 3 подзадачами и эпик без подзадач.

		Task task1 = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000"
				, formatter), 60L);

		Task taskUpdate = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00" + ".000000000", formatter), 60L);

		Epic epicUpdate = new Epic(12, "Выучить алфавит", "Выучить несколько букв алфавита", Status.IN_PROGRESS, LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000",formatter), 120L);

		Task task0 = new Task("БухнУть!", "Злоупотребить спиртным", LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 20L);

		Task task2 = new Task("Покупки", "Купить продукты в магазине");

		Epic epic1 = new Epic("Выучить алфавит", "Выучить несколько букв алфавита");

		SubTask subTask1 = new SubTask("Выучить А", "Выучить букву А", LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", formatter), 120L, 3);

		SubTask subTask2 = new SubTask("Выучить Б", "Выучить букву Б", LocalDateTime.parse("2023-07-15T11:40:00.000000000", formatter), 360L, 3);

		SubTask subTask3 = new SubTask("Выучить В", "Выучить букву В", LocalDateTime.parse("2023-07" + "-15T17:40:00.000000000", formatter), 600L, 3);

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

		taskManager.updateEpic(epicUpdate);

	}
}