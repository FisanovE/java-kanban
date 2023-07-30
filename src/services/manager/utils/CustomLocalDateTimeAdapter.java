package services.manager.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomLocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

	@Override
	public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
		jsonWriter.value(localDateTime.format(DateUtils.formatter2));
	}

	@Override
	public LocalDateTime read(final JsonReader jsonReader) throws IOException {
		return LocalDateTime.parse(jsonReader.nextString(), DateUtils.formatter2);
	}
}
