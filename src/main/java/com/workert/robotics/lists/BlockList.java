package com.workert.robotics.lists;

import com.workert.robotics.Robotics;
import com.workert.robotics.blocks.CodeEditor;
import com.workert.robotics.blocks.SmasherBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockList {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			Robotics.MOD_ID);

	public static final RegistryObject<Block> TIN_ORE = registerBlock("tin_ore", () -> new DropExperienceBlock(
			BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(3.0F, 3.0F)
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore",
			() -> new DropExperienceBlock(
					BlockBehaviour.Properties.copy(TIN_ORE.get()).sound(SoundType.DEEPSLATE).strength(4.5F, 3.0F)));

	public static final RegistryObject<Block> TIN_BLOCK = registerBlock("tin_block", () -> new Block(
			BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(5.0F, 6.0F)
					.requiresCorrectToolForDrops()));
	
	public static final RegistryObject<Block> BRONZE_BLOCK = registerBlock("bronze_block", () -> new Block(
			BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(6.0F, 7.0F)
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> SMASHER_BLOCK = registerBlock("smasher_block", () -> new SmasherBlock(
			BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(9f)
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> CODE_EDITOR = registerBlock("code_editor",
			() -> new CodeEditor(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion()));

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn, ItemList.ROBOTICS_TAB);
		return toReturn;
	}

	private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
		return ItemList.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);

	}
}
