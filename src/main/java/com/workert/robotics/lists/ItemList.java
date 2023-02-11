package com.workert.robotics.lists;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.AllSections;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.workert.robotics.Robotics;
import com.workert.robotics.items.BaseRobotItem;
import com.workert.robotics.items.ExtendOBootsItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.Tags;

public class ItemList {
	public static void register() {
	}

	static {
		Robotics.REGISTRATE.creativeModeTab(() -> ItemList.ROBOTICS_TAB);
		Robotics.REGISTRATE.startSection(AllSections.MATERIALS);
	}

	public static final CreativeModeTab ROBOTICS_TAB = new CreativeModeTab(Robotics.MOD_ID) {
		@Override
		public ItemStack makeIcon() {
			return ItemList.BRONZE_INGOT.get().getDefaultInstance();
		}
	};

	public static final ItemEntry<Item> TIN_INGOT = Robotics.REGISTRATE.item("tin_ingot", Item::new)
			.tag(Tags.Items.INGOTS).tag(AllTags.forgeItemTag("ingots/tin")).register();
	public static final ItemEntry<Item> TIN_NUGGET = Robotics.REGISTRATE.item("tin_nugget", Item::new)
			.tag(Tags.Items.NUGGETS).tag(AllTags.forgeItemTag("nuggets/tin")).register();
	public static final ItemEntry<Item> RAW_TIN = Robotics.REGISTRATE.item("raw_tin", Item::new)
			.tag(Tags.Items.RAW_MATERIALS).register();

	public static final ItemEntry<Item> BRONZE_INGOT = Robotics.REGISTRATE.item("bronze_ingot", Item::new)
			.tag(Tags.Items.INGOTS).tag(AllTags.forgeItemTag("ingots/bronze")).register();
	public static final ItemEntry<Item> BRONZE_NUGGET = Robotics.REGISTRATE.item("bronze_nugget", Item::new)
			.tag(Tags.Items.NUGGETS).tag(AllTags.forgeItemTag("nuggets/bronze")).register();

	public static final ItemEntry<Item> PROGRAM = Robotics.REGISTRATE.item("program",
			Item::new).properties(properties -> properties.stacksTo(1).rarity(Rarity.UNCOMMON)).register();

	public static final ItemEntry<BaseRobotItem> CLOCKCOPTER = Robotics.REGISTRATE.item("clockcopter",
			BaseRobotItem::new).onRegister(item -> item.setEntity(() -> EntityList.CLOCKCOPTER.get())).register();


	/*public static final ItemEntry<BaseRobotItem> MINER = Robotics.REGISTRATE.item("miner",
			BaseRobotItem::new).onRegister(item -> item.setEntity(() -> EntityList.MINER.get())).register();*/

	public static final ItemEntry<BaseRobotItem> CODE_DRONE = Robotics.REGISTRATE.item("code_drone",
			BaseRobotItem::new).onRegister(item -> item.setEntity(() -> EntityList.CODE_DRONE.get())).register();

	public static final ItemEntry<ExtendOBootsItem> EXTEND_O_BOOTS = Robotics.REGISTRATE.item("extend_o_boots",
			ExtendOBootsItem::new).register();
}
