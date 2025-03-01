package com.workert.robotics.content.robotics.codeeditor;

import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.workert.robotics.base.registries.PacketRegistry;
import com.workert.robotics.unused.CodeHelper;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.theme.Color;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class CodeEditorScreen extends AbstractSimiScreen {

	private final String DEFAULT_TEXT = "\n\n\n/*\nThis is a comment showing some basic commands. It is meant to allow certain text editors to autocomplete them.\nFor more information visit the Programming Mechanic page of the Create Robotics Wiki:\nhttps://github.com/Worker20/CreateRobotics/wiki/Programming-Mechanic\n\nDirection.UP\nDirection.DOWN\nDirection.NORTH\nDirection.EAST\nDirection.SOUTH\nDirection.WEST\n{variables}\n{commands}\n*/";

	protected AllGuiTextures background;
	private IconButton confirmButton;
	private IconButton editButton;

	private File editFile = null;

	public CodeEditorScreen(String code) {
		super(Component.literal("Code Editor"));
		try {
			this.editFile = File.createTempFile("robotprogram-", ".code");
			FileWriter writer = new FileWriter(this.editFile);
			if (code != "") {
				writer.write(code);
			} else {
				String defaultText = this.DEFAULT_TEXT;

				final String[] variables = {""};
				CodeHelper.internalVariableLookupMap.forEach(
						(variable, function) -> variables[0] = variables[0].concat("\n" + variable));
				defaultText = defaultText.replace("{variables}", variables[0]);

				final String[] commands = {""};
				CodeHelper.commandMap.forEach((command, biConsumer) -> {
					commands[0] = commands[0].concat("\nrobot." + command);
				});
				defaultText = defaultText.replace("{commands}", commands[0]);

				writer.write(defaultText);
			}
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.background = AllGuiTextures.STATION;
	}

	@Override
	protected void init() {
		this.setWindowSize(this.background.getWidth(), this.background.getHeight());
		super.init();
		this.clearWidgets();

		int x = this.guiLeft;
		int y = this.guiTop;

		this.editButton = new IconButton(x + this.background.getWidth() - 63, y + this.background.getHeight() - 24,
				AllIcons.I_CONFIG_OPEN).withCallback((mouseX, mouseY) -> Util.getPlatform().openFile(this.editFile));
		this.addRenderableWidget(this.editButton);

		this.confirmButton = new IconButton(x + this.background.getWidth() - 33, y + this.background.getHeight() - 24,
				AllIcons.I_CONFIRM).withCallback((mouseX, mouseY) -> this.onClose());
		this.addRenderableWidget(this.confirmButton);

		// this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = this.guiLeft;
		int y = this.guiTop;

		this.background.render(graphics, x, y, Color.BLACK);

		graphics.pose().pushPose();
		TransformStack<PoseTransformStack> msr = TransformStack.of(graphics.pose());
		msr.pushPose().translate(x + this.background.getWidth() + 4, y + this.background.getHeight() + 4, 100).scale(40)
				.rotateX(-22).rotateY(63);

		graphics.pose().popPose();

		graphics.drawCenteredString(new NoShadowFontWrapper(this.font), "Drone Programmer",
				this.width / 2,
				this.guiTop + 4, 0x442000);

		graphics.drawCenteredString(new NoShadowFontWrapper(this.font), "yes I will make", this.width / 2,
				this.guiTop + 64, 0x4a4a4a);
		graphics.drawCenteredString(new NoShadowFontWrapper(this.font), "my own background!",
				this.width / 2,
				this.guiTop + 73, 0x4a4a4a);

		graphics.drawCenteredString(new NoShadowFontWrapper(this.font), "Click this to edit code ->",
				this.width / 2 - 27, this.guiTop + 109, 0x442000);
	}

	@Override
	public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
		this.renderBackground(graphics);
		super.render(graphics, pMouseX, pMouseY, pPartialTick);
	}

	@Override
	public void onClose() {
		try {
			FileInputStream inputStream = new FileInputStream(this.editFile);
			try {
				String fileCode = IOUtils.toString(inputStream);
				PacketRegistry.getChannel().sendToServer(new ReturnEditedCodePacket(fileCode));
			} finally {
				inputStream.close();
			}
			super.onClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.editFile.delete();
		super.onClose();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}


}
