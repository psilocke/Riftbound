package com.psilocke.riftbound.registry;

import com.psilocke.riftbound.Riftbound;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Registration {
	public static final DeferredRegister<Block> BLOCKS = create(ForgeRegistries.BLOCKS);
	public static final DeferredRegister<Item> ITEMS = create(ForgeRegistries.ITEMS);
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = create(ForgeRegistries.TILE_ENTITIES);
	public static final DeferredRegister<EntityType<?>> ENTITIES = create(ForgeRegistries.ENTITIES);

	public static final ItemGroup MOD_CREATIVE_TAB = (new ItemGroup(-1, "tab_riftbound") {
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.EVERLASTING_ENDER_PEARL.get());
		}
	});

	public static void register() {
	    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
	    BLOCKS.register(modEventBus);
	    ITEMS.register(modEventBus);
	    TILE_ENTITIES.register(modEventBus);
	    ENTITIES.register(modEventBus);

	    ModBlocks.register();
	    ModItems.register();
	    ModTileEntityTypes.register();
	    ModEntityTypes.register();
	}

	private static <T extends IForgeRegistryEntry<T>> DeferredRegister<T> create(IForgeRegistry<T> registry) {
	    return DeferredRegister.create(registry, Riftbound.MOD_ID);
	}

}