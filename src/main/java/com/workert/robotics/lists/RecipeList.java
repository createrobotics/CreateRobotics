package com.workert.robotics.lists;

import com.workert.robotics.Robotics;
import com.workert.robotics.recipe.SmasherBlockRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeList {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, Robotics.MOD_ID);

	public static final RegistryObject<RecipeSerializer<SmasherBlockRecipe>> SMASHER_BLOCK_SERIALIZER = RECIPE_SERIALIZERS
			.register("smashing", () -> SmasherBlockRecipe.Serializer.INSTANCE);

	public static void register(IEventBus eventBus) {
		RECIPE_SERIALIZERS.register(eventBus);
	}
}
