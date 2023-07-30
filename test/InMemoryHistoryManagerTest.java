import models.business.Task;
import models.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.manager.event.impl.InMemoryHistoryManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

	InMemoryHistoryManager historyManager;
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]");

	@BeforeEach
	void setUp() {
		historyManager = new InMemoryHistoryManager();
	}

	@Test
	@DisplayName ("Добавление задачи в историю")
	void shouldAddNewTask() {
		Task task = new Task(1, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);

		historyManager.add(task);

		final List<Task> savedTasks = historyManager.getHistory();
		assertFalse(savedTasks.isEmpty(), "Задача не добавлена.");
		assertEquals(task, savedTasks.get(0), "Задачи не совпадают.");
	}

	@Test
	@DisplayName ("Добавление двойника задачи в историю")
	void shouldAddDoubleTask() {
		Task task = new Task(1, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);

		historyManager.add(task);
		historyManager.add(task);

		final List<Task> savedTasks = historyManager.getHistory();
		assertEquals(1, savedTasks.size(), "Количество задач не совпадает.");
	}

	@Test
	@DisplayName ("Получение задачи из истории")
	void shouldGetHistory() {
		Task task = new Task(1, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);

		historyManager.add(task);

		final List<Task> savedTasks = historyManager.getHistory();
		assertFalse(savedTasks.isEmpty(), "Задача не добавлена.");
		assertEquals(task, savedTasks.get(0), "Задачи не совпадают.");
	}

	@Test
	@DisplayName ("Получение задачи при пустой истории")
	void shouldGetHistoryWhenHistoryIsEmpty() {
		final List<Task> savedTasks = historyManager.getHistory();
		assertTrue(savedTasks.isEmpty(), "История не пуста.");
	}

	@Test
	@DisplayName ("Удаление задачи из истории")
	void shouldTaskFromHistory() {
		Task task = new Task(1, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		historyManager.add(task);

		historyManager.removeTaskFromHistory(task.getID());

		final List<Task> savedTasks = historyManager.getHistory();
		assertTrue(savedTasks.isEmpty(), "Задача не удалена.");
	}

	@Test
	@DisplayName ("Удаление задачи при пустой истории")
	void shouldTaskFromHistoryWhenHistoryIsEmpty() {
		Task task = new Task(1, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		String consoleOutput = null;

		PrintStream originalOut = System.out;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
			PrintStream capture = new PrintStream(outputStream);
			System.setOut(capture);

			historyManager.removeTaskFromHistory(task.getID());

			capture.flush();
			consoleOutput = outputStream.toString();
			System.setOut(originalOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Задача с этим номером ID отсутствует.\r\n", consoleOutput, "Вывод об ошибке не совпадает.");
	}

	@Test
	@DisplayName ("Удаление задачи из начала истории")
	void shouldTaskFromStartHistory() {
		Task task1 = new Task(1, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		Task task2 = new Task(2, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		Task task3 = new Task(3, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		historyManager.add(task1);
		historyManager.add(task2);
		historyManager.add(task3);

		historyManager.removeTaskFromHistory(task1.getID());

		final List<Task> savedTasks = historyManager.getHistory();
		assertFalse(savedTasks.contains(task1), "Задача не удалена.");
	}

	@Test
	@DisplayName ("Удаление задачи из середины истории")
	void shouldTaskFromMddHistory() {
		Task task1 = new Task(1, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		Task task2 = new Task(2, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		Task task3 = new Task(3, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		historyManager.add(task1);
		historyManager.add(task2);
		historyManager.add(task3);

		historyManager.removeTaskFromHistory(task2.getID());

		final List<Task> savedTasks = historyManager.getHistory();
		assertFalse(savedTasks.contains(task2), "Задача не удалена.");
	}

	@Test
	@DisplayName ("Удаление задачи из конца истории")
	void shouldTaskFromEndHistory() {
		Task task1 = new Task(1, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		Task task2 = new Task(2, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		Task task3 = new Task(3, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T08:30:00.000000000", formatter), 60L);
		historyManager.add(task1);
		historyManager.add(task2);
		historyManager.add(task3);

		historyManager.removeTaskFromHistory(task3.getID());

		final List<Task> savedTasks = historyManager.getHistory();
		assertFalse(savedTasks.contains(task3), "Задача не удалена.");
	}

}