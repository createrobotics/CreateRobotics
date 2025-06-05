package com.workert.robotics.base.expandedarm;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.workert.robotics.Robotics;
import com.workert.robotics.base.registries.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import java.util.*;

public class CustomArmBehavior {
    private static final Set<BlockPos> stoppedBelts = new HashSet<>();
    private static final Map<BlockPos, ProcessingData> processingItems = new HashMap<>();
    private static final int PROCESSING_TIME = 40; // 1 segundo (20 ticks)

    private static class ProcessingData {
        ItemStack processingItem;
        int remainingTicks;
        
        ProcessingData(ItemStack item, int ticks) {
            this.processingItem = item;
            this.remainingTicks = ticks;
        }
    }

    public static void checkBeltsForCustomProcessing(ArmBlockEntity arm, List<ArmInteractionPoint> points) {
        if (arm.getLevel().isClientSide || arm.getSpeed() == 0) 
            return;

        processingItems.entrySet().removeIf(entry -> {
            BlockPos pos = entry.getKey();
            ProcessingData data = entry.getValue();
            
            if (--data.remainingTicks <= 0) {
                completeProcessing(arm.getLevel(), pos, data);
                return true;
            }
            return false;
        });

        for (ArmInteractionPoint point : points) {
            if (!point.isValid() || !isInCustomMode(point)) 
                continue;
            checkBeltAtPoint(arm, point);
        }
    }

    private static void checkBeltAtPoint(ArmBlockEntity arm, ArmInteractionPoint point) {
        BlockPos targetPos = point.getPos();
        Level level = arm.getLevel();

        if (processingItems.containsKey(targetPos)) 
            return;

        TransportedItemStackHandlerBehaviour handler = 
            BlockEntityBehaviour.get(level, targetPos, TransportedItemStackHandlerBehaviour.TYPE);

        if (handler == null) 
            return;

        handler.handleProcessingOnAllItems(item -> {
            if (item == null || item.stack.isEmpty())
                return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();

            SimpleContainer container = new SimpleContainer(1);
            container.setItem(0, item.stack);
            RecipeWrapper recipeInv = new RecipeWrapper(new InvWrapper(container));
            
            if (ModRecipeTypes.CUSTOM_PROCESSING.find(recipeInv, level).isPresent()) {
                stopBelt(level, targetPos);
                processingItems.put(targetPos, new ProcessingData(item.stack.copy(), PROCESSING_TIME));
                return TransportedItemStackHandlerBehaviour.TransportedResult.removeItem();
            }
            return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
        });
    }

    private static void completeProcessing(Level level, BlockPos pos, ProcessingData data) {
        SimpleContainer container = new SimpleContainer(1);
        container.setItem(0, data.processingItem);
        RecipeWrapper recipeInv = new RecipeWrapper(new InvWrapper(container));
        
        Optional<CustomProcessingRecipe> recipe = ModRecipeTypes.CUSTOM_PROCESSING.find(recipeInv, level);
        
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
            result.setCount(data.processingItem.getCount());
            
            TransportedItemStackHandlerBehaviour handler = 
                BlockEntityBehaviour.get(level, pos, TransportedItemStackHandlerBehaviour.TYPE);
                
            if (handler != null) {
                TransportedItemStack transportedResult = new TransportedItemStack(result);
                if (data.processingItem.hasTag()) {
                    transportedResult.stack.setTag(data.processingItem.getTag().copy());
                }
                
                handler.handleProcessingOnAllItems(item -> 
                    TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(transportedResult));
            }
        }

        startBelt(level, pos);
        Robotics.LOGGER.info("Procesamiento completado en {}", pos);
    }

    public static void stopBelt(Level level, BlockPos pos) {
        if (!stoppedBelts.contains(pos)) {
            stoppedBelts.add(pos);
            TransportedItemStackHandlerBehaviour beltBehaviour = 
                BlockEntityBehaviour.get(level, pos, TransportedItemStackHandlerBehaviour.TYPE);
            
            if (beltBehaviour != null) {
                beltBehaviour.handleCenteredProcessingOnAllItems(0.51f, transported -> 
                    TransportedItemStackHandlerBehaviour.TransportedResult.doNothing());
            }
        }
    }

    public static void startBelt(Level level, BlockPos pos) {
        if (stoppedBelts.remove(pos)) {
            TransportedItemStackHandlerBehaviour beltBehaviour = 
                BlockEntityBehaviour.get(level, pos, TransportedItemStackHandlerBehaviour.TYPE);
            
            if (beltBehaviour != null) {
                beltBehaviour.handleCenteredProcessingOnAllItems(0.51f, transported -> 
                    TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(transported));
            }
        }
    }

    public static boolean isInCustomMode(ArmInteractionPoint point) {
        if (point instanceof ArmInteractionPointAccess access) {
            return access.isCustomMode();
        }
        return false;
    }
}