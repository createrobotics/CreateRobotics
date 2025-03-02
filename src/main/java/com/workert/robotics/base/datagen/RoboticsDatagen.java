package com.workert.robotics.base.datagen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.infrastructure.data.CreateRegistrateTags;
import com.tterrag.registrate.providers.ProviderType;
import com.workert.robotics.Robotics;
import com.workert.robotics.base.datagen.recipe.SequencedAssemblyRecipeGen;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.function.BiConsumer;

public class RoboticsDatagen {
	public static void gatherData(GatherDataEvent event) {
		addExtraRegistrateData();

		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();

		generator.addProvider(event.includeServer(), new SequencedAssemblyRecipeGen(output));

		if (event.includeServer()) {
			ProcessingRecipeGen.registerAll(generator, output);
		}
	}

	private static void addExtraRegistrateData() {
		CreateRegistrateTags.addGenerators();

		Create.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
			BiConsumer<String, String> langConsumer = provider::add;

			provideDefaultLang("interface", langConsumer);
			provideDefaultLang("tooltips", langConsumer);
			PonderIndex.getLangAccess().provideLang(Robotics.MOD_ID, langConsumer);
		});
	}

	private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
		String path = "assets/robotics/lang/default/" + fileName + ".json";
		JsonElement jsonElement = FilesHelper.loadJsonResource(path);
		if (jsonElement == null) {
			throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
		}
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getAsString();
			consumer.accept(key, value);
		}
	}
}
