package com.psilocke.riftbound;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.psilocke.riftbound.build.ModBlocks;
import com.psilocke.riftbound.build.Registration;
import com.psilocke.riftbound.client.render.ModEntityRendererManager;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("examplemod")
public class Riftbound{
 public static final String MOD_ID = "riftbound";
	
	public Riftbound() {
		Registration.register();
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(ModBlocks.WAYSTONE_BLOCK.get(), RenderType.cutout());
		ModEntityRendererManager.registerRenderers(event.getMinecraftSupplier().get().getEntityRenderDispatcher());
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
	}
}
