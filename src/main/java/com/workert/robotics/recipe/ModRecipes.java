package com.workert.robotics.recipe;

import com.workert.robotics.Robotics;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Robotics.MOD_ID);

    public static final RegistryObject<RecipeSerializer<SmasherBlockRecipe>> SMASHER_BLOCK_SERIALIZER =
            SERIALIZERS.register("smashing", () -> SmasherBlockRecipe.Serializer.INSTANCE );
    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
    }
}
