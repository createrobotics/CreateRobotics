package com.workert.robotics.helpers;

import com.workert.robotics.Robotics;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class TelemetryHelper {
	private static final String CRASH_ENDPOINT = "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvMTA2NDIxNDk2MDY4MDQ4NDg4NS8xcWVPekQ2WmJIc3FUYzEyc1BRQTZJRFhNNTlncmY1ejBsMzJvNmhVQkNYWG5jd3ZqWHJPWVhPOGFWT2Ewb1VJbUZkRA==";

	protected static boolean closedCrashGui = false;

	public static void sendCrashReport(File crashFile) {
		CompletableFuture.runAsync(() -> {
			TelemetryHelper.setHeadless(false);
			JFrame frame = new JFrame();
			// TODO Better layout of text and buttons
			// TODO automatic close timer
			frame.setLayout(new CardLayout());

			frame.setName("Crash Reporting");
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

			JPanel buttonPanel = new JPanel();
			JButton sendButton = new JButton();
			sendButton.setSize(200, 80);
			sendButton.setText("Submit Crash Report");
			sendButton.setAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {

					Robotics.LOGGER.info("Sending Crash Report File");
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
					} catch (IOException exception) {
						throw new RuntimeException(exception);
					}
					TelemetryHelper.closedCrashGui = true;
				}
			});
			buttonPanel.add(sendButton);

			JButton closeButton = new JButton();
			closeButton.setSize(200, 80);
			closeButton.setText("Don't send and close");
			closeButton.setAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					TelemetryHelper.closedCrashGui = true;
				}
			});
			buttonPanel.add(closeButton);

			JPanel labelPanel = new JPanel();
			JLabel label = new JLabel();
			label.setText("Minecraft Crashed and it seems that you have a Test Version of Create Robotics installed." +
					CommonComponents.NEW_LINE.getString() +
					"Would you like to submit the Crash Report to Create Robotics?");
			labelPanel.add(label);

			JPanel cards = new JPanel(new CardLayout());
			cards.add(buttonPanel);
			cards.add(labelPanel);

			frame.add(labelPanel, BorderLayout.PAGE_START);
			frame.add(cards, BorderLayout.CENTER);

			frame.setResizable(false);
			frame.setSize(500, 120);
			frame.requestFocus();
			frame.setVisible(true);
			TelemetryHelper.setHeadless(true);
		});

		if (Minecraft.getInstance().getWindow().isFullscreen()) {
			Minecraft.getInstance().getWindow().toggleFullScreen();
			Minecraft.getInstance().options.fullscreen().set(false);
		}
		Minecraft.getInstance().setWindowActive(false);

		while (!TelemetryHelper.closedCrashGui) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void setHeadless(boolean headless) {
		System.setProperty("java.awt.headless", Boolean.toString(headless));
		if (GraphicsEnvironment.isHeadless() != headless)
			Robotics.LOGGER.warn("Couldn't change Java Headless Mode to " + headless);
	}
}