package com.workert.robotics.base.datagen.recipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.contraptions.components.press.PressingRecipe;
import com.simibubi.create.content.contraptions.components.saw.CuttingRecipe;
import com.simibubi.create.content.contraptions.itemAssembly.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.workert.robotics.Robotics;
import com.workert.robotics.base.registries.BlockRegistry;
import com.workert.robotics.base.registries.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

public class SequencedAssemblyRecipeGen extends CreateRecipeProvider {
	public SequencedAssemblyRecipeGen(DataGenerator dataGenerator) {
		super(dataGenerator);
	}

	GeneratedRecipe CLOCKCOPTER = this.create("clockcopter",
			builder -> builder.require(BlockRegistry.BRONZE_BLOCK.get())
					.transitionTo(ItemRegistry.INCOMPLETE_CLOCKCOPTER.get())
					.addOutput(ItemRegistry.CLOCKCOPTER.get(), 1)
					.loops(1)
					.addStep(CuttingRecipe::new, recipeBuilder -> recipeBuilder.duration(400))
					.addStep(DeployerApplicationRecipe::new,
							recipeBuilder -> recipeBuilder.require(AllBlocks.COGWHEEL.get()))
					.addStep(DeployerApplicationRecipe::new,
							recipeBuilder -> recipeBuilder.require(AllBlocks.LARGE_COGWHEEL.get()))
					.addStep(DeployerApplicationRecipe::new,
							recipeBuilder -> recipeBuilder.require(AllBlocks.LARGE_COGWHEEL.get()))
					.addStep(DeployerApplicationRecipe::new,
							recipeBuilder -> recipeBuilder.require(ItemRegistry.TIN_NUGGET.get()))
					.addStep(PressingRecipe::new, recipeBuilder -> recipeBuilder.duration(200))
	);

	GeneratedRecipe CODE_DRONE = this.create("code_drone",
			builder -> builder.require(BlockRegistry.BRONZE_BLOCK.get())
					.transitionTo(ItemRegistry.INCOMPLETE_CODE_DRONE.get())
					.addOutput(ItemRegistry.CODE_DRONE.get(), 1)
					.loops(3)
					.addStep(CuttingRecipe::new, recipeBuilder -> recipeBuilder.duration(400))
					.addStep(DeployerApplicationRecipe::new,
							recipeBuilder -> recipeBuilder.require(AllBlocks.COGWHEEL.get()))
					.addStep(DeployerApplicationRecipe::new,
							recipeBuilder -> recipeBuilder.require(AllBlocks.LARGE_COGWHEEL.get()))
					.addStep(DeployerApplicationRecipe::new,
							recipeBuilder -> recipeBuilder.require(ItemRegistry.TIN_NUGGET.get()))
					.addStep(PressingRecipe::new, recipeBuilder -> recipeBuilder.duration(200))
	);

	protected CreateRecipeProvider.GeneratedRecipe create(String name, UnaryOperator<SequencedAssemblyRecipeBuilder> transform) {
		CreateRecipeProvider.GeneratedRecipe generatedRecipe =
				c -> transform.apply(new SequencedAssemblyRecipeBuilder(new ResourceLocation(Robotics.MOD_ID, name)))
						.build(c);
		this.all.add(generatedRecipe);
		return generatedRecipe;
	}

	@Override
	public @NotNull String getName() {
		return "Create Robotics' Sequenced Assembly Recipes";
	}
}
