import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;
import server.HttpTaskServer;
import server.KVServer;
import services.manager.event.TasksManager;
import services.manager.utils.Managers;
import services.manager.utils.DateUtils;
import java.io.IOException;
import java.time.LocalDateTime;


public class Main {
	static HttpTaskServer httpServer;

	static KVServer kvServer;

	public static void main(String[] args) throws IOException, InterruptedException {
		kvServer = new KVServer();
		kvServer.start();
		httpServer = new HttpTaskServer();
		httpServer.start();

//Тестирование по ТЗ ФП-5

		//TasksManager taskManager = Managers.getDefaultHttpManager("http://localhost:8080");


//Создайте 2 задачи, один эпик с 3 подзадачами и эпик без подзадач.

		Task task1 = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000"
				, DateUtils.formatter), 60L);

		Task taskUpdate = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00" + ".000000000", DateUtils.formatter), 60L);

		Epic epicUpdate = new Epic(12, "Выучить алфавит", "Выучить несколько букв алфавита", Status.IN_PROGRESS, LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000",DateUtils.formatter), 120L);

		Task task0 = new Task("БухнУть!", "Злоупотребить спиртным", LocalDateTime.parse("2023-07-15T08:30:00.000000000", DateUtils.formatter), 20L);

		Task task2 = new Task("Покупки", "Купить продукты в магазине");

		Epic epic1 = new Epic("Выучить алфавит", "Выучить несколько букв алфавита");

		SubTask subTask1 = new SubTask("Выучить А", "Выучить букву А", LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", DateUtils.formatter), 120L, 3);

		SubTask subTask2 = new SubTask("Выучить Б", "Выучить букву Б", LocalDateTime.parse("2023-07-15T11:40:00.000000000", DateUtils.formatter), 360L, 3);

		SubTask subTask3 = new SubTask("Выучить В", "Выучить букву В", LocalDateTime.parse("2023-07" + "-15T17:40:00.000000000", DateUtils.formatter), 600L, 3);

		Epic epic2 = new Epic("Отдохнуть", "Отдохнуть от дел праведных");

		System.out.println();
		httpServer.manager.addNewTask(task1);
		httpServer.manager.addNewTask(task0);
		httpServer.manager.addNewTask(task2);
		httpServer.manager.addNewEpic(epic1);
		httpServer.manager.addNewEpic(epic2);
		httpServer.manager.addNewSubTask(subTask1);
		httpServer.manager.addNewSubTask(subTask2);
		httpServer.manager.addNewSubTask(subTask3);
		System.out.println();

//Запросите некоторые из них, чтобы заполнилась история просмотра.


		System.out.print("Просмотр задачи: " + httpServer.manager.getEpicByID(3));
		System.out.print("Просмотр задачи: " + httpServer.manager.getEpicByID(4));
		System.out.print("Просмотр задачи: " + httpServer.manager.getTaskByID(1));
		System.out.print("Просмотр задачи: " + httpServer.manager.getTaskByID(2));
		System.out.print("Просмотр задачи: " + httpServer.manager.getSubTaskByID(6));
		System.out.println("Просмотр задачи: " + httpServer.manager.getSubTaskByID(7));

		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач:\n" + httpServer.manager.getHistory());
		System.out.println("Задачи по приоритету: \n" + httpServer.manager.getPrioritizedTasks());

		System.out.print("Просмотр задачи: " + httpServer.manager.getEpicByID(4));
		System.out.print("Просмотр задачи: " + httpServer.manager.getEpicByID(3));
		System.out.print("Просмотр задачи: " + httpServer.manager.getTaskByID(1));
		System.out.print("Просмотр задачи: " + httpServer.manager.getSubTaskByID(7));
		System.out.println("Просмотр задачи: " + httpServer.manager.getSubTaskByID(5));

		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач: \n" + httpServer.manager.getHistory());
		System.out.println("Задачи по приоритету: \n" + httpServer.manager.getPrioritizedTasks());

		System.out.println("Удалена задача 2");
		httpServer.manager.removeTaskByID(2);
		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач: \n" + httpServer.manager.getHistory());
		System.out.println("Задачи по приоритету: \n" + httpServer.manager.getPrioritizedTasks());

		System.out.println("Удален эпик 3");
		httpServer.manager.removeEpicByID(3);
		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач: \n" + httpServer.manager.getHistory());
		System.out.println("Задачи по приоритету: \n" + httpServer.manager.getPrioritizedTasks());

		httpServer.manager.updateEpic(epicUpdate);

		httpServer.stop();
		kvServer.stop();
	}


}