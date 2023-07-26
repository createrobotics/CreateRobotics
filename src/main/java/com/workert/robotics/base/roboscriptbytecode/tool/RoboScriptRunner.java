package com.workert.robotics.base.roboscriptbytecode.tool;
import com.workert.robotics.base.roboscriptbytecode.RoboScript;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RoboScriptRunner {

	// credit to Crafting Interpreters and the cLox language for most of the understanding of bytecode interpreters.
	// check it out at https://craftinginterpreters.com.

	// credit to jakslxpwrm on discord for helping with optimizations and a lot of C to Java conversions

	private static final String sourcePath = "src/main/java/com/workert/robotics/base/roboscriptbytecode/tool/script.robo";


	private static String readFile() throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(sourcePath));
		return new String(bytes, Charset.defaultCharset());
	}

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
		}.runString(readFile());
	}
}
