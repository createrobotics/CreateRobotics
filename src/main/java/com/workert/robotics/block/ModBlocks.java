package com.workert.robotics.block;

import com.workert.robotics.Robotics;
import com.workert.robotics.block.custom.SmasherBlock;
import com.workert.robotics.item.ModCreativeModeTab;
import com.workert.robotics.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			Robotics.MOD_ID);

	public static final RegistryObject<Block> TIN_ORE = registerBlock("tin_ore",
			() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(9f).requiresCorrectToolForDrops()),
			ModCreativeModeTab.ROBOTICS_TAB);

	public static final RegistryObject<Block> TIN_BLOCK = registerBlock("tin_block",
			() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(9f).requiresCorrectToolForDrops()),
			ModCreativeModeTab.ROBOTICS_TAB);

	public static final RegistryObject<Block> BRONZE_BLOCK = registerBlock("bronze_block",
			() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(9f).requiresCorrectToolForDrops()),
			ModCreativeModeTab.ROBOTICS_TAB);

	public static final RegistryObject<Block> DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore",
			() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(9f).requiresCorrectToolForDrops()),
			ModCreativeModeTab.ROBOTICS_TAB);

	public static final RegistryObject<Block> SMASHER_BLOCK = registerBlock("smasher_block",
			() -> new SmasherBlock(
					BlockBehaviour.Properties.of(Material.METAL).strength(9f).requiresCorrectToolForDrops()),
			ModCreativeModeTab.ROBOTICS_TAB);

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block,
			CreativeModeTab tab) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn, tab);
		return toReturn;
	}

	private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
			CreativeModeTab tab) {

		return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);

	}
}
