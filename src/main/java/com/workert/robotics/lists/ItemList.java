package com.workert.robotics.lists;

import java.util.Optional;

import com.workert.robotics.Robotics;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemList {
	public static final CreativeModeTab ROBOTICS_TAB = new CreativeModeTab("robotics") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemList.BRONZE_INGOT.get());
		}
	};

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Robotics.MOD_ID);

	public static final RegistryObject<Item> TIN_INGOT = registerBasicItem("tin_ingot", Optional.empty());
	public static final RegistryObject<Item> TIN_NUGGET = registerBasicItem("tin_nugget", Optional.empty());
	public static final RegistryObject<Item> RAW_TIN = registerBasicItem("raw_tin", Optional.empty());

	public static final RegistryObject<Item> BRONZE_INGOT = registerBasicItem("bronze_ingot", Optional.empty());
	public static final RegistryObject<Item> BRONZE_NUGGET = registerBasicItem("bronze_nugget", Optional.empty());

	public static final RegistryObject<Item> CLOCKCOPTER = ITEMS.register("clockcopter",
			() -> new ForgeSpawnEggItem(() -> EntityList.CLOCKCOPTER.get(), 0xb0b0b0, 0xb3793b,
					new Item.Properties().tab(ROBOTICS_TAB)));

	public static final RegistryObject<Item> MINER = ITEMS.register("miner",
			() -> new ForgeSpawnEggItem(() -> EntityList.MINER.get(), 0xb0b0b0, 0xb3793b,
					new Item.Properties().tab(ROBOTICS_TAB)));

	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}

	public static RegistryObject<Item> registerBasicItem(String id, Optional<Item.Properties> itemProperties) {
		RegistryObject<Item> registyObject = ITEMS.register(id,
				() -> new Item(itemProperties.orElse(new Item.Properties()).tab(ROBOTICS_TAB)));
		return registyObject;
	}
}
