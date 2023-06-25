import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import services.manager.Managers;
import services.manager.TasksManager;

public class Main {
	public static void main(String[] args) {

//Тестирование по ТЗ ФП-5

		TasksManager taskManager = Managers.getDefault();

//Создайте 2 задачи, один эпик с 3 подзадачами и эпик без подзадач.

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

//запросите созданные задачи несколько раз в разном порядке
//после каждого запроса выведите историю и убедитесь, что в ней нет повторов

		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(3));
		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(4));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(1));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(2));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(6));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(7));

		System.out.println("Последние задачи: " + taskManager.getHistory());

		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(4));
		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(3));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(1));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(2));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(7));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(5));

		System.out.println("Последние задачи: " + taskManager.getHistory());

//удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться
		System.out.println("Удалена задача 2");
		taskManager.removeTaskByID(2);
		System.out.println("Последние задачи: " + taskManager.getHistory());

//удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
		System.out.println("Удален эпик 1");
		taskManager.removeEpicByID(3);
		System.out.println("Последние задачи: " + taskManager.getHistory());

	}
}