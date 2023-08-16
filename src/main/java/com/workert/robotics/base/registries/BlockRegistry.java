package com.workert.robotics.base.registries;

import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.logistics.block.display.AllDisplayBehaviours;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.workert.robotics.Robotics;
import com.workert.robotics.content.computers.computer.ComputerBlock;
import com.workert.robotics.content.computers.computer.ComputerDisplaySource;
import com.workert.robotics.content.computers.computer.TerminalDisplaySource;
import com.workert.robotics.content.computers.inputs.InputBlockItem;
import com.workert.robotics.content.computers.inputs.redstonedetector.RedstoneDetectorBlock;
import com.workert.robotics.content.computers.inputs.scanner.ScannerBlock;
import com.workert.robotics.content.robotics.codeeditor.CodeEditorBlock;
import com.workert.robotics.unused.smasher.SmasherBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;

import static com.simibubi.create.foundation.data.TagGen.tagBlockAndItem;

public class BlockRegistry {
	public static void register() {
	}

	static {
		Robotics.REGISTRATE.startSection(AllSections.SCHEMATICS);
	}

	public static final BlockEntry<DropExperienceBlock> TIN_ORE = Robotics.REGISTRATE
			.block("tin_ore", DropExperienceBlock::new)
			.lang("Tin ore")
			.properties(properties -> properties
					.of(Material.STONE)
					.sound(SoundType.STONE)
					.strength(3.0F, 3.0F)
					.requiresCorrectToolForDrops())
			.transform(TagGen.pickaxeOnly())
			.loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
					RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(ItemRegistry.RAW_TIN.get())
							.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.tag(Tags.Blocks.ORES)
			.transform(tagBlockAndItem("ores/tin", "ores_in_ground/stone"))
			.tag(Tags.Items.ORES)
			.build()
			.register();

	public static final BlockEntry<DropExperienceBlock> DEEPSLATE_TIN_ORE = Robotics.REGISTRATE
			.block("deepslate_tin_ore", DropExperienceBlock::new)
			.lang("Deepslate tin ore")
			.initialProperties(BlockRegistry.TIN_ORE)
			.properties(properties -> properties
					.sound(SoundType.DEEPSLATE)
					.strength(4.5F, 3.0F)
					.requiresCorrectToolForDrops())
			.transform(TagGen.pickaxeOnly())
			.loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
					RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(ItemRegistry.RAW_TIN.get())
							.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
			.tag(BlockTags.NEEDS_IRON_TOOL).tag(Tags.Blocks.ORES)
			.transform(tagBlockAndItem("ores/tin", "ores_in_ground/deepslate"))
			.tag(Tags.Items.ORES)
			.build()
			.register();

	public static final BlockEntry<Block> TIN_BLOCK = Robotics.REGISTRATE
			.block("tin_block", Block::new)
			.lang("Tin block")
			.properties(properties -> properties
					.of(Material.METAL)
					.sound(SoundType.METAL)
					.strength(5.0F, 6.0F)
					.requiresCorrectToolForDrops())
			.transform(TagGen.pickaxeOnly())
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.tag(Tags.Blocks.STORAGE_BLOCKS)
			.tag(BlockTags.BEACON_BASE_BLOCKS)
			.transform(tagBlockAndItem("storage_blocks/tin"))
			.tag(Tags.Items.STORAGE_BLOCKS)
			.build()
			.register();

	public static final BlockEntry<Block> BRONZE_BLOCK = Robotics.REGISTRATE
			.block("bronze_block", Block::new)
			.lang("Bronze block")
			.properties(properties -> properties
					.of(Material.METAL)
					.sound(SoundType.METAL)
					.strength(6.0F, 7.0F)
					.requiresCorrectToolForDrops())
			.transform(TagGen.pickaxeOnly())
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.tag(Tags.Blocks.STORAGE_BLOCKS)
			.tag(BlockTags.BEACON_BASE_BLOCKS)
			.transform(tagBlockAndItem("storage_blocks/bronze"))
			.tag(Tags.Items.STORAGE_BLOCKS)
			.build()
			.register();

	public static final BlockEntry<SmasherBlock> SMASHER = Robotics.REGISTRATE
			.block("smasher", SmasherBlock::new)
			.lang("Smasher")
			.blockstate((dataGenContext, provider) -> provider.horizontalBlock(dataGenContext.get(),
					provider.models().getExistingFile(provider.modLoc("block/smasher"))))
			.properties(properties -> properties
					.of(Material.METAL)
					.sound(SoundType.METAL)
					.strength(9f)
					.requiresCorrectToolForDrops())
			.simpleItem()
			.register();

	public static final BlockEntry<CodeEditorBlock> CODE_EDITOR = Robotics.REGISTRATE.block("code_editor",
					CodeEditorBlock::new)
			.lang("Code Editor")
			.blockstate((dataGenContext, provider) -> provider.horizontalBlock(dataGenContext.get(),
					provider.models().getExistingFile(provider.modLoc("block/code_editor"))))
			.properties(properties -> properties
					.of(Material.WOOD)
					.sound(SoundType.WOOD)
					.noOcclusion())
			.simpleItem()
			.register();

	public static final BlockEntry<ComputerBlock> COMPUTER = Robotics.REGISTRATE
			.block("computer", ComputerBlock::new)
			.lang("Computer")
			.blockstate((dataGenContext, provider) -> provider.simpleBlock(dataGenContext.get(),
					provider.models().getExistingFile(provider.modLoc("block/computer"))))
			.initialProperties(() -> Blocks.STONE)
			.transform(TagGen.pickaxeOnly())
			.transform(BlockStressDefaults.setImpact(12))
			.onRegister(AllDisplayBehaviours.assignDataBehaviour(
					new TerminalDisplaySource(), "terminal"))
			.onRegister(AllDisplayBehaviours.assignDataBehaviour(new ComputerDisplaySource(), "computer"))
			.simpleItem()
			.properties(BlockBehaviour.Properties::noOcclusion).register();

	public static final BlockEntry<RedstoneDetectorBlock> REDSTONE_DETECTOR = Robotics.REGISTRATE
			.block("redstone_detector", RedstoneDetectorBlock::new)
			.lang("Redstone Detector")
			.blockstate((dataGenContext, provider) -> provider.getVariantBuilder(dataGenContext.get()).partialState()
					.with(RedstoneDetectorBlock.LIT, true).addModels(new ConfiguredModel(
							provider.models().getExistingFile(provider.modLoc("block/redstone_detector_on"))))
					.partialState().with(RedstoneDetectorBlock.LIT, false).addModels(new ConfiguredModel(
							provider.models().getExistingFile(provider.modLoc("block/redstone_detector")))))
			.initialProperties(() -> Blocks.STONE)
			.transform(TagGen.pickaxeOnly())
			.item(InputBlockItem::new)
			.build()
			.register();
	public static final BlockEntry<ScannerBlock> SCANNER = Robotics.REGISTRATE
			.block("scanner", ScannerBlock::new)
			.lang("Scanner")
			.blockstate((dataGenContext, provider) -> provider.simpleBlock(dataGenContext.get(),
					provider.models().getExistingFile(provider.modLoc("block/scanner"))))
			.initialProperties(() -> Blocks.STONE)
			.transform(TagGen.pickaxeOnly())
			.properties(BlockBehaviour.Properties::noOcclusion)
			.item(InputBlockItem::new)
			.build()
			.register();
}
