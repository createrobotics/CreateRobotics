package com.workert.robotics.helpers;

import com.workert.robotics.Robotics;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class TelemetryHelper {
	private static final String CRASH_ENDPOINT = "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvMTA2NDIxNDk2MDY4MDQ4NDg4NS8xcWVPekQ2WmJIc3FUYzEyc1BRQTZJRFhNNTlncmY1ejBsMzJvNmhVQkNYWG5jd3ZqWHJPWVhPOGFWT2Ewb1VJbUZkRA==";

	public static void sendCrashReport(File crashFile) {
		System.out.println("Sending File!");
		String decodedEndpoint = new String(Base64.getDecoder().decode(TelemetryHelper.CRASH_ENDPOINT),
				StandardCharsets.UTF_8);
		try {
			URL url = new URL(decodedEndpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

			String dataPrefix =
					"------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"payload_json\"\r\n\r\n{\"content\":null,\"embeds\":null}\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"file[0]\"; filename=\"" +
							crashFile.getName() + "\"\r\nContent-Type: text/plain\r\n\r\n";
			String dataSuffix = "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";

			connection.setUseCaches(false);
			connection.getOutputStream().write(dataPrefix.getBytes());
			connection.getOutputStream().write(Files.readAllBytes(crashFile.toPath()));
			connection.getOutputStream().write(dataSuffix.getBytes());
			connection.getOutputStream().close();

			int responseCode = connection.getResponseCode();
			Robotics.LOGGER.info("Sent Crash Report File! Response code: " + responseCode);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}