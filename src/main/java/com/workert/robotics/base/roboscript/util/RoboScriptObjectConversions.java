package com.workert.robotics.base.roboscript.util;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The {@code RoboScriptObjectConversions} class provides static utility methods for converting Java objects to types usable in RoboScript.
 */
public final class RoboScriptObjectConversions {
	public static List itemStack(@Nonnull ItemStack itemStack) {
		return List.of(
				Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemStack.getItem())).toString(),
				(double) itemStack.getCount());
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

	public static List<String> stringifyAllElements(List<Object> elements) {
		List<String> stringList = new ArrayList<>();
		if (elements != null && !elements.isEmpty())
			for (Object object : elements) {
				stringList.add(stringify(object));
			}
		return stringList;
	}

	public static Object prepareForRoboScriptUse(Object object) {
		if (object == null)
			return null;

		if (object instanceof List<?> list) {
			List<Object> preparedList = new ArrayList<>();
			for (Object listObject : list) {
				preparedList.add(prepareForRoboScriptUse(listObject));
			}
			return preparedList;
		}

		if (object.getClass().isArray()) {
			List<Object> list = new ArrayList<>();
			int length = Array.getLength(object);

			for (int i = 0; i < length; i++) {
				list.add(prepareForRoboScriptUse(Array.get(object, i)));
			}

			return list;
		}

		if (object instanceof Number number)
			return number.doubleValue();

		if (object instanceof CharSequence charSequence)
			return charSequence.toString();

		if (object instanceof ItemStack itemStack)
			return RoboScriptObjectConversions.itemStack(itemStack);
		if (object instanceof RoboScriptObject) {
			return object;
		}

		throw new IllegalArgumentException("Illegal RoboScript method return type");
	}
}
