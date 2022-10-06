package com.workert.robotics.entities;

import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionType;
import com.workert.robotics.Robotics;
import com.workert.robotics.contraptions.DroneContraption;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
	public static ContraptionType DRONE_CONTRAPTION = ContraptionType.register("drone", DroneContraption::new);

	public static DeferredRegister<EntityType<?>> EntityTypes = DeferredRegister.create(ForgeRegistries.ENTITIES,
			Robotics.MOD_ID);

	public static final RegistryObject<EntityType<DroneContraptionEntity>> DRONE = EntityTypes.register("drone",
			() -> EntityType.Builder.<DroneContraptionEntity>of(DroneContraptionEntity::new, MobCategory.MISC)
					.build(new ResourceLocation(Robotics.MOD_ID, "drone").toString()));

	public static final RegistryObject<EntityType<Clocktoper>> CLOCKTOPER = EntityTypes.register("clocktoper",
			() -> EntityType.Builder.<Clocktoper>of(Clocktoper::new, MobCategory.MISC)
					.build(new ResourceLocation(Robotics.MOD_ID, "clocktoper").toString()));

	public static void addEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(ModEntities.CLOCKTOPER.get(), Clocktoper.createAttributes());
	}
}
