public class SubTask extends Task {
	private int epicID;

	public SubTask(int ID, String name, String annotation, String status, int epicID) {
		super(ID, name, annotation, status);
		this.epicID = epicID;
	}

	public int getEpicID() {
		return epicID;
	}
}
