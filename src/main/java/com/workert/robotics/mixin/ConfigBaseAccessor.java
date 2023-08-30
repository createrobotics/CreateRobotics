package com.workert.robotics.mixin;
import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ConfigBase.class, remap = false)
public interface ConfigBaseAccessor {
	@Invoker("registerAll")
	void invokeRegisterAll(final ForgeConfigSpec.Builder builder);
}
