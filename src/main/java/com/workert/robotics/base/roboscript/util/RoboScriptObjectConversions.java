package com.workert.robotics.base.roboscript.util;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
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
}
