import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.enums.Status;
import server.HttpTaskServer;
import services.manager.event.TasksManager;
import services.manager.utils.Managers;
import services.manager.utils.DateUtils;
import java.io.IOException;
import java.time.LocalDateTime;


public class Main {
	static HttpTaskServer httpTaskServer;

	public static void main(String[] args) throws IOException, InterruptedException {
		httpTaskServer = new HttpTaskServer();
		httpTaskServer.start();
	/*	HttpTaskManager manager = new HttpTaskManager("http://localhost:8080");
		server = new KVServer();
		server.start();
		taskNew = new Task(1, "Выучить А", "Выучить букву А",
				Status.NEW, LocalDateTime.parse("2023-07-15T09:40:00" + ".000000000", DateUtils.formatter), 120L);
		manager.addNewTask(taskNew);
*/

		/*KVTaskClient client = new KVTaskClient("http://localhost:8080");
		taskNew = new Task(1, "Выучить А", "Выучить букву А", Status.NEW, LocalDateTime.parse("2023-07-15T09:40:00" + ".000000000", DateUtils.formatter), 120L);
		String id = String.valueOf(taskNew.getID());
		String json = gson.toJson(taskNew);
		client.put(id, json);
		String req = client.load(id);
		System.out.println(req);
		Task actual = gson.fromJson(req, Task.class);
		System.out.println(actual);*/

		/*HttpClient client = HttpClient.newHttpClient();

		URI uri = URI.create("http://localhost:8080/register");
		HttpRequest requestRegister = HttpRequest.newBuilder().uri(uri).GET().build();

		HttpResponse<String> responseRegister = client.send(requestRegister, HttpResponse.BodyHandlers.ofString());

		apiToken = responseRegister.body();


		taskNew = new Task(1, "Выучить А", "Выучить букву А", Status.NEW, LocalDateTime.parse("2023-07-15T09:40:00" + ".000000000", DateUtils.formatter), 120L);
		URI uri2 = URI.create("http://localhost:8080/save/0007?API_TOKEN=" + apiToken);
		String json = gson.toJson(taskNew);
		*//*System.out.print("json: ");
		System.out.println(json);*//*

		final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest requestSave = HttpRequest.newBuilder().uri(uri2).POST(body).build();
		*//*System.out.print("requestSave: ");
		System.out.println(requestSave.bodyPublisher());*//*

		HttpResponse<String> responseSave = client.send(requestSave, HttpResponse.BodyHandlers.ofString());
		*//*System.out.println("responseSave: " + responseSave.toString());
		System.out.println("body: " + responseSave.body());
		System.out.println(KVServer.getEntrySetMap());
		System.out.println(KVServer.getValuetMap("0007"));*//*

		URI uri3 = URI.create("http://localhost:8080/load/0007?API_TOKEN=" + apiToken);
		HttpRequest requestLoad = HttpRequest.newBuilder().uri(uri3).GET().build();

		HttpResponse<String> responseLoad = client.send(requestLoad, HttpResponse.BodyHandlers.ofString());
		//System.out.println("responseLoad: " + responseLoad.toString());
		//System.out.println("bodyLoad: " + responseLoad.body());

		//Type taskType = new TypeToken<Task>() {}.getType();
		JsonElement jsonElement = JsonParser.parseString(responseLoad.body());
		//System.out.println(jsonString);
		//Task actual = gson.fromJson(jsonElement, Task.class);
		String str = gson.toJson(responseLoad.body());
		System.out.println("actual " + responseLoad.body());
		//Task actual = gson.fromJson(str, Task.class);*/



		//server.stop();



//Тестирование по ТЗ ФП-5

		TasksManager taskManager = Managers.getDefaultHttpManager("http://localhost:8080");


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
		taskManager.addNewTask(task1);
		taskManager.addNewTask(task0);
		taskManager.addNewTask(task2);
		taskManager.addNewEpic(epic1);
		taskManager.addNewEpic(epic2);
		taskManager.addNewSubTask(subTask1);
		taskManager.addNewSubTask(subTask2);
		taskManager.addNewSubTask(subTask3);
		System.out.println();

//Запросите некоторые из них, чтобы заполнилась история просмотра.


		System.out.print("Просмотр задачи: " + taskManager.getEpicByID(3));
		System.out.print("Просмотр задачи: " + taskManager.getEpicByID(4));
		System.out.print("Просмотр задачи: " + taskManager.getTaskByID(1));
		System.out.print("Просмотр задачи: " + taskManager.getTaskByID(2));
		System.out.print("Просмотр задачи: " + taskManager.getSubTaskByID(6));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(7));

		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач:\n" + taskManager.getHistory());
		System.out.println("Задачи по приоритету: \n" + taskManager.getPrioritizedTasks());

		System.out.print("Просмотр задачи: " + taskManager.getEpicByID(4));
		System.out.print("Просмотр задачи: " + taskManager.getEpicByID(3));
		System.out.print("Просмотр задачи: " + taskManager.getTaskByID(1));
		System.out.print("Просмотр задачи: " + taskManager.getSubTaskByID(7));
		System.out.println("Просмотр задачи: " + taskManager.getSubTaskByID(5));

		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач: \n" + taskManager.getHistory());
		System.out.println("Задачи по приоритету: \n" + taskManager.getPrioritizedTasks());

		System.out.println("Удалена задача 2");
		taskManager.removeTaskByID(2);
		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач: \n" + taskManager.getHistory());
		System.out.println("Задачи по приоритету: \n" + taskManager.getPrioritizedTasks());

		System.out.println("Удален эпик 3");
		taskManager.removeEpicByID(3);
		//System.out.println("Последние задачи: " + taskManager.getHistory());
		System.out.println("История задач: \n" + taskManager.getHistory());
		System.out.println("Задачи по приоритету: \n" + taskManager.getPrioritizedTasks());

		taskManager.updateEpic(epicUpdate);

	}


}