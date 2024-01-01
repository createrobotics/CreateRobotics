package com.workert.robotics.base.registries;

import com.workert.robotics.Robotics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ArmorMaterialRegistry implements ArmorMaterial {
	EXTEND_O_BOOTS("scaffolding", 4, new int[] {1, 2, 2, 1}, 0, SoundEvents.SCAFFOLDING_PLACE, 0.0F, 0.0F,
			() -> Ingredient.of(Items.SCAFFOLDING));

	private static final int[] HEALTH_PER_SLOT = new int[] {13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] slotProtections;
	private final int enchantability;
	private final SoundEvent sound;
	private final float toughness;
	private final float knockbackResistance;
	private final LazyLoadedValue<Ingredient> repairIngredient;

	ArmorMaterialRegistry(String name, int durabilityMultiplier, int[] slotProtections, int enchantability, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.slotProtections = slotProtections;
		this.enchantability = enchantability;
		this.sound = sound;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type pSlot) {
		return HEALTH_PER_SLOT[pSlot.getSlot().getIndex()] * this.durabilityMultiplier;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type pSlot) {
		return this.slotProtections[pSlot.getSlot().getIndex()];
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.sound;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	@Override
	public String getName() {
		return new ResourceLocation(Robotics.MOD_ID, this.name).toString();
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
