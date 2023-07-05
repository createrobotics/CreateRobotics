package com.workert.robotics.base.roboscriptbytecode;
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
		Assembler assembler = new Assembler();
		String source = readFile();
		try {
			Chunk c = assembler.assemble(source);
			new Printer().disassembleChunk(c, "From ASM");
			new VirtualMachine().interpret(c);

		} catch (AssembleError e) {
			System.err.println("[line " + e.line + "] " + e.message);
			return;
		}
	}

}
