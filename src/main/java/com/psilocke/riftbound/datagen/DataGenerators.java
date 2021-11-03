package com.psilocke.riftbound.datagen;

import com.psilocke.riftbound.Riftbound;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Riftbound.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
	private DataGenerators() {
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		
		//generate block states, block models, and item models
		gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
		gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));
		
		//generate block tags
		ModBlockTagsProvider blockTags = new ModBlockTagsProvider(gen, existingFileHelper);
		gen.addProvider(blockTags);
		gen.addProvider(new ModItemTagsProvider(gen, blockTags, existingFileHelper));
		
		//generate recipes
		gen.addProvider(new ModRecipeProvider(gen));
		//generate block loot tables
		gen.addProvider(new ModLootTableProvider(gen));
	}
}
