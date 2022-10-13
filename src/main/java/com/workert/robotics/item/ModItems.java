package com.workert.robotics.item;

import java.util.Optional;

import com.workert.robotics.Robotics;
import com.workert.robotics.entities.ModEntities;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Robotics.MOD_ID);

	public static final RegistryObject<Item> TIN_INGOT = registerItem("tin_ingot", Optional.empty());
	public static final RegistryObject<Item> TIN_NUGGET = registerItem("tin_nugget", Optional.empty());
	public static final RegistryObject<Item> RAW_TIN = registerItem("raw_tin", Optional.empty());

	public static final RegistryObject<Item> BRONZE_INGOT = registerItem("bronze_ingot", Optional.empty());
	public static final RegistryObject<Item> BRONZE_NUGGET = registerItem("bronze_nugget", Optional.empty());

	public static final RegistryObject<Item> CLOCKCOPTER = ITEMS.register("clockcopter",
			() -> new BaseRobotItem(new Item.Properties().tab(ModCreativeModeTab.ROBOTICS_TAB),
					ModEntities.CLOCKCOPTER.get()));

	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}

	public static RegistryObject<Item> registerItem(String id, Optional<Item.Properties> itemProperties) {
		RegistryObject<Item> registyObject = ITEMS.register(id,
				() -> new Item(itemProperties.orElse(new Item.Properties()).tab(ModCreativeModeTab.ROBOTICS_TAB)));
		return registyObject;
	}
}
