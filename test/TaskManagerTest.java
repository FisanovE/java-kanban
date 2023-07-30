import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.manager.event.TasksManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TasksManager> {
	T taskManager;
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]");

	@Test
	@DisplayName ("Создание новой задачи")
	void shouldCreateNewTask() {
		Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);

		final int taskId = taskManager.addNewTask(task);

		final Task savedTask = taskManager.getTaskByID(taskId);
		assertNotNull(savedTask, "Задача не найдена.");
		assertEquals(task, savedTask, "Задачи не совпадают.");
	}

	@Test
	@DisplayName ("Создание новой задачи в занятое время")

	void shouldNotCreateNewTaskInBusyTime() {
		Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		Task task2 = new Task(2,"Покупки", "Купить продукты", Status.NEW,
				LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);
			taskManager.addNewTask(task);
			taskManager.addNewTask(task2);
			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals("Задача \"Покупки\" не создана. В промежуток времени с 15.07.23|08:00 по 15.07.23|09:00 уже есть" +
				" другая задача.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<Task> tasks = taskManager.getAllTasks();
		assertEquals(1, tasks.size(), "Создана задача в занятое время.");
	}

	@Test
	@DisplayName ("Создание нового эпика")
	void shouldCreateNewEpic() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		final int epicId = taskManager.addNewEpic(epic);

		final Epic savedEpic = taskManager.getEpicByID(epicId);

		assertNotNull(savedEpic, "Эпик не найден.");
		assertEquals(epic, savedEpic, "Эпики не совпадают.");

	}

	@Test
	@DisplayName ("Создание нового эпика в занятое время")
	void shouldNotCreateNewEpicInBusyTime() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон");
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L, epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);
		Epic epic2 = new Epic("Покупки", "Купить продукты");
		int epicId2 = taskManager.addNewEpic(epic2);
		SubTask subTask2 = new SubTask("Учёба", "Учиться читать",
				LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L, epicId2);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);
			int subTaskID2 = taskManager.addNewSubTask(subTask2);
			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Подзадача \"Учёба\" не создана. В промежуток времени с 15.07.23|08:00 по 15.07.23|09:00 уже есть" +
				" другая подзадача.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertEquals(1, subTasks.size(), "Создана подзадача в занятое время.");

	}

	@Test
	@DisplayName ("Создание новой подзадачи")
	void shouldCreateNewSubTask() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);

		int subTaskID = taskManager.addNewSubTask(subTask);

		final SubTask savedSubTask = taskManager.getSubTaskByID(subTaskID);
		assertNotNull(savedSubTask, "Подзадача не найдена.");
		assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");
	}

	@Test
	@DisplayName ("Создание новой подзадачи при пустом списке эпиков")
	void shouldCreateNewSubTaskWhenEpicStorageIsEmpty() {
		final Epic epic = new Epic(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epic.getID());

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			int subTaskID = taskManager.addNewSubTask(subTask);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Эпик с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
	}


	@Test
	@DisplayName ("Создание новой подзадачи в занятое время")

	void shouldNotCreateNewSubTaskInBusyTime() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L, epicId);
		SubTask subTask2 = new SubTask("Покупки", "Купить продукты",
				LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L, epicId);
		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);
			taskManager.addNewSubTask(subTask);
			taskManager.addNewSubTask(subTask2);
			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Подзадача \"Покупки\" не создана. В промежуток времени с 15.07.23|08:00 по 15.07.23|09:00 уже " +
				"есть" +
				" другая подзадача.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertEquals(1, subTasks.size(), "Создана подзадача в занятое время.");
	}

	@Test
	@DisplayName ("Обновление задачи")
	void shouldUpdateTask() {
		final Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);
		final Task taskUpdate = new Task(taskId,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);

		taskManager.updateTask(taskUpdate);

		final Task savedTask = taskManager.getTaskByID(taskId);
		assertNotNull(savedTask, "Задача не найдена.");
		assertEquals(taskUpdate, savedTask, "Задачи не совпадают.");
	}

	@Test
	@DisplayName ("Обновление задачи при пустом списке задач")
	void shouldNotUpdateTaskWhenStorageIsEmpty() {
		final Task taskUpdate = new Task(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.updateTask(taskUpdate);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Задача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<Task> tasks = taskManager.getAllTasks();
		assertEquals(0, tasks.size(), "Создана не верная задача.");
	}

	@Test
	@DisplayName ("Обновление задачи при не верном ID")
	void shouldUpdateTaskWhenBadId() {
		final Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);
		final Task taskUpdate = new Task(3,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.updateTask(taskUpdate);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Задача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<Task> tasks = taskManager.getAllTasks();
		assertEquals(1, tasks.size(), "Создана не верная задача.");
	}


	/**
	 * метод для получения статуса эпика после ввода подзадач с заданныи статусом
	 */
	public Status getStatusEpicAfterUpdate(int subTaskId, int subTaskId2, Status status1, Status status2, int epicId) {
		SubTask subTaskUpdate = new SubTask(subTaskId,"Купить компоненты", "Купить компоненты для бетона",
				status1, LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		SubTask subTaskUpdate2 = new SubTask(subTaskId2,"Залить бетон", "Залить бетон из купленных компонентов",
				status2, LocalDateTime.parse("2023-07-15T11:40:00.000000000", formatter),
				360L, epicId);
		taskManager.updateSubTask(subTaskUpdate);
		taskManager.updateSubTask(subTaskUpdate2);
		return taskManager.getEpicByID(epicId).getStatus();
	}



	@Test
	@DisplayName ("Обновление эпика: статус")
	void shouldUpdateEpicStatus() {
		final Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон");
		final int epicId = taskManager.addNewEpic(epic);

		final SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		final SubTask subTask2 = new SubTask("Залить бетон", "Залить бетон из купленных компонентов",
				LocalDateTime.parse("2023-07-15T11:40:00.000000000", formatter),
				360L, epicId);
		final int subTaskId = taskManager.addNewSubTask(subTask);
		final int subTaskId2 = taskManager.addNewSubTask(subTask2);

		assertAll(
				() -> assertEquals(Status.NEW, getStatusEpicAfterUpdate(subTaskId, subTaskId2, Status.NEW, Status.NEW, epicId),
						"Статус эпика не равен \"NEW\"."),
				() -> assertEquals(Status.IN_PROGRESS, getStatusEpicAfterUpdate(subTaskId, subTaskId2, Status.NEW, Status.DONE,
								epicId),
						"Статус эпика не равен \"IN_PROGRESS\"."),
				() -> assertEquals(Status.IN_PROGRESS, getStatusEpicAfterUpdate(subTaskId, subTaskId2, Status.IN_PROGRESS, Status.IN_PROGRESS,
								epicId),
						"Статус эпика не равен \"IN_PROGRESS\"."),
				() -> assertEquals(Status.DONE, getStatusEpicAfterUpdate(subTaskId, subTaskId2, Status.DONE, Status.DONE,
								epicId),
						"Статус эпика не равен \"DONE\".")
		);
	}

	@Test
	@DisplayName ("Обновление эпика: статус при отсутствии подзадач")
	void shouldUpdateEpicStatusIfNoSubTasks() {
		final Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон");
		final int epicId = taskManager.addNewEpic(epic);

		taskManager.updateEpic(epic);

		assertEquals(Status.NEW, taskManager.getEpicByID(epicId).getStatus(),
				"Статус эпика не равен \"NEW\" при отсутствии подзадач.");
	}

	@Test
	@DisplayName ("Обновление эпика: время")
	void shouldUpdateEpicTime() {
		final Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон");
		final int epicId = taskManager.addNewEpic(epic);

		final SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		final SubTask subTask2 = new SubTask("Залить бетон", "Залить бетон из купленных компонентов",
				LocalDateTime.parse("2023-07-15T11:40:00.000000000", formatter),
				360L, epicId);
		final int subTaskId = taskManager.addNewSubTask(subTask);
		final int subTaskId2 = taskManager.addNewSubTask(subTask2);

		final Epic epic2 = taskManager.getEpicByID(epicId);

		assertAll(
				() -> assertEquals(LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
						taskManager.getEpicByID(epicId).getStartTime(), "Время начала эпиков не совпадает."),
				() -> assertEquals(LocalDateTime.parse("2023-07-15T17:40:00.000000000", formatter),
						taskManager.getEpicByID(epicId).getEndTime(), "Время окончания эпиков не совпадает.")
		);
	}

	@Test
	@DisplayName ("Обновление эпика при пустом списке эпиков")
	void shouldNotUpdateEpicWhenStorageIsEmpty() {
		final Epic epicUpdate = new Epic(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);
		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.updateEpic(epicUpdate);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Эпик с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<Epic> epics = taskManager.getAllEpics();
		assertEquals(0, epics.size(), "Создан не верный эпик.");
	}

	@Test
	@DisplayName ("Обновление эпика при не верном ID")
	void shouldUpdateEpicWhenBadId() {
		final Epic epic = new Epic(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);
		taskManager.addNewEpic(epic);
		final Epic epicUpdate = new Epic(2,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.updateEpic(epicUpdate);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Эпик с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<Epic> epics = taskManager.getAllEpics();
		assertEquals(1, epics.size(), "Создан не верный эпик.");
	}

	@Test
	@DisplayName ("Обновление подзадачи")
	void shouldUpdateSubTask() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);
		final SubTask subTaskUpdate = new SubTask(subTaskID, "Купить компоненты", "Купить компоненты для бетона",
				Status.DONE, LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);

		taskManager.updateSubTask(subTaskUpdate);

		final SubTask savedSubTask = taskManager.getSubTaskByID(subTaskID);
		assertNotNull(savedSubTask, "Подзадача не найдена.");
		assertEquals(subTaskUpdate, savedSubTask, "Подзадачи не совпадают.");
	}

	@Test
	@DisplayName ("Обновление подзадачи при пустом списке подзадач")
	void shouldNotUpdateSubTaskWhenStorageIsEmpty() {
		final SubTask subTaskUpdate = new SubTask(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L,1);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.updateSubTask(subTaskUpdate);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Подзадача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertEquals(0, subTasks.size(), "Создана не верная подзадача.");
	}

	@Test
	@DisplayName ("Обновление подзадачи при не верном ID")
	void shouldUpdateSubTaskWhenBadId() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);
		final SubTask subTaskUpdate = new SubTask(3,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L,1);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.updateSubTask(subTaskUpdate);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Подзадача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertEquals(1, subTasks.size(), "Создана не верная подзадача.");
	}

	@Test
	@DisplayName ("Получение по ID задачи ")
	void shouldReturnTaskByID() {
		final Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);

		final Task savedTask = taskManager.getTaskByID(taskId);

		assertNotNull(savedTask, "Задача не найдена.");
		assertEquals(task, savedTask, "Задачи не совпадают.");
	}

	@Test
	@DisplayName ("Получение по ID задачи при пустом списке задач")
	void shouldNotReturnTaskByIDWhenStorageIsEmpty() {
		final Task task = new Task(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);
		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.getTaskByID(task.getID());

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Задача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		assertNull(taskManager.getTaskByID(task.getID()), "Получена неверная задача.");
	}

	@Test
	@DisplayName ("Получение по ID задачи при не верном ID")
	void shouldReturnTaskWhenBadId() {
		final Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.getTaskByID(3);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Задача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
	}

	@Test
	@DisplayName ("Получение по ID эпика")
	void shouldReturnEpicByID() {
		final Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		final int epicId = taskManager.addNewEpic(epic);

		final Task savedEpic = taskManager.getEpicByID(epicId);

		assertNotNull(savedEpic, "Эпик не найден.");
		assertEquals(epic, savedEpic, "Эпики не совпадают.");
	}

	@Test
	@DisplayName ("Получение по ID эпика при пустом списке эпиков")
	void shouldNotReturnEpicByIDWhenStorageIsEmpty() {
		final Epic epic = new Epic(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.getEpicByID(epic.getID());

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Эпик с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		assertNull(taskManager.getEpicByID(epic.getID()), "Получен неверный эпик.");
	}

	@Test
	@DisplayName ("Получение по ID эпика при не верном ID")
	void shouldReturnEpicWhenBadId() {
		final Epic epic = new Epic(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);
		taskManager.addNewEpic(epic);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.getEpicByID(3);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Эпик с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
	}

	@Test
	@DisplayName ("Получение по ID подзадачи")
	void shouldReturnSubTaskByID() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);
		final SubTask savedSubTask = taskManager.getSubTaskByID(subTaskID);

		assertNotNull(savedSubTask, "Подзадача не найдена.");
		assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");
	}

	@Test
	@DisplayName ("Получение по ID подзадачи при пустом списке подзадач")
	void shouldNotReturnSubTaskByIDWhenStorageIsEmpty() {
		final SubTask subTask = new SubTask(2,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L, 1);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.getSubTaskByID(subTask.getID());

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Подзадача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		assertNull(taskManager.getSubTaskByID(subTask.getID()), "Получена неверная подзадача.");
	}

	@Test
	@DisplayName ("Получение по ID подзадачи при не верном ID")
	void shouldReturnSubTaskWhenBadId() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.getSubTaskByID(7);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Подзадача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
	}

	@Test
	@DisplayName ("Получение списка всех задач")
	void shouldReturnListAllTasks() {
		final Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		taskManager.addNewTask(task);

		final List<Task> tasks = taskManager.getAllTasks();

		assertNotNull(tasks, "Задачи не возвращаются.");
		assertEquals(1, tasks.size(), "Неверное количество задач.");
		assertEquals(task, tasks.get(0), "Задачи не совпадают.");
	}

	@Test
	@DisplayName ("Получение списка всех задач при пустом списке задач")
	void shouldReturnListAllTasksWhenStorageIsEmpty() {
		assertNotNull(taskManager.getAllTasks(),"Список не возвращается");
		assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пуст." );
	}

	@Test
	@DisplayName ("Получение списка всех эпиков")
	void shouldReturnListAllEpics() {
		final Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		taskManager.addNewEpic(epic);

		final List<Epic> epics = taskManager.getAllEpics();

		assertNotNull(epics, "Эпики не возвращаются.");
		assertEquals(1, epics.size(), "Неверное количество эпиков.");
		assertEquals(epic, epics.get(0), "Эпики не совпадают.");
	}

	@Test
	@DisplayName ("Получение списка всех эпиков при пустом списке эпиков")
	void shouldReturnListAllEpicsWhenStorageIsEmpty() {
		assertNotNull(taskManager.getAllEpics(),"Список не возвращается");
		assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков не пуст." );
	}

	@Test
	@DisplayName ("Получение списка всех подзадач")
	void shouldReturnListAllSubTasks() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);
		final List<SubTask> subTasks = taskManager.getAllSubTasks();

		assertNotNull(subTasks, "Подзадачи не возвращаются.");
		assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
		assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");
	}

	@Test
	@DisplayName ("Получение списка всех подзадач при пустом списке подзадач")
	void shouldReturnListAllSubTasksWhenStorageIsEmpty() {
		assertNotNull(taskManager.getAllSubTasks(),"Список не возвращается");
		assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пуст." );
	}

	@Test
	@DisplayName ("Получение списка подзадач эпика")
	void shouldReturnListAllSubTasksByEpic() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L,
				epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);
		final List<SubTask> subTasks = taskManager.getAllSubTasksByEpic(epicId);

		assertNotNull(subTasks, "Подзадачи не возвращаются.");
		assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
		assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");
	}

	@Test
	@DisplayName ("Получение списка подзадач эпика при пустом списке эпиков")
	void shouldReturnListAllSubTasksByEpicWhenStorageIsEmpty() {
		final Epic epic = new Epic(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);
		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.getAllSubTasksByEpic(epic.getID());

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Эпик с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		final List<SubTask> subTasks =  taskManager.getAllSubTasksByEpic(epic.getID());
		assertTrue(subTasks.isEmpty(), "Создан не верный эпик.");
	}

	@Test
	@DisplayName ("Удаление по ID задачи")
	void shouldRemoveTaskByID() {
		final Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);
		final List<Task> tasks = taskManager.getAllTasks();
		assertNotNull(tasks, "Задачи не возвращаются.");
		assertEquals(1, tasks.size(), "Неверное количество задач.");
		assertEquals(task, tasks.get(0), "Задачи не совпадают.");

		taskManager.removeTaskByID(taskId);

		final List<Task> tasks2 = taskManager.getAllTasks();
		assertTrue(tasks2.isEmpty(), "Задачи не удаляются.");
	}

	@Test
	@DisplayName ("Удаление по ID задачи при пустом списке задач")
	void shouldRemoveTaskByIDWhenStorageIsEmpty() {
		final Task task = new Task(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.removeTaskByID(task.getID());

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Задача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		final List<Task> tasks =  taskManager.getAllTasks();
		assertTrue(tasks.isEmpty(), "Список задач не пуст.");
	}

	@Test
	@DisplayName ("Удаление по ID задачи при не верном ID")
	void shouldRemoveTaskWhenBadId() {
		final Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.removeTaskByID(3);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Задача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<Task> tasks = taskManager.getAllTasks();
		assertEquals(1, tasks.size(), "Удалена не та задача.");
	}

	@Test
	@DisplayName ("Удаление по ID эпика")
	void shouldRemoveEpicByID() {
		final Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		final int epicId = taskManager.addNewEpic(epic);
		final SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		taskManager.addNewSubTask(subTask);

		taskManager.removeEpicByID(epicId);

		final List<Epic> epics2 = taskManager.getAllEpics();
		assertTrue(epics2.isEmpty(), "Эпики не удаляются.");
		final List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertFalse(subTasks.contains(subTask), "Подзадачи эпика не удаляются.");
	}

	@Test
	@DisplayName ("Удаление по ID эпика при пустом списке эпиков")
	void shouldRemoveEpicByIDWhenStorageIsEmpty() {
		final Epic epic = new Epic(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.removeEpicByID(epic.getID());

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Эпик с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		final List<Epic> epics = taskManager.getAllEpics();
		assertTrue(epics.isEmpty(), "Список эпиков не пуст.");
	}

	@Test
	@DisplayName ("Удаление по ID эпика при не верном ID")
	void shouldRemoveEpicWhenBadId() {
		final Epic epic = new Epic(1,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L);
		taskManager.addNewEpic(epic);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.removeEpicByID(3);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Эпик с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		List<Epic> epics = taskManager.getAllEpics();
		assertEquals(1, epics.size(), "Удален не тот эпик.");
	}

	@Test
	@DisplayName ("Удаление по ID подзадачи")
	void shouldRemoveSubTaskByID() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);
		final List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertNotNull(subTasks, "Подзадачи не возвращаются.");
		assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
		assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");

		taskManager.removeSubTaskByID(subTaskID);

		final List<SubTask> subTasks2 = taskManager.getAllSubTasks();
		assertTrue(subTasks2.isEmpty(), "Подзадачи не удаляются.");
	}

	@Test
	@DisplayName ("Удаление по ID подзадачи при пустом списке подзадач")
	void shouldRemoveSubTaskByIDWhenStorageIsEmpty() {
		final SubTask subTask = new SubTask(2,"Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:00:00.000000000", formatter), 60L, 1);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.removeSubTaskByID(subTask.getID());

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Подзадача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		final List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertTrue(subTasks.isEmpty(), "Список подзадач не пуст.");
	}

	@Test
	@DisplayName ("Удаление по ID подзадачи при не верном ID")
	void shouldRemoveSubTaskWhenBadId() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);

		String consoleOutput = null;
		PrintStream originalOut = System.out;
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			taskManager.removeSubTaskByID(7);

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Подзадача с этим номером ID отсутствует.\r\n",consoleOutput, "Вывод об ошибке не совпадает.");
		final List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertEquals(1, subTasks.size(), "Удалена не та подзадача.");
	}

	@Test
	@DisplayName ("Удаление всех задач")
	void shouldRemoveAllTasks() {
		final Task task = new Task("Уборка", "Сделать уборку в кухне",
				LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		taskManager.addNewTask(task);
		final List<Task> tasks = taskManager.getAllTasks();
		assertNotNull(tasks, "Задачи не возвращаются.");
		assertEquals(1, tasks.size(), "Неверное количество задач.");
		assertEquals(task, tasks.get(0), "Задачи не совпадают.");

		taskManager.removeAllTasks();

		final List<Task> tasks2 = taskManager.getAllTasks();
		assertTrue(tasks2.isEmpty(), "Задачи не удаляются.");
	}


	@Test
	@DisplayName ("Удаление всех эпиков")
	void shouldRemoveAllEpics() {
		final Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		final int epicId = taskManager.addNewEpic(epic);
		final SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		taskManager.addNewSubTask(subTask);

		taskManager.removeAllEpics();

		final List<Epic> epics2 = taskManager.getAllEpics();
		assertTrue(epics2.isEmpty(), "Эпики не удаляются.");
		final List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertTrue(subTasks.isEmpty(), "Подзадачи не удаляются.");
	}

	@Test
	@DisplayName ("Удаление всех подзадач")
	void shouldRemoveAllSubTasks() {
		Epic epic = new Epic("Залить бетон", "Купить компоненты для бетона и залить бетон",
				LocalDateTime.parse("2023-07-15T16:27:00.000000000", formatter), 1L);
		int epicId = taskManager.addNewEpic(epic);
		SubTask subTask = new SubTask("Купить компоненты", "Купить компоненты для бетона",
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				120L, epicId);
		int subTaskID = taskManager.addNewSubTask(subTask);
		final List<SubTask> subTasks = taskManager.getAllSubTasks();
		assertNotNull(subTasks, "Подзадачи не возвращаются.");
		assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
		assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");

		taskManager.removeAllSubTasks(epicId);

		final List<SubTask> subTasks2 = taskManager.getAllSubTasks();
		assertTrue(subTasks2.isEmpty(), "Подзадачи не удаляются.");
	}

}
