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
				for (Class<?> parameterClass : method.getParameterTypes()) {
					if (!parameterClass.equals(double.class)) continue usableMathMethodTest;
				}
				usableMathMethods.add(method);
			}

			for (Method mathMethod : usableMathMethods) {
				roboScript.defineFunction(mathMethod.getName(), mathMethod.getParameterCount(),
						(interpreter, arguments, errorToken) -> {
							try {
								return mathMethod.invoke(null, arguments.toArray());
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


		/*roboScript.defineFunction("sin", 1, (interpreter, arguments) -> {
			if (!(arguments.get(0) instanceof Double d))
				return null;
			return Math.sin(d);
		});
		roboScript.defineFunction("cos", 1, (interpreter, arguments) -> {
			if (!(arguments.get(0) instanceof Double d))
				return null;
			return Math.cos(d);
		});
		roboScript.defineFunction("tan", 1, (interpreter, arguments) -> {
			if (!(arguments.get(0) instanceof Double d))
				return null;
			return Math.tan(d);
		});

		roboScript.defineFunction("ceil", 1, (interpreter, arguments) -> {
			if (!(arguments.get(0) instanceof Double d))
				return null;
			return Math.ceil(d);
		});
		roboScript.defineFunction("floor", 1, (interpreter, arguments) -> {
			if (!(arguments.get(0) instanceof Double d))
				return null;
			return Math.floor(d);
		});*/
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
