package com.psilocke.riftbound.client.render;

import com.psilocke.riftbound.build.ModEntityTypes;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IReloadableResourceManager;

public class ModEntityRendererManager {

	public ModEntityRendererManager() {
	}

	public static void registerRenderers(EntityRendererManager manager) {
		manager.register(ModEntityTypes.EVERLASTING_ENDER_PEARL.get(), new EverlastingEnderPearlRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
	}
}
