package com.workert.robotics.client.screens;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.workert.robotics.entities.CodeDrone;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

public class CodeDroneScreen extends AbstractSimiScreen {

	protected CodeDrone drone;

	protected AllGuiTextures background;
	private IconButton confirmButton;

	private EditBox codeTextBox;

	public CodeDroneScreen(CodeDrone drone) {
		this.drone = drone;
		this.background = AllGuiTextures.STATION;
	}

	@Override
	protected void init() {
		this.setWindowSize(this.background.width, this.background.height);
		super.init();
		this.clearWidgets();

		int x = this.guiLeft;
		int y = this.guiTop;

		this.confirmButton = new IconButton(x + this.background.width - 33, y + this.background.height - 24,
				AllIcons.I_CONFIRM);
		this.confirmButton.withCallback((mouseX, mouseY) -> {
			this.drone.droneCode = this.codeTextBox.getValue();
			this.onClose();
		});
		this.addRenderableWidget(this.confirmButton);

		this.codeTextBox = new EditBox(this.font, x + 23, y + 44, this.background.width - 54, 44,
				new TextComponent("Code Editor"));
		this.codeTextBox.setBordered(true); // Maybe false looks better?
		this.codeTextBox.setMaxLength(23);
		this.codeTextBox.setTextColor(0xFFFFFF);
		this.codeTextBox.setValue(this.drone.droneCode);
		this.codeTextBox.mouseClicked(0, 0, 0);

		this.addRenderableWidget(this.codeTextBox);
	}

	@Override
	protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		int x = this.guiLeft;
		int y = this.guiTop;

		this.background.render(ms, x, y, this);

		ms.pushPose();
		TransformStack msr = TransformStack.cast(ms);
		msr.pushPose().translate(x + this.background.width + 4, y + this.background.height + 4, 100).scale(40)
				.rotateX(-22).rotateY(63);

		ms.popPose();
	}

	@Override
	public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		super.render(ms, mouseX, mouseY, partialTicks);
		GuiComponent.drawCenteredString(ms, new NoShadowFontWrapper(this.font), "Drone Programmer", this.width / 2,
				this.guiTop + 4, 0x442000);
	}

}