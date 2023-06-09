package com.workert.robotics.content.computers.computer;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.workert.robotics.base.roboscript.Interpreter;
import com.workert.robotics.base.roboscript.RoboScript;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class ComputerBlockEntity extends KineticTileEntity {

	private String script = "";
	private String terminal = "";
	private boolean running = false;

	public RoboScript roboScript;
	private CompoundTag savedVariables = new CompoundTag();

	public ComputerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
		this.roboScript = new RoboScript() {
			@Override
			public void saveVariableExternally(Map.Entry<String, Object> variableMap) {
				if (variableMap.getValue() instanceof Double doubleValue) {
					ComputerBlockEntity.this.savedVariables.putDouble(variableMap.getKey(), doubleValue);
				} else if (variableMap.getValue() instanceof String stringValue) {
					ComputerBlockEntity.this.savedVariables.putString(variableMap.getKey(), stringValue);
				} else if (variableMap.getValue() instanceof Boolean booleanValue) {
					ComputerBlockEntity.this.savedVariables.putBoolean(variableMap.getKey(), booleanValue);
				} else {
					ComputerBlockEntity.this.savedVariables.putString(variableMap.getKey(),
							Interpreter.stringify(variableMap.getValue()));
				}
			}

			@Override
			public Map<String, Object> getExternallySavedVariables() {
				Map<String, Object> variableMap = new HashMap<>();

				ComputerBlockEntity.this.savedVariables.getAllKeys().forEach(identifier -> {
					byte valueTagType = ComputerBlockEntity.this.savedVariables.get(identifier).getId();
					if (valueTagType == Tag.TAG_DOUBLE) {
						variableMap.put(identifier, ComputerBlockEntity.this.savedVariables.getDouble(identifier));
					} else if (valueTagType == Tag.TAG_STRING) {
						variableMap.put(identifier, ComputerBlockEntity.this.savedVariables.getString(identifier));
					} else if (valueTagType == Tag.TAG_BYTE) {
						variableMap.put(identifier, ComputerBlockEntity.this.savedVariables.getBoolean(identifier));
					}
				});
				return variableMap;
			}

			@Override
			public RunningState getRunningState() {
				if (!ComputerBlockEntity.this.isSpeedRequirementFulfilled())
					return RunningState.ENERGY_REQUIREMENT_NOT_MET;
				return super.getRunningState();
			}
		};
	}


	public void setScript(String script) {
		this.script = script;
	}

	public String getScript() {
		return this.script;
	}

	public void runScript() {
		this.roboScript.runString(this.script);
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getTerminal() {
		return this.terminal;
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		this.script = compound.getString("Script");
		this.terminal = compound.getString("Terminal");
		this.running = compound.getBoolean("Running");
		this.savedVariables = compound.getCompound("SavedVariables");
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putString("Script", this.script);
		compound.putString("Terminal", this.terminal);
		compound.putBoolean("Running", this.running);
		compound.put("SavedVariables", this.savedVariables);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.isSpeedRequirementFulfilled()) {
			this.roboScript.requestStop();
			this.setTerminal(this.getTerminal() + "ERROR: Speed requirement not fulfilled, stopped program." + "\n");
		}
	}
}
