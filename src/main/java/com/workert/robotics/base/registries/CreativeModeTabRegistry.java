package com.workert.robotics.base.registries;

import com.simibubi.create.Create;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeModeTabRegistry {
	private static final DeferredRegister<CreativeModeTab> REGISTER =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Create.ID);

	public static final RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB = REGISTER.register("base",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.create.base"))
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
					.icon(BlockRegistry.TIN_ORE::asStack)
					//.displayItems(new RegistrateDisplayItemsGenerator(true,	CreativeModeTabRegistry.BASE_CREATIVE_TAB))
					.build());

	public static void register(IEventBus modEventBus) {
		REGISTER.register(modEventBus);
	}
}