import models.business.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.manager.event.impl.InMemoryTasksManager;
import services.manager.utils.Managers;

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
	@DisplayName ("Получение истории при отсутствии задач")
	void shouldReturnHistoryListWhenNoTasks() {

		final List<Task> history = taskManager.getHistory();

		assertNotNull(history, "Список задач не создан.");
		assertTrue(history.isEmpty(), "История не пуста.");
	}

	@Test
	@DisplayName ("Получение задач, с сортировкой по времени")
	void shouldReturnPrioritizedTasks() {
		Task task = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000", formatter), 60L);
		final int taskId = taskManager.addNewTask(task);
		taskManager.getTaskByID(taskId);

		final TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

		assertNotNull(prioritizedTasks, "Список задач пуст.");
		assertEquals(1, prioritizedTasks.size(), "Список задач пуст.");
	}

	@Test
	@DisplayName ("Получение задач, с сортировкой по времени при отсутствии задач")
	void shouldReturnPrioritizedTasksWhenNoTasks() {

		final TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

		assertNotNull(prioritizedTasks, "Список задач не создан.");
		assertTrue(prioritizedTasks.isEmpty(), "Список задач не пуст.");
	}

}
