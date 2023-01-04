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

@Mod.EventBusSubscriber(modid = Robotics.MOD_ID)
public class ExtendOBootsItem extends ArmorItem {
    private static boolean activated = false;
    private static int currentHeight = 0;
    private static final int MAX_HEIGHT = 10;
    private static double pX, pY, pZ;
    private static Player plr;

    public ExtendOBootsItem(ArmorMaterial pMaterial, EquipmentSlot pSlot, Properties pProperties) {
        super(pMaterial, pSlot, pProperties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        super.onArmorTick(stack, level, player);
        if (!level.isClientSide()) {
            plr = player;
            if (activated) {
                teleport(player, pX, pY + currentHeight, pZ, player.getXRot(), player.getYRot());
            }
        }
    }

    @SubscribeEvent
    public static void detectScroll(InputEvent.MouseScrollingEvent mouseEvent) {
        if (mouseEvent.getScrollDelta() > 0 && currentHeight < 10 && activated) {
            System.out.println("New height, " + currentHeight);
            currentHeight += 1;
        } else if (mouseEvent.getScrollDelta() < 0 && currentHeight > 0 && activated) {
            currentHeight -= 1;
            System.out.println("New height, " + currentHeight);
        }
    }

    @SubscribeEvent
    public static void detectInput(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_LEFT_CONTROL && event.getAction() == InputConstants.REPEAT) {
            System.out.println("ACTIVATED, currHeight: " + currentHeight);
            pX = plr.getX();
            pY = plr.getY();
            pZ = plr.getZ();
            activated = true;
        } else if (event.getKey() == GLFW.GLFW_KEY_LEFT_CONTROL && event.getAction() == InputConstants.REPEAT && currentHeight == 0) {
            System.out.println("DEACTIVATED, currHeight: " + currentHeight);
            activated = false;
        }
    }

    public static void teleport(Player player, double pX, double pY, double pZ, float xRot, float yRot) {
        player.moveTo(pX, pY, pZ, yRot, xRot);
    }
}