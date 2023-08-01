package com.workert.robotics.mixin;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ToolboxTileEntity.class, remap = false)
public interface ToolboxTileEntityAccessor {
	@Accessor
	ToolboxInventory getInventory();
}
