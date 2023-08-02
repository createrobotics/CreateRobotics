package com.workert.robotics.mixin;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import com.simibubi.create.foundation.utility.WorldAttached;
import com.workert.robotics.content.robotics.flyingtoolbox.FakeToolboxTileEntity;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolbox;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolboxHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

@Mixin(value = ToolboxHandler.class, remap = false)
public abstract class ToolboxHandlerMixin {
	@Final
	@Shadow
	public static WorldAttached<WeakHashMap<BlockPos, ToolboxTileEntity>> toolboxes;

	@Shadow
	public static void syncData(Player player) {
	}

	/**
	 * @author OutCraft
	 * @reason Adding fake toolboxes to {@code getNearest}
	 */
	@Overwrite
	public static List<ToolboxTileEntity> getNearest(LevelAccessor world, Player player, int maxAmount) {
		Vec3 location = player.position();
		double maxRange = ToolboxHandler.getMaxRange(player);

		HashMap<BlockPos, ToolboxTileEntity> toolboxTileEntities = new HashMap<>();

		toolboxTileEntities.putAll(toolboxes.get(world));

		FlyingToolboxHandler.flyingToolboxes.get(world).forEach(
				flyingToolbox -> toolboxTileEntities.put(flyingToolbox.blockPosition(),
						flyingToolbox.getFakeToolboxTileEntity()));

		return toolboxTileEntities
				.keySet()
				.stream()
				.filter(p -> ToolboxHandler.distance(location, p) < maxRange * maxRange)
				.sorted((p1, p2) -> Double.compare(ToolboxHandler.distance(location, p1),
						ToolboxHandler.distance(location, p2)))
				.limit(maxAmount)
				.map(toolboxTileEntities::get)
				.filter(ToolboxTileEntity::isFullyInitialized)
				.collect(Collectors.toList());
	}

	@Inject(method = "entityTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isLoaded(Lnet/minecraft/core/BlockPos;)Z", remap = true), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void connectFlyingToolboxes(Entity entity, Level world, CallbackInfo ci, ServerPlayer player, boolean sendData, CompoundTag compound, int i, String key, CompoundTag data, BlockPos pos, int slot) {
		if (data.hasUUID("EntityUUID") && !world.isClientSide) {
			if (((ServerLevel) world).getEntity(data.getUUID("EntityUUID")) instanceof FlyingToolbox flyingToolbox) {
				flyingToolbox.getFakeToolboxTileEntity().connectPlayer(slot, player, i);
				player.getPersistentData()
						.getCompound("CreateToolboxData").getCompound(key)
						.put("Pos", NbtUtils.writeBlockPos(flyingToolbox.blockPosition()));
			} else {
				compound.remove(key);
			}
			syncData(player);
			ci.cancel();
		}
	}

	@Inject(method = "unequip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;", shift = At.Shift.BEFORE, remap = true), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void unequip(Player player, int hotbarSlot, boolean keepItems, CallbackInfo ci, CompoundTag compound, Level world, String key, CompoundTag prevData, BlockPos prevPos, int prevSlot) {
		if (!world.isClientSide && prevData.hasUUID("EntityUUID")) {
			Entity entity = ((ServerLevel) world).getEntity(prevData.getUUID("EntityUUID"));
			if (!(entity instanceof FlyingToolbox flyingToolbox))
				throw new IllegalStateException("Entity with UUID is not a FlyingToolbox Entity");
			FakeToolboxTileEntity fakeToolboxTileEntity = flyingToolbox.getFakeToolboxTileEntity();
			fakeToolboxTileEntity.unequip(prevSlot, player, hotbarSlot,
					keepItems || !ToolboxHandler.withinRange(player, fakeToolboxTileEntity));
			syncData(player);
		}
	}
}