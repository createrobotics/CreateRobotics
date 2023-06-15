package com.workert.robotics.base.roboscript.ingame;
import java.util.Arrays;
import java.util.List;

public class LineLimitedString {
	private String baseString = "";
	public final int lineLimit;

	public LineLimitedString(int lineLimit) {
		this.lineLimit = lineLimit;
	}

	public LineLimitedString(int lineLimit, String baseString) {
		this.lineLimit = lineLimit;
		this.addText(baseString);
	}

	public void addText(String text) {
		StringBuilder builder = new StringBuilder(this.baseString);
		builder.append(text);

		List<String> lines = Arrays.asList(builder.toString().split("\n"));
		int size = lines.size();
		int limit = Math.min(size, this.lineLimit);
		lines = lines.subList(size - limit, size);

		this.baseString = String.join("\n", lines);
	}

	public String getString() {
		return this.baseString;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof String string) {
			return string.equals(this.getString());
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return this.getString();
	}
}
