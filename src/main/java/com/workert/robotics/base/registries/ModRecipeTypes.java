package com.workert.robotics.base.registries;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.workert.robotics.base.expandedarm.CustomProcessingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public enum ModRecipeTypes implements IRecipeTypeInfo {
    CUSTOM_PROCESSING(CustomProcessingRecipe::new);

    private final ResourceLocation id;
    private final RegistryObject<RecipeSerializer<?>> serializerObject;
    private final RegistryObject<RecipeType<?>> typeObject;

    ModRecipeTypes(ProcessingRecipeFactory<?> factory) {
        String name = "custom_processing";
        id = new ResourceLocation("robotics", name); // Usar constructor con namespace explícito
        serializerObject = Registers.SERIALIZER_REGISTER.register(name,
                () -> new ProcessingRecipeSerializer<>(factory));
        typeObject = Registers.TYPE_REGISTER.register(name,
                () -> RecipeType.simple(id));
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) typeObject.get();
    }

    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    public Optional<CustomProcessingRecipe> find(RecipeWrapper customRecipeInv, Level level) {
        return level.getRecipeManager()
                .getRecipeFor((RecipeType<CustomProcessingRecipe>) typeObject.get(), 
                             customRecipeInv, 
                             level);
    }

    private static class Registers {

        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER =
                DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "robotics");
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER =
                DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "robotics");
    }
}