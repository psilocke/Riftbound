package com.psilocke.riftbound.datagen;

import java.util.function.Consumer;

import com.psilocke.riftbound.build.ModBlocks;
import com.psilocke.riftbound.build.ModItems;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;

public class ModRecipeProvider extends RecipeProvider {

	public ModRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}
	
	
	
	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		//ITEMS
		ShapedRecipeBuilder.shaped(ModItems.EVERLASTING_ENDER_PEARL.get())
			.define('E', Items.ENDER_PEARL)
			.define('N', Items.NETHER_STAR)
			.define('Q', Items.QUARTZ)
			.pattern(" E ")
			.pattern("QNQ")
			.pattern(" Q ")
			.unlockedBy("has_item", has(Items.NETHER_STAR))
			.save(consumer);
		
		ShapedRecipeBuilder.shaped(ModItems.RIFT_PEARL.get())
			.define('E', ModItems.EVERLASTING_ENDER_PEARL.get())
			.define('Q', Items.QUARTZ)
			.define('C', Items.CHORUS_FRUIT)
			.define('R', Items.REDSTONE)
			.pattern("QRQ")
			.pattern("CEC")
			.pattern("QRQ")
			.unlockedBy("has_item", has(ModBlocks.WAYSTONE_BLOCK.get()))
			.save(consumer);
		
		
		
		
		//BLOCKS
		
		//craft monolith
		ShapedRecipeBuilder.shaped(ModBlocks.WAYSTONE_BLOCK.get())
			.define('L', Items.LODESTONE)
			.define('A', Items.POLISHED_ANDESITE_SLAB)
			.define('O', Items.CRYING_OBSIDIAN)
			.define('S', Items.POLISHED_BASALT)
			.pattern("SSS")
			.pattern("SLS")
			.pattern("AOA")
			.unlockedBy("has_item", has(Blocks.LODESTONE))
			.save(consumer);
	}

}
