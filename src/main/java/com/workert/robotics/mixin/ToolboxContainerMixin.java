package com.workert.robotics.mixin;
import com.simibubi.create.content.curiosities.toolbox.ToolboxContainer;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolbox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ToolboxContainer.class)
public class ToolboxContainerMixin {
	@Inject(method = "createOnClient(Lnet/minecraft/network/FriendlyByteBuf;)Lcom/simibubi/create/content/curiosities/toolbox/ToolboxTileEntity;", at = @At("TAIL"), remap = false, locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void createOnClient(FriendlyByteBuf extraData, CallbackInfoReturnable<ToolboxTileEntity> cir, BlockPos readBlockPos, CompoundTag readNbt, ClientLevel world, BlockEntity tileEntity) {
		if (extraData.isReadable()) {
			Entity entity = Minecraft.getInstance().level.getEntity(extraData.readInt());
			if (entity instanceof FlyingToolbox flyingToolbox) {
				ToolboxTileEntity toolbox = flyingToolbox.getFakeToolboxTileEntity();
				toolbox.readClient(readNbt);
				cir.setReturnValue(toolbox);
			}
		}
	}
}
