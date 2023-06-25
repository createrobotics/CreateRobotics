package com.workert.robotics.base.roboscript;
public class StringDefaultFunctionHelper {
	public static Object get(String string, Token name) {
		switch (name.lexeme) {
			case "length" -> {
				return RoboScript.defineCallable(name.lexeme, 0, (interpreter, objects, errorToken) -> string.length());
			}
			case "getCharAt" -> {
				return RoboScript.defineCallable(name.lexeme, 1, (interpreter, objects, errorToken) -> {
					if (!(objects.get(0) instanceof Double index))
						throw new RoboScriptRuntimeError(errorToken, "Index must be a number.");
					if (index.isNaN() || Math.round(index) != index || index < 0)
						throw new RoboScriptRuntimeError(errorToken, "Index must be a positive whole number.");
					if (string.length() - 1 < index)
						throw new RoboScriptRuntimeError(errorToken, "Index out of array bounds.");
					return String.valueOf((string.charAt((int) index.doubleValue())));
				});
			}
			case "withCharAt" -> {
				return RoboScript.defineCallable(name.lexeme, 2, (interpreter, objects, errorToken) -> {
					if (!(objects.get(0) instanceof Double index))
						throw new RoboScriptRuntimeError(errorToken, "Index must be a number.");
					if (index.isNaN() || Math.round(index) != index || index < 0)
						throw new RoboScriptRuntimeError(errorToken, "Index must be a positive whole number.");
					if (!(objects.get(1) instanceof String s && s.length() == 1))
						throw new RoboScriptRuntimeError(errorToken, "Value must be a String with the length of 1.");
					if (string.length() - 1 < index)
						throw new RoboScriptRuntimeError(errorToken, "Index out of array bounds.");

					return string.substring(0, index.intValue()) + s + string.substring(index.intValue() + 1);
				});
			}
			case "substring" -> {
				return RoboScript.defineCallable(name.lexeme, 2, (interpreter, objects, errorToken) -> {
					if (!(objects.get(0) instanceof Double index1 && objects.get(1) instanceof Double index2))
						throw new RoboScriptRuntimeError(errorToken, "Both indexes must be a number.");
					if ((index1.isNaN() || Math.round(index1) != index1 || index1 < 0) || (index2.isNaN() || Math.round(
							index2) != index2 || index2 < 0))
						throw new RoboScriptRuntimeError(errorToken, "Indexes must be a positive whole number.");
					if (string.length() - 1 < index1 || string.length() - 1 < index2)
						throw new RoboScriptRuntimeError(errorToken, "Index out of array bounds.");

					return string.substring(index1.intValue(), index2.intValue());
				});
			}
			case "toUpperCase" -> {
				return RoboScript.defineCallable(name.lexeme, 0,
						(interpreter, objects, errorToken) -> string.toUpperCase());
			}
			case "toLowerCase" -> {
				return RoboScript.defineCallable(name.lexeme, 0,
						(interpreter, objects, errorToken) -> string.toLowerCase());
			}
			case "replace" -> {
				return RoboScript.defineCallable(name.lexeme, 2, (interpreter, objects, errorToken) -> {
					if (!(objects.get(0) instanceof String target && objects.get(1) instanceof String replacement))
						throw new RoboScriptRuntimeError(errorToken, "Both target and replacement must be a String.");
					return string.replace(target, replacement);
				});
			}
			case "regexReplace" -> {
				return RoboScript.defineCallable(name.lexeme, 2, (interpreter, objects, errorToken) -> {
					if (!(objects.get(0) instanceof String regex && objects.get(1) instanceof String replacement))
						throw new RoboScriptRuntimeError(errorToken, "Both regex and replacement must be a String.");
					return string.replaceAll(regex, replacement);
				});
			}
		}
		throw new RoboScriptRuntimeError(name, "Undefined property in String '" + name.lexeme + "'.");
	}
}
