package com.workert.robotics.block.entity;

import com.workert.robotics.Robotics;
import com.workert.robotics.block.ModBlocks;
import com.workert.robotics.block.entity.custom.SmasherBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Robotics.MOD_ID);

    public static final RegistryObject<BlockEntityType<SmasherBlockEntity>> SMASHER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("smasher_block_entity", () ->
                    BlockEntityType.Builder.of(SmasherBlockEntity::new,
                            ModBlocks.SMASHER_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}