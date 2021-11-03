package com.psilocke.riftbound.datagen;

import com.psilocke.riftbound.Riftbound;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, Riftbound.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		waystoneBlockBuilder("waystone_stone_block");
		
		ModelFile ItemGenerated = getExistingFile(mcLoc("item/generated"));
		
		itemBuilder(ItemGenerated, "everlasting_ender_pearl");
		itemBuilder(ItemGenerated, "rift_pearl");
	}
	
	public void waystoneBlockBuilder(String name) {
		withExistingParent(name, modLoc("block/waystone/"+name+"_empty"));
	}
	
	private ItemModelBuilder itemBuilder(ModelFile itemGenerated, String name) {
		return getBuilder(name).parent(itemGenerated).texture("layer0","item/"+name);
	}

}
