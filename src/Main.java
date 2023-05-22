import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;
import services.manager.InMemoryTaskManager;
import services.manager.Managers;

public class Main {
	public static void main(String[] args) {

//Тестирование по ТЗ ФП-3

		InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();

//Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.

		Task task1 = new Task(0, "Уборка", "Сделать уборку в кухне", Status.NEW);
		Task task2 = new Task(0, "Покупки", "Купить продукты в магазине", Status.NEW);

		Epic epic1 = new Epic(0, "Залить бетон", "Купить компоненты для бетона и залить бетон");


		SubTask subTask1 = new SubTask(0, "Купить компоненты", "Купить компоненты для бетона", Status.NEW, 1);
		SubTask subTask2 = new SubTask(0, "Залить бетон", "Залить бетон из купленных компонентов", Status.NEW, 1);

		SubTask updateSubTask1 = new SubTask(5, "Купить компоненты", "Купить компоненты для бетона", Status.DONE, 1);
		SubTask updateSubTask2 = new SubTask(6, "Залить бетон", "Залить бетон из купленных компонентов", Status.DONE, 1);

		Epic epic2 = new Epic(0, "Отдохнуть", "Отдохнуть от дел праведных");

		SubTask subTask3 = new SubTask(0, "Начать отдыхать", "Забросить всё и начать отдыхать", Status.NEW, 2);
		SubTask updateSubTask3 = new SubTask(7, "Начать отдыхать", "Забросить всё и начать отдыхать", Status.IN_PROGRESS, 2);

		taskManager.addNewEpicTask(epic1);
		taskManager.addNewEpicTask(epic2);
		taskManager.addNewTask(task1);
		taskManager.addNewTask(task2);

		taskManager.addNewSubTask(subTask1);
		taskManager.addNewSubTask(subTask2);

		taskManager.addNewSubTask(subTask3);

//Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)

		System.out.println("Задачи: " + taskManager.getAllTasks());
		System.out.println("Подзадачи: " + taskManager.getAllSubTasks());
		System.out.println("Эпики: " + taskManager.getAllEpics());

/*Измените статусы созданных объектов, распечатайте.
Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.*/

		taskManager.updateSubTask(updateSubTask1);
		System.out.println("Обновлена подзадача1 эпика1");
		System.out.println("Подзадачи: " + taskManager.getAllSubTasks());
		System.out.println("Эпики: " + taskManager.getAllEpics());

		taskManager.updateSubTask(updateSubTask2);
		taskManager.updateSubTask(updateSubTask3);
		System.out.println("Обновлена подзадача2 эпика1");
		System.out.println("Обновлена подзадача1 эпика2");
		System.out.println("Подзадачи: " + taskManager.getAllSubTasks());
		System.out.println("Эпики: " + taskManager.getAllEpics());


//Тестирование по ТЗ ФП-4

		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(1));
		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(2));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(3));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(4));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(5));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(6));

		System.out.println("Последние задачи: " + taskManager.getHistory());

		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(1));
		System.out.println("Просмотр задачи: " + taskManager.getEpicByID(2));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(3));
		System.out.println("Просмотр задачи: " + taskManager.getTaskByID(4));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(5));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(6));

		System.out.println("Последние задачи: " + taskManager.getHistory());
	}
}