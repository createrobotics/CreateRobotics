package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;

public class FakeToolboxTileEntity extends ToolboxTileEntity {
	FlyingToolbox flyingToolbox;

	public FakeToolboxTileEntity(FlyingToolbox flyingToolbox) {
		super(AllTileEntities.TOOLBOX.get(), BlockPos.ZERO, null);
		this.flyingToolbox = flyingToolbox;
	}

	@Override
	@NotNull
	public BlockPos getBlockPos() {
		return this.flyingToolbox.blockPosition();
	}

	@Override
	public DyeColor getColor() {
		return this.flyingToolbox.getColor();
	}

	@Override
	public void sendData() {
		super.sendData();
		this.flyingToolbox.sendData();
	}
}
