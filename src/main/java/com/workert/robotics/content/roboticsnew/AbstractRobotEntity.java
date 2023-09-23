package com.workert.robotics.content.roboticsnew;
import com.simibubi.create.content.curiosities.armor.BackTankUtil;
import com.workert.robotics.base.roboscript.RoboScript;
import com.workert.robotics.base.roboscript.ingame.LineLimitedString;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public abstract class AbstractRobotEntity extends Mob {
	private static final int maxAir = BackTankUtil.maxAirWithoutEnchants() * 10;
	private int air;

	private final RoboScript roboScript = this.initializeRoboScript();
	private String script = "";
	private LineLimitedString terminal = new LineLimitedString(ComputerBlockEntity.TERMINAL_LINE_LIMIT);


	protected AbstractRobotEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		this.air = maxAir;
	}


	private RoboScript initializeRoboScript() {
		return new RoboScript() {
			@Override
			protected void handlePrintMessage(String message) {

			}

			@Override
			protected void handleErrorMessage(String error) {

			}
		};
	}

	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		if (pSource == DamageSource.OUT_OF_WORLD) return super.hurt(pSource, pAmount);
		this.consumeAir((int) pAmount * 2);
		return false;
	}

	public void consumeAir(int amount) {
		if ((this.air = Math.max(0, this.air - amount)) == 0)
			this.onAirFullyDepleted();
	}


	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		pCompound.putInt("Air", this.air);

		super.addAdditionalSaveData(pCompound);
	}

	abstract void onAirFullyDepleted();
}
