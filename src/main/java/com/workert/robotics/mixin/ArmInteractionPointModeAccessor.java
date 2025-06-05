package com.workert.robotics.mixin;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArmInteractionPoint.class)
public interface ArmInteractionPointModeAccessor {
    @Accessor("mode")
    void setMode(ArmInteractionPoint.Mode mode);

    @Accessor("mode")
    ArmInteractionPoint.Mode getMode();
}