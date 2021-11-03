package com.psilocke.riftbound.datagen;

import com.psilocke.riftbound.Riftbound;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, Riftbound.MOD_ID, existingFileHelper);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addTags() {
		
	}
}
