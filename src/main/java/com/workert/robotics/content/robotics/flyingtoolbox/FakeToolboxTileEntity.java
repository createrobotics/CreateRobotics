package com.workert.robotics.content.robotics.flyingtoolbox;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
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
		this.flyingToolbox.sendData();
	}

	@Override
	public void sendToContainer(FriendlyByteBuf buffer) {
		super.sendToContainer(buffer);
		buffer.writeInt(this.flyingToolbox.getId());
	}

	@Override
	public void causeBlockUpdate() {
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean canPlayerUse(Player player) {
		return player.distanceToSqr(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D,
				this.getBlockPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		if (!clientPacket)
			this.flyingToolbox.writeFakeToolboxTileEntityCompound(compound);
	}
}
