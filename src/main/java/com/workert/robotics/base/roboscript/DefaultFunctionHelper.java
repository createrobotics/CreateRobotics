package com.workert.robotics.base.roboscript;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

class DefaultFunctionHelper {
	public static void defineDefaultFunctions(RoboScript roboScript) {
		roboScript.defineFunction("print", 1, (interpreter, arguments, errorToken) -> {
			roboScript.print(Interpreter.stringify(arguments.get(0)));
			return null;
		});

		roboScript.defineFunction("sleep", 1, (interpreter, arguments, errorToken) -> {
			if (!(arguments.get(0) instanceof Double d))
				return null;
			try {
				Thread.sleep((long) Math.floor(d * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		});

		defineMathFunctions(roboScript);
	}

	private static void defineMathFunctions(RoboScript roboScript) {
		try {
			Class<StrictMath> strictMathClass = StrictMath.class;
			List<Method> methods = getPublicMethods(strictMathClass);

			List<Method> usableMathMethods = new ArrayList<>();
			usableMathMethodTest:
			for (Method method : methods) {
				if (!Modifier.isStatic(method.getModifiers())) continue;
				if (Number.class.isAssignableFrom(method.getReturnType())) continue;
				for (Class<?> parameterClass : method.getParameterTypes()) {
					if (!parameterClass.equals(double.class)) continue usableMathMethodTest;
				}
				usableMathMethods.add(method);
			}

			for (Method mathMethod : usableMathMethods) {
				roboScript.defineFunction(mathMethod.getName(), mathMethod.getParameterCount(),
						(interpreter, arguments, errorToken) -> {
							try {
								return ((Number) mathMethod.invoke(null, arguments.toArray())).doubleValue();
							} catch (InvocationTargetException exception) {
								exception.printStackTrace();
								throw new RoboScriptRuntimeError(errorToken,
										exception.getCause() != null ? exception.getCause()
												.getMessage() : "An internal Java exception occurred. Please take a look at the game logs to learn more.");
							} catch (IllegalAccessException e) {
								throw new RuntimeException(e);
							}
						});
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static List<Method> getPublicMethods(Class clazz) {
		List<Method> publicMethods = new ArrayList<>();
		for (Method method : clazz.getDeclaredMethods()) {
			if (!Modifier.isPublic(method.getModifiers())) continue;
			if (!Modifier.isStatic(method.getModifiers())) continue;
			publicMethods.add(method);
		}
		return publicMethods;
	}
}
