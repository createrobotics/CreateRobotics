package com.workert.robotics.content.computers.computer;
import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.source.DisplaySource;
import com.simibubi.create.content.logistics.block.display.target.DisplayTargetStats;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayLayout;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplaySection;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayTileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class ComputerDisplaySource extends DisplaySource {

	@Override
	public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
		if (context.level().isClientSide)
			return List.of(Component.empty());
		if (!(context.getSourceTE() instanceof ComputerBlockEntity computer))
			return List.of(Component.empty());
		if (!computer.isSpeedRequirementFulfilled())
			return List.of(Component.empty());
		return stringListToComponentList(computer.getOutputDisplay());
	}


	private static List<MutableComponent> stringListToComponentList(List<String> stringList) {
		List<MutableComponent> componentList = new ArrayList<>();
		for (String s : stringList) {
			componentList.add(Component.literal(s));
		}
		return componentList;
	}

	@Override
	public int getPassiveRefreshTicks() {
		return 2;
	}

	@Override
	protected String getTranslationKey() {
		return super.getTranslationKey();
	}

	@Override
	public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayTileEntity flapDisplay, FlapDisplayLayout layout) {
		String layoutKey = "Instant";

		if (layout.isLayout(layoutKey))
			return;

		FlapDisplaySection name = new FlapDisplaySection(FlapDisplaySection.MONOSPACE, "instant", false, false);
		layout.configure(layoutKey, List.of(name));
	}
}
