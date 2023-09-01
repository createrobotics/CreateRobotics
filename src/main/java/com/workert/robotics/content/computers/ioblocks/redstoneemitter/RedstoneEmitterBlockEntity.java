package com.workert.robotics.content.computers.ioblocks.redstoneemitter;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.workert.robotics.base.roboscript.RoboScriptClass;
import com.workert.robotics.base.roboscript.RoboScriptField;
import com.workert.robotics.base.roboscript.RoboScriptNativeMethod;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import com.workert.robotics.base.roboscript.util.RoboScriptArgumentPredicates;
import com.workert.robotics.base.roboscript.util.RoboScriptObjectConversions;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import com.workert.robotics.content.computers.ioblocks.IOBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RedstoneEmitterBlockEntity extends SyncedTileEntity implements IOBlockEntity {

	public static RoboScriptClass roboScriptBlockClass = makeClass();

	private String signalName = "";
	private BlockPos targetPos = this.getBlockPos();
	public RoboScriptObject roboScriptBlockInstance = this.makeInstance();

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
			this.getConnectedComputer().connectedIOBlocks.put(this.signalName, this.getBlockEntityPos());
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


	private static RoboScriptClass makeClass() {
		RoboScriptClass clazz = new RoboScriptClass();
		RoboScriptNativeMethod setPower = new RoboScriptNativeMethod((byte) 1);
		setPower.function = (vm, fun) ->
		{
			getBlockEntityFromMethod((RoboScriptNativeMethod) fun).setRedstoneLevel(Math.min(
					RoboScriptArgumentPredicates.asPositiveFullNumber(vm.popStack(), true), 15));
			return null;
		};
		RoboScriptNativeMethod getPower = new RoboScriptNativeMethod((byte) 0);
		getPower.function = (vm, fun) -> RoboScriptObjectConversions.prepareForRoboScriptUse(
				getBlockEntityFromMethod((RoboScriptNativeMethod) fun).redstoneLevel);

		clazz.functions.put("setPower", setPower);
		clazz.functions.put("getPower", getPower);
		return clazz;
	}

	private RoboScriptObject makeInstance() {
		RoboScriptObject object = new RoboScriptObject(roboScriptBlockClass);
		object.fields.put("", new RoboScriptField(this, true));
		return object;
	}

	private static RedstoneEmitterBlockEntity getBlockEntityFromMethod(RoboScriptNativeMethod method) {
		return ((RedstoneEmitterBlockEntity) ((RoboScriptObject) method.getParentClassInstance()).fields.get("").value);
	}

}
