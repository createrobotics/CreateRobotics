package com.workert.robotics.block.inputblock.scanner;

import com.simibubi.create.content.contraptions.components.press.PressingBehaviour;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;

public class BeltScannerCallbacks {
	public static BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack s, TransportedItemStackHandlerBehaviour i, ScannerBehaviour behaviour) {
		if (behaviour.specifics.getKineticSpeed() == 0) return BeltProcessingBehaviour.ProcessingResult.PASS;
		if (behaviour.running) return BeltProcessingBehaviour.ProcessingResult.PASS;
		behaviour.start();
		return BeltProcessingBehaviour.ProcessingResult.HOLD;
	}

	public static BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack s, TransportedItemStackHandlerBehaviour i, ScannerBehaviour behaviour) {
		if (behaviour.specifics.getKineticSpeed() == 0) return BeltProcessingBehaviour.ProcessingResult.PASS;
		if (!behaviour.running) return BeltProcessingBehaviour.ProcessingResult.PASS;
		if (behaviour.runningTicks != PressingBehaviour.CYCLE / 2) return BeltProcessingBehaviour.ProcessingResult.HOLD;

		behaviour.specifics.scanOnBelt(s);

		return BeltProcessingBehaviour.ProcessingResult.HOLD;
	}
}
