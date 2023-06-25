package services.manager;

import models.business.Epic;
import models.business.SubTask;
import models.business.Task;

import java.util.List;

public interface TasksManager {

	int addNewTask(Task task);

	int addNewEpic(Epic epic);

	int addNewSubTask(SubTask sub);

	void updateTask(Task task);

	void updateEpic(Epic epic);

	void updateSubTask(SubTask sub);

	Task getTaskByID(int ID);

	Epic getEpicByID(int ID);

	SubTask getSubTaskByID(int ID);

	List<Task> getAllTasks();

	List<Epic> getAllEpics();

	List<SubTask> getAllSubTasks();

	List<SubTask> getAllSubTasksByEpic(int ID);

	void removeTaskByID(int ID);

	void removeEpicByID(int ID);

	void removeSubTaskByID(int ID);

	void removeAllTasks();

	void removeAllEpics();

	void removeAllSubTasks(int ID);

	List<Task> getHistory();
}
