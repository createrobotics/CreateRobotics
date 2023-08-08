package com.workert.robotics.base.roboscript;

import com.workert.robotics.unused.roboscriptast.RoboScriptRuntimeError;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The {@code RoboScriptArgumentPredicates} class provides static utility methods for validating and converting objects to specific types
 * while allowing for nullable arguments.
 * <p>
 * This class only throws {@link RuntimeError}s.
 */
public final class RoboScriptArgumentPredicates {

	/**
	 * Converts the specified object to the specified return type using the provided function, but allows for the argument to be null.
	 * <p>
	 * This method is useful when you have an object that you want to convert to a specific type but need to handle the case where the object might be null. By using the `optional` method, you can avoid null pointer exceptions and gracefully handle nullable arguments.
	 * <p>
	 * <b>Code Example:</b>
	 * <pre>{@code
	 * Object maybeNumber;
	 *
	 * // Convert `maybeNumber` to a `Double` using the `asNumber` method, allowing for null input
	 * Double definitelyNumberOrNull = RoboScriptArgumentPredicates.optional(maybeNumber, RoboScriptArgumentPredicates::asNumber);
	 * System.out.println(definitelyNumberOrNull); // This will definitely be a number or null. If `maybeNumber` is neither, it will throw.
	 * }</pre>
	 * <p>
	 * You can also use a lambda expression or method reference as the conversion function. This allows for more complex conversions or custom logic:
	 * <p>
	 * <b>Another more complex code example:</b>
	 * <pre>{@code
	 * // Convert `maybeNumber` to a positive `Double` number, allowing for null input
	 * Double definitelyPositiveNumberOrNull = RoboScriptArgumentPredicates.optional(maybeNumber, (object) -> RoboScriptArgumentPredicates.asPositiveNumber(object, true));
	 * System.out.println(definitelyPositiveNumberOrNull); // Definitely a positive number or null.
	 * }</pre>
	 *
	 * @param object            the object to be converted
	 * @param predicateFunction the conversion function, which takes an object and returns the desired return type
	 * @param <ReturnType>      the type of the returned object
	 * @return the converted object, or null if the input object is null
	 */
	public static <ReturnType> ReturnType optional(Object object, Function<Object, ReturnType> predicateFunction) {
		if (object == null) return null;
		return predicateFunction.apply(object);
	}

	/**
	 * Converts the specified object to a {@code Double} number.
	 *
	 * @param object the object to be converted
	 * @return the converted {@code Double} number
	 * @throws RoboScriptRuntimeError if the argument is not a number
	 */
	public static Double asNumber(Object object) {
		if (object instanceof Double number && !number.isNaN())
			return number;
		throw new RuntimeError("Argument must be a number.");
	}

	/**
	 * Converts the specified object to an {@code Integer} whole number.
	 *
	 * @param object the object to be converted
	 * @return the converted {@code Integer} whole number
	 * @throws RoboScriptRuntimeError if the argument is not a whole number
	 */
	public static Integer asFullNumber(Object object) {
		Double number = asNumber(object);

		if (Math.round(number) == number)
			return number.intValue();
		throw new RuntimeError("Argument must be a whole number.");
	}

	/**
	 * Converts the specified object to a positive {@code Double} number.
	 *
	 * @param object      the object to be converted
	 * @param includeZero flag indicating whether zero is considered a valid value
	 * @return the converted positive {@code Double} number
	 * @throws RoboScriptRuntimeError if the argument is not a positive number
	 */
	public static Double asPositiveNumber(Object object, boolean includeZero) {
		Double number = asNumber(object);
		if (includeZero && number >= 0)
			return number;
		if (!includeZero && number > 0)
			return number;

		if (includeZero)
			throw new RuntimeError("Argument must be positive or zero.");
		else
			throw new RuntimeError("Argument must be positive.");
	}

	/**
	 * Converts the specified object to a positive {@code Integer} whole number.
	 *
	 * @param object      the object to be converted
	 * @param includeZero flag indicating whether zero is considered a valid value
	 * @return the converted positive {@code Integer} whole number
	 * @throws RoboScriptRuntimeError if the argument is not a positive whole number
	 */
	public static Integer asPositiveFullNumber(Object object, boolean includeZero) {
		Integer number = asFullNumber(object);
		if (includeZero && number >= 0)
			return number;
		if (!includeZero && number > 0)
			return number;

		throw new RuntimeError("Argument must be positive.");
	}

	/**
	 * Converts the specified object to a {@code String}.
	 *
	 * @param object the object to be converted
	 * @return the converted {@code String}
	 * @throws RoboScriptRuntimeError if the argument is not a string
	 */
	public static String asString(Object object) {
		if (object instanceof String string)
			return string;
		throw new RuntimeError("Argument must be a String.");
	}

	/**
	 * Converts the specified object to a non-empty {@code String}.
	 *
	 * @param object the object to be converted
	 * @return the converted non-empty {@code String}
	 * @throws RoboScriptRuntimeError if the argument is an empty string
	 */
	public static String asNonEmptyString(Object object) {
		String string = asString(object);
		if (!string.isEmpty())
			return string;
		throw new RuntimeError("Argument must not be empty.");
	}

	public static List<String> stringifyAllElements(List<Object> elements) {
		List<String> stringList = new ArrayList<>();
		for (Object object : elements) {
			stringList.add(stringify(object));
		}
		return stringList;
	}

	public static BlockPos asBlockPos(Object[] argumentList, int startingIndex) {
		return new BlockPos(asNumber(argumentList[(startingIndex)]),
				asNumber(argumentList[(startingIndex + 1)]),
				asNumber(argumentList[(startingIndex + 2)]));
	}

	/**
	 * Converts the specified three objects to a {@link BlockPos}.
	 *
	 * @param xPosObject the object to be used as X-Position
	 * @param yPosObject the object to be used as Y-Position
	 * @param zPosObject the object to be used as Z-Position
	 * @return the converted non-empty {@link BlockPos}
	 * @throws RoboScriptRuntimeError if one or more of the arguments is not a number
	 */
	public static BlockPos asBlockPos(Object xPosObject, Object yPosObject, Object zPosObject) {
		return new BlockPos(asNumber(xPosObject), asNumber(yPosObject),
				asNumber(zPosObject));
	}

	/**
	 * Converts the specified object to an {@link Item}.
	 * <p>
	 * May return {@link Items#AIR} if the Item ID is invalid.
	 *
	 * @param object the object to be converted
	 * @return the converted non-empty {@link Item}
	 * @throws RoboScriptRuntimeError if the argument is not a string with an Item ID
	 */
	public static Item asItem(Object object) {
		String itemId = asNonEmptyString(object);
		return Registry.ITEM.get(new ResourceLocation(itemId.split(":")[0], itemId.trim().split(":")[1]));
	}

	/**
	 * Gets a string value for an object passed in.
	 *
	 * @param object The object being stringified.
	 * @return The string value of the object.
	 */
	public static String stringify(@Nullable Object object) {
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
}
