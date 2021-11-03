package com.psilocke.riftbound.common.item;

import com.psilocke.riftbound.common.entity.EverlastingEnderPearlEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class EverlastingEnderPearlItem extends Item {

	public EverlastingEnderPearlItem(Properties properties) {
		super(properties);
	}
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
		player.getCooldowns().addCooldown(this, 20);
		if (!world.isClientSide) {
			EverlastingEnderPearlEntity enderpearlentity = new EverlastingEnderPearlEntity(world, player);
			enderpearlentity.setItem(itemstack);
			enderpearlentity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1.5F, 1.0F);
			world.addFreshEntity(enderpearlentity);
		}

		player.awardStat(Stats.ITEM_USED.get(this));
		if (!player.abilities.instabuild) {
			itemstack.shrink(1);
		}
		return ActionResult.sidedSuccess(itemstack, world.isClientSide());
	}
	@Override
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}
}
