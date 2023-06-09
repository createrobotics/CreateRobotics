package com.workert.robotics.base.lists;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.workert.robotics.Robotics;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import com.workert.robotics.content.computers.computer.ComputerInstance;
import com.workert.robotics.content.computers.computer.ComputerRenderer;
import com.workert.robotics.content.computers.inputs.redstonedetector.RedstoneDetectorBlockEntity;
import com.workert.robotics.content.computers.inputs.scanner.ScannerBlockEntity;
import com.workert.robotics.content.computers.inputs.scanner.ScannerInstance;
import com.workert.robotics.content.computers.inputs.scanner.ScannerRenderer;
import com.workert.robotics.unused.smasher.SmasherBlockEntity;

public class BlockEntityList {
	public static void register() {
	}

	public static final BlockEntityEntry<SmasherBlockEntity> SMASHER_BLOCK_ENTITY = Robotics.REGISTRATE
			.tileEntity("smasher", SmasherBlockEntity::new)
			.validBlock(() -> BlockList.SMASHER.get())
			.register();

	public static final BlockEntityEntry<ComputerBlockEntity> COMPUTER = Robotics.REGISTRATE
			.tileEntity("computer", ComputerBlockEntity::new)
			.instance(() -> ComputerInstance::new)
			.validBlocks(BlockList.COMPUTER)
			.renderer(() -> ComputerRenderer::new)
			.register();

	public static final BlockEntityEntry<RedstoneDetectorBlockEntity> REDSTONE_DETECTOR = Robotics.REGISTRATE
			.tileEntity("redstone_detector", RedstoneDetectorBlockEntity::new)
			.validBlocks(BlockList.REDSTONE_DETECTOR)
			.register();
	public static final BlockEntityEntry<ScannerBlockEntity> SCANNER = Robotics.REGISTRATE
			.tileEntity("scanner", ScannerBlockEntity::new)
			.instance(() -> ScannerInstance::new)
			.validBlocks(BlockList.SCANNER)
			.renderer(() -> ScannerRenderer::new)
			.register();
}