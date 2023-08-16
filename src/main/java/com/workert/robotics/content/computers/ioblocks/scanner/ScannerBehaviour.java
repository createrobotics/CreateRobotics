package com.workert.robotics.content.computers.ioblocks.scanner;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public class ScannerBehaviour extends BeltProcessingBehaviour {
	public static final int CYCLE = 240;
	public static final int ENTITY_SCAN = 10;
	public ScanningBehaviorSpecifics specifics;
	public int prevRunningTicks;
	public int runningTicks;
	public boolean running;
	public boolean finished;

	public interface ScanningBehaviorSpecifics {
		boolean scanOnBelt(TransportedItemStack itemStack);

		float getKineticSpeed();
	}

	public <T extends SmartTileEntity & ScanningBehaviorSpecifics> ScannerBehaviour(ScannerBlockEntity be) {
		super(be);
		this.specifics = be;
		this.whenItemEnters((s, i) -> BeltScannerCallbacks.onItemReceived(s, i, this));
		this.whileItemHeld((s, i) -> BeltScannerCallbacks.whenItemHeld(s, i, this));
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		this.running = nbt.getBoolean("Running");
		this.finished = nbt.getBoolean("Finished");
		this.prevRunningTicks = this.runningTicks = nbt.getInt("Ticks");
		super.read(nbt, clientPacket);
	}


	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		nbt.putBoolean("Running", this.running);
		nbt.putBoolean("Finished", this.finished);
		nbt.putInt("Ticks", this.runningTicks);
		super.write(nbt, clientPacket);
	}

	@Override
	public void tick() {
		super.tick();

		Level level = this.getWorld();
		BlockPos worldPosition = this.getPos();

		if (level.isClientSide && this.runningTicks == -CYCLE / 2) {
			this.prevRunningTicks = CYCLE / 2;
			return;
		}

		if (this.runningTicks == CYCLE / 2 && this.specifics.getKineticSpeed() != 0) {
			if (level.getBlockState(worldPosition.below(2)).getSoundType() == SoundType.WOOL)
				AllSoundEvents.MECHANICAL_PRESS_ACTIVATION_ON_BELT.playOnServer(level, worldPosition);
			else AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(level, worldPosition, .5f,
					.75f + (Math.abs(this.specifics.getKineticSpeed()) / 1024f));

			if (!level.isClientSide) this.tileEntity.sendData();
		}

		if (!level.isClientSide && this.runningTicks > CYCLE) {
			this.finished = true;
			this.running = false;
			this.tileEntity.sendData();
			return;
		}

		this.prevRunningTicks = this.runningTicks;
		this.runningTicks += this.getRunningTickSpeed();
		if (this.prevRunningTicks < CYCLE / 2 && this.runningTicks >= CYCLE / 2) {
			this.runningTicks = CYCLE / 2;
			// Pause the ticks until a packet is received
			if (level.isClientSide && !this.tileEntity.isVirtual()) this.runningTicks = -(CYCLE / 2);
		}
	}

	public float getRenderedHeadOffset(float partialTicks) {
        /*if (!running)
            return 0;
        int runningTicks = Math.abs(this.runningTicks);
        float ticks = Mth.lerp(partialTicks, prevRunningTicks, runningTicks);
        if (runningTicks < (CYCLE * 2) / 3) {
            System.out.println("Going down!");
            return (float) Mth.clamp(Math.pow(ticks / CYCLE * 2, 3), 0, 1);
        }
        System.out.println("Going down!");
        return Mth.clamp((CYCLE - ticks) / CYCLE * 3, 0, 1);*/

		if (!this.running) return 0;
		int runningTicks = Math.abs(this.runningTicks);
		float ticks = Mth.lerp(partialTicks, this.prevRunningTicks, runningTicks);
		if (runningTicks < (CYCLE * 2) / 3) {
			return (float) Mth.clamp(Math.pow(ticks / CYCLE * 2, 3), 0, 1);
		}
		return Mth.clamp((CYCLE - ticks) / CYCLE * 3, 0, 1);
	}

	public int getRunningTickSpeed() {
		float speed = this.specifics.getKineticSpeed();
		if (speed == 0) return 0;
		return (int) Mth.lerp(Mth.clamp(Math.abs(speed) / 512f, 0, 1), 1, 60);
	}

	public void start() {
		this.running = true;
		this.prevRunningTicks = 0;
		this.runningTicks = 0;
		this.tileEntity.sendData();
	}
}
