package com.workert.robotics.mixin;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.workert.robotics.base.expandedarm.CustomArmBehavior;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ArmBlockEntity.class)
public abstract class ArmBlockEntityMixin {
    @Shadow
    public List<ArmInteractionPoint> inputs;

    @Shadow
    public List<ArmInteractionPoint> outputs;

    @Inject(method = "tick()V", at = @At("RETURN"), remap = false)
    public void onTick(CallbackInfo ci) {
        ArmBlockEntity arm = (ArmBlockEntity) (Object) this;
        Level level = arm.getLevel();
        if (level.isClientSide() || arm.getSpeed() == 0)
            return;

        List<ArmInteractionPoint> allPoints = new ArrayList<>();
        allPoints.addAll(inputs);
        allPoints.addAll(outputs);
        
        CustomArmBehavior.checkBeltsForCustomProcessing(arm, allPoints);
    }
}