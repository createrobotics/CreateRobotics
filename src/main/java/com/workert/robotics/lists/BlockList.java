package com.workert.robotics.lists;

import java.util.function.Supplier;

import com.workert.robotics.Robotics;
import com.workert.robotics.blocks.CodeEditor;
import com.workert.robotics.blocks.DroneAssembler;
import com.workert.robotics.blocks.SmasherBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockList {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			Robotics.MOD_ID);

	public static final RegistryObject<Block> TIN_ORE = registerBlock("tin_ore",
			() -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(9f)
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> TIN_BLOCK = registerBlock("tin_block",
			() -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(9f)
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> BRONZE_BLOCK = registerBlock("bronze_block",
			() -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(9f)
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore",
			() -> new OreBlock(BlockBehaviour.Properties.copy(TIN_ORE.get()).sound(SoundType.DEEPSLATE)));

	public static final RegistryObject<Block> SMASHER_BLOCK = registerBlock("smasher_block",
			() -> new SmasherBlock(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(9f)
					.requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> DRONE_ASSEMBLER = registerBlock("drone_assembler",
			() -> new DroneAssembler(Properties.of(Material.METAL).sound(SoundType.METAL)));

	public static final RegistryObject<Block> CODE_EDITOR = registerBlock("code_editor",
			() -> new CodeEditor(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion()));

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn, ItemList.ROBOTICS_TAB);
		return toReturn;
	}

	private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
			CreativeModeTab tab) {

		return ItemList.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);

	}
}
