package com.workert.robotics.base.roboscriptbytecode.tool;
import com.workert.robotics.base.roboscriptbytecode.RoboScript;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RoboASMRunner {
	private static final String sourcePath = "src/main/java/com/workert/robotics/base/roboscriptbytecode/tool/script.roboasm";


	private static String readFile() throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(sourcePath));
		return new String(bytes, Charset.defaultCharset());
	}

	public static void main(String[] args) throws IOException {
		String source = readFile();
		new RoboScript() {
			@Override
			protected void handleErrorMessage(String message) {
				System.err.println(message);
			}

			@Override
			protected void handlePrintMessage(String message) {
				System.out.println(message);
			}
		}.runASMString(source);
	}

}
