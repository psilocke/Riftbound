package com.psilocke.riftbound.registry;

import com.psilocke.riftbound.common.entity.EverlastingEnderPearlEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraftforge.fml.RegistryObject;

public class ModEntityTypes {
	
	public static final RegistryObject<EntityType<EverlastingEnderPearlEntity>> EVERLASTING_ENDER_PEARL = register(
			"everlasting_ender_pearl", 
			EntityType.Builder.<EverlastingEnderPearlEntity>of(
					EverlastingEnderPearlEntity::new, 
					EntityClassification.MISC
				).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
	
	static void register() {}

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Builder<T> builder) {
		return Registration.ENTITIES.register(name, () -> builder.build(name));
	}
}
