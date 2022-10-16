package com.workert.robotics.event;

import com.workert.robotics.Robotics;
import com.workert.robotics.recipe.SmasherBlockRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = Robotics.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>>
                                                   event) {
        event.getRegistry().registerAll(

        );
    }
    @SubscribeEvent
    public static void registerRecipeTypes(final RegistryEvent.Register<RecipeSerializer<?>> event){
        Registry.register(Registry.RECIPE_TYPE, SmasherBlockRecipe.Type.ID, SmasherBlockRecipe.Type.INSTANCE);
    }
}
