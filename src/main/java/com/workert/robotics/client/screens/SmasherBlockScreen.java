package com.workert.robotics.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.workert.robotics.Robotics;
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
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

		if (menu.isCrafting()) {
			blit(pPoseStack, x + 103, y + 41, 176, 0, 8, menu.getScaledProgress());
			// la x es de donde comienza la flecha, y de donde comienza la flecha desde su altura o barra de progreso, el otro numero es la y de lo que
			//quiero dibujar y el otro numero es el ancho de lo que dibujare y el otro es la altura que se dibujara progresivamente :)
		}
	}

	@Override
	public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
		renderBackground(pPoseStack);
		super.render(pPoseStack, mouseX, mouseY, delta);
		renderTooltip(pPoseStack, mouseX, mouseY);
	}
}
