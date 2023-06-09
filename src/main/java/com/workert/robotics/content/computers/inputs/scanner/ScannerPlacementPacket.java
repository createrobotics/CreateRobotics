package com.lightdev6.computing.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.workert.robotics.content.computers.inputs.scanner.ScannerBlockEntity;
import com.workert.robotics.content.computers.inputs.scanner.ScannerTargetHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScannerPlacementPacket extends SimplePacketBase {
	private BlockPos pos;
	private BlockPos targetPos;

	public ScannerPlacementPacket(BlockPos pos, BlockPos targetPos) {
		this.pos = pos;
		this.targetPos = targetPos;
	}

	public ScannerPlacementPacket(FriendlyByteBuf buffer) {
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
			Level level = player.level;
			if (level == null || !level.isLoaded(this.pos))
				return;
			BlockEntity blockEntity = level.getBlockEntity(this.pos);
			if (blockEntity instanceof ScannerBlockEntity scanner)
				scanner.setTargetPos(this.targetPos);

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
					() -> () -> ScannerTargetHandler.flushSettings(this.pos)));
		}
	}
}
