package com.workert.robotics.world.feature;

import com.workert.robotics.Robotics;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPlacedFeatures {
	public static final DeferredRegister<PlacedFeature> FEATURES = DeferredRegister
			.create(Registry.PLACED_FEATURE_REGISTRY, Robotics.MOD_ID);

	public static final RegistryObject<PlacedFeature> TIN_ORE_PLACED = FEATURES.register("tin_ore_placed",
			() -> new PlacedFeature(
					(Holder<ConfiguredFeature<?, ?>>) (Holder<? extends ConfiguredFeature<?, ?>>) ModConfiguredFeatures.TIN_ORE,
					ModOrePlacement.commonOrePlacement(7, // VeinsPerChunk
							HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80),
									VerticalAnchor.aboveBottom(80)))));

}