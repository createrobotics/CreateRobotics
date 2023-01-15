package com.workert.robotics.world.feature;

import com.google.common.base.Suppliers;
import com.workert.robotics.Robotics;
import com.workert.robotics.lists.BlockList;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class ModConfiguredFeatures {
	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(
			Registry.CONFIGURED_FEATURE_REGISTRY, Robotics.MOD_ID);
	public static final Supplier<List<OreConfiguration.TargetBlockState>> OVERWORLD_TIN_ORES = Suppliers.memoize(
			() -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES,
							BlockList.TIN_ORE.get().defaultBlockState()),
					OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES,
							BlockList.DEEPSLATE_TIN_ORE.get().defaultBlockState())));

	public static final RegistryObject<ConfiguredFeature<?, ?>> TIN_ORE = CONFIGURED_FEATURES.register("tin_ore",
			() -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OVERWORLD_TIN_ORES.get(), 9)));

	public static void register(IEventBus eventBus) {
		CONFIGURED_FEATURES.register(eventBus);
	}
}
