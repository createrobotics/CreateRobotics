package com.workert.robotics.items;

import com.mojang.blaze3d.platform.InputConstants;
import com.workert.robotics.Robotics;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

@Mod.EventBusSubscriber(modid = Robotics.MOD_ID)
public class ExtendOBootsItem extends ArmorItem {
    private boolean activated, lControlHeld = false;
    private int currentHeight = 0;
    private static final int MAX_HEIGHT = 10;
    private double pX, pY, pZ;
    private Player plr;

    public ExtendOBootsItem(ArmorMaterial pMaterial, EquipmentSlot pSlot, Properties pProperties) {
        super(pMaterial, pSlot, pProperties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        super.onArmorTick(stack, level, player);
        if (!level.isClientSide()) {
            plr = player;
            if (activated && Math.abs(player.getX() - pX) < 0.0025d && Math.abs(player.getZ() - pZ) < 0.0025d) {
                teleport(player, player.getX(), pY + currentHeight, player.getZ());
            } else if (activated) {
                currentHeight = 0;
                activated = false;
                teleport(player, player.getX(), pY, player.getZ());
                System.out.println("DEACTIVATING");
            }
        }
    }

    @SubscribeEvent
    public void detectScroll(InputEvent.MouseScrollingEvent mouseEvent) {
        if (mouseEvent.getScrollDelta() > 0 && currentHeight < 10 && activated && lControlHeld) {
            System.out.println("New height, " + currentHeight);
            currentHeight += 1;
        } else if (mouseEvent.getScrollDelta() < 0 && currentHeight > 0 && activated && lControlHeld) {
            currentHeight -= 1;
            System.out.println("New height, " + currentHeight);
        }
    }

    @SubscribeEvent
    public void detectInput(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_LEFT_CONTROL && event.getAction() == InputConstants.PRESS && plr.isOnGround()) {
            System.out.println("ACTIVATED, currHeight: " + currentHeight);
            pX = plr.getX();
            pY = plr.getY();
            pZ = plr.getZ();
            activated = true;
        } else if (event.getKey() == GLFW.GLFW_KEY_LEFT_CONTROL && event.getAction() == InputConstants.RELEASE && currentHeight == 0) {
            System.out.println("DEACTIVATED, currHeight: " + currentHeight);
            activated = false;
        }

        if (event.getKey() == GLFW.GLFW_KEY_LEFT_CONTROL && event.getAction() == InputConstants.PRESS) {
            lControlHeld = true;
        } else if (event.getKey() == GLFW.GLFW_KEY_LEFT_CONTROL && event.getAction() == InputConstants.RELEASE) {
            lControlHeld = false;
        }
    }

    public static void teleport(Player player, double pX, double pY, double pZ) {
        player.teleportTo(pX, pY, pZ);
    }
}