package com.psilocke.riftbound.datagen;

import com.psilocke.riftbound.Riftbound;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends BlockTagsProvider{

	public ModBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
		super(generatorIn, Riftbound.MOD_ID, existingFileHelper);
	}
	
	@Override
	protected void addTags() {
		
	}
}
