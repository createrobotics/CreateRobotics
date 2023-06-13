package com.workert.robotics.content.computers.computer;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.workert.robotics.base.roboscript.RoboScript;
import com.workert.robotics.base.roboscript.ingame.CompoundTagEnvironmentConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ComputerBlockEntity extends KineticTileEntity {

	public String script = "";
	public String terminal = "";
	public boolean running = false;

	public final RoboScript roboScript;

	public ComputerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
		this.roboScript = new RoboScript() {

			@Override
			public void print(String message) {
				ComputerBlockEntity.this.terminal = ComputerBlockEntity.this.terminal.concat(message + "\n");
				//System.out.println(message);
			}

			@Override
			public void error(String error) {
				ComputerBlockEntity.this.terminal = ComputerBlockEntity.this.terminal.concat(error + "\n");
				//System.err.println(error);
			}
		};
	}

	public void runScript() {
		this.roboScript.runString(this.script);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		this.script = compound.getString("Script");
		this.terminal = compound.getString("Terminal");
		this.running = compound.getBoolean("Running");
		this.roboScript.setValues(
				CompoundTagEnvironmentConversion.valuesFromTag(compound.getList("Memory", Tag.TAG_COMPOUND)));
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putString("Script", this.script);
		compound.putString("Terminal", this.terminal);
		compound.putBoolean("Running", this.running);

		compound.put("Memory",
				CompoundTagEnvironmentConversion.valuesToTag(this.roboScript.interpreter.getValues()));

	}

	@Override
	public void tick() {
		super.tick();
		if (!this.isSpeedRequirementFulfilled()) {
			this.roboScript.requestStop();
			this.terminal.concat("ERROR: Speed requirement not fulfilled, stopped program.\n");
		}
	}
}
