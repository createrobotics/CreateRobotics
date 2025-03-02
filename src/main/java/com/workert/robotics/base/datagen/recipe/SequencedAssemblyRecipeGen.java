package com.workert.robotics.base.datagen.recipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.workert.robotics.Robotics;
import com.workert.robotics.base.registries.BlockRegistry;
import com.workert.robotics.base.registries.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public class SequencedAssemblyRecipeGen extends CreateRecipeProvider {
	public SequencedAssemblyRecipeGen(PackOutput packOutput) {
		super(packOutput);
	}

	GeneratedRecipe CLOCKCOPTER = this.create("clockcopter",
			builder -> builder.require(BlockRegistry.BRONZE_BLOCK.get())
					.transitionTo(ItemRegistry.INCOMPLETE_CLOCKCOPTER.asItem())
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
					.transitionTo(ItemRegistry.INCOMPLETE_CODE_DRONE.asItem())
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
}
