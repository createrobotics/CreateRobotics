package com.workert.robotics.helpers.exceptions;

import java.io.Serial;
import java.util.List;

import com.workert.robotics.helpers.CodeHelper;

/**
 * Thrown to indicate that a registered command in {@link CodeHelper} has been
 * passed an illegal or inappropriate argument.
 */
public class IllegalCommandArgumentTypeException extends RuntimeException {
	public List<Class<?>> expectedArgumentTypes;
	public String prefix;

	/**
	 * Constructs an {@code IllegalCommandArgumentTypeException} with the specified
	 * prefix and expected argumentTypes.
	 *
	 * @param prefix the command prefix, see {@link CodeHelper#registerCommand}.
	 * @param expectedArgumentTypes a list with all expected argument type classes
	 * in the expected order, like <code>List.of(String.class, Double.class)</code>
	 */
	public IllegalCommandArgumentTypeException(String prefix, List<Class<?>> expectedArgumentTypes) {
		super();
		this.prefix = prefix;
		this.expectedArgumentTypes = expectedArgumentTypes;
	}

	@Override
	public String getLocalizedMessage() {
		String expectedTypes = "";
		this.expectedArgumentTypes.forEach(argumentClass -> expectedTypes.concat(argumentClass.getName()));
		return "The command \"" + this.prefix + "\" expected arguments of types " + expectedTypes;
	}

	@Serial
	private static final long serialVersionUID = 8966731762969884824L;
}
