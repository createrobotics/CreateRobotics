package com.workert.robotics.base.registries;


import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;


import net.minecraft.world.level.Level;

import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
public class CustomProcessingRecipe extends ProcessingRecipe<RecipeWrapper> {

    public CustomProcessingRecipe(ProcessingRecipeParams params) {
        super(ModRecipeTypes.CUSTOM_PROCESSING, params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0)
                .test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 2;
    }


    }

