import models.business.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.manager.InMemoryTasksManager;
import services.manager.Managers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTasksManager> {

	@BeforeEach
	void setUp() {
		taskManager = (InMemoryTasksManager) Managers.getDefault();
	}

	@Test
	@DisplayName ("Создание новой задачи")
	void shouldCreateNewTask() {
		Task task = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);

		final int taskId = taskManager.addNewTask(task);

		final Task savedTask = taskManager.getTaskByID(taskId);
		assertNotNull(savedTask, "Задача не найдена.");
		assertEquals(task, savedTask, "Задачи не совпадают.");
	}

	@Test
	@DisplayName ("Установка значения счетчика")
	void shouldSetCounterValue() {
		int setCount = 3;
		taskManager.setCounter(setCount);
		assertEquals(setCount, taskManager.getCounter(), "Значения счетчика не совпадают.");
	}

	@Test
	@DisplayName ("Получение истории")
	void shouldReturnHistoryList() {
		Task task = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);
		taskManager.getTaskByID(taskId);

		final List<Task> history = taskManager.getHistory();

		assertNotNull(history, "История пустая.");
		assertEquals(1, history.size(), "История пустая.");
	}

	@Test
	@DisplayName ("Сортировка задач по времени")
	void shouldReturnPrioritizedTasks() {
		Task task = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);
		taskManager.getTaskByID(taskId);

		final TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

		assertNotNull(prioritizedTasks, "Список задач пуст.");
		assertEquals(1, prioritizedTasks.size(), "Список задач пуст.");
	}

}