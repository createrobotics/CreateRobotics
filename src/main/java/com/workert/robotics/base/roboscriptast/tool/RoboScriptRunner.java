package com.workert.robotics.base.roboscriptast.tool;
import com.workert.robotics.base.roboscriptast.RoboScript;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class RoboScriptRunner {
	private static final String sourcePath = "src/main/java/com/workert/robotics/base/roboscriptast/tool/script.robo";

	private static final RoboScript program = new RoboScript() {
		@Override
		public void print(String message) {
			System.out.println(message);
		}

		@Override
		public void reportCompileError(String error) {
			System.err.println(error);
		}
	};

	private static Map<String, Object> variableMap = new HashMap<>();

	public static void main(String[] args) throws IOException {
		runFile();
	}

	private static void runFile() throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(sourcePath));
		program.runString(new String(bytes, Charset.defaultCharset()));
	}
}