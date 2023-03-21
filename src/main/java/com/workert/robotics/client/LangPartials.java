package com.workert.robotics.client;

import com.google.gson.JsonElement;
import com.simibubi.create.foundation.data.LangPartial;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.workert.robotics.Robotics;

import java.util.function.Supplier;

public enum LangPartials implements LangPartial {
	TOOLTIPS("Item Descriptions");

	private String display;
	private Supplier<JsonElement> provider;

	private LangPartials(String display) {
		this.display = display;
		this.provider = this::fromResource;
	}

	private LangPartials(String display, Supplier<JsonElement> customProvider) {
		this.display = display;
		this.provider = customProvider;
	}

	@Override
	public String getDisplayName() {
		return this.display;
	}

	@Override
	public JsonElement provide() {
		return this.provider.get();
	}

	private JsonElement fromResource() {
		String fileName = Lang.asId(this.name());
		String filepath = "assets/" + Robotics.MOD_ID + "/lang/default/" + fileName + ".json";
		JsonElement element = FilesHelper.loadJsonResource(filepath);
		if (element == null)
			throw new IllegalStateException(String.format("Could not find default lang file: %s", filepath));
		return element;
	}

}