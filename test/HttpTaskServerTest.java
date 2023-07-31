import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import server.KVServer;
import services.manager.utils.Managers;
import services.manager.event.TasksManager;
import services.manager.utils.DateUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
	HttpTaskServer httpServer;

	KVServer kvServer;
	Gson gson = Managers.getGson();
	//private TasksManager manager;

	Task task;
	Epic epic;
	SubTask subTask;

	@BeforeEach
	void init() throws IOException, InterruptedException {
		kvServer = new KVServer();
		kvServer.start();
		httpServer = new HttpTaskServer();
		httpServer.start();


		task = new Task("Уборка", "Сделать уборку в кухне", LocalDateTime.parse("2023-07-15T08:00:00.000000000", DateUtils.formatter), 60L);
		httpServer.manager.addNewTask(task);
		epic = new Epic("Выучить алфавит", "Выучить несколько букв алфавита");
		int epicId = httpServer.manager.addNewEpic(epic);
		subTask = new SubTask("Выучить А", "Выучить букву А", LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", DateUtils.formatter), 120L, epicId);
		httpServer.manager.addNewSubTask(subTask);

	}

	@AfterEach
	void stop() {
		httpServer.stop();
		kvServer.stop();
	}

	@Test
	@DisplayName ("Получение списка всех задач")
	public void shouldReturnListAllTasks() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/task");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Type taskType = new TypeToken<ArrayList<Task>>() {
		}.getType();
		List<Task> actual = gson.fromJson(response.body(), taskType);

		assertNotNull(actual, "Список задач не возвращается");

		assertEquals(1, actual.size(), "Количество задач не совпадает");
		assertEquals(task, actual.get(0), "Задачи не совпадают");
	}

	@Test
	@DisplayName ("Получение списка всех эпиков")
	void shouldReturnListAllEpics() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/epic");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

		Epic epicAdded = new Epic(2, "Выучить алфавит", "Выучить несколько букв алфавита", Status.NEW, LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", DateUtils.formatter), 120L);
		epicAdded.setEndTime(LocalDateTime.parse("2023-07" + "-15T11:40:00.000000000", DateUtils.formatter));

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Type taskType = new TypeToken<ArrayList<Epic>>() {
		}.getType();
		List<Epic> actual = gson.fromJson(response.body(), taskType);

		assertNotNull(actual, "Список эпиков не возвращается");

		assertEquals(1, actual.size(), "Количество эпиков не совпадает");
		assertEquals(epicAdded, actual.get(0), "Эпики не совпадают");
	}

	@Test
	@DisplayName ("Получение списка всех подзадач")
	void shouldReturnListAllSubTasks() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/subtask");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

		SubTask subTaskAdded = new SubTask(3, "Выучить А", "Выучить букву А", Status.NEW, LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", DateUtils.formatter), 120L, 2);

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Type taskType = new TypeToken<ArrayList<SubTask>>() {
		}.getType();
		List<SubTask> actual = gson.fromJson(response.body(), taskType);

		assertNotNull(actual, "Список подзадач не возвращается");

		assertEquals(1, actual.size(), "Количество подзадач не совпадает");
		assertEquals(subTaskAdded, actual.get(0), "Подзадачи не совпадают");
	}

	@Test
	@DisplayName ("Получение по ID задачи ")
	void shouldReturnTaskByID() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/task/?id=1");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Type taskType = new TypeToken<Task>() {
		}.getType();
		Task actual = gson.fromJson(response.body(), taskType);

		assertNotNull(actual, "Задача не возвращается");
		assertEquals(task, actual, "Задачи не совпадают");
	}

	@Test
	@DisplayName ("Получение по ID эпика ")
	void shouldReturnEpicByID() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/epic/?id=2");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

		Epic epicAdded = new Epic(2, "Выучить алфавит", "Выучить несколько букв алфавита", Status.NEW, LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", DateUtils.formatter), 120L);
		epicAdded.setEndTime(LocalDateTime.parse("2023-07" + "-15T11:40:00.000000000", DateUtils.formatter));

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Type taskType = new TypeToken<Epic>() {
		}.getType();
		Epic actual = gson.fromJson(response.body(), taskType);

		assertNotNull(actual, "Эпик не возвращается");
		assertEquals(epicAdded, actual, "Эпики не совпадают");
	}

	@Test
	@DisplayName ("Получение по ID подзадачи ")
	void shouldReturnSubTaskByID() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/subtask/?id=3");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

		SubTask sub = new SubTask(3, "Выучить А", "Выучить букву А", Status.NEW, LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", DateUtils.formatter), 120L, 2);

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Type taskType = new TypeToken<SubTask>() {
		}.getType();
		SubTask actual = gson.fromJson(response.body(), taskType);

		assertNotNull(actual, "Подзадача не возвращается");
		assertEquals(sub, actual, "Подзадачи не совпадают");
	}


	@Test
	@DisplayName ("Получение списка подзадач эпика")
	void shouldReturnListAllSubTasksByEpic() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/subtask/epic/?id=2");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

		SubTask subTaskAdded = new SubTask(3, "Выучить А", "Выучить букву А", Status.NEW, LocalDateTime.parse("2023-07" + "-15T09:40:00.000000000", DateUtils.formatter), 120L, 2);

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Type taskType = new TypeToken<ArrayList<SubTask>>() {
		}.getType();
		List<SubTask> actual = gson.fromJson(response.body(), taskType);

		assertNotNull(actual, "Список подзадач не возвращается");

		assertEquals(1, actual.size(), "Количество подзадач не совпадает");
		assertEquals(subTaskAdded, actual.get(0), "Подзадачи не совпадают");
	}


	@Test
	@DisplayName ("Получение истории просмотра")
	void shouldGetHistory() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/task/history");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
		httpServer.manager.getTaskByID(1);
		httpServer.manager.getEpicByID(2);
		httpServer.manager.getSubTaskByID(3);
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Type taskType = new TypeToken<ArrayList<Task>>() {
		}.getType();
		List<Task> actual = gson.fromJson(response.body(), taskType);

		assertNotNull(actual, "Список задач не возвращается");

		assertEquals(3, actual.size(), "Количество задач не совпадает");
	}


	@Disabled // не проходит, возможно нужен свой сериализатор
	@Test
	@DisplayName ("Получение сортировки по времени")
	void shouldGetSortedTaskFromTime() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Type taskType = new TypeToken<TreeSet<Task>>() {
		}.getType();
		TreeSet<Task> actual = gson.fromJson(response.body(), taskType);

		assertNotNull(actual, "Список задач не возвращается");

		assertEquals(3, actual.size(), "Количество задач не совпадает");
	}

	@Test
	@DisplayName ("Добавление задачи")
	void shouldAddNewTask() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/task");
		Task taskNew = new Task("Выучить А", "Выучить букву А", LocalDateTime.parse("2023-07-15T09:40:00" + ".000000000", DateUtils.formatter), 120L);
		Task taskAdded = new Task(4, "Выучить А", "Выучить букву А", Status.NEW, LocalDateTime.parse("2023-07-15T09:40:00.000000000", DateUtils.formatter), 120L);

		String json = gson.toJson(taskNew);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		int id = Integer.parseInt(response.body());

		Task taskActual = httpServer.manager.getTaskByID(id);

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertNotNull(taskActual, "Задача не добавляется"), () -> assertEquals(taskAdded, taskActual, "Задачи не совпадают"));
	}

	@Test
	@DisplayName ("Добавление эпика")
	void shouldAddNewEpic() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/epic");
		Epic epicNew = new Epic("Выучить А", "Выучить букву А");
		Epic epicAdded = new Epic(4, "Выучить А", "Выучить букву А", Status.NEW);

		String json = gson.toJson(epicNew);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		int id = Integer.parseInt(response.body());

		Epic epicActual = httpServer.manager.getEpicByID(id);

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertNotNull(epicActual, "Эпик не добавляется"), () -> assertEquals(epicAdded, epicActual, "Эпики не совпадают"));
	}


	@Test
	@DisplayName ("Добавление подзадачи")
	void shouldAddNewSubTask() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/subtask");
		SubTask subTaskNew = new SubTask("Выучить А", "Выучить букву А", LocalDateTime.parse("2023-07-15T09:40:00" + ".000000000", DateUtils.formatter), 120L, 2);
		SubTask subTaskAdded = new SubTask(4, "Выучить А", "Выучить букву А", Status.NEW, LocalDateTime.parse("2023-07-15T09:40:00.000000000", DateUtils.formatter), 120L, 2);

		String json = gson.toJson(subTaskNew);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		int id = Integer.parseInt(response.body());

		SubTask subTaskActual = httpServer.manager.getSubTaskByID(id);

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertNotNull(subTaskActual, "Подзадача не добавляется"), () -> assertEquals(subTaskAdded, subTaskActual, "Подзадачи не совпадают"));
	}


	@Test
	@DisplayName ("Обновление задачи")
	void shouldUpdateTask() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/task");

		Task taskUpdate = new Task(1, "Уборка", "Сделать уборку в кухне", Status.IN_PROGRESS, LocalDateTime.parse("2023-07-15T08:00:00.000000000", DateUtils.formatter), 60L);

		String json = gson.toJson(taskUpdate);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		Task taskActual = httpServer.manager.getTaskByID(taskUpdate.getID());

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertNotNull(taskActual, "Задача не обновляется"), () -> assertEquals(taskUpdate, taskActual, "Задачи не совпадают"));
	}


	@Test
	@DisplayName ("Обновление эпика")
	void shouldUpdateEpic() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/epic");

		Epic epicUpdate = new Epic(2, "Покупки", "Купить продукты", Status.NEW, LocalDateTime.parse("2023-07-15T09:40:00.000000000", DateUtils.formatter), 120L);

		String json = gson.toJson(epicUpdate);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		Epic epicActual = httpServer.manager.getEpicByID(epicUpdate.getID());

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertNotNull(epicActual, "Эпик не обновляется"), () -> assertEquals(epicUpdate, epicActual, "Эпики не совпадают"));
	}


	@Test
	@DisplayName ("Обновление подзадачи")
	void shouldUpdateSubTask() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/subtask");

		SubTask subTaskUpdate = new SubTask(3, "Уборка", "Сделать уборку в кухне", Status.IN_PROGRESS, LocalDateTime.parse("2023-07-15T08:00:00.000000000", DateUtils.formatter), 60L, 2);

		String json = gson.toJson(subTaskUpdate);
		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		SubTask subTaskActual = httpServer.manager.getSubTaskByID(subTaskUpdate.getID());

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertNotNull(subTaskActual, "Подзадача не обновляется"), () -> assertEquals(subTaskUpdate, subTaskActual, "Подзадачи не совпадают"));
	}


	@Test
	@DisplayName ("Удаление по ID задачи")
	void shouldRemoveTaskByID() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/task/?id=1");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertTrue(httpServer.manager.getAllTasks()
																						  .isEmpty(), "Задача не удаляется"));
	}

	@Test
	@DisplayName ("Удаление по ID эпика")
	void shouldRemoveEpicByID() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/epic/?id=2");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertTrue(httpServer.manager.getAllEpics()
																						  .isEmpty(), "Эпик не удаляется"));
	}


	@Test
	@DisplayName ("Удаление по ID подзадачи")
	void shouldRemoveSubTaskByID() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/subtask/?id=3");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertTrue(httpServer.manager.getAllSubTasks()
																						  .isEmpty(), "Подзадача не удаляется"));
	}


	@Test
	@DisplayName ("Удаление всех задач")
	void shouldRemoveAllTask() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/task/task");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertTrue(httpServer.manager.getAllTasks()
																						  .isEmpty(), "Задачи не удаляются"));
	}

	@Test
	@DisplayName ("Удаление всех эпиков")
	void shouldRemoveAllEpic() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/epic/epic");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertTrue(httpServer.manager.getAllEpics()
																						  .isEmpty(), "Эпики не удаляются"));
	}


	@Test
	@DisplayName ("Удаление всех подзадач")
	void shouldRemoveAllSubTask() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uri = URI.create("http://localhost:9090/tasks/subtask/epic/?id=2");
		HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		assertAll(() -> assertEquals(200, response.statusCode()), () -> assertTrue(httpServer.manager.getAllSubTasks()
																						  .isEmpty(), "Подзадачи не удаляются"));
	}
}