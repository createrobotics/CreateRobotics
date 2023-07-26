package com.workert.robotics.base.registries;

import com.tterrag.registrate.util.entry.EntityEntry;
import com.workert.robotics.Robotics;
import com.workert.robotics.content.robotics.clockcopter.Clockcopter;
import com.workert.robotics.content.robotics.clockcopter.ClockcopterRenderer;
import com.workert.robotics.content.robotics.codedrone.CodeDrone;
import com.workert.robotics.content.robotics.codedrone.CodeDroneRenderer;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolbox;
import com.workert.robotics.content.robotics.flyingtoolbox.FlyingToolboxRenderer;
import com.workert.robotics.content.utility.extendoboots.ExtendOBoots;
import com.workert.robotics.content.utility.extendoboots.ExtendOBootsRenderer;
import com.workert.robotics.unused.miner.Miner;
import com.workert.robotics.unused.miner.MinerRenderer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EntityRegistry {
	public static void register() {
	}

	public static final EntityEntry<Clockcopter> CLOCKCOPTER = Robotics.REGISTRATE
			.entity("clockcopter", Clockcopter::new, MobCategory.MISC)
			.lang("Clockcopter")
			.properties(properties -> properties.sized(0.9f, 1f))
			.attributes(() -> Mob.createMobAttributes()
					.add(Attributes.MOVEMENT_SPEED, 0.2F)
					.add(Attributes.MAX_HEALTH, 1.0D)
					.add(Attributes.FLYING_SPEED, 0.8F))
			.renderer(() -> ClockcopterRenderer::new)
			.register();

	public static final EntityEntry<Miner> MINER = Robotics.REGISTRATE
			.entity("miner", Miner::new, MobCategory.MISC)
			.lang("Miner")
			.properties(properties -> properties.sized(1.2f, 1.8f))
			.attributes(() -> Mob.createMobAttributes()
					.add(Attributes.MOVEMENT_SPEED, 0.2F)
					.add(Attributes.MAX_HEALTH, 1.0D))
			.renderer(() -> MinerRenderer::new)
			.register();

	public static final EntityEntry<CodeDrone> CODE_DRONE = Robotics.REGISTRATE
			.entity("code_drone", CodeDrone::new, MobCategory.MISC)
			.lang("Juan")
			.properties(properties -> properties.sized(1f, 0.4f))
			.attributes(() -> Mob.createMobAttributes()
					.add(Attributes.MOVEMENT_SPEED, 0.2F)
					.add(Attributes.MAX_HEALTH, 1.0D)
					.add(Attributes.FLYING_SPEED, 0.8F))
			.renderer(() -> CodeDroneRenderer::new)
			.register();

	public static final EntityEntry<ExtendOBoots> EXTEND_O_BOOTS = Robotics.REGISTRATE
			.entity("extend_o_boots", ExtendOBoots::new, MobCategory.MISC)
			.lang("Extend-O-Boots")
			.properties(properties -> properties.sized(1f, 0f))
			.attributes(() -> Mob.createMobAttributes()
					.add(Attributes.MOVEMENT_SPEED, 0F)
					.add(Attributes.MAX_HEALTH, 1.0D))
			.renderer(() -> ExtendOBootsRenderer::new)
			.register();

	public static final EntityEntry<FlyingToolbox> FLYING_TOOLBOX = Robotics.REGISTRATE
			.entity("flying_toolbox", FlyingToolbox::new, MobCategory.MISC)
			.lang("Flying Toolbox")
			.properties(properties -> properties.sized(0.9f, 1f))
			.attributes(() -> Mob.createMobAttributes()
					.add(Attributes.MOVEMENT_SPEED, 0.2F)
					.add(Attributes.MAX_HEALTH, 1.0D)
					.add(Attributes.FLYING_SPEED, 0.8F))
			.renderer(() -> FlyingToolboxRenderer::new)
			.register();
}
