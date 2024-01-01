package com.workert.robotics.mixin;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ToolboxBlockEntity.class, remap = false)
public interface ToolboxBlockEntityAccessor {
	@Accessor
	ToolboxInventory getInventory();
}
