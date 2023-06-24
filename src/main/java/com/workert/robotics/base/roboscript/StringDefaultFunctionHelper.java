package com.workert.robotics.base.roboscript;
public class StringDefaultFunctionHelper {
	public static Object get(String gettable, Token name) {
		switch (name.lexeme) {
			case "length" -> {
				return RoboScript.defineCallable("length", 0, (interpreter, objects, errorToken) -> gettable.length());
			}
			case "getCharAt" -> {
				return RoboScript.defineCallable("getCharAt", 1, (interpreter, objects, errorToken) -> {
					if (!(objects.get(0) instanceof Double d))
						throw new RuntimeError(errorToken, "Index must be a number.");
					if (Math.round(d) != d || d < 0)
						throw new RuntimeError(errorToken, "Index must be a positive whole number.");
					if (gettable.length() - 1 >= d)
						return String.valueOf((gettable.charAt((int) (float) d.doubleValue())));
					throw new RuntimeError(errorToken, "Index out of array bounds.");
				});
			}
			case "withCharAt" -> {
				return RoboScript.defineCallable("setCharAt", 2, (interpreter, objects, errorToken) -> {
					if (!(objects.get(0) instanceof Double d))
						throw new RuntimeError(errorToken, "Index must be a number.");
					if (Math.round(d) != d || d < 0)
						throw new RuntimeError(errorToken, "Index must be a positive whole number.");
					if (!(objects.get(1) instanceof String s && s.length() == 1))
						throw new RuntimeError(errorToken, "Value must be a String with the length of 1.");
					if (gettable.length() - 1 < d)
						throw new RuntimeError(errorToken, "Index out of array bounds.");
					return gettable.substring(0, d.intValue()) + s + gettable.substring(d.intValue() + 1);
				});
			}
		}
		throw new RuntimeError(name, "Undefined property in String '" + name.lexeme + "'.");
	}
}
