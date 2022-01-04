package com.psilocke.riftbound.registry;

import java.util.function.Supplier;

import com.psilocke.riftbound.common.block.waystone.WaystoneStoneBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;



public class ModBlocks {

	public static final RegistryObject<WaystoneStoneBlock> WAYSTONE_BLOCK = register("waystone_stone_block", () ->
	new WaystoneStoneBlock(Block.Properties.of(Material.STONE)
				.strength(1,10)
				.requiresCorrectToolForDrops()
				.sound(SoundType.LODESTONE)
		)
	);
	
	public static void register() {}

	private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return Registration.BLOCKS.register(name, block);
    }
	
	private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> ret = registerNoItem(name, block);
        Registration.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(Registration.MOD_CREATIVE_TAB)));
        return ret;
    }

}
