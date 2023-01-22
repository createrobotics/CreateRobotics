package com.workert.robotics.client.screens;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.workert.robotics.lists.PacketList;
import com.workert.robotics.packets.ReturnEditedCodePacket;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class CodeEditorScreen extends AbstractSimiScreen {

	private final String code;

	protected AllGuiTextures background;
	private IconButton confirmButton;
	private IconButton editButton;

	private final File editFile;

	public CodeEditorScreen(String code) {
		super(Component.literal("Code Editor"));
		this.code = code;
		try {
			this.editFile = File.createTempFile("robotprogram-", ".code");
			FileWriter writer = new FileWriter(this.editFile);
			writer.write(code);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.background = AllGuiTextures.STATION;
	}

	@Override
	protected void init() {
		this.setWindowSize(this.background.width, this.background.height);
		super.init();
		this.clearWidgets();

		int x = this.guiLeft;
		int y = this.guiTop;

		this.editButton = new IconButton(x + this.background.width - 63, y + this.background.height - 24,
				AllIcons.I_CONFIG_OPEN).withCallback((mouseX, mouseY) -> {
			Util.getPlatform().openFile(this.editFile);
		});
		this.addRenderableWidget(this.editButton);

		this.confirmButton = new IconButton(x + this.background.width - 33, y + this.background.height - 24,
				AllIcons.I_CONFIRM).withCallback((mouseX, mouseY) -> {
			try {
				FileInputStream inputStream = new FileInputStream(this.editFile);
				try {
					String fileCode = IOUtils.toString(inputStream);
					PacketList.CHANNEL.sendToServer(new ReturnEditedCodePacket(fileCode));
				} finally {
					inputStream.close();
				}
				this.onClose();
			} catch (Exception e) {
				e.fillInStackTrace();
			}
		});
		this.addRenderableWidget(this.confirmButton);

		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
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

		GuiComponent.drawCenteredString(ms, new NoShadowFontWrapper(this.font), "Drone Programmer", this.width / 2,
				this.guiTop + 4, 0x442000);

		GuiComponent.drawCenteredString(ms, new NoShadowFontWrapper(this.font), "yes I will make", this.width / 2,
				this.guiTop + 64, 0x4a4a4a);
		GuiComponent.drawCenteredString(ms, new NoShadowFontWrapper(this.font), "my own background!", this.width / 2,
				this.guiTop + 73, 0x4a4a4a);

		GuiComponent.drawCenteredString(ms, new NoShadowFontWrapper(this.font), "Click this to edit code ->",
				this.width / 2 - 27, this.guiTop + 109, 0x442000);
	}

	@Override
	public void render(PoseStack ms, int pMouseX, int pMouseY, float pPartialTick) {
		this.renderBackground(ms);
		super.render(ms, pMouseX, pMouseY, pPartialTick);
	}

	@Override
	public void onClose() {
		this.editFile.delete();
		super.onClose();
	}
}
