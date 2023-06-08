package com.workert.robotics.content.computing.inputs.redstonedetector;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RedstoneDetectorPlacementPacket extends SimplePacketBase {
	private BlockPos pos;
	private BlockPos targetPos;

	public RedstoneDetectorPlacementPacket(BlockPos pos, BlockPos targetPos) {
		this.pos = pos;
		this.targetPos = targetPos;
	}

	public RedstoneDetectorPlacementPacket(FriendlyByteBuf buffer) {
		this.pos = buffer.readBlockPos();
		this.targetPos = buffer.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeBlockPos(this.targetPos);
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			ServerPlayer player = context.get().getSender();
			if (player == null)
				return;
			Level world = player.level;
			if (world == null || !world.isLoaded(this.pos))
				return;
			BlockEntity te = world.getBlockEntity(this.pos);
			if (te instanceof RedstoneDetectorBlockEntity)
				((RedstoneDetectorBlockEntity) te).setTargetPos(this.targetPos);
		});
	}


	public static class ClientBoundRequest extends SimplePacketBase {
		BlockPos pos;

		public ClientBoundRequest(BlockPos pos) {
			this.pos = pos;
		}

		public ClientBoundRequest(FriendlyByteBuf buffer) {
			this.pos = buffer.readBlockPos();
		}

		@Override
		public void write(FriendlyByteBuf buffer) {
			buffer.writeBlockPos(this.pos);
		}

		@Override
		public void handle(Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
					() -> () -> RedstoneDetectorTargetHandler.flushSettings(this.pos)));
		}
	}
}
