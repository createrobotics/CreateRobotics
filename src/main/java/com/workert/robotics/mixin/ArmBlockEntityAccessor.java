package com.workert.robotics.mixin;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArmBlockEntity.class)
public interface ArmBlockEntityAccessor {
    @Accessor("phase")
    ArmBlockEntity.Phase getPhase();
}
