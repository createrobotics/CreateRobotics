package com.workert.robotics.base.registries;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.workert.robotics.Robotics;
import com.workert.robotics.content.computers.computer.ComputerBlockEntity;
import com.workert.robotics.content.computers.ioblocks.redstonedetector.RedstoneDetectorBlockEntity;
import com.workert.robotics.content.computers.ioblocks.redstoneemitter.RedstoneEmitterBlockEntity;
import com.workert.robotics.content.computers.ioblocks.redstonepulser.RedstonePulserBlockEntity;
import com.workert.robotics.content.computers.ioblocks.scanner.ScannerBlockEntity;
import com.workert.robotics.content.computers.ioblocks.scanner.ScannerRenderer;
import com.workert.robotics.content.computers.ioblocks.scanner.ScannerVisual;
import com.workert.robotics.content.robotics.drone_delivery.drone_port.DronePortBlockEntity;


public class BlockEntityRegistry {
	public static void register() {
	}



	public static final BlockEntityEntry<ComputerBlockEntity> COMPUTER = Robotics.REGISTRATE
			.blockEntity("computer", ComputerBlockEntity::new)
			.visual(() -> SingleAxisRotatingVisual.of(AllPartialModels.SHAFTLESS_COGWHEEL))
			.validBlock(BlockRegistry.COMPUTER)
			.register();

	public static final BlockEntityEntry<DronePortBlockEntity> DRONE_PORT = Robotics.REGISTRATE
			.blockEntity("drone_port", DronePortBlockEntity::new)
			.validBlock(BlockRegistry.DRONE_PORT)
			.register();

	public static final BlockEntityEntry<RedstoneDetectorBlockEntity> REDSTONE_DETECTOR = Robotics.REGISTRATE
			.blockEntity("redstone_detector", RedstoneDetectorBlockEntity::new)
			.validBlock(BlockRegistry.REDSTONE_DETECTOR)
			.register();
	public static final BlockEntityEntry<RedstoneEmitterBlockEntity> REDSTONE_EMITTER = Robotics.REGISTRATE
			.blockEntity("redstone_emitter", RedstoneEmitterBlockEntity::new)
			.validBlock(BlockRegistry.REDSTONE_EMITTER)
			.register();
	public static final BlockEntityEntry<RedstonePulserBlockEntity> REDSTONE_PULSER = Robotics.REGISTRATE
			.blockEntity("redstone_pulser", RedstonePulserBlockEntity::new)
			.validBlock(BlockRegistry.REDSTONE_PULSER)
			.register();

	public static final BlockEntityEntry<ScannerBlockEntity> SCANNER = Robotics.REGISTRATE
			.blockEntity("scanner", ScannerBlockEntity::new)
			.visual(() -> ScannerVisual::new)
			.validBlock(BlockRegistry.SCANNER.lazy())
			.renderer(() -> ScannerRenderer::new)
			.register();
}