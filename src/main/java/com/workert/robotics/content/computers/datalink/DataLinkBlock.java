package com.workert.robotics.content.computers.datalink;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DataLinkBlock extends WrenchableDirectionalBlock implements ITE<DataLinkBlockEntity> {
	public DataLinkBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<DataLinkBlockEntity> getTileEntityClass() {
		return DataLinkBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends DataLinkBlockEntity> getTileEntityType() {
		return null;
	}
}
