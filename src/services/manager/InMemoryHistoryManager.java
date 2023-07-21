package services.manager;

import models.business.Node;
import models.business.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
	private final Map<Integer, Node<Task>> nodeTaskStorage = new HashMap<>();
	private final CustomLinkedList<Task> historyStorage = new CustomLinkedList<>();

	@Override
	public void add(Task task) {
		if (task != null) {
			if (nodeTaskStorage.get(task.getID()) != null) {
				historyStorage.removeNode(nodeTaskStorage.get(task.getID()));
			}
			nodeTaskStorage.put(task.getID(), historyStorage.linkLast(task));
		}
	}

	@Override
	public List<Task> getHistory() {
		return new ArrayList<>(historyStorage.getTasks());
	}

	public void removeTaskFromHistory(int id) {
		if (nodeTaskStorage.get(id) != null) {
			historyStorage.removeNode(nodeTaskStorage.get(id));
			nodeTaskStorage.remove(id);
		} else {
			System.out.println("Задача с этим номером ID отсутствует.");
		}
	}

	private static class CustomLinkedList<Task> {
		private Node<Task> head;
		private Node<Task> tail;
		private int size = 0;

		public Node<Task> linkLast(Task task) {
			final Node<Task> oldTail = tail;
			final Node<Task> newNode = new Node<>(oldTail, task, null);
			tail = newNode;
			if (oldTail == null) {
				head = newNode;
			} else {
				oldTail.next = newNode;
			}
			size++;
			return newNode;
		}

		ArrayList<Task> getTasks() {
			ArrayList<Task> history = new ArrayList<>();
			Node<Task> it = tail;
			if (it != null) {
				while (true) {
					history.add(it.task);
					if (it.prev == null) {
						break;
					} else {
						it = it.prev;
					}
				}
			}
			return history;
		}

		void removeNode(Node<Task> node) {
			final Node<Task> next = node.next;
			final Node<Task> prev = node.prev;

			if (prev == null) {
				head = next;
			} else {
				prev.next = next;
				node.prev = null;
			}

			if (next == null) {
				tail = prev;
			} else {
				next.prev = prev;
				node.next = null;
			}

			node.task = null;
			size--;
		}
	}
}
