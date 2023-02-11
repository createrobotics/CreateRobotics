package com.workert.robotics.lists;

import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.workert.robotics.Robotics;
import com.workert.robotics.blocks.CodeEditor;
import com.workert.robotics.blocks.SmasherBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraftforge.common.Tags;

import static com.simibubi.create.foundation.data.TagGen.tagBlockAndItem;

public class BlockList {
	public static void register() {
	}

	static {
		Robotics.REGISTRATE.creativeModeTab(() -> ItemList.ROBOTICS_TAB);
		Robotics.REGISTRATE.startSection(AllSections.SCHEMATICS);
	}

	public static final BlockEntry<DropExperienceBlock> TIN_ORE = Robotics.REGISTRATE.block(
					"tin_ore", DropExperienceBlock::new)
			.properties(properties -> properties.of(Material.STONE).sound(SoundType.STONE).strength(3.0F, 3.0F)
					.requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly()).loot((lt, b) -> lt.add(b,
					RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
							RegistrateBlockLootTables.applyExplosionDecay(b,
									LootItem.lootTableItem(ItemList.RAW_TIN.get())
											.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.tag(Tags.Blocks.ORES)
			.transform(tagBlockAndItem("ores/tin", "ores_in_ground/stone"))
			.tag(Tags.Items.ORES).build().register();

	public static final BlockEntry<DropExperienceBlock> DEEPSLATE_TIN_ORE = Robotics.REGISTRATE.block(
					"deepslate_tin_ore", DropExperienceBlock::new)
			.initialProperties(() -> BlockList.TIN_ORE.get())
			.properties(properties -> properties.sound(SoundType.DEEPSLATE).strength(4.5F, 3.0F)
					.requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly()).loot((lt, b) -> lt.add(b,
					RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
							RegistrateBlockLootTables.applyExplosionDecay(b,
									LootItem.lootTableItem(ItemList.RAW_TIN.get())
											.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.tag(Tags.Blocks.ORES)
			.transform(tagBlockAndItem("ores/tin", "ores_in_ground/deepslate"))
			.tag(Tags.Items.ORES).build().register();

	public static final BlockEntry<Block> TIN_BLOCK = Robotics.REGISTRATE.block("tin_block", Block::new).properties(
					properties -> properties.of(Material.METAL).sound(SoundType.METAL).strength(5.0F, 6.0F)
							.requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly()).tag(BlockTags.NEEDS_IRON_TOOL)
			.tag(Tags.Blocks.STORAGE_BLOCKS).tag(BlockTags.BEACON_BASE_BLOCKS)
			.transform(tagBlockAndItem("storage_blocks/tin")).tag(Tags.Items.STORAGE_BLOCKS).build().register();

	public static final BlockEntry<Block> BRONZE_BLOCK = Robotics.REGISTRATE.block("bronze_block", Block::new)
			.properties(
					properties -> properties.of(Material.METAL).sound(SoundType.METAL).strength(6.0F, 7.0F)
							.requiresCorrectToolForDrops()).transform(TagGen.pickaxeOnly())
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.tag(Tags.Blocks.STORAGE_BLOCKS).tag(BlockTags.BEACON_BASE_BLOCKS)
			.transform(tagBlockAndItem("storage_blocks/bronze")).tag(Tags.Items.STORAGE_BLOCKS).build().register();

	public static final BlockEntry<SmasherBlock> SMASHER = Robotics.REGISTRATE.block(
			"smasher", SmasherBlock::new).properties(properties ->
			properties.of(Material.METAL).sound(SoundType.METAL).strength(9f)
					.requiresCorrectToolForDrops()).simpleItem().register();

	public static final BlockEntry<CodeEditor> CODE_EDITOR = Robotics.REGISTRATE.block(
			"code_editor", CodeEditor::new).properties(properties ->
			properties.of(Material.WOOD).sound(SoundType.WOOD).noOcclusion()).simpleItem().register();
}
