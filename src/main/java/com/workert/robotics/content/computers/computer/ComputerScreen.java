package com.workert.robotics.content.computers.computer;


import com.lightdev6.computing.AllPackets;
import com.lightdev6.computing.gui.MultiLineTextBox;
import com.lightdev6.computing.packets.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ComputerScreen extends AbstractSimiScreen {
	private MultiLineEditBox terminal;
	private MultiLineTextBox output;
	private BlockPos blockPos;
	private IconButton save;
	private IconButton run;
	private IconButton clear;


	private ComputerBlockEntity computer;

	private int timer;

	public ComputerScreen(ComputerBlockEntity computer) {
		super(Component.literal("Edit Signal Name"));
		this.computer = computer;
		this.blockPos = computer.getBlockPos();
	}


	@Override
	public void tick() {
		super.tick();
        this.computer.getUpdateTag();
        this.output.setValue(this.computer.getTerminal());
		if (this.timer >= 4) {
            this.timer = 0;
			AllPackets.channel.sendToServer(new ComputerRequestUpdatePacket(this.blockPos));
		} else {
            this.timer++;
		}

		if (this.computer.getRunning()) {
            this.run.setIcon(AllIcons.I_STOP);
            this.run.setToolTip(Component.literal("Stop Program"));
		} else if (this.computer.isSpeedRequirementFulfilled()) {
            this.run.setIcon(AllIcons.I_PLAY);
            this.run.setToolTip(Component.literal("Run Program"));
		} else {
            this.run.setIcon(AllIcons.I_PAUSE);
            this.run.setToolTip(Component.literal("Not enough speed"));
		}
	}

	@Override
	protected void init() {
		int width = 380;
		int height = 210;
        this.setWindowSize(width, height);
		super.init();

		int x = this.guiLeft;
		int y = this.guiTop;


		if (this.computer.getRunning()) {
            this.run = new IconButton(x - 20, y, AllIcons.I_STOP);
		} else {
            this.run = new IconButton(x - 20, y, AllIcons.I_PLAY);
		}
        this.run.withCallback(() -> {
			if (this.computer.getRunning()) {
                this.stop();
			} else {
                this.run();
			}
		});
        this.run.setToolTip(Component.literal("Run Program"));
        this.addRenderableWidget(this.run);

        this.save = new IconButton(x - 20, y + 20, AllIcons.I_CONFIG_SAVE);
        this.save.withCallback(() -> {
            this.save();
		});
        this.save.setToolTip(Component.literal("Save Script"));
        this.addRenderableWidget(this.save);

        this.terminal = new MultiLineEditBox(this.font, x, y, width, 150, Components.immutableEmpty(),
				Components.immutableEmpty());
        this.terminal.setValue(this.computer.getScript());
        this.setInitialFocus(this.terminal);

        this.addRenderableWidget(this.terminal);

        this.output = new MultiLineTextBox(this.font, x, y + 153, width, 57, Components.immutableEmpty(),
				Components.immutableEmpty());
        this.output.setValue(this.computer.getTerminal());
        this.addRenderableWidget(this.output);

        this.clear = new IconButton(x - 20, y + 153, AllIcons.I_TRASH);
        this.clear.withCallback(() -> {
            this.clearTerminal();
		});
        this.clear.setToolTip(Component.literal("Clear Terminal"));
        this.addRenderableWidget(this.clear);


	}


	@Override
	protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		int x = this.guiLeft;
		int y = this.guiTop;


	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.confirm();
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_TAB && this.terminal.isFocused()) {
            this.terminal.textField.insertText("    ");
		}

		//getting a character by the index of the cursor will always return the character infront of the cursor.
		if (keyCode == GLFW.GLFW_KEY_ENTER && this.terminal.isFocused()) {
			if (this.terminal.textField.cursor != 0) {
				if (this.terminal.getValue().charAt(this.terminal.textField.cursor - 1) == "{".charAt(0)) {
                    this.terminal.textField.insertText("\n    \n}");
                    this.terminal.textField.cursor -= 2;
                    this.terminal.textField.selectCursor = this.terminal.textField.cursor;
					return true;

				}

			}
		}
		return this.terminal.keyPressed(keyCode, scanCode, modifiers);


	}

	@Override
	public boolean charTyped(char p_94683_, int p_94684_) {
		if (p_94683_ == "(".charAt(0)) {
            this.terminal.textField.insertText("()");
            this.terminal.textField.cursor -= 1;
            this.terminal.textField.selectCursor = this.terminal.textField.cursor;
			return true;
		}


		return super.charTyped(p_94683_, p_94684_);
	}

	private void confirm() {
        this.save();
        this.onClose();
	}

	private void save() {
		AllPackets.channel.sendToServer(new ConfigureComputerScriptPacket(this.blockPos, this.terminal.getValue()));


	}

	private void run() {
		AllPackets.channel.sendToServer(new ComputerSendRunPacket(this.blockPos));
	}

	private void stop() {
		AllPackets.channel.sendToServer(new ComputerSendStopPacket(this.blockPos));
	}

	private void clearTerminal() {
		AllPackets.channel.sendToServer(new ComputerSendTerminalPacket(this.blockPos, ""));
	}
}
