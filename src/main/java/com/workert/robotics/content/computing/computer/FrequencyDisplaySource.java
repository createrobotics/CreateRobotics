package com.workert.robotics.content.computing.computer;

import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.source.SingleLineDisplaySource;
import com.simibubi.create.content.logistics.block.display.target.DisplayTargetStats;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplaySection;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FrequencyDisplaySource extends SingleLineDisplaySource {
	@Override
	protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
		if (!(context.level() instanceof ServerLevel level))
			return null;
		if (!(context.getSourceTE() instanceof ComputerBlockEntity computer))
			return null;
		if (!computer.isSpeedRequirementFulfilled())
			return null;
		return Component.literal(computer.getDisplayFreq(context.sourceConfig().getInt("DisplayFrequency")));
	}

	@Override
	protected boolean allowsLabeling(DisplayLinkContext context) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
		super.initConfigurationWidgets(context, builder, isFirstLine);
		if (isFirstLine)
			return;

		builder.addSelectionScrollInput(0, 80, (si, l) -> {
			si.forOptions(
							Lang.translatedOptions("display_source.computer_display_frequency", "display_zero", "display_one",
									"display_two", "display_three", "display_four", "display_five"))
					.titled(Lang.translateDirect("display_source.computer_display_frequency"));
		}, "DisplayFrequency");

	}

	@Override
	public int getPassiveRefreshTicks() {
		return 10;
	}

	@Override
	protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
		return "Instant";
	}

	@Override
	protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
		return new FlapDisplaySection(size * FlapDisplaySection.MONOSPACE, "instant", false, false);
	}
}
