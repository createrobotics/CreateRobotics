package com.workert.robotics.base.registries;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public enum ModRecipeTypes implements IRecipeTypeInfo {

    // Define tus tipos de recetas aquí
    CUSTOM_PROCESSING(CustomProcessingRecipe::new);
    // Puedes añadir más tipos según necesites

    private final ResourceLocation id;
    private final RegistryObject<RecipeSerializer<?>> serializerObject;
    private final RegistryObject<RecipeType<?>> typeObject;

    ModRecipeTypes(ProcessingRecipeFactory<?> factory) {
        String name = name().toLowerCase(); // Convierte el nombre del enum a minúsculas
        id = new ResourceLocation("robotics", name); // Usa el ID de tu mod
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

    // Registra tus recetas
    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    private static class Registers {
        // Reemplaza "robotics" con el ID de tu mod
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER =
                DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "robotics");
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER =
                DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "robotics");
    }
}
