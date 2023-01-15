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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Field;

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
			this.editFile = File.createTempFile("code-", ".code");
			if (code != null) {
				code.replace("|", CommonComponents.NEW_LINE.getString());
				BufferedWriter writer = new BufferedWriter(new FileWriter(this.editFile));
				writer.write(code);
				writer.close();
			}
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
					this.code.replace(CommonComponents.NEW_LINE.getString(), "|");
					PacketList.CHANNEL.sendToServer(new ReturnEditedCodePacket(fileCode));
				} finally {
					inputStream.close();
				}
				this.editFile.delete();
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
	}

	@Override
	public void render(PoseStack ms, int pMouseX, int pMouseY, float pPartialTick) {
		GuiComponent.drawCenteredString(ms, new NoShadowFontWrapper(this.font), "Drone Programmer", this.width / 2,
				this.guiTop + 4, 0x442000);

		this.renderBackground(ms);
		this.setFocused(null);

		super.render(ms, pMouseX, pMouseY, pPartialTick);
	}

	private static void setHeadless(boolean enabled) {
		try {
			System.setProperty("java.awt.headless", Boolean.toString(enabled));
			Field defaultHeadlessField = java.awt.GraphicsEnvironment.class.getDeclaredField("defaultHeadless");
			defaultHeadlessField.setAccessible(true);
			defaultHeadlessField.set(null, enabled);
			Field headlessField = java.awt.GraphicsEnvironment.class.getDeclaredField("headless");
			headlessField.setAccessible(true);
			headlessField.set(null, enabled);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

}