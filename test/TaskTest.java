import models.business.Task;
import models.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]");

	Task task;

	@BeforeEach
	void setUp() {
		task = new Task(1, "Уборка", "Сделать уборку в кухне", Status.DONE,
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter), 60L);
	}



	@Test
	@DisplayName ("Получение имени задачи")
	void shouldGetName() {
		assertEquals(task.getName(), "Уборка", "Имена задач не совпадают");
	}

	@Test
	@DisplayName ("Получение аннотации задачи")
	void shouldGetAnnotation() {
		assertEquals(task.getAnnotation(), "Сделать уборку в кухне", "Аннотации задач не совпадают");
	}

	@Test
	@DisplayName ("Получение ID задачи")
	void shouldGetID() {
		assertEquals(task.getID(), 1, "ID задач не совпадают");
	}

	@Test
	@DisplayName ("Получение статуса задачи")
	void shouldGetStatus() {
		assertEquals(task.getStatus(), Status.DONE, "Статусы задач не совпадают");
	}

	@Test
	@DisplayName ("Установка ID задачи")
	void shouldSetID() {
		int ID = 1;
		task.setID(ID);
		assertEquals(task.getID(), ID, "ID задач не совпадают");
	}

	@Test
	@DisplayName ("Установка статуса задачи")
	void shouldSetStatus() {
		int ID = 1;
		task.setID(ID);
		assertEquals(task.getID(), ID, "Статусы задач не совпадают");
	}

	@Test
	@DisplayName ("Получение задачи в виде строки")
	void testToString() {
		String sampleText = "15.07.23|09:40__15.07.23|10:40__1__Уборка__DONE__Сделать уборку в кухне\n";
		assertEquals(task.toString(), sampleText, "Текст задач не совпадает");
	}

	@Test
	@DisplayName ("Получение времени окончания задачи")
	void shouldGetEndTime() {
		assertEquals(task.getEndTime(),
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter).plus(Duration.ofMinutes(60L)),
				"Время окончания задач не совпадает");
	}

	@Test
	@DisplayName ("Получение времени начала задачи")
	void shouldGetStartTime() {
		assertEquals(task.getStartTime(),
				LocalDateTime.parse("2023-07-15T09:40:00.000000000", formatter),
				"Время начала задач не совпадает");
	}

	@Test
	@DisplayName ("Получение длительности задачи")
	void shouldGetDuration() {
		assertEquals(task.getDuration(), 60L,"Длительность задач не совпадает");
	}
}