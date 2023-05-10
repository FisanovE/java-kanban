public class Task {
	private String name;
	private String annotation;
	private int ID;
	private String status;

	public Task(String name, String annotation) {
		this.name = name;
		this.annotation = annotation;
	}

	public Task(int ID, String name, String annotation) {
		this.ID = ID;
		this.name = name;
		this.annotation = annotation;
	}

	public Task(int ID, String name, String annotation, String status) {
		this.ID = ID;
		this.name = name;
		this.annotation = annotation;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public String getAnnotation() {
		return annotation;
	}

	public int getID() {
		return ID;
	}

	public String getStatus() {
		return status;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		String result = "__" +
				"__" + name +
				"__" + status +
				"__" + annotation + "\n";
		return result;
	}
}


