package com.workert.robotics.mixin;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ToolboxTileEntity.class)
public interface ToolboxTileEntityAccessor {
	@Accessor
	ToolboxInventory getInventory();
}
