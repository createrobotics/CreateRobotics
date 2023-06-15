package com.workert.robotics.content.computers.computer;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.workert.robotics.base.roboscript.RoboScript;
import com.workert.robotics.base.roboscript.ingame.CompoundTagEnvironmentConversionHelper;
import com.workert.robotics.base.roboscript.ingame.LineLimitedString;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ComputerBlockEntity extends KineticTileEntity {
	public final RoboScript roboScript;
	private String script = "";

	public static final int TERMINAL_LINE_LIMIT = 2048;
	private LineLimitedString terminal = new LineLimitedString(TERMINAL_LINE_LIMIT);

	private boolean running = false;


	public ComputerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
		this.roboScript = new RoboScript() {

			@Override
			public void print(String message) {
				ComputerBlockEntity.this.terminal.addText(message + "\n");
				ComputerBlockEntity.this.notifyUpdate();
			}

			@Override
			public void error(String error) {
				ComputerBlockEntity.this.terminal.addText(error + "\n");
				ComputerBlockEntity.this.notifyUpdate();
			}
		};
	}

	public void runScript() {
		this.roboScript.runString(this.script);
		this.running = true;
	}

	public void turnOff() {
		this.roboScript.requestStop();
		this.running = false;
	}

	public void interpretSignal(String function, List<Object> args) {
		if (this.running) this.roboScript.runFunction(function, args);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		this.script = compound.getString("Script");
		this.terminal = new LineLimitedString(TERMINAL_LINE_LIMIT, compound.getString("Terminal"));
		this.running = compound.getBoolean("Running");
		this.roboScript.putVariables(
				CompoundTagEnvironmentConversionHelper.valuesFromCompoundTag(
						compound.getCompound("Memory")));
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putString("Script", this.script);
		compound.putString("Terminal", this.terminal.getString());
		compound.putBoolean("Running", this.running);

		compound.put("Memory",
				CompoundTagEnvironmentConversionHelper.valuesToTag(this.roboScript.getVariables()));

	}

	@Override
	public void tick() {
		super.tick();
		if (!this.isSpeedRequirementFulfilled()) {
			this.roboScript.requestStop();
			this.running = false;
			this.terminal.addText("ERROR: Speed requirement not fulfilled, stopped program.\n");
		}
	}

	public LineLimitedString getTerminal() {
		return this.terminal;
	}

	public String getScript() {
		return this.script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public boolean getRunning() {
		return this.running;
	}

	public void clearTerminal() {
		this.terminal = new LineLimitedString(TERMINAL_LINE_LIMIT);
	}
}
