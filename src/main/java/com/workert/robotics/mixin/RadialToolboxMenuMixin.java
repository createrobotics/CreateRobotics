package com.workert.robotics.mixin;
import com.simibubi.create.content.curiosities.toolbox.RadialToolboxMenu;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import com.workert.robotics.base.registries.PacketRegistry;
import com.workert.robotics.content.robotics.flyingtoolbox.FakeToolboxTileEntity;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolboxEquipPacket;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RadialToolboxMenu.class)
public class RadialToolboxMenuMixin {
	@Shadow
	private ToolboxTileEntity selectedBox;

	@Shadow
	private RadialToolboxMenu.State state;

	@Inject(method = "removed", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/network/simple/SimpleChannel;sendToServer(Ljava/lang/Object;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void removed(CallbackInfo ci, int selected) {
		if (this.selectedBox instanceof FakeToolboxTileEntity fakeToolboxTileEntity) {
			PacketRegistry.CHANNEL.sendToServer(
					new FlyingToolboxEquipPacket(fakeToolboxTileEntity.flyingToolbox.getId(),
							fakeToolboxTileEntity.flyingToolbox.blockPosition(), selected,
							Minecraft.getInstance().player.getInventory().selected));
			ci.cancel();
		}
	}

	@Inject(method = "mouseClicked", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/network/simple/SimpleChannel;sendToServer(Ljava/lang/Object;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void removed(double x, double y, int button, CallbackInfoReturnable<Boolean> cir, int selected) {
		if (this.selectedBox instanceof FakeToolboxTileEntity fakeToolboxTileEntity) {
			PacketRegistry.CHANNEL.sendToServer(
					new FlyingToolboxEquipPacket(fakeToolboxTileEntity.flyingToolbox.getId(),
							fakeToolboxTileEntity.flyingToolbox.blockPosition(), selected,
							Minecraft.getInstance().player.getInventory().selected));
			this.state = RadialToolboxMenu.State.SELECT_BOX;
			cir.setReturnValue(true);
		}
	}
}
