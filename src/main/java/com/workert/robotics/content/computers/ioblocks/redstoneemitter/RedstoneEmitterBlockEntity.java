package com.workert.robotics.content.computers.ioblocks.redstoneemitter;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.workert.robotics.base.roboscript.RoboScriptClass;
import com.workert.robotics.base.roboscript.RoboScriptHelper;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import com.workert.robotics.content.computers.ioblocks.IOBlockEntity;
import com.workert.robotics.content.computers.ioblocks.IORoboScriptBlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RedstoneEmitterBlockEntity extends SyncedTileEntity implements IOBlockEntity {

	public static RoboScriptClass roboScriptBlockClass = IORoboScriptBlockHelper.createClass()
			.addMethod("setPower", 1, (vm, fun) ->
			{
				((RedstoneEmitterBlockEntity) IORoboScriptBlockHelper.getBlockEntityFromMethod(fun)).setRedstoneLevel(
						Math.min(RoboScriptHelper.doubleToInt(RoboScriptHelper.asPositiveWholeDouble(vm.popStack())),
								15));
				return null;
			})
			.addMethod("getPower", 0, (vm, fun) -> RoboScriptHelper.numToDouble(
					((RedstoneEmitterBlockEntity) IORoboScriptBlockHelper.getBlockEntityFromMethod(fun)).redstoneLevel))
			.build();

	private String signalName = "";
	private BlockPos targetPos = this.getBlockPos();
	public RoboScriptObject roboScriptBlockInstance = IORoboScriptBlockHelper.createObject(roboScriptBlockClass, this)
			.build();

	int redstoneLevel = 0;

	public RedstoneEmitterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		this.signalName = compound.getString("SignalName");
		this.targetPos = NbtUtils.readBlockPos(compound.getCompound("TargetPosition"));
		this.redstoneLevel = compound.getInt("RedstoneLevel");
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putString("SignalName", this.signalName);
		compound.put("TargetPosition", NbtUtils.writeBlockPos(this.targetPos));
		compound.putInt("RedstoneLevel", this.redstoneLevel);
	}

	@Override
	public String getSignalName() {
		return this.signalName;
	}

	@Override
	public void setSignalName(String signalName) {
		this.signalName = signalName;
		if (this.getConnectedComputer() != null) {
			this.getConnectedComputer().connectedBlocks.put(this.signalName, this.getBlockEntityPos());
		}
	}

	@Override
	public BlockPos getTargetPos() {
		return this.targetPos;
	}

	@Override
	public BlockPos getBlockEntityPos() {
		return this.getBlockPos();
	}

	@Override
	public SyncedTileEntity getBlockEntity() {
		return this;
	}

	@Override
	public RoboScriptObject getRoboScriptObject() {
		return this.roboScriptBlockInstance;
	}

	@Nullable
	public ComputerBlockEntity getConnectedComputer() {
		return this.getLevel().getExistingBlockEntity(this.targetPos) instanceof ComputerBlockEntity e ? e : null;
	}

	private void setRedstoneLevel(int redstoneLevel) {
		this.redstoneLevel = redstoneLevel;
		this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 0);
	}

}
