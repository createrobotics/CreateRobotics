package com.workert.robotics.world.feature;

import com.workert.robotics.Robotics;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPlacedFeatures {
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(
			Registry.PLACED_FEATURE_REGISTRY, Robotics.MOD_ID);

	public static final RegistryObject<PlacedFeature> TIN_ORE_PLACED = PLACED_FEATURES.register("tin_ore_placed",
			() -> new PlacedFeature(ModConfiguredFeatures.TIN_ORE.getHolder().get(),
					ModOrePlacement.commonOrePlacement(7, // VeinsPerChunk
							HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80),
									VerticalAnchor.aboveBottom(80)))));

	public static void register(IEventBus eventBus) {
		PLACED_FEATURES.register(eventBus);
	}
}