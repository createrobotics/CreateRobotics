package com.workert.robotics.content.computers.computer;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class ComputerDisplaySource extends DisplaySource {

	@Override
	public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
		if (context.level().isClientSide)
			return EMPTY;
		if (!(context.getSourceBlockEntity() instanceof ComputerBlockEntity computer))
			return EMPTY;
		if (computer.getSpeed() == 0)
			return EMPTY;


		return stringListToComponentList(computer.getOutputDisplay());
	}

	@Override
	public int getPassiveRefreshTicks() {
		return 2;
	}

	@Override
	public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay,
									  FlapDisplayLayout layout) {
		String layoutKey = "Instant";
		if (!layout.isLayout(layoutKey))
			layout.configure(layoutKey,
					ImmutableList.of(this.createSectionForValue(context, flapDisplay.getMaxCharCount())));
	}

	protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
		return new FlapDisplaySection(size * FlapDisplaySection.MONOSPACE, "instant", false, false);
	}

	@Override
	protected String getTranslationKey() {
		return "computer";
	}

	private static List<MutableComponent> stringListToComponentList(List<String> stringList) {
		if (stringList == null) return EMPTY;
		List<MutableComponent> componentList = new ArrayList<>();
		for (String s : stringList) {
			componentList.add(Component.literal(s));
		}
		return componentList;
	}
}
