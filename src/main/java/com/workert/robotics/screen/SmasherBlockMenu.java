package com.workert.robotics.screen;

import com.workert.robotics.block.entity.custom.SmasherBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class SmasherBlockMenu extends AbstractContainerMenu {

    private final SmasherBlockEntity blockEntity;
    private final Level level;

    public SmasherBlockMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }
    public SmasherBlockMenu(int pContainerId, Inventory inv, BlockEntity entity) {
        super(pMenuType, pContainerId);
        checkContainerSize(inv, 3);
        blockEntity = ((SmasherBlockEntity) entity);
        this.level = inv.player.level;



    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }
}
