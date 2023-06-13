package com.workert.robotics.base.roboscript.ingame;
import com.workert.robotics.base.roboscript.RoboScriptArray;
import com.workert.robotics.base.roboscript.RoboScriptVariable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompoundTagEnvironmentConversion {

	//To tag

	public static ListTag valuesToTag(Map<String, RoboScriptVariable> values) {
		ListTag tag = new ListTag();
		for (Map.Entry<String, RoboScriptVariable> entry : values.entrySet()) {
			CompoundTag compoundTag = new CompoundTag();
			if (!entry.getValue().staticc) continue;
			Object value = entry.getValue().value;
			if (value instanceof Double d) {
				compoundTag.putString("Identifier", entry.getKey());
				compoundTag.putDouble("Value", d);
				tag.add(compoundTag);
			} else if (value instanceof String s) {
				compoundTag.putString("Identifier", entry.getKey());
				compoundTag.putString("Value", s);
				tag.add(compoundTag);
			} else if (value instanceof Boolean b) {
				compoundTag.putString("Identifier", entry.getKey());
				compoundTag.putBoolean("Value", b);
				tag.add(compoundTag);
			} else if (value instanceof RoboScriptArray a) {
				compoundTag.putString("Identifier", entry.getKey());
				compoundTag.put("Value", arrayToTag(a));
				tag.add(compoundTag);
			}
		}
		return tag;
	}


	private static ListTag arrayToTag(RoboScriptArray a) {
		ListTag tag = new ListTag();
		for (Object value : a.elements) {
			if (value instanceof Double d) {
				CompoundTag objectTag = new CompoundTag();
				objectTag.putDouble("Value", d);
				tag.add(objectTag);
			} else if (value instanceof String s) {
				CompoundTag objectTag = new CompoundTag();
				objectTag.putString("Value", s);
				tag.add(objectTag);
			} else if (value instanceof Boolean b) {
				CompoundTag objectTag = new CompoundTag();
				objectTag.putBoolean("Value", b);
				tag.add(objectTag);
			} else if (value instanceof RoboScriptArray array) {
				CompoundTag objectTag = new CompoundTag();
				objectTag.put("Value", arrayToTag(array));
				tag.add(objectTag);
			}
		}
		return tag;
	}


	//From tag

	public static Map<String, RoboScriptVariable> valuesFromTag(ListTag tag) {
		Map<String, RoboScriptVariable> values = new HashMap<>();

		for (int i = 0; i < tag.size(); i++) {
			CompoundTag compoundTag = tag.getCompound(i);
			String name = compoundTag.getString("Identifier");
			Object value;
			Byte type = compoundTag.getTagType("Value");
			if (type == Tag.TAG_DOUBLE) {
				value = compoundTag.getDouble("Value");
			} else if (type == Tag.TAG_STRING) {
				value = compoundTag.getString("Value");
			} else if (type == Tag.TAG_BYTE) {
				value = compoundTag.getBoolean("Value");
			} else if (type == Tag.TAG_LIST) {
				value = arrayFromTag(compoundTag.getList("Value", Tag.TAG_COMPOUND));
			} else {
				value = null;
			}
			values.put(name, new RoboScriptVariable(true, value));
		}
		return values;
	}

	private static RoboScriptArray arrayFromTag(ListTag tag) {
		List<Object> elements = new ArrayList<>();

		for (int i = 0; i < tag.size(); i++) {
			CompoundTag compoundTag = tag.getCompound(i);
			Object value;
			Byte type = compoundTag.getTagType("Value");
			if (type == Tag.TAG_DOUBLE) {
				value = compoundTag.getDouble("Value");
			} else if (type == Tag.TAG_STRING) {
				value = compoundTag.getString("Value");
			} else if (type == Tag.TAG_BYTE) {
				value = compoundTag.getBoolean("Value");
			} else if (type == Tag.TAG_LIST) {
				value = arrayFromTag(compoundTag.getList("Value", Tag.TAG_COMPOUND));
			} else {
				value = null;
			}
			elements.add(value);
		}
		return new RoboScriptArray(elements);
	}


}
