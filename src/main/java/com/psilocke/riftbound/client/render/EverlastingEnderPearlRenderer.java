package com.psilocke.riftbound.client.render;

import com.psilocke.riftbound.Riftbound;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

public class EverlastingEnderPearlRenderer<T extends Entity & IRendersAsItem> extends SpriteRenderer<T> {

	private final ItemRenderer itemRenderer;
	private final float scale;
	private final boolean fullBright;

	public EverlastingEnderPearlRenderer(EntityRendererManager renderManager, net.minecraft.client.renderer.ItemRenderer itemRenderer, float p_i226035_3_, boolean p_i226035_4_) {
		super(renderManager, itemRenderer);
		this.itemRenderer = itemRenderer;
		this.scale = p_i226035_3_;
		this.fullBright = p_i226035_4_;
	}

	public EverlastingEnderPearlRenderer(EntityRendererManager renderManager, net.minecraft.client.renderer.ItemRenderer itemRenderer) {
		this(renderManager, itemRenderer, 1.0F, false);
	}

	protected int getBlockLightLevel(T entity, BlockPos pos) {
		return this.fullBright ? 15 : super.getBlockLightLevel(entity, pos);
	}

	public void render(T entity, float f1, float f2, MatrixStack stack, IRenderTypeBuffer buffer, int num) {
		if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
			stack.pushPose();
			stack.scale(this.scale, this.scale, this.scale);
			stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			this.itemRenderer.renderStatic(entity.getItem(), ItemCameraTransforms.TransformType.GROUND, num, OverlayTexture.NO_OVERLAY, stack, buffer);
			stack.popPose();
			super.render(entity, f1, f2, stack, buffer, num);
		}
	}

	public ResourceLocation getTextureLocation(Entity entity) {
		return new ResourceLocation(Riftbound.MOD_ID + ":textures/item/everlasting_ender_pearl.png");
	}
}
