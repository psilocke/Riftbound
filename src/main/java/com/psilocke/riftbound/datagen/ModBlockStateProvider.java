package com.psilocke.riftbound.datagen;

import com.psilocke.riftbound.Riftbound;
import com.psilocke.riftbound.common.block.waystone.WaystoneStoneBlock;
import com.psilocke.riftbound.registry.ModBlocks;

import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

	public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper existingFileHelper) {
		super(gen, Riftbound.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		
		waystoneBlock(ModBlocks.WAYSTONE_BLOCK.get(), "waystone_stone_block");
	}
	
	@SuppressWarnings("static-access")
	public void waystoneBlock(WaystoneStoneBlock block, String name) {
		ModelFile empty = models().getExistingFile(modLoc("block/waystone/"+name+"_empty"));
		ModelFile filled = models().getExistingFile(modLoc("block/waystone/"+name+"_filled"));
		ModelFile dummy = models().getExistingFile(modLoc("block/waystone/"+name+"_dummy"));
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        builder.partialState()
	    	.with(block.SLOT_FILLED, false)
	    	.with(block.HALF, DoubleBlockHalf.LOWER)
	    	.addModels(new ConfiguredModel(empty));
        builder.partialState()
	    	.with(block.SLOT_FILLED, true)
	    	.with(block.HALF, DoubleBlockHalf.LOWER)
	    	.addModels(new ConfiguredModel(filled));
	    builder.partialState()
	    	.with(block.HALF, DoubleBlockHalf.UPPER)
	    	.addModels(new ConfiguredModel(dummy));
    }
}
