package com.workert.robotics.base.registries;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.workert.robotics.Robotics;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeModeTabRegistry {
	private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Robotics.MOD_ID);

	public static final RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB = CREATIVE_MODE_TABS.register("base",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.robotics.base"))
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
					.icon(ItemRegistry.CLOCKCOPTER::asStack)
					.displayItems(new RegistrateDisplayItemsGenerator(true, CreativeModeTabRegistry.BASE_CREATIVE_TAB))
					.build());

	public static void register(IEventBus modEventBus) {
		CREATIVE_MODE_TABS.register(modEventBus);
	}

	private record RegistrateDisplayItemsGenerator(boolean addItems, RegistryObject<CreativeModeTab> tabFilter) implements CreativeModeTab.DisplayItemsGenerator {
		private static final Predicate<Item> IS_ITEM_3D_PREDICATE;

		static {
			MutableObject<Predicate<Item>> isItem3d = new MutableObject<>(item -> false);
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
				isItem3d.setValue(item -> {
					ItemRenderer itemRenderer = Minecraft.getInstance()
							.getItemRenderer();
					BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
					return model.isGui3d();
				});
			});
			IS_ITEM_3D_PREDICATE = isItem3d.getValue();
		}

		private static Predicate<Item> makeExclusionPredicate() {
			Set<Item> exclusions = new ReferenceOpenHashSet<>();

			List<ItemProviderEntry<?>> simpleExclusions = List.of(
					ItemRegistry.INCOMPLETE_CODE_DRONE,
					ItemRegistry.INCOMPLETE_CLOCKCOPTER
			);

			exclusions.addAll(simpleExclusions.stream().map(ItemProviderEntry::asItem).toList());

			return exclusions::contains;
		}

		private static List<ItemOrdering> makeOrderings() {
			List<ItemOrdering> orderings = new ReferenceArrayList<>();

			Map<ItemProviderEntry<?>, ItemProviderEntry<?>> simpleBeforeOrderings = Map.of(
			);

			Map<ItemProviderEntry<?>, ItemProviderEntry<?>> simpleAfterOrderings = Map.of(
			);

			simpleBeforeOrderings.forEach((entry, otherEntry) -> {
				orderings.add(RegistrateDisplayItemsGenerator.ItemOrdering.before(entry.asItem(), otherEntry.asItem()));
			});

			simpleAfterOrderings.forEach((entry, otherEntry) -> {
				orderings.add(RegistrateDisplayItemsGenerator.ItemOrdering.after(entry.asItem(), otherEntry.asItem()));
			});

			return orderings;
		}

		private static Function<Item, ItemStack> makeStackFunc() {
			Map<Item, Function<Item, ItemStack>> factories = new Reference2ReferenceOpenHashMap<>();

			Map<ItemProviderEntry<?>, Function<Item, ItemStack>> simpleFactories = Map.of(
			);

			simpleFactories.forEach((entry, factory) -> {
				factories.put(entry.asItem(), factory);
			});

			return item -> {
				Function<Item, ItemStack> factory = factories.get(item);
				if (factory != null) {
					return factory.apply(item);
				}
				return new ItemStack(item);
			};
		}

		private static Function<Item, CreativeModeTab.TabVisibility> makeVisibilityFunc() {
			Map<Item, CreativeModeTab.TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();

			Map<ItemProviderEntry<?>, CreativeModeTab.TabVisibility> simpleVisibilities = Map.of(
			);

			simpleVisibilities.forEach((entry, factory) -> {
				visibilities.put(entry.asItem(), factory);
			});

			return item -> {
				CreativeModeTab.TabVisibility visibility = visibilities.get(item);
				if (visibility != null) {
					return visibility;
				}
				return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
			};
		}

		@Override
		public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
			Predicate<Item> exclusionPredicate = makeExclusionPredicate();
			List<ItemOrdering> orderings = makeOrderings();
			Function<Item, ItemStack> stackFunc = makeStackFunc();
			Function<Item, CreativeModeTab.TabVisibility> visibilityFunc = makeVisibilityFunc();

			List<Item> items = new LinkedList<>();
			if (this.addItems) {
				items.addAll(this.collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE.negate())));
			}
			items.addAll(this.collectBlocks(exclusionPredicate));
			if (this.addItems) {
				items.addAll(this.collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE)));
			}

			applyOrderings(items, orderings);
			outputAll(output, items, stackFunc, visibilityFunc);
		}

		private List<Item> collectBlocks(Predicate<Item> exclusionPredicate) {
			List<Item> items = new ReferenceArrayList<>();
			for (RegistryEntry<Block> entry : Robotics.REGISTRATE.getAll(Registries.BLOCK)) {
				if (!CreateRegistrate.isInCreativeTab(entry, this.tabFilter))
					continue;
				Item item = entry.get()
						.asItem();
				if (item == Items.AIR)
					continue;
				if (!exclusionPredicate.test(item))
					items.add(item);
			}
			items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
			return items;
		}

		private List<Item> collectItems(Predicate<Item> exclusionPredicate) {
			List<Item> items = new ReferenceArrayList<>();
			for (RegistryEntry<Item> entry : Robotics.REGISTRATE.getAll(Registries.ITEM)) {
				if (!CreateRegistrate.isInCreativeTab(entry, this.tabFilter))
					continue;
				Item item = entry.get();
				if (item instanceof BlockItem)
					continue;
				if (!exclusionPredicate.test(item))
					items.add(item);
			}
			return items;
		}

		private static void applyOrderings(List<Item> items, List<ItemOrdering> orderings) {
			for (RegistrateDisplayItemsGenerator.ItemOrdering ordering : orderings) {
				int anchorIndex = items.indexOf(ordering.anchor());
				if (anchorIndex != -1) {
					Item item = ordering.item();
					int itemIndex = items.indexOf(item);
					if (itemIndex != -1) {
						items.remove(itemIndex);
						if (itemIndex < anchorIndex) {
							anchorIndex--;
						}
					}
					if (ordering.type() == RegistrateDisplayItemsGenerator.ItemOrdering.Type.AFTER) {
						items.add(anchorIndex + 1, item);
					} else {
						items.add(anchorIndex, item);
					}
				}
			}
		}

		private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, ItemStack> stackFunc, Function<Item, CreativeModeTab.TabVisibility> visibilityFunc) {
			for (Item item : items) {
				output.accept(stackFunc.apply(item), visibilityFunc.apply(item));
			}
		}

		private record ItemOrdering(Item item, Item anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type type) {
			public static RegistrateDisplayItemsGenerator.ItemOrdering before(Item item, Item anchor) {
				return new RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor,
						RegistrateDisplayItemsGenerator.ItemOrdering.Type.BEFORE);
			}

			public static RegistrateDisplayItemsGenerator.ItemOrdering after(Item item, Item anchor) {
				return new RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor,
						RegistrateDisplayItemsGenerator.ItemOrdering.Type.AFTER);
			}

			public enum Type {
				BEFORE,
				AFTER
			}
		}
	}
}