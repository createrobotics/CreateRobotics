package com.workert.robotics.lists;

import com.workert.robotics.Robotics;
import com.workert.robotics.blocks.blockentities.SmasherBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityList {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
			ForgeRegistries.BLOCK_ENTITY_TYPES, Robotics.MOD_ID);

	public static final RegistryObject<BlockEntityType<SmasherBlockEntity>> SMASHER_BLOCK_ENTITY = BLOCK_ENTITIES.register(
			"smasher_block_entity",
			() -> BlockEntityType.Builder.of(SmasherBlockEntity::new, BlockList.SMASHER_BLOCK.get()).build(null));

	public static void register(IEventBus eventBus) {
		BLOCK_ENTITIES.register(eventBus);
	}
}