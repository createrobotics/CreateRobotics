package com.workert.robotics.base.roboscriptbytecode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;

public class Assembler {
	private static final Map<String, Byte> mnemonics = initializeMnemonics();
	private String source;

	private int start = 0;
	private int current = 0;
	private int line = 1;

	private static Map<String, Byte> initializeMnemonics() {
		Map<String, Byte> mnemonics = new HashMap<>();

		mnemonics.put("push", OP_CONSTANT);
		mnemonics.put("neg", OP_NEGATE);
		mnemonics.put("ret", OP_RETURN);

		return mnemonics;
	}


	protected Chunk assemble(String asm) {
		this.source = asm;
		Chunk chunk = new Chunk();
		List<String> lines = Arrays.asList(this.source.split("\n"));
		for (int i = 0; i < lines.size(); i++) {
			this.start = this.current = 0;
			this.line = i + 1;
			String lineString = lines.get(this.line - 1);
			if (lineString.isBlank()) continue;
			this.consumeWhiteSpace(lineString);
			if (this.getCurrentChar(lineString) == ';' || this.isAtEnd(lineString)) continue;
			while (isAlphaNumeric(this.getCurrentChar(lineString)) && !this.isAtEnd(lineString)) {
				if (isDigit(this.getCurrentChar(lineString)))
					throw new AssembleError("Cannot have digit inside or before mnemonic.", this.line);
				this.consumeNextChar(lineString);
			}
			String mnemonic = lineString.substring(this.start, this.current);
			this.start = this.current;
			if (!mnemonics.containsKey(mnemonic)) throw new AssembleError("Invalid mnemonic.", this.line);
			byte instruction = mnemonics.get(mnemonic);
			chunk.writeCode(instruction, this.line);
			this.consumeWhiteSpace(lineString);
			switch (instruction) {
				case OP_CONSTANT -> {
					if (this.isAtEnd(lineString))
						throw new AssembleError("Expected a literal after 'CONST'.", this.line);
					Object constant = this.getLiteral(lineString);
					int lookUp = chunk.addConstant(constant);
					chunk.writeCode((byte) lookUp, this.line);
					break;
				}
			}
			if (lineString.substring(0, this.current).isBlank())
				throw new AssembleError("Instruction already complete; may not have anymore arguments", this.line);
		}

		return chunk;
	}

	private void consumeWhiteSpace(String line) {
		while (this.getCurrentChar(line) == ' ') {
			this.consumeNextChar(line);
		}

		this.start = this.current;
	}

	private Object getLiteral(String line) {
		this.start = this.current;
		if (this.getCurrentChar(line) == '"') {
			this.consumeNextChar(line);
			this.start = this.current;
			while (this.getCurrentChar(line) != '"' && !this.isAtEnd(line)) {
				this.consumeNextChar(line);
			}
			if (this.isAtEnd(line)) throw new AssembleError("Unterminated string.", this.line);
			String result = line.substring(this.start, this.current);
			this.start = this.current;
			return result;
		}
		if (isDigit(this.getCurrentChar(line))) {
			while (isDigit(this.getCurrentChar(line))) {
				this.consumeNextChar(line);
			}
			double result = Double.parseDouble(line.substring(this.start, this.current));
			this.start = this.current;
			return result;
		}
		if (isAlphaNumeric(this.getCurrentChar(line))) {
			while (isAlphaNumeric(this.getCurrentChar(line))) {
				this.consumeNextChar(line);
			}
			String text = line.substring(this.start, this.current);
			switch (text) {
				case "true":
					this.start = this.current;
					return true;
				case "false":
					this.start = this.current;
					return false;
				case "null":
					this.start = this.current;
					return null;
				default:
					this.start = this.current;
					throw new AssembleError("Not a valid literal.", this.line);
			}
		}
		throw new AssembleError("Expected literal.", this.line);
	}

	private char getCurrentChar(String line) {
		if (this.isAtEnd(line)) {
			return '\0';
		}
		return line.charAt(this.current);
	}

	private char consumeNextChar(String line) {
		return line.charAt(this.current++);
	}

	private boolean isAtEnd(String line) {
		return this.current >= line.length();
	}

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private static boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private static boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}


}



