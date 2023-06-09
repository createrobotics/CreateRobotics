package com.workert.robotics.content.computers.inputs;
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

public class InputPlacementPacket extends SimplePacketBase {
	private BlockPos pos;
	private BlockPos targetPos;

	public InputPlacementPacket(BlockPos pos, BlockPos targetPos) {
		this.pos = pos;
		this.targetPos = targetPos;
	}

	public InputPlacementPacket(FriendlyByteBuf buffer) {
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
			if (player == null) return;
			Level world = player.level;
			if (world == null || !world.isLoaded(this.pos)) return;
			BlockEntity be = world.getBlockEntity(this.pos);
			if (be instanceof IInputBlockEntity inputBlockEntity) inputBlockEntity.setTargetPos(this.targetPos);
		});
		context.get().setPacketHandled(true);
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
			context.get().enqueueWork(
					() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> InputTargetHandler.flushSettings(
							this.pos)));
			context.get().setPacketHandled(true);
		}
	}
}
