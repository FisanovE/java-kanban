package services.manager.event.impl;

import client.KVTaskClient;
import com.google.gson.Gson;
import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import services.manager.utils.Managers;
import services.manager.exceptions.ManagerSaveException;

import java.io.IOException;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
	private Gson gson = Managers.getGson();
	KVTaskClient client;

	public HttpTaskManager(String path) throws IOException, InterruptedException {
		super(path);
		client = new KVTaskClient(path);
	}

	@Override
	public void save() {
		try {
			List<Task> tasks = getAllTasks();
			List<Epic> epics = getAllEpics();
			List<SubTask> subTasks = getAllSubTasks();
			List<Task> history = getAllTasks();
			int counter = getCounter();

			String jsonTasks = gson.toJson(tasks);
			client.put("tasks", jsonTasks);
			String jsonEpics = gson.toJson(epics);
			client.put("epics", jsonEpics);
			String jsonSubTasks = gson.toJson(subTasks);
			client.put("subTasks", jsonSubTasks);
			String jsonHistory = gson.toJson(history);
			client.put("history", jsonHistory);
			String jsonCounter = gson.toJson(history);
			client.put("counter", jsonCounter);

		} catch (IOException | InterruptedException e) {
			throw new ManagerSaveException("Произошла ошибка при записи файла");
		}

	}

}

