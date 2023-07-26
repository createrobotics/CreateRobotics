package com.workert.robotics.base.roboscriptast;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Function;

/**
 * The {@code RoboScriptArgumentPredicates} class provides utility methods for validating and converting objects to specific types
 * while allowing for nullable arguments.
 */
public class RoboScriptArgumentPredicates {
	private final Token errorToken;

	/**
	 * Constructs a {@code RoboScriptArgumentPredicates} instance with the specified error token.
	 *
	 * @param errorToken the token to be used for error reporting
	 */
	public RoboScriptArgumentPredicates(Token errorToken) {
		this.errorToken = errorToken;
	}

	/**
	 * Converts the specified object to the specified return type using the provided function, but allows for the argument to be null.
	 * <p>
	 * This method is useful when you have an object that you want to convert to a specific type but need to handle the case where the object might be null. By using the `asOptional` method, you can avoid null pointer exceptions and gracefully handle nullable arguments.
	 * <p>
	 * <b>Code Example:</b>
	 * <pre>{@code
	 * RoboScriptArgumentPredicates predicates = new RoboScriptArgumentPredicates(YOUR_ERROR_TOKEN_HERE);
	 *
	 * Object maybeNumber;
	 *
	 * // Convert `maybeNumber` to a `Double` using the `asNumber` method, allowing for null input
	 * Double definitelyNumberOrNull = predicates.asOptional(maybeNumber, predicates::asNumber);
	 * System.out.println(definitelyNumberOrNull); // This will definitely be a number or null. If `maybeNumber` is neither, it will throw.
	 * }</pre>
	 * <p>
	 * You can also use a lambda expression or method reference as the conversion function. This allows for more complex conversions or custom logic:
	 * <p>
	 * <b>Another more complex code example:</b>
	 * <pre>{@code
	 * // Convert `maybeNumber` to a positive `Double` number, allowing for null input
	 * Double definitelyPositiveNumberOrNull = predicates.asOptional(maybeNumber, (object) -> predicates.asPositiveNumber(object, true));
	 * System.out.println(definitelyPositiveNumberOrNull); // Definitely a positive number or null.
	 * }</pre>
	 *
	 * @param object       the object to be converted
	 * @param function     the conversion function, which takes an object and returns the desired return type
	 * @param <ReturnType> the type of the returned object
	 * @return the converted object, or null if the input object is null
	 */
	public <ReturnType> ReturnType optional(Object object, Function<Object, ReturnType> function) {
		if (object == null) return null;
		return function.apply(object);
	}

	/**
	 * Converts the specified object to a {@code Double} number.
	 *
	 * @param object the object to be converted
	 * @return the converted {@code Double} number
	 * @throws RoboScriptRuntimeError if the argument is not a number
	 */
	public Double asNumber(Object object) {
		if (object instanceof Double number && !number.isNaN())
			return number;
		throw new RoboScriptRuntimeError(this.errorToken, "Argument must be a number.");
	}

	/**
	 * Converts the specified object to an {@code Integer} whole number.
	 *
	 * @param object the object to be converted
	 * @return the converted {@code Integer} whole number
	 * @throws RoboScriptRuntimeError if the argument is not a whole number
	 */
	public Integer asFullNumber(Object object) {
		Double number = this.asNumber(object);

		if (Math.round(number) == number)
			return number.intValue();
		throw new RoboScriptRuntimeError(this.errorToken, "Argument must be a whole number.");
	}

	/**
	 * Converts the specified object to a positive {@code Double} number.
	 *
	 * @param object      the object to be converted
	 * @param includeZero flag indicating whether zero is considered a valid value
	 * @return the converted positive {@code Double} number
	 * @throws RoboScriptRuntimeError if the argument is not a positive number
	 */
	public Double asPositiveNumber(Object object, boolean includeZero) {
		Double number = this.asNumber(object);
		if (includeZero && number >= 0)
			return number;
		if (!includeZero && number > 0)
			return number;

		throw new RoboScriptRuntimeError(this.errorToken, "Argument must be positive.");
	}

	/**
	 * Converts the specified object to a positive {@code Integer} whole number.
	 *
	 * @param object      the object to be converted
	 * @param includeZero flag indicating whether zero is considered a valid value
	 * @return the converted positive {@code Integer} whole number
	 * @throws RoboScriptRuntimeError if the argument is not a positive whole number
	 */
	public Integer asPositiveFullNumber(Object object, boolean includeZero) {
		Integer number = this.asFullNumber(object);
		if (includeZero && number >= 0)
			return number;
		if (!includeZero && number > 0)
			return number;

		throw new RoboScriptRuntimeError(this.errorToken, "Argument must be positive.");
	}

	/**
	 * Converts the specified object to a {@code String}.
	 *
	 * @param object the object to be converted
	 * @return the converted {@code String}
	 * @throws RoboScriptRuntimeError if the argument is not a string
	 */
	public String asString(Object object) {
		if (object instanceof String string)
			return string;
		throw new RoboScriptRuntimeError(this.errorToken, "Argument must be a String.");
	}

	/**
	 * Converts the specified object to a non-empty {@code String}.
	 *
	 * @param object the object to be converted
	 * @return the converted non-empty {@code String}
	 * @throws RoboScriptRuntimeError if the argument is an empty string
	 */
	public String asNonEmptyString(Object object) {
		String string = this.asString(object);
		if (!string.isEmpty())
			return string;
		throw new RoboScriptRuntimeError(this.errorToken, "Argument must not be empty.");
	}

	// TODO: outcraft figure out why this was static i changed it so it would compile
	public BlockPos asBlockPos(List<Object> argumentList, int startingIndex) {
		return new BlockPos(this.asNumber(argumentList.get(startingIndex)),
				this.asNumber(argumentList.get(startingIndex + 1)),
				this.asNumber(argumentList.get(startingIndex + 2)));
	}

	public BlockPos asBlockPos(Object object1, Object object2, Object object3) {
		return new BlockPos(this.asNumber(object1), this.asNumber(object2),
				this.asNumber(object3));
	}

	public Item asItem(Object object) {
		String itemId = this.asNonEmptyString(object);
		// May return Items.AIR
		return Registry.ITEM.get(new ResourceLocation(itemId.split(":")[0], itemId.trim().split(":")[1]));
	}
}
