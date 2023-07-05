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

		mnemonics.put("CONST", OP_CONSTANT);
		mnemonics.put("NEG", OP_NEGATE);
		mnemonics.put("RET", OP_RETURN);

		return mnemonics;
	}


	protected Chunk assemble(String asm) throws AssembleError {
		this.source = asm;
		Chunk chunk = new Chunk();

		List<String> lines = Arrays.asList(this.source.split("\n"));

		for (int i = 0; i < lines.size(); i++) {
			this.start = this.current = 0;
			this.line = i + 1;
			if (lines.get(this.line).isBlank()) continue;
			this.consumeWhiteSpace(lines.get(this.line));
			if (this.getCurrentChar(lines.get(this.line)) == ';' || this.isAtEnd(lines.get(this.line))) continue;
			while (isAlphaNumeric(this.getCurrentChar(lines.get(this.line))) && !this.isAtEnd(lines.get(this.line))) {
				if (isDigit(this.getCurrentChar(lines.get(this.line))))
					throw new AssembleError("Cannot have digit inside or before mnemonic.", this.line);
				this.consumeNextChar(lines.get(this.line));
			}
			String mnemonic = lines.get(this.line).substring(this.start, this.current);
			this.start = this.current;
			if (!mnemonics.containsKey(mnemonic)) throw new AssembleError("Invalid mnemonic.", this.line);
			byte instruction = mnemonics.get(mnemonic);
			chunk.writeCode(instruction, this.line);
			this.consumeWhiteSpace(lines.get(this.line));
			if (this.isAtEnd(lines.get(this.line))) continue;
			switch (instruction) {
				case OP_CONSTANT -> {
					this.consumeWhiteSpace(lines.get(this.line));
					if (this.isAtEnd(lines.get(this.line)))
						throw new AssembleError("Expected a literal after 'CONST'.", this.line);
					Object constant = this.getLiteral(lines.get(this.line));
				}
				default -> {
					continue;
				}
			}


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
					return true;
				case "false":
					return false;
				case "null":
					return null;
				default:
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



