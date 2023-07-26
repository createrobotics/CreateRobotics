package com.workert.robotics.unused.roboscriptast.ingame;
import com.workert.robotics.unused.roboscriptast.RoboScriptArray;
import com.workert.robotics.unused.roboscriptast.RoboScriptVariable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompoundTagEnvironmentConversionHelper {

	// Conversions to tag

	public static CompoundTag valuesToTag(Map<String, RoboScriptVariable> values) {
		CompoundTag compoundTag = new CompoundTag();
		for (Map.Entry<String, RoboScriptVariable> entry : values.entrySet()) {
			Object value = entry.getValue().value;
			if (value instanceof Double doubleValue) {
				compoundTag.putDouble(entry.getKey(), doubleValue);
			} else if (value instanceof String stringValue) {
				compoundTag.putString(entry.getKey(), stringValue);
			} else if (value instanceof Boolean booleanValue) {
				compoundTag.putBoolean(entry.getKey(), booleanValue);
			} else if (value instanceof RoboScriptArray arrayValue) {
				compoundTag.put(entry.getKey(), arrayToListTag(arrayValue));
			}
		}
		return compoundTag;
	}


	private static ListTag arrayToListTag(RoboScriptArray roboScriptArray) {
		ListTag tag = new ListTag();
		for (Object value : roboScriptArray.elements) {
			CompoundTag objectTag = new CompoundTag();

			if (value instanceof Double doubleValue) {
				objectTag.putDouble("Value", doubleValue);
			} else if (value instanceof String stringValue) {
				objectTag.putString("Value", stringValue);
			} else if (value instanceof Boolean booleanValue) {
				objectTag.putBoolean("Value", booleanValue);
			} else if (value instanceof RoboScriptArray array) {
				objectTag.put("Value", arrayToListTag(array));
			}
			tag.add(objectTag);
		}
		return tag;
	}


	// Conversions From tag

	public static Map<String, RoboScriptVariable> valuesFromCompoundTag(CompoundTag compoundTag) {
		Map<String, RoboScriptVariable> valueMap = new HashMap<>();
		compoundTag.getAllKeys().forEach(key -> {
			Object value = switch (compoundTag.getTagType(key)) {
				case Tag.TAG_DOUBLE -> compoundTag.getDouble(key);
				case Tag.TAG_STRING -> compoundTag.getString(key);
				case Tag.TAG_BYTE -> compoundTag.getBoolean(key);
				case Tag.TAG_LIST -> arrayFromListTag(compoundTag.getList(key, Tag.TAG_COMPOUND));
				default -> null;
			};
			valueMap.put(key, new RoboScriptVariable(true, value));
		});
		return valueMap;
	}

	private static RoboScriptArray arrayFromListTag(ListTag listTag) {
		List<Object> listElements = new ArrayList<>();
		listTag.forEach(tag -> {
			CompoundTag compoundTag = (CompoundTag) tag;
			Object value = switch (compoundTag.getTagType("Value")) {
				case Tag.TAG_DOUBLE -> compoundTag.getDouble("Value");
				case Tag.TAG_STRING -> compoundTag.getString("Value");
				case Tag.TAG_BYTE -> compoundTag.getBoolean("Value");
				case Tag.TAG_LIST -> arrayFromListTag(compoundTag.getList("Value", Tag.TAG_COMPOUND));
				default -> null;
			};
			listElements.add(value);
		});
		return new RoboScriptArray(listElements);
	}
}