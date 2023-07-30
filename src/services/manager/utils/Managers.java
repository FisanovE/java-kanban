package services.manager.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import services.manager.event.HistoryManager;
import services.manager.event.TasksManager;
import services.manager.event.impl.FileBackedTasksManager;
import services.manager.event.impl.HttpTaskManager;
import services.manager.event.impl.InMemoryHistoryManager;
import services.manager.event.impl.InMemoryTasksManager;
import services.manager.utils.CustomLocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {

	public static TasksManager getDefault() {
		return new InMemoryTasksManager();
	}

	public static HistoryManager getDefaultHistory() {
		return new InMemoryHistoryManager();
	}
	public static FileBackedTasksManager getDefaultFileManager(String path) {
		return new FileBackedTasksManager(path);
	}

	public static HttpTaskManager getDefaultHttpManager(String path) throws IOException, InterruptedException {
		return new HttpTaskManager(path);
	}

	public static Gson getGson() {
		return new GsonBuilder() .registerTypeAdapter(LocalDateTime.class, new CustomLocalDateTimeAdapter().nullSafe()) .create();
	}

}
