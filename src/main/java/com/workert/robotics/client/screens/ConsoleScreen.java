package com.workert.robotics.client.screens;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.Components;
import com.workert.robotics.entities.AbstractRobotEntity;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;

public class ConsoleScreen extends AbstractSimiScreen {
	private MultiLineEditBox consoleOutput;
	private IconButton close;
	private IconButton run;

	private AbstractRobotEntity robot;

	public ConsoleScreen(AbstractRobotEntity robot) {
		super(Component.literal("Edit Signal Name"));
		this.robot = robot;
	}


	@Override
	public void tick() {
		super.tick();

		if (this.robot.getAir() > 0) {
			this.run.setIcon(AllIcons.I_PLAY);
			this.run.setToolTip(Component.literal("Currently running"));
		} else {
			this.run.setIcon(AllIcons.I_PAUSE);
			this.run.setToolTip(Component.literal("Not enough Air Pressure"));
		}
		this.consoleOutput.setValue(this.robot.getConsoleOutput());
	}

	@Override
	protected void init() {
		int width = 380;
		int height = 210;
		this.setWindowSize(width, height);
		super.init();
		int x = this.guiLeft;
		int y = this.guiTop;

		this.run = new IconButton(x - 20, y, AllIcons.I_STOP);
		this.run.withCallback(() -> {
		});
		this.addRenderableWidget(this.run);

		this.close = new IconButton(x - 20, y + 20, AllIcons.I_MTD_CLOSE);
		this.close.withCallback(this::onClose);
		this.close.setToolTip(Component.literal("Close"));
		this.addRenderableWidget(this.close);

		this.consoleOutput = new MultiLineEditBox(this.font, x, y, width, 210, Components.immutableEmpty(),
				Components.immutableEmpty()) {
			@Override
			public boolean isFocused() {
				return false;
			}
		};
		this.addRenderableWidget(this.consoleOutput);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	@Override
	protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
	}
}
