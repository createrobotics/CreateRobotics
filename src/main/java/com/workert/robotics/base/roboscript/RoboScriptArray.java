package com.workert.robotics.base.roboscript;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoboScriptArray extends RoboScriptGettable {
	public final List<Object> elements;
	private final Map<String, Object> fields = new HashMap<>();

	public RoboScriptArray(List<Object> elements) {
		this.register();
		this.elements = elements;
	}

	@Override
	public Object get(Token name) {
		if (this.fields.containsKey(name.lexeme))
			return this.fields.get(name.lexeme);

		throw new RuntimeError(name, "Undefined property in Array '" + name.lexeme + "'.");
	}

	public Object get(double a, Token bracket) {
		if (Math.round(a) != a || a < 0) throw new RuntimeError(bracket, "Index must be a positive whole number.");
		if (this.elements.size() - 1 >= a) return this.elements.get((int) (float) a);
		throw new RuntimeError(bracket, "Index out of array bounds.");
	}

	public void set(double index, Object value, Token token) {
		if (Math.round(index) != index || index < 0)
			throw new RuntimeError(token, "Index must be a positive whole number.");
		if (this.elements.size() - 1 < index) throw new RuntimeError(token, "Index out of array bounds.");
		this.elements.set((int) (float) index, value);
	}

	public void register() {
		this.defineFunction("append", 1, (interpreter, objects, errorToken) -> {
			this.elements.add(objects.get(0));
			return null;
		}, this.fields);
		this.defineFunction("add", 1, (interpreter, objects, errorToken) -> {
			this.elements.add(objects.get(0));
			return null;
		}, this.fields);
		this.defineFunction("size", 0, (interpreter, objects, errorToken) -> (double) this.elements.size(),
				this.fields);
		this.defineFunction("length", 0, (interpreter, objects, errorToken) -> (double) this.elements.size(),
				this.fields);
		this.defineFunction("clear", 0, (interpreter, objects, errorToken) -> {
			this.elements.clear();
			return null;
		}, this.fields);
		this.defineFunction("join", 1,
				(interpreter, objects, errorToken) -> String.join(Interpreter.stringify(objects.get(0)),
						stringifyAllElements(this.elements)), this.fields);
	}

	public static List<String> stringifyAllElements(List<Object> elements) {
		List<String> stringList = new ArrayList<>();
		for (Object object : elements) {
			stringList.add(Interpreter.stringify(object));
		}
		return stringList;
	}

	@Override
	public String toString() {
		return stringifyAllElements(this.elements).toString();
	}
}
