package com.workert.robotics.helpers;

import com.workert.robotics.Robotics;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TelemetryHelper {
	private static final String CRASH_ENDPOINT = "YUhSMGNITTZMeTlrYVhOamIzSmtMbU52YlM5aGNHa3ZkMlZpYUc5dmEzTXZNVEEyTkRJeE5EazJNRFk0TURRNE5EZzROUzh4Y1dWUGVrUTJXbUpJYzNGVVl6RXljMUJSUVRaSlJGaE5OVGxuY21ZMWVqQnNNekp2Tm1oVlFrTllXRzVqZDNacVdISlBXVmhQT0dGV1QyRXdiMVZKYlVaa1JBPT0=";

	protected static boolean closedCrashGui = false;

	public static void sendCrashReport(String crashText) {
		if (crashText.matches("(?s).*\\bMod\\b.*\\brequires\\b.*")) {
			TelemetryHelper.closedCrashGui = true;
			return;
		}

		TelemetryHelper.setHeadless(false);

		CompletableFuture.runAsync(() -> {
			try {
				UIManager.setLookAndFeel(new NimbusLookAndFeel());
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}

			JFrame frame = new JFrame("Crash Reporting");

			JPanel content = new JPanel(new BorderLayout());
			content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			JLabel crashInfo = new JLabel(
					"<html>Minecraft crashed, and it seems like you<br>have a Test Version of Create Robotics installed.<br>Would you like to submit the<br>Crash Report to Create Robotics?</html>",
					SwingConstants.CENTER);
			crashInfo.setFont(new Font("Sans-Serif", Font.PLAIN, 18));
			crashInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			JPanel buttons = new JPanel();
			Dimension buttonDimension = new Dimension(180, 40);

			JButton sendButton = new JButton();
			sendButton.setPreferredSize(buttonDimension);
			sendButton.setAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Robotics.LOGGER.info("Sending Crash Report File");
					String decodedEndpoint = new String(
							Base64.getDecoder().decode(Base64.getDecoder().decode(TelemetryHelper.CRASH_ENDPOINT)),
							StandardCharsets.UTF_8);
					try {
						URL url = new URL(decodedEndpoint);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();

						connection.setDoOutput(true);
						connection.setRequestMethod("POST");
						connection.setRequestProperty("Content-Type",
								"multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

						String dataPrefix = "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"payload_json\"\r\n\r\n{\"content\":null,\"embeds\":[{\"title\":\"Crash Report\",\"color\":16711680,\"author\":{\"name\":\"" + (Minecraft.getInstance().player == null ? "Couldn't get player name" : (Minecraft.getInstance().player.getDisplayName()
								.getString() + "\",\"icon_url\": \"https://crafatar.com/avatars/uuid")) + "\"}}],\"attachments\":[]}\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"file[0]\"; filename=\"crash-" + Util.getFilenameFormattedDateTime() + ".txt\"\r\nContent-Type: text/plain\r\n\r\n";
						String dataSuffix = "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--";

						connection.setUseCaches(false);
						connection.getOutputStream().write(dataPrefix.getBytes());
						connection.getOutputStream().write(crashText.getBytes());
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
			sendButton.setText("Submit Crash Report");

			JButton closeButton = new JButton();
			closeButton.setPreferredSize(buttonDimension);

			closeButton.setAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					TelemetryHelper.closedCrashGui = true;
				}
			});
			closeButton.setText("Don't send and close");

			buttons.add(sendButton);
			buttons.add(closeButton);

			JLabel autoCloseInfo = new JLabel("<html><i>This Screen will auto-close in 15 seconds</i></html>",
					SwingConstants.CENTER);
			autoCloseInfo.setFont(new Font("Serif", Font.PLAIN, 12));
			autoCloseInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			content.add(crashInfo, BorderLayout.NORTH);
			content.add(buttons, BorderLayout.CENTER);
			content.add(autoCloseInfo, BorderLayout.SOUTH);

			frame.add(content);

			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.setResizable(false);
			frame.pack();
			frame.requestFocus();
			frame.setVisible(true);

			Executors.newSingleThreadScheduledExecutor()
					.schedule(() -> TelemetryHelper.closedCrashGui = true, 15, TimeUnit.SECONDS);
		});

		Minecraft.getInstance().getWindow().close();

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

		if (GraphicsEnvironment.isHeadless() != headless) {
			Robotics.LOGGER.error("Couldn't change Java Headless Mode to " + headless);
			System.exit(-42);
		}
	}
}
