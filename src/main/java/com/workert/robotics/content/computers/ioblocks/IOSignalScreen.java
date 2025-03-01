package com.workert.robotics.content.computers.ioblocks;

import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.workert.robotics.base.registries.PacketRegistry;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.glfw.GLFW;

public class IOSignalScreen extends AbstractSimiScreen {

	private AllGuiTextures background;
	private EditBox nameField;
	private BlockPos blockPos;
	private IconButton abort;
	private IconButton confirm;

	private final Component abortLabel = Component.translatable("action.discard");
	private final Component confirmLabel = Component.translatable("action.saveToFile");

	private BlockEntity be;

	public IOSignalScreen(BlockEntity be) {
		super(Component.literal("Edit Signal Name"));
		this.background = AllGuiTextures.SCHEMATIC_PROMPT;

		this.be = be;
		this.blockPos = be.getBlockPos();
	}

	@Override
	protected void init() {
		if (!(this.be instanceof IOBlockEntity inputBlock)) {
			this.onClose();
			return;
		}
		this.setWindowSize(this.background.getWidth(), this.background.getHeight());
		super.init();

		int x = this.guiLeft;
		int y = this.guiTop;

		this.nameField = new EditBox(this.font, x + 49, y + 26, 131, 10, Component.empty());
		this.nameField.setTextColor(-1);
		this.nameField.setTextColorUneditable(-1);
		this.nameField.setBordered(false);
		this.setFocused(this.nameField);
		this.nameField.setValue(inputBlock.getSignalName());
		this.addRenderableWidget(this.nameField);


		this.abort = new IconButton(x + 7, y + 53, AllIcons.I_TRASH);
		this.abort.withCallback(() -> {
			CreateClient.SCHEMATIC_AND_QUILL_HANDLER.discard();
			this.onClose();
		});
		this.abort.setToolTip(this.abortLabel);
		this.addRenderableWidget(this.abort);

		this.confirm = new IconButton(x + 158, y + 53, AllIcons.I_CONFIRM);
		this.confirm.withCallback(() -> {
			this.confirm();
		});
		this.confirm.setToolTip(this.confirmLabel);
		this.addRenderableWidget(this.confirm);


	}

	@Override
	protected void renderWindow(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int x = this.guiLeft;
		int y = this.guiTop;
		this.background.render(guiGraphics, x, y, Color.BLACK);
		guiGraphics.drawCenteredString(this.font, this.title, x + (this.background.getWidth() - 8) / 2, y + 3,
				0xFFFFFF);
		GuiGameElement.of(AllItems.ANDESITE_ALLOY.asStack()).at(x + 22, y + 23, 0).render(guiGraphics);


	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER) {
			this.confirm();
			return true;
		}
		if (keyCode == 256 && this.shouldCloseOnEsc()) {
			this.onClose();
			return true;
		}
		return this.nameField.keyPressed(keyCode, scanCode, modifiers);
	}

	private void confirm() {
		PacketRegistry.getChannel()
				.sendToServer(new ConfigureIOBlockSignalPacket(this.blockPos, this.nameField.getValue()));
		this.onClose();
	}
}
