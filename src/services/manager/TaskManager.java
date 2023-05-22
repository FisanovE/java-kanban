package services.manager;

import models.business.Epic;
import models.business.SubTask;
import models.business.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

	public int addNewTask(Task task);

	public int addNewEpicTask(Epic epic);

	public int addNewSubTask(SubTask sub);

	public void updateTask(Task task);

	public void updateEpic(Epic epic);

	public void updateSubTask(SubTask sub);

	public Task getTaskByID(int ID);

	public Epic getEpicByID(int ID);

	public SubTask getSubTaskByID(int ID);

	public ArrayList<Task> getAllTasks();

	public ArrayList<Epic> getAllEpics();

	public ArrayList<SubTask> getAllSubTasks();

	public ArrayList<SubTask> getAllSubTasksByEpic(int ID);

	public void removeTaskByID(int ID);

	public void removeEpicByID(int ID);

	public void removeSubTaskByID(int ID);

	public void removeAllTasks();

	public void removeAllEpics();

	public void removeAllSubTasks(int ID);


}
