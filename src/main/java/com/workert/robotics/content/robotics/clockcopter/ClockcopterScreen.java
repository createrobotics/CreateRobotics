package com.workert.robotics.content.robotics.clockcopter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.client.gui.components.EditBox;

public class ClockcopterScreen extends AbstractSimiScreen {

	protected Clockcopter copter;

	protected AllGuiTextures screen;
	private IconButton confirmButton;

	private EditBox codeTextBox;

	public ClockcopterScreen(Clockcopter copter) {
		this.copter = copter;
		this.screen = AllGuiTextures.STATION;
	}

	@Override
	protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {

	}
}
