package com.workert.robotics.lists;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.workert.robotics.Robotics;
import com.workert.robotics.blocks.blockentities.SmasherBlockEntity;

public class BlockEntityList {
	public static void register() {
	}

	public static final BlockEntityEntry<SmasherBlockEntity> SMASHER_BLOCK_ENTITY = Robotics.REGISTRATE.tileEntity(
			"smasher_block_entity", SmasherBlockEntity::new).validBlock(() -> BlockList.SMASHER.get()).register();
}