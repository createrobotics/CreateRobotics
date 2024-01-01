package com.workert.robotics.mixin;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import net.minecraft.core.BlockPos;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ToolboxBlockEntity.class, remap = false)
public abstract class ToolboxTileEntityMixin {
	@Redirect(method = {"updateOpenCount", "tickAudio"}, at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/simibubi/create/content/equipment/toolbox/ToolboxBlockEntity;worldPosition:Lnet/minecraft/core/BlockPos;", remap = true))
	BlockPos worldPosition(ToolboxBlockEntity instance) {
		return instance.getBlockPos();
	}
}
