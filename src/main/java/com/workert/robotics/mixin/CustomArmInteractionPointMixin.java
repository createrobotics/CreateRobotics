package com.workert.robotics.mixin;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.workert.robotics.base.expandedarm.ArmInteractionPointAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ArmInteractionPoint.class, remap = false)
public abstract class CustomArmInteractionPointMixin implements ArmInteractionPointAccess {
    @Unique
    private boolean isCustomMode = false;
    
    @Shadow(remap = false)
    protected ArmInteractionPoint.Mode mode;
    
    @Override
    public boolean isCustomMode() {
        return isCustomMode;
    }
    
    @Override
    public void setCustomMode(boolean custom) {
        this.isCustomMode = custom;
    }
    
    @Inject(
        method = "cycleMode()V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void onCycleMode(CallbackInfo ci) {
        if (mode == ArmInteractionPoint.Mode.DEPOSIT) {
            mode = ArmInteractionPoint.Mode.TAKE;
            isCustomMode = false;
        } else if (mode == ArmInteractionPoint.Mode.TAKE && !isCustomMode) {
            isCustomMode = true;
        } else {
            mode = ArmInteractionPoint.Mode.DEPOSIT;
            isCustomMode = false;
        }
        ci.cancel();
    }
    
    @Inject(
        method = "getMode()Lcom/simibubi/create/content/kinetics/mechanicalArm/ArmInteractionPoint$Mode;",
        at = @At("RETURN"),
        cancellable = true,
        remap = false
    )
    private void getCustomMode(CallbackInfoReturnable<ArmInteractionPoint.Mode> cir) {
        if (isCustomMode) {
            cir.setReturnValue(ArmInteractionPoint.Mode.TAKE);
        }
    }
    
    @Inject(
        method = "serialize(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/BlockPos;)V",
        at = @At("RETURN"),
        remap = false
    )
    private void serializeCustomMode(CompoundTag nbt, BlockPos anchor, CallbackInfo ci) {
        nbt.putBoolean("CustomMode", isCustomMode);
    }
    
    @Inject(
        method = "deserialize(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/BlockPos;)V",
        at = @At("RETURN"),
        remap = false
    )
    private void deserializeCustomMode(CompoundTag nbt, BlockPos anchor, CallbackInfo ci) {
        isCustomMode = nbt.getBoolean("CustomMode");
    }
}