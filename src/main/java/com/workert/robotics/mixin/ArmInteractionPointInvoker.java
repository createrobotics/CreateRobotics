package com.workert.robotics.mixin;


import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmInteractionPoint.class)
public interface ArmInteractionPointInvoker {
    @Invoker("getInteractionDirection")
    Direction invokeGetInteractionDirection();
}