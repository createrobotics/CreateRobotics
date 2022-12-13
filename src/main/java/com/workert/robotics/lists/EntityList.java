package com.workert.robotics.lists;

import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionType;
import com.workert.robotics.Robotics;
import com.workert.robotics.contraptions.DroneContraption;
import com.workert.robotics.entities.Clockcopter;
import com.workert.robotics.entities.DroneContraptionEntity;

import com.workert.robotics.entities.Miner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityList {
	public static ContraptionType DRONE_CONTRAPTION = ContraptionType.register("drone", DroneContraption::new);

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			Robotics.MOD_ID);

	public static final RegistryObject<EntityType<DroneContraptionEntity>> DRONE = ENTITY_TYPES.register("drone",
			() -> EntityType.Builder.<DroneContraptionEntity>of(DroneContraptionEntity::new, MobCategory.MISC)
					.build(new ResourceLocation(Robotics.MOD_ID, "drone").toString()));

	public static final RegistryObject<EntityType<Clockcopter>> CLOCKCOPTER = ENTITY_TYPES.register("clockcopter",
			() -> EntityType.Builder.<Clockcopter>of(Clockcopter::new, MobCategory.MISC).sized(0.8f, 0.8f)
					.build(new ResourceLocation(Robotics.MOD_ID, "clockcopter").toString()));

	public static final RegistryObject<EntityType<Miner>> MINER = ENTITY_TYPES.register("miner",
			() -> EntityType.Builder.<Miner>of(Miner::new, MobCategory.MISC).sized(0.8f, 0.8f)
					.build(new ResourceLocation(Robotics.MOD_ID, "miner").toString()));



	public static void addEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(EntityList.CLOCKCOPTER.get(), Clockcopter.createAttributes());
	event.put(EntityList.MINER.get(), Miner.createAttributes());

	}

}
