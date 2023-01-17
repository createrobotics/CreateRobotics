package com.workert.robotics.mixin;

import com.workert.robotics.helpers.TelemetryHelper;
import net.minecraft.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {

	@Inject(method = "saveToFile", at = @At(value = "RETURN"))
	private void saveToFile(File pToFile, CallbackInfoReturnable<Boolean> cir) {
		TelemetryHelper.sendCrashReport(pToFile);
	}
}
