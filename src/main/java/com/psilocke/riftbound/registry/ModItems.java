package com.psilocke.riftbound.registry;

import com.psilocke.riftbound.common.item.EverlastingEnderPearlItem;
import com.psilocke.riftbound.common.item.RiftPearlItem;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.fml.RegistryObject;

public class ModItems {

	private static Properties itemProperties = new Item.Properties().tab(Registration.MOD_CREATIVE_TAB);
	
	public static final RegistryObject<Item> EVERLASTING_ENDER_PEARL = Registration.ITEMS.register("everlasting_ender_pearl", () ->
		new EverlastingEnderPearlItem(itemProperties
				.fireResistant()
				.stacksTo(1)
				
			)
	);
	
	public static final RegistryObject<Item> RIFT_PEARL = Registration.ITEMS.register("rift_pearl", () ->
		new RiftPearlItem(itemProperties
				.fireResistant()
				.stacksTo(1)
			
			)
	);
	
	static void register() {}
	
}
