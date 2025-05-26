package com.workert.robotics.base.registries;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.ComputerDisplaySource;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.workert.robotics.Robotics;
import com.workert.robotics.content.computers.computer.ComputerBlock;
import com.workert.robotics.content.computers.computer.TerminalDisplaySource;
import com.workert.robotics.content.computers.ioblocks.IOBlockItem;
import com.workert.robotics.content.computers.ioblocks.redstonedetector.RedstoneDetectorBlock;
import com.workert.robotics.content.computers.ioblocks.redstoneemitter.RedstoneEmitterBlock;
import com.workert.robotics.content.computers.ioblocks.redstonepulser.RedstonePulserBlock;
import com.workert.robotics.content.computers.ioblocks.scanner.ScannerBlock;
import com.workert.robotics.content.robotics.codeeditor.CodeEditorBlock;
import com.workert.robotics.content.robotics.drone_delivery.drone_port.DronePortBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;

import static com.simibubi.create.foundation.data.TagGen.tagBlockAndItem;

public class BlockRegistry {

	public static void register() {
	}

	static {
		Robotics.REGISTRATE.setCreativeTab(CreativeModeTabRegistry.BASE_CREATIVE_TAB);
	}

	public static final BlockEntry<DropExperienceBlock> TIN_ORE = Robotics.REGISTRATE
			.block("tin_ore", DropExperienceBlock::new)
			.lang("Tin Ore")
			.properties(properties -> properties.of()
					.sound(SoundType.STONE)
					.strength(3.0F, 3.0F)
					.requiresCorrectToolForDrops())
			.transform(TagGen.pickaxeOnly())
			.loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
					LootItem.lootTableItem(ItemRegistry.RAW_TIN.get())
							.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))))
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.tag(Tags.Blocks.ORES)
			.transform(tagBlockAndItem("ores/tin", "ores_in_ground/stone"))
			.tag(Tags.Items.ORES)
			.build()
			.register();

	public static final BlockEntry<DropExperienceBlock> DEEPSLATE_TIN_ORE = Robotics.REGISTRATE
			.block("deepslate_tin_ore", DropExperienceBlock::new)
			.lang("Deepslate Tin Ore")
			.initialProperties(BlockRegistry.TIN_ORE)
			.properties(properties -> properties
					.sound(SoundType.DEEPSLATE)
					.strength(4.5F, 3.0F)
					.requiresCorrectToolForDrops())
			.transform(TagGen.pickaxeOnly())
			.loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
					LootItem.lootTableItem(ItemRegistry.RAW_TIN.get())
							.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))))
			.tag(BlockTags.NEEDS_IRON_TOOL).tag(Tags.Blocks.ORES)
			.transform(tagBlockAndItem("ores/tin", "ores_in_ground/deepslate"))
			.tag(Tags.Items.ORES)
			.build()
			.register();

	public static final BlockEntry<Block> TIN_BLOCK = Robotics.REGISTRATE
			.block("tin_block", Block::new)
			.lang("Block of Tin")
			.properties(properties -> properties.of()
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
			.lang("Block of Bronze")
			.properties(properties -> properties.of()
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



	public static final BlockEntry<CodeEditorBlock> CODE_EDITOR = Robotics.REGISTRATE.block("code_editor",
					CodeEditorBlock::new)
			.lang("Code Editor")
			.blockstate((dataGenContext, provider) -> provider.horizontalBlock(dataGenContext.get(),
					provider.models().getExistingFile(provider.modLoc("block/code_editor"))))
			.properties(properties -> properties.of()
					.sound(SoundType.WOOD)
					.noOcclusion())
			.item()
			.onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.robotics.code_editor"))
			.build()
			.register();

	public static final BlockEntry<ComputerBlock> COMPUTER = Robotics.REGISTRATE
			.block("computer", ComputerBlock::new)
			.lang("Computer")
			.blockstate((dataGenContext, provider) -> provider.simpleBlock(dataGenContext.get(),
					provider.models().getExistingFile(provider.modLoc("block/computer"))))
			.initialProperties(() -> Blocks.STONE)
			.transform(TagGen.pickaxeOnly())
			//.transform(CStress.setImpact(12))
			.onRegister((block) -> DisplaySource.BY_BLOCK.add(block, new TerminalDisplaySource()))
			.onRegister((block) -> DisplaySource.BY_BLOCK.add(block, new ComputerDisplaySource()))
			.simpleItem()
			.properties(BlockBehaviour.Properties::noOcclusion).register();

	public static final BlockEntry<DronePortBlock> DRONE_PORT = Robotics.REGISTRATE
			.block("drone_port", DronePortBlock::new)
			.lang("Drone Port")
			.initialProperties(() -> Blocks.STONE)
			.transform(TagGen.pickaxeOnly())
			.simpleItem()
			.properties(BlockBehaviour.Properties::noOcclusion).register();

	public static final BlockEntry<RedstoneDetectorBlock> REDSTONE_DETECTOR = Robotics.REGISTRATE
			.block("redstone_detector", RedstoneDetectorBlock::new)
			.lang("Redstone Detector")
			.blockstate((dataGenContext, provider) -> provider.getVariantBuilder(dataGenContext.get())
					.partialState().with(RedstoneDetectorBlock.LIT, true).addModels(new ConfiguredModel(
							provider.models()
									.cubeAll("redstone_detector_on", provider.modLoc("block/redstone_detector_on"))))
					.partialState().with(RedstoneDetectorBlock.LIT, false).addModels(new ConfiguredModel(
							provider.models()
									.cubeAll("redstone_detector", provider.modLoc("block/redstone_detector"))))
			)
			.initialProperties(() -> Blocks.STONE)
			.transform(TagGen.pickaxeOnly())
			.item(IOBlockItem::new)
			.build()
			.register();

	public static final BlockEntry<RedstoneEmitterBlock> REDSTONE_EMITTER = Robotics.REGISTRATE
			.block("redstone_emitter", RedstoneEmitterBlock::new)
			.lang("Redstone Emitter")
			.blockstate((dataGenContext, provider) -> provider.getVariantBuilder(dataGenContext.get()).partialState()
					.with(RedstoneDetectorBlock.LIT, true).addModels(new ConfiguredModel(
							provider.models().cubeBottomTop("redstone_emitter",
									provider.modLoc("block/redstone_emitter_side"),
									provider.modLoc("block/redstone_emitter_bottom"),
									provider.modLoc("block/redstone_emitter_top"))))
					.partialState().with(RedstoneDetectorBlock.LIT, false).addModels(new ConfiguredModel(
							provider.models().getExistingFile(provider.modLoc("block/redstone_emitter")))))
			.initialProperties(() -> Blocks.STONE)
			.transform(TagGen.pickaxeOnly())
			.item(IOBlockItem::new)
			.build()
			.register();
	public static final BlockEntry<RedstonePulserBlock> REDSTONE_PULSER = Robotics.REGISTRATE
			.block("redstone_pulser", RedstonePulserBlock::new)
			.lang("Redstone Pulser")
			.blockstate((dataGenContext, provider) -> provider.getVariantBuilder(dataGenContext.get())
					.partialState().with(RedstonePulserBlock.LIT, true).addModels(new ConfiguredModel(
							provider.models()
									.cubeAll("redstone_detector_on", provider.modLoc("block/redstone_detector_on"))))
					.partialState().with(RedstonePulserBlock.LIT, false).addModels(new ConfiguredModel(
							provider.models()
									.cubeAll("redstone_detector", provider.modLoc("block/redstone_detector"))))
			)
			.initialProperties(() -> Blocks.STONE)
			.transform(TagGen.pickaxeOnly())
			.item(IOBlockItem::new)
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
			.item(IOBlockItem::new)
			.build()
			.register();
}
