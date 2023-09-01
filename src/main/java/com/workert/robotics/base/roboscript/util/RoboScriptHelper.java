package com.workert.robotics.base.roboscript.util;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import com.workert.robotics.base.roboscript.RuntimeError;
import net.minecraft.core.BlockPos;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RoboScriptHelper {

	public static String stringify(Object object) {
		if (object == null) return "null";

		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}

		return object.toString();
	}

	public static List<String> stringifyAllElements(List<Object> elements) {
		List<String> stringList = new ArrayList<>();
		if (elements != null && !elements.isEmpty())
			for (Object object : elements) {
				stringList.add(stringify(object));
			}
		return stringList;
	}

	public static String asString(Object object, String message) {
		if (object instanceof String s) return s;
		throw new RuntimeError(message);
	}

	public static String asString(Object object) {
		return asString(object, "Argument must be a string.");
	}

	public static String asNonEmptyString(Object object, String message) {
		String string = asString(object, message);
		if (!string.isEmpty())
			return string;
		throw new RuntimeError(message);
	}

	public static String asNonEmptyString(Object object) {
		return asNonEmptyString(object, "Argument must be a non-empty string.");
	}

	public static double asDouble(Object object, String message) {
		if (object instanceof Double d) return d;
		throw new RuntimeError(message);
	}

	public static double asDouble(Object object) {
		return asDouble(object, "Argument must be a number.");
	}

	public static double asPositiveDouble(Object object, String message) {
		if (!(object instanceof Double d) || d < 0) throw new RuntimeError(message);
		return d;
	}

	public static double asPositiveDouble(Object object) {
		return asPositiveDouble(object, "Argument must be a positive number.");
	}

	public static double asWholeDouble(Object object, String message) {
		if (!(object instanceof Double d) || Math.round(d) != d) throw new RuntimeError(message);
		return d;
	}

	public static double asWholeDouble(Object object) {
		return asWholeDouble(object, "Argument must be a whole number.");
	}

	public static double asPositiveWholeDouble(Object object, String message) {
		if (!(object instanceof Double d) || d < 0 || Math.round(d) != d) throw new RuntimeError(message);
		return d;
	}

	public static double asPositiveWholeDouble(Object object) {
		return asPositiveWholeDouble(object, "Argument must be a positive whole number.");
	}

	public static boolean asBool(Object object, String message) {
		if (object instanceof Boolean b) return b;
		throw new RuntimeError(message);
	}

	public static boolean asBool(Object object) {
		return asBool(object, "Argument must be a bool.");
	}

	public static int doubleToInt(double d) {
		if (Math.round(d) != d) throw new IllegalArgumentException(
				"Passed in value for 'doubleToInt' was not a whole number. Be sure to check it beforehand.");
		return (int) d;
	}

	public static double numToDouble(Number n) {
		return n.doubleValue();
	}

	public static List<Object> arrayToRoboScriptList(Object object) {
		if (!object.getClass().isArray()) throw new IllegalArgumentException(
				"Passed in value for 'arrayToRoboScriptList' was not an array. Be sure to check it beforehand.");
		List<Object> list = new ArrayList<>();
		int length = Array.getLength(object);

		for (int i = 0; i < length; i++) {
			list.add(prepareForRoboScriptUse(Array.get(object, i)));
		}

		return list;
	}

	public static List<Object> listToRoboScriptList(List<?> list) {
		List<Object> preparedList = new ArrayList<>();
		for (Object listObject : list) {
			preparedList.add(prepareForRoboScriptUse(listObject));
		}
		return preparedList;
	}

	public static BlockPos asBlockPos(Object a, Object b, Object c, String message) {
		int ai = doubleToInt(asWholeDouble(a, message));
		int bi = doubleToInt(asWholeDouble(a, message));
		int ci = doubleToInt(asWholeDouble(a, message));
		return new BlockPos(ai, bi, ci);
	}

	public static BlockPos asBlockPos(Object a, Object b, Object c) {
		return asBlockPos(a, b, c, "Argument must be a whole number.");
	}


	public static Object prepareForRoboScriptUse(Object object) {
		if (object == null)
			return null;

		if (object instanceof List<?> list) {
			return listToRoboScriptList(list);
		}

		if (object.getClass().isArray()) {
			return arrayToRoboScriptList(object);
		}

		if (object instanceof Number number)
			return numToDouble(number);

		if (object instanceof CharSequence charSequence)
			return charSequence.toString();

		if (object instanceof RoboScriptObject) {
			return object;
		}

		throw new IllegalArgumentException("Illegal RoboScript method return type");
	}

}
