package com.workert.robotics.content.computers.computer;


import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.source.SingleLineDisplaySource;
import com.simibubi.create.content.logistics.block.display.target.DisplayTargetStats;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplaySection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TerminalDisplaySource extends SingleLineDisplaySource {
	@Override
	protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
		if (context.level().isClientSide)
			return EMPTY_LINE;
		if (!(context.getSourceTE() instanceof ComputerBlockEntity computer))
			return EMPTY_LINE;
		if (computer.getSpeed() == 0)
			return EMPTY_LINE;
		String[] lines = computer.getTerminal().getString().split("\n");
		return Component.literal(lines[lines.length - 1]);
	}


	@Override
	public int getPassiveRefreshTicks() {
		return 5;
	}

	@Override
	protected boolean allowsLabeling(DisplayLinkContext context) {
		return false;
	}

	@Override
	protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
		return "Instant";
	}

	@Override
	protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
		return new FlapDisplaySection(size * FlapDisplaySection.MONOSPACE, "instant", false, false);
	}

	@Override
	protected String getTranslationKey() {
		return "stop_watch";
	}
}
