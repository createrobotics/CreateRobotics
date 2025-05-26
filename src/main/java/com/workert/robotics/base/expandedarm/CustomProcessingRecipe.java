package com.workert.robotics.base.expandedarm;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.workert.robotics.base.registries.ModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.RegistryAccess;

@ParametersAreNonnullByDefault
public class CustomProcessingRecipe extends ProcessingRecipe<RecipeWrapper> {
    
    public CustomProcessingRecipe(ProcessingRecipeParams params) {
        super(ModRecipeTypes.CUSTOM_PROCESSING, params);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CUSTOM_PROCESSING.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CUSTOM_PROCESSING.getType();
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0).test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 2;
    }


    @Deprecated
    public ItemStack getResultItem() {
        if (results.isEmpty())
            return ItemStack.EMPTY;
        return results.get(0).getStack();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return getResultItem();
    }
}