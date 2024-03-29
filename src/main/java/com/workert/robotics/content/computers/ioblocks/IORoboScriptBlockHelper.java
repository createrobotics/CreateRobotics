package com.workert.robotics.content.computers.ioblocks;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.workert.robotics.base.roboscript.*;

public class IORoboScriptBlockHelper {

	public static IORoboScriptBlockClassBuilder createClass() {
		return new IORoboScriptBlockClassBuilder();
	}

	public static IORoboScriptBlockInstanceBuilder createObject(RoboScriptClass clazz, SyncedBlockEntity be) {
		return new IORoboScriptBlockInstanceBuilder(clazz, be);
	}

	public static SyncedBlockEntity getBlockEntityFromMethod(RoboScriptNativeFunction f) {
		return (SyncedBlockEntity) ((RoboScriptNativeMethod<RoboScriptObject>) f).instance.fields.get("").value;
	}


	public static class IORoboScriptBlockClassBuilder {
		private final RoboScriptClass clazz = new RoboScriptClass();

		public IORoboScriptBlockClassBuilder addMethod(String name, int argumentCount, RoboScriptNativeFunction.Caller function) {
			RoboScriptNativeMethod<RoboScriptObject> method = new RoboScriptNativeMethod<>((byte) argumentCount);
			method.function = function;
			this.clazz.functions.put(name, method);
			return this;
		}

		public RoboScriptClass build() {
			return this.clazz;
		}
	}

	public static class IORoboScriptBlockInstanceBuilder {
		private final RoboScriptObject object;
		private final SyncedBlockEntity be;

		IORoboScriptBlockInstanceBuilder(RoboScriptClass clazz, SyncedBlockEntity be) {
			this.object = new RoboScriptObject(clazz);
			this.be = be;
		}

		public IORoboScriptBlockInstanceBuilder addField(String name, Object value, boolean isFinal) {
			this.object.fields.put(name, new RoboScriptField(value, isFinal));
			return this;
		}

		public RoboScriptObject build() {
			this.object.fields.put("", new RoboScriptField(this.be, true));
			return this.object;
		}
	}


}
