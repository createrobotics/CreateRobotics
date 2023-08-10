package com.workert.robotics.base.roboscript.tool;
import com.workert.robotics.base.roboscript.RoboScript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RoboScriptRunner {
	private static final String sourcePath = "src/main/java/com/workert/robotics/base/roboscript/tool/script.robo";

	public static void main(String[] args) throws IOException {
		new RoboScript() {
			@Override
			protected void handlePrintMessage(String message) {
				System.out.println(message);
			}

			@Override
			protected void handleErrorMessage(String error) {
				System.err.println(error);
			}
		}.runString(Files.readString(Paths.get(sourcePath)));
	}
}
