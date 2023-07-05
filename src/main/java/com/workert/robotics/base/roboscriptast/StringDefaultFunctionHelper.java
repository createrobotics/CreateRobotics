package com.workert.robotics.base.roboscriptast;
public class StringDefaultFunctionHelper {
	public static Object get(String string, Token name) {
		switch (name.lexeme) {
			case "length" -> {
				return RoboScript.defineCallable(name.lexeme, 0, (interpreter, objects, errorToken) -> string.length());
			}
			case "getCharAt" -> {
				return RoboScript.defineCallable(name.lexeme, 1, (interpreter, objects, errorToken) -> {
					int index = new RoboScriptArgumentPredicates(errorToken).asPositiveFullNumber(objects.get(0), true);

					if (string.length() - 1 < index)
						throw new RoboScriptRuntimeError(errorToken, "Index out of array bounds.");
					return String.valueOf((string.charAt(index)));
				});
			}
			case "withCharAt" -> {
				return RoboScript.defineCallable(name.lexeme, 2, (interpreter, objects, errorToken) -> {
					RoboScriptArgumentPredicates predicates = new RoboScriptArgumentPredicates(errorToken);
					int index = predicates.asPositiveFullNumber(objects.get(0), true);
					String newChar = predicates.asNonEmptyString(objects.get(1));
					if (newChar.length() != 1)
						throw new RoboScriptRuntimeError(errorToken, "Value must be a String with the length of 1.");
					if (string.length() - 1 < index)
						throw new RoboScriptRuntimeError(errorToken, "Index out of array bounds.");

					return string.substring(0, index) + newChar + string.substring(index + 1);
				});
			}
			case "substring" -> {
				return RoboScript.defineCallable(name.lexeme, 2, (interpreter, objects, errorToken) -> {
					RoboScriptArgumentPredicates predicates = new RoboScriptArgumentPredicates(errorToken);
					int index1 = predicates.asPositiveFullNumber(objects.get(0), true);
					int index2 = predicates.asPositiveFullNumber(objects.get(1), true);

					if (string.length() - 1 < index1 || string.length() - 1 < index2)
						throw new RoboScriptRuntimeError(errorToken, "Index out of array bounds.");

					return string.substring(index1, index2);
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
					RoboScriptArgumentPredicates predicates = new RoboScriptArgumentPredicates(errorToken);
					String target = predicates.asNonEmptyString(objects.get(0));
					String replacement = predicates.asString(objects.get(1));
					return string.replace(target, replacement);
				});
			}
			case "regexReplace" -> {
				return RoboScript.defineCallable(name.lexeme, 2, (interpreter, objects, errorToken) -> {
					RoboScriptArgumentPredicates predicates = new RoboScriptArgumentPredicates(errorToken);
					String regex = predicates.asNonEmptyString(objects.get(0));
					String replacement = predicates.asString(objects.get(1));
					return string.replaceAll(regex, replacement);
				});
			}
		}
		throw new RoboScriptRuntimeError(name, "Undefined property in String '" + name.lexeme + "'.");
	}
}
