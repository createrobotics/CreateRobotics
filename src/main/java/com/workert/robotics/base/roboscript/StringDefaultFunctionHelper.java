package com.workert.robotics.base.roboscript;
public class StringDefaultFunctionHelper {
	public static Object get(String gettable, Token name) {
		switch (name.lexeme) {
			case "length" -> {
				return RoboScript.defineCallable("length", 0, (interpreter, objects) -> gettable.length());
			}
			case "getCharAt" -> {
				return RoboScript.defineCallable("getCharAt", 1, (interpreter, objects) -> {
					if (!(objects.get(0) instanceof Double d))
						return null; //TODO: add better runtime error support right here
					if (Math.round(d) != d || d < 0)
						return null; //throw new RuntimeError(bracket, "Index must be a positive whole number.");
					if (gettable.length() - 1 >= d)
						return String.valueOf((gettable.charAt((int) (float) d.doubleValue())));
					return null; //throw new RuntimeError(bracket, "Index out of array bounds.");
				});
			}
			case "withCharAt" -> {
				return RoboScript.defineCallable("setCharAt", 2, (interpreter, objects) -> {
					if (!(objects.get(0) instanceof Double d))
						return null; //TODO: add better runtime error support right here
					if (Math.round(d) != d || d < 0)
						return null; //throw new RuntimeError(bracket, "Index must be a positive whole number.");
					if (!(objects.get(1) instanceof String s && s.length() == 1))
						return null; //throw new RuntimeError(bracket, "Value must be a String with the length of 1.");
					if (gettable.length() - 1 < d)
						return null; //throw new RuntimeError(bracket, "Index out of array bounds.");
					return gettable.substring(0, d.intValue()) + s + gettable.substring(d.intValue() + 1);
				});
			}
		}
		throw new RuntimeError(name, "Undefined property in String '" + name.lexeme + "'.");
	}
}
