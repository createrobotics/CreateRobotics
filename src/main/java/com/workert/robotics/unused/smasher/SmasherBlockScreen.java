package com.workert.robotics.unused.smasher;

import com.mojang.blaze3d.systems.RenderSystem;
import com.workert.robotics.Robotics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SmasherBlockScreen extends AbstractContainerScreen<SmasherBlockMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(Robotics.MOD_ID,
			"textures/gui/smasher_block_gui.png");

	public SmasherBlockScreen(SmasherBlockMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;

		//this.blit(guiGraphics, x, y, 0, 0, this.imageWidth, this.imageHeight);

		if (this.menu.isCrafting()) {
			//this.blit(guiGraphics, x + 103, y + 41, 176, 0, 8, this.menu.getScaledProgress());
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, delta);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}
}
