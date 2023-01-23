package com.workert.robotics.lists;

import com.workert.robotics.Robotics;
import com.workert.robotics.entities.Clockcopter;
import com.workert.robotics.entities.CodeDrone;
import com.workert.robotics.entities.ExtendOBoots;
import com.workert.robotics.entities.Miner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityList {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
			ForgeRegistries.ENTITY_TYPES, Robotics.MOD_ID);

	public static final RegistryObject<EntityType<Clockcopter>> CLOCKCOPTER = ENTITY_TYPES.register("clockcopter",
			() -> EntityType.Builder.of(Clockcopter::new, MobCategory.MISC).sized(0.9f, 1f)
					.build(new ResourceLocation(Robotics.MOD_ID, "clockcopter").toString()));

	public static final RegistryObject<EntityType<Miner>> MINER = ENTITY_TYPES.register("miner",
			() -> EntityType.Builder.of(Miner::new, MobCategory.MISC).sized(1.2f, 1.8f)
					.build(new ResourceLocation(Robotics.MOD_ID, "miner").toString()));

	public static final RegistryObject<EntityType<CodeDrone>> CODE_DRONE = ENTITY_TYPES.register("code_drone",
			() -> EntityType.Builder.of(CodeDrone::new, MobCategory.MISC).sized(1f, 0.4f)
					.build(new ResourceLocation(Robotics.MOD_ID, "code_drone").toString()));

	public static final RegistryObject<EntityType<ExtendOBoots>> EXTEND_O_BOOTS = ENTITY_TYPES.register(
			"extend_o_boots",
			() -> EntityType.Builder.of(ExtendOBoots::new, MobCategory.MISC).sized(0.2f, 0.2f).noSummon()
					.clientTrackingRange(64).build(new ResourceLocation(Robotics.MOD_ID, "extend_o_boots").toString()));

	public static void addEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(EntityList.CLOCKCOPTER.get(), Clockcopter.createAttributes());
		event.put(EntityList.MINER.get(), Miner.createAttributes());
		event.put(EntityList.CODE_DRONE.get(), CodeDrone.createAttributes());
		event.put(EntityList.EXTEND_O_BOOTS.get(), ExtendOBoots.createAttributes());
	}

}
