package server;

import com.google.gson.Gson;
import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import services.manager.utils.Managers;
import services.manager.event.TasksManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {


	private static final int PORT = 9090;
	private final HttpServer server;

	Gson gson;
	public final TasksManager manager;

	public HttpTaskServer() throws IOException, InterruptedException {
		server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
		server.createContext("/tasks", this :: handleTasks);
		this.manager = Managers.getDefaultHttpManager("http://localhost:8080");
		gson = Managers.getGson();

	}

	private void handleTasks(HttpExchange httpExchange) {
		try {
			String path = httpExchange.getRequestURI().getPath();
			String requestMetod = httpExchange.getRequestMethod();

			switch (requestMetod) {
//Получение всех задач
				case "GET": {
					if (Pattern.matches("^/tasks/task$", path)) {
						String response = gson.toJson(manager.getAllTasks());
						sendText(httpExchange, response);
						return;
					}

					if (Pattern.matches("^/tasks/epic$", path)) {
						String response = gson.toJson(manager.getAllEpics());
						sendText(httpExchange, response);
						return;
					}

					if (Pattern.matches("^/tasks/subtask$", path)) {
						String response = gson.toJson(manager.getAllSubTasks());
						sendText(httpExchange, response);
						return;
					}
//Получение задачи по id
					if (Pattern.matches("^/tasks/task/$", path) && !httpExchange.getRequestURI().getQuery().isEmpty()) {
						String pathId = httpExchange.getRequestURI().getQuery().replaceFirst("id=", "");
						int id = parsePathId(pathId);
						if (id != -1) {
							String response = gson.toJson(manager.getTaskByID(id));
							sendText(httpExchange, response);
							break;
						} else {
							System.out.println("Получен не корректный id " + pathId);
							httpExchange.sendResponseHeaders(405, 0);
						}
					}

					if (Pattern.matches("^/tasks/epic/$", path) && !httpExchange.getRequestURI().getQuery().isEmpty()) {
						String pathId = httpExchange.getRequestURI().getQuery().replaceFirst("id=", "");
						int id = parsePathId(pathId);
						if (id != -1) {
							String response = gson.toJson(manager.getEpicByID(id));
							sendText(httpExchange, response);
							return;
						} else {
							System.out.println("Получен не корректный id " + pathId);
							httpExchange.sendResponseHeaders(405, 0);
						}
					}

					if (Pattern.matches("^/tasks/subtask/$", path) && !httpExchange.getRequestURI().getQuery()
																				   .isEmpty()) {
						String pathId = httpExchange.getRequestURI().getQuery().replaceFirst("id=", "");
						int id = parsePathId(pathId);
						if (id != -1) {
							String response = gson.toJson(manager.getSubTaskByID(id));
							sendText(httpExchange, response);
							break;
						} else {
							System.out.println("Получен не корректный id " + pathId);
							httpExchange.sendResponseHeaders(405, 0);
						}
					}

//Получение всех подзадач эпика

					if (Pattern.matches("^/tasks/subtask/epic/$", path) && !httpExchange.getRequestURI().getQuery()
																						.isEmpty()) {
						String pathId = httpExchange.getRequestURI().getQuery().replaceFirst("id=", "");
						int id = parsePathId(pathId);
						if (id != -1) {
							String response = gson.toJson(manager.getAllSubTasksByEpic(id));
							sendText(httpExchange, response);
							break;
						} else {
							System.out.println("Получен не корректный id " + pathId);
							httpExchange.sendResponseHeaders(405, 0);
						}
					}

//Получение истории

					if (Pattern.matches("^/tasks/task/history$", path)) {
						String response = gson.toJson(manager.getHistory());
						sendText(httpExchange, response);
						return;
					}

//Получение сортировки по времени

					if (Pattern.matches("^/tasks$", path)) {
						String response = gson.toJson(manager.getPrioritizedTasks());
						sendText(httpExchange, response);
						return;
					}


					System.out.println("Получен не корректный путь GET-запроса " + path);
					httpExchange.sendResponseHeaders(405, 0);
					break;
				}

//	Добавление и обновление задачи
				case "POST": {
					if (Pattern.matches("^/tasks/task$", path)) {
						InputStream inputStream = httpExchange.getRequestBody();
						String body = new String(inputStream.readAllBytes());
						Task task = gson.fromJson(body, Task.class);
						if (task.getID() == 0) {
							int id = manager.addNewTask(task);
							System.out.println("Задача добавлена");
							String response = gson.toJson(id);
							sendText(httpExchange, response);
						} else {
							manager.updateTask(task);
							System.out.println("Задача обновлена");
							httpExchange.sendResponseHeaders(200, 0);
						}
						break;
					}

					if (Pattern.matches("^/tasks/epic$", path)) {
						InputStream inputStream = httpExchange.getRequestBody();
						String body = new String(inputStream.readAllBytes());
						Epic epic = gson.fromJson(body, Epic.class);
						if (epic.getID() == 0) {
							int id = manager.addNewEpic(epic);
							System.out.println("Эпик добавлен");
							String response = gson.toJson(id);
							sendText(httpExchange, response);
						} else {
							manager.updateEpic(epic);
							System.out.println("Эпик обновлен");
							httpExchange.sendResponseHeaders(200, 0);
						}
						break;
					}

					if (Pattern.matches("^/tasks/subtask$", path)) {
						InputStream inputStream = httpExchange.getRequestBody();
						String body = new String(inputStream.readAllBytes());
						SubTask sub = gson.fromJson(body, SubTask.class);
						if (sub.getID() == 0) {
							int id = manager.addNewSubTask(sub);
							System.out.println("Подзадача добавлена");
							String response = gson.toJson(id);
							sendText(httpExchange, response);
						} else {
							manager.updateSubTask(sub);
							System.out.println("Подзадача обновлена");
							httpExchange.sendResponseHeaders(200, 0);
						}
						break;
					}

					System.out.println("Получен не корректный путь POST-запроса " + path);
					httpExchange.sendResponseHeaders(405, 0);
					break;
				}


//	Удаление всех задач
				case "DELETE": {
					if (Pattern.matches("^/tasks/task/task$", path)) {
						manager.removeAllTasks();
						System.out.println("Все задачи удалены");
						httpExchange.sendResponseHeaders(200, 0);
						return;
					}
					if (Pattern.matches("^/tasks/epic/epic$", path)) {
						manager.removeAllEpics();
						System.out.println("Все эпики удалены");
						httpExchange.sendResponseHeaders(200, 0);
						return;
					}

					if (Pattern.matches("^/tasks/subtask/epic/$", path) && !httpExchange.getRequestURI().getQuery()
																						.isEmpty()) {
						String pathId = httpExchange.getRequestURI().getQuery().replaceFirst("id=", "");
						int id = parsePathId(pathId);
						if (id != -1) {
							manager.removeAllSubTasks(id);
							System.out.println("Все подзадачи удалены у эпика id: " + id);
							httpExchange.sendResponseHeaders(200, 0);
							return;
						} else {
							System.out.println("Получен не корректный id " + pathId);
							httpExchange.sendResponseHeaders(405, 0);
						}
					}

//	Удаление задач по id
					if (Pattern.matches("^/tasks/task/$", path) && !httpExchange.getRequestURI().getQuery().isEmpty()) {
						String pathId = httpExchange.getRequestURI().getQuery().replaceFirst("id=", "");
						int id = parsePathId(pathId);
						if (id != -1) {
							manager.removeTaskByID(id);
							System.out.println("Удалена задача id: " + id);
							httpExchange.sendResponseHeaders(200, 0);
							return;
						} else {
							System.out.println("Получен не корректный id " + pathId);
							httpExchange.sendResponseHeaders(405, 0);
						}
					}

					if (Pattern.matches("^/tasks/epic/$", path) && !httpExchange.getRequestURI().getQuery().isEmpty()) {
						String pathId = httpExchange.getRequestURI().getQuery().replaceFirst("id=", "");
						int id = parsePathId(pathId);
						if (id != -1) {
							manager.removeEpicByID(id);
							System.out.println("Удален эпик id: " + id);
							httpExchange.sendResponseHeaders(200, 0);
							return;
						} else {
							System.out.println("Получен не корректный id " + pathId);
							httpExchange.sendResponseHeaders(405, 0);
						}
					}

					if (Pattern.matches("^/tasks/subtask/$", path) && !httpExchange.getRequestURI().getQuery()
																				   .isEmpty()) {
						String pathId = httpExchange.getRequestURI().getQuery().replaceFirst("id=", "");
						int id = parsePathId(pathId);
						if (id != -1) {
							manager.removeSubTaskByID(id);
							System.out.println("Удалена подзадача id: " + id);
							httpExchange.sendResponseHeaders(200, 0);
							return;
						} else {
							System.out.println("Получен не корректный id " + pathId);
							httpExchange.sendResponseHeaders(405, 0);
						}
					}

					System.out.println("Получен не корректный путь DELETE-запроса " + path);
					httpExchange.sendResponseHeaders(405, 0);
				}

				default: {
					System.out.println("Ждём GET или POST или DELETE запрос, а получили " + requestMetod);
					httpExchange.sendResponseHeaders(405, 0);
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			httpExchange.close();
		}
	}

	public void start() {
		System.out.println("Запускаем HTTP-сервер на порту " + PORT);
		server.start();
	}

	public void stop() {
		server.stop(0);
		System.out.println("Остановили HTTP-сервер на порту " + PORT);

	}

	private String readText(HttpExchange h) throws IOException {
		return new String(h.getRequestBody().readAllBytes(), UTF_8);
	}

	private void sendText(HttpExchange h, String text) throws IOException {
		byte[] resp = text.getBytes(UTF_8);
		h.getResponseHeaders().add("Content-Type", "application/json");
		h.sendResponseHeaders(200, 0);
		h.getResponseBody().write(resp);
	}

	private int parsePathId(String path) {
		try {
			return Integer.parseInt(path);
		} catch (NumberFormatException exception) {
			return -1;
		}
	}
}

