package client;

import com.google.gson.Gson;
import server.KVServer;
import services.manager.utils.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
	String url;
	private static String apiToken;

	public KVTaskClient(String url) throws IOException, InterruptedException {
		this.url = url;
		HttpClient client = HttpClient.newHttpClient();

		URI uriRegister = URI.create(url + "/register");
		HttpRequest requestRegister = HttpRequest.newBuilder().uri(uriRegister).GET().build();

		HttpResponse<String> responseRegister = client.send(requestRegister, HttpResponse.BodyHandlers.ofString());

		apiToken = responseRegister.body();
	}

	public void put(String key, String json) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uriSave = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);

		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
		HttpRequest requestSave = HttpRequest.newBuilder().uri(uriSave).header("Content-type", "application/json")
											 .POST(body).build();

		HttpResponse<String> responseSave = client.send(requestSave, HttpResponse.BodyHandlers.ofString());
	}

	public String load(String key) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		URI uriLoad = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);
		HttpRequest requestLoad = HttpRequest.newBuilder().uri(uriLoad).GET().build();

		HttpResponse<String> responseLoad = client.send(requestLoad, HttpResponse.BodyHandlers.ofString());
		return responseLoad.body();
	}

}
