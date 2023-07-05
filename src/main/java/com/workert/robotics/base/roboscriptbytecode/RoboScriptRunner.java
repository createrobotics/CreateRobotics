package com.workert.robotics.base.roboscriptbytecode;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;

public class RoboScriptRunner {
	private static final String sourcePath = "src/main/java/com/workert/robotics/base/roboscriptbytecode/tool/script.roboasm";


	private static String readFile() throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(sourcePath));
		return new String(bytes, Charset.defaultCharset());
	}

	public static void main(String[] args) {
		Chunk chunk = new Chunk();
		int constant = chunk.addConstant(1);
		chunk.writeCode(OP_CONSTANT, 1);
		chunk.writeCode((byte) constant, 1);

		constant = chunk.addConstant(2);
		chunk.writeCode(OP_CONSTANT, 1);
		chunk.writeCode((byte) constant, 1);

		chunk.writeCode(OP_ADD, 1);


		// chunk.writeCode(OP_NEGATE, 1);
		chunk.writeCode(OP_RETURN, 1);
		Printer printer = new Printer();
		printer.disassembleChunk(chunk, "test chunk");
		VirtualMachine vm = new VirtualMachine();
		vm.interpret(chunk);
	}
}
