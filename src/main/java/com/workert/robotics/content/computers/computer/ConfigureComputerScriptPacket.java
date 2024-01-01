package com.workert.robotics.content.computers.computer;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureComputerScriptPacket extends BlockEntityConfigurationPacket<ComputerBlockEntity> {
	private String script;

	public ConfigureComputerScriptPacket(FriendlyByteBuf buffer) {
		super(buffer);
	}

	public ConfigureComputerScriptPacket(BlockPos pos, String script) {
		super(pos);
		this.script = script;
	}

	@Override
	protected void writeSettings(FriendlyByteBuf buffer) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Script", this.script);
		buffer.writeNbt(compoundTag);
	}

	@Override
	protected void readSettings(FriendlyByteBuf buffer) {
		this.script = buffer.readNbt().getString("Script");
	}

	@Override
	protected void applySettings(ComputerBlockEntity computerBlockEntity) {
		computerBlockEntity.setScript(this.script);
	}
}
