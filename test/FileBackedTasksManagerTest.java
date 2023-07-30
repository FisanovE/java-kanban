import services.manager.utils.Managers;
import services.manager.exceptions.ManagerSaveException;
import models.business.Task;
import models.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.manager.event.impl.FileBackedTasksManager;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest <FileBackedTasksManager> {

	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]");

	@BeforeEach
	void setUp() throws IOException, InterruptedException {
		//taskManager = Managers.getDefaultFileManager("resources/savedData.csv");
		taskManager = Managers.getDefaultFileManager("resources/savedData.csv");
	}


	@Test
	@DisplayName ("Загрузка менеджера из файла")
	public void shouldLoadFromFile() throws IOException {
		Task task = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);

		FileBackedTasksManager taskManagerFromFile = FileBackedTasksManager.load(new File("resources" + "/savedData.csv"));
		Task savedTask = taskManagerFromFile.getTaskByID(taskId);

		assertAll(() -> assertNotNull(savedTask, "Задача не найдена."),
				() -> assertEquals(task.getName(), savedTask.getName(), "Поля \"name\" не совпадают."),
				() -> assertEquals(task.getAnnotation(), savedTask.getAnnotation(), "Поля \"annotation\" не совпадают."),
				() -> assertEquals(task.getID(), savedTask.getID(), "Поля \"ID\" не совпадают."),
				() -> assertEquals(task.getStatus(), savedTask.getStatus(), "Поля \"status\" не совпадают."),
				() -> assertEquals(task.getStartTime(), savedTask.getStartTime(), "Поля \"startTime\" не совпадают."),
				() -> assertEquals(task.getDuration(), savedTask.getDuration(), "Поля \"duration\" не совпадают.")

		);
	}

	@Test
	@DisplayName ("Загрузка менеджера из файла при пустой истории")
	public void shouldLoadFromFileWhenHistoryIsEmpty() throws IOException {
		try (FileWriter fw = new FileWriter("resources/savedData.csv");
			 BufferedWriter writer = new BufferedWriter(fw)) {
			writer.write("id,type,name,status,description,startTime,duration,epic" + "\n" +
					"1,TASK,Уборка,NEW,Сделать уборку в кухне,2023-07-15T08:00,60," + "\n" +
					"4,EPIC,Отдохнуть,NEW,Отдохнуть от дел праведных,null,0,");
		} catch (IOException e) {
			throw new ManagerSaveException("Произошла ошибка при записи файла");
		}

		FileBackedTasksManager taskManagerFromFile = FileBackedTasksManager.load(new File("resources" + "/savedData.csv"));

		assertAll(
				() -> assertTrue(taskManagerFromFile.getHistory().isEmpty(), "История не пуста."),
				() -> assertEquals(1, taskManagerFromFile.getAllTasks().size(), "Задача не загружена."),
				() -> assertTrue(taskManagerFromFile.getHistory().isEmpty(), "История не пуста.")
		);
	}

	@Test
	@DisplayName ("Загрузка менеджера из файла при пустом списке задач")
	public void shouldLoadFromFileWhenListOfTasksIsEmpty() throws IOException {
		try  {
			FileOutputStream fos = new FileOutputStream("resources/savedData.csv", false);
		} catch (IOException e) {
			throw new ManagerSaveException("Произошла ошибка при записи файла");
		}

		FileBackedTasksManager taskManagerFromFile = FileBackedTasksManager.load(new File("resources" + "/savedData.csv"));

		assertAll(
				() -> assertTrue(taskManagerFromFile.getHistory().isEmpty(), "История не пуста."),
				() -> assertTrue(taskManagerFromFile.getAllTasks().isEmpty(), "Список задач не пуст."),
				() -> assertTrue(taskManagerFromFile.getAllEpics().isEmpty(), "Список эпиков не пуст."),
				() -> assertTrue(taskManagerFromFile.getAllSubTasks().isEmpty(), "Список подзадач не пуст.")
		);
	}

	@Test
	@DisplayName ("Сохранение в файл")
	public void shouldSaveToFile() throws IOException {
		Task task = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);
		Task savedTask = taskManager.getTaskByID(1);

		taskManager.save();

		FileBackedTasksManager taskManagerFromFile = FileBackedTasksManager.load(new File("resources" + "/savedData.csv"));
		Task savedTask2 = taskManager.getTaskByID(1);
		Task savedHistory = taskManager.getTaskByID(1);
		assertAll(() -> assertNotNull(savedTask2, "Задача не найдена."),
				() -> assertEquals(savedTask.getName(), savedTask2.getName(), "Поля \"name\" не совпадают."),
				() -> assertEquals(savedTask.getAnnotation(), savedTask2.getAnnotation(), "Поля \"annotation\" не совпадают."),
				() -> assertEquals(savedTask.getID(), savedTask2.getID(), "Поля \"ID\" не совпадают."),
				() -> assertEquals(savedTask.getStatus(), savedTask2.getStatus(), "Поля \"status\" не совпадают."),
				() -> assertEquals(savedTask.getStartTime(), savedTask2.getStartTime(), "Поля \"startTime\" не совпадают."),
				() -> assertEquals(savedTask.getDuration(), savedTask2.getDuration(), "Поля \"duration\" не совпадают."),

				() -> assertNotNull(savedHistory, "Задача не найдена."),
				() -> assertEquals(savedTask.getName(), savedHistory.getName(), "Поля \"name\" не совпадают."),
				() -> assertEquals(savedTask.getAnnotation(), savedHistory.getAnnotation(), "Поля \"annotation\" не совпадают."),
				() -> assertEquals(savedTask.getID(), savedHistory.getID(), "Поля \"ID\" не совпадают."),
				() -> assertEquals(savedTask.getStatus(), savedHistory.getStatus(), "Поля \"status\" не совпадают."),
				() -> assertEquals(savedTask.getStartTime(), savedHistory.getStartTime(), "Поля \"startTime\" не совпадают."),
				() -> assertEquals(savedTask.getDuration(), savedHistory.getDuration(), "Поля \"duration\" не совпадают.")

		);
	}

	@Test
	@DisplayName ("Сохранение в файл при пустой истории")
	public void shouldSaveToFileWhenHistoryIsEmpty() throws IOException {
		Task task = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);
		Task savedTask = new Task(1, "Уборка", "Сделать уборку в кухне",Status.NEW, LocalDateTime.parse("2023-07-15T08:00:00" +
				".000000000", formatter), 60L);

		taskManager.save();

		FileBackedTasksManager taskManagerFromFile = FileBackedTasksManager.load(new File("resources" +
				"/savedData.csv"));
		List<Task> savedHistory = taskManager.getHistory();
		Task savedTask2 = taskManager.getTaskByID(1);
		assertAll(() -> assertNotNull(savedTask2, "Задача не найдена."),
				() -> assertEquals(savedTask.getName(), savedTask2.getName(), "Поля \"name\" не совпадают."),
				() -> assertEquals(savedTask.getAnnotation(), savedTask2.getAnnotation(), "Поля \"annotation\" не совпадают."),
				() -> assertEquals(savedTask.getID(), savedTask2.getID(), "Поля \"ID\" не совпадают."),
				() -> assertEquals(savedTask.getStatus(), savedTask2.getStatus(), "Поля \"status\" не совпадают."),
				() -> assertEquals(savedTask.getStartTime(), savedTask2.getStartTime(), "Поля \"startTime\" не совпадают."),
				() -> assertEquals(savedTask.getDuration(), savedTask2.getDuration(), "Поля \"duration\" не совпадают."),

				() -> assertTrue(savedHistory.isEmpty(), "История не пуста.")
		);
	}

	@Test
	@DisplayName ("Сохранение в файл при пустом списке задач")
	public void shouldSaveToFileWhenListOfTasksIsEmpty() throws IOException {

		taskManager.save();

		FileBackedTasksManager taskManagerFromFile = FileBackedTasksManager.load(new File("resources" + "/savedData.csv"));

		assertAll(
				() -> assertTrue(taskManagerFromFile.getHistory().isEmpty(), "История не пуста."),
				() -> assertTrue(taskManagerFromFile.getAllTasks().isEmpty(), "Список задач не пуст."),
				() -> assertTrue(taskManagerFromFile.getAllEpics().isEmpty(), "Список эпиков не пуст."),
				() -> assertTrue(taskManagerFromFile.getAllSubTasks().isEmpty(), "Список подзадач не пуст.")
		);
	}

	@Test
	@DisplayName ("Загрузка истории из строки")
	public void shouldLoadHistoryFromString() {
		List<Integer> inst = Arrays.asList(1,2,3,4,5);
		List<Integer> exp = new ArrayList<>(FileBackedTasksManager.historyFromString("1,2,3,4,5"));
	 assertEquals(inst, exp, "Значения не совпадают.");

	}

}

