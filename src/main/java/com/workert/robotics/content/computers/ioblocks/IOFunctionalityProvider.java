package com.workert.robotics.content.computers.ioblocks;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.workert.robotics.base.roboscript.RoboScriptClass;
import com.workert.robotics.base.roboscript.RoboScriptField;
import com.workert.robotics.base.roboscript.RoboScriptNativeMethod;
import com.workert.robotics.base.roboscript.RoboScriptObject;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract sealed class IOFunctionalityProvider<T extends SyncedTileEntity> {
	private static final Map<UUID, IOFunctionalityProvider<?>> uuidMap = new HashMap<>();
	private final UUID uuid = UUID.randomUUID();

	private final T blockEntity;

	private final BlockPos targetPos;

	private String signalName = "";


	public IOFunctionalityProvider(T blockEntity, BlockPos targetPos) {
		this.blockEntity = blockEntity;
		this.targetPos = targetPos;
		uuidMap.put(this.uuid, this);
	}

	private static IOFunctionalityProvider<?> getProviderFromUUID(UUID uuid) {
		return uuidMap.get(uuid);
	}

	public static IOFunctionalityProvider<?> getProviderFromClassInstance(RoboScriptObject classInstance) {
		return getProviderFromUUID(UUID.fromString((String) classInstance.fields.get("").value));
	}

	abstract RoboScriptClass getSignalClass();

	public RoboScriptObject getSignalClassInstance() {
		RoboScriptObject object = new RoboScriptObject(this.getSignalClass());
		object.fields.put("", new RoboScriptField(this.uuid.toString(), true));
		return object;
	}

	public T getBlockEntity() {
		return this.blockEntity;
	}

	public BlockPos getTargetPos() {
		return this.targetPos;
	}

	/**
	 * Will either return the {@link ComputerBlockEntity} at this provider's {@code targetPos} or throw.
	 *
	 * @return The {@link ComputerBlockEntity} at {@code targetPos}
	 */
	@Nonnull
	public ComputerBlockEntity getConnectedComputer() {
		return (ComputerBlockEntity) Objects.requireNonNull(
				this.blockEntity.getLevel().getExistingBlockEntity(this.targetPos));
	}

	public UUID getProviderUUID() {
		return this.uuid;
	}

	public String getSignalName() {
		return this.signalName;
	}

	public void setSignalName(String signalName) {
		this.signalName = signalName;
	}


	/**
	 * Used to input information to a computer
	 *
	 * @param <T> the Tile Entity class
	 */
	public static final class Input<T extends SyncedTileEntity> extends IOFunctionalityProvider<T> {
		public Input(T tileEntity, BlockPos targetPos) {
			super(tileEntity, targetPos);
		}

		@Override
		RoboScriptClass getSignalClass() {
			return null;
		}
	}

	/**
	 * Used to output information from a computer
	 *
	 * @param <T> the Tile Entity class
	 */
	public static non-sealed abstract class Output<T extends SyncedTileEntity> extends IOFunctionalityProvider<T> {
		public Output(T tileEntity, BlockPos targetPos) {
			super(tileEntity, targetPos);
		}

		/**
		 * A list with all Methods used for outputting.
		 * <p>
		 * If needed use
		 * <pre>{@code getProviderFromClassInstance((RoboScriptObject) ((RoboScriptNativeMethod) fun).getParentClassInstance())}</pre>
		 * to get the {@link IOFunctionalityProvider} of the method and access {@link IOFunctionalityProvider#getBlockEntity()} or {@link IOFunctionalityProvider#getConnectedComputer()}
		 */
		abstract Map<String, RoboScriptNativeMethod> getNativeMethods();

		@Override
		RoboScriptClass getSignalClass() {
			RoboScriptClass clazz = new RoboScriptClass();
			clazz.functions.putAll(this.getNativeMethods());
			return clazz;
		}

		/* private static final class RoboScriptIOOutputMethod implements RoboScriptCallable {
			final byte argumentCount;
			public Caller function;

			private UUID functionalityProviderUUID = null;

			public RoboScriptIOOutputMethod(byte argumentCount) {
				this.argumentCount = argumentCount;
			}

			@Override
			public void call(VirtualMachine vm, byte argumentCount, boolean asSignal) {
				if (this.argumentCount != argumentCount)
					throw new RuntimeError(
							"Expected " + this.argumentCount + " argument(s) but got " + argumentCount + ".");
				Object returnValue = this.function.call(vm, this);
				if (!asSignal)
					vm.stack[vm.stackSize - 1] = returnValue;
				else
					vm.queueStop();
			}


			@FunctionalInterface
			public interface Caller {
				Object call(VirtualMachine vm, RoboScriptIOOutputMethod function);
			}
		}*/
	}
}
