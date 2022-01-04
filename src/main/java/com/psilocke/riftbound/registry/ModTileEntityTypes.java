package com.psilocke.riftbound.registry;

import java.util.function.Supplier;

import com.psilocke.riftbound.common.tileentity.MonolithCoreTileEntity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

public class ModTileEntityTypes {
	
	public static final RegistryObject<TileEntityType<MonolithCoreTileEntity>> MONOLITH_BLOCK = register(
			"monolith_core_tile_entity",
			MonolithCoreTileEntity::new,
			ModBlocks.WAYSTONE_BLOCK
	);
	
	public static void register() {}
	
	private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, RegistryObject<? extends Block> block) {
        return Registration.TILE_ENTITIES.register(name, () -> {
            //noinspection ConstantConditions - null in build
            return TileEntityType.Builder.of(factory, block.get()).build(null);
        });
    }
}
