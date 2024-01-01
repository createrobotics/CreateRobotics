package com.workert.robotics.base.datagen;
import com.simibubi.create.foundation.data.LangMerger;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.workert.robotics.Robotics;
import com.workert.robotics.base.client.LangPartials;
import com.workert.robotics.base.datagen.recipe.SequencedAssemblyRecipeGen;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class RoboticsDatagen {
	// TODO Update Translations to 1.20.1 registrate
	public static void gatherData(GatherDataEvent event) {
		DataGenerator dataGenerator = event.getGenerator();

		if (event.includeClient()) {
			PonderLocalization.provideRegistrateLang(Robotics.REGISTRATE);
			dataGenerator.addProvider(true,
					new LangMerger(dataGenerator, Robotics.MOD_ID, "Create Robotics", LangPartials.values()));
		}

		if (event.includeServer()) {
			dataGenerator.addProvider(true, new SequencedAssemblyRecipeGen(dataGenerator));
		}
	}
}
