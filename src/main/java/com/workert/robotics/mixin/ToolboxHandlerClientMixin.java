package com.workert.robotics.mixin;
import com.simibubi.create.content.curiosities.toolbox.RadialToolboxMenu;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandlerClient;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.workert.robotics.content.robotics.flyingtoolbox.FakeToolboxTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ToolboxHandlerClient.class)
public class ToolboxHandlerClientMixin {
	@Inject(method = "onKeyInput", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void onKeyInput(int key, boolean pressed, CallbackInfo ci, Minecraft mc, LocalPlayer player, Level level, List toolboxes, CompoundTag compound, String slotKey, boolean equipped, BlockPos pos, double max, boolean canReachToolbox) {
		if (canReachToolbox) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof FakeToolboxTileEntity fakeToolboxTileEntity) {
				RadialToolboxMenu screen = new RadialToolboxMenu(toolboxes,
						RadialToolboxMenu.State.SELECT_ITEM_UNEQUIP, fakeToolboxTileEntity);
				screen.prevSlot(compound.getCompound(slotKey)
						.getInt("Slot"));
				ScreenOpener.open(screen);
				ci.cancel();
			}
		}
	}
}
