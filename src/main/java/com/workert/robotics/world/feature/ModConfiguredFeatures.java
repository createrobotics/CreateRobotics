package com.workert.robotics.world.feature;

import java.util.List;

import com.workert.robotics.lists.BlockList;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class ModConfiguredFeatures {
	public static final List<OreConfiguration.TargetBlockState> OVERWORLD_TIN_ORES = List.of(
			OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, BlockList.TIN_ORE.get().defaultBlockState()),
			OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES,
					BlockList.DEEPSLATE_TIN_ORE.get().defaultBlockState()));

	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> TIN_ORE = FeatureUtils.register("tin_ore",
			Feature.ORE, new OreConfiguration(OVERWORLD_TIN_ORES, 9));
}
