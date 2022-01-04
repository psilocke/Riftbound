package com.psilocke.riftbound.common.item;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.psilocke.riftbound.common.block.waystone.WaystoneStoneBlock;
import com.psilocke.riftbound.registry.ModBlocks;
import com.psilocke.riftbound.registry.ModItems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class RiftPearlItem extends Item {
	private static final Logger LOGGER = LogManager.getLogger();

	public RiftPearlItem(Properties properties) {
		super(properties);
	}

	
	
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if(!world.isClientSide) {
			player.sendMessage(new StringTextComponent("Stored Pos: "+getLinkedPosition(player.getItemInHand(hand).getStack())), Util.NIL_UUID);
			player.sendMessage(new StringTextComponent("Stored Dim: "+getDimension(player.getItemInHand(hand).getStack())), Util.NIL_UUID);
			player.sendMessage(new StringTextComponent("Active Dim: "+world.dimension().getRegistryName().getNamespace()), Util.NIL_UUID);
		}
		return super.use(world, player, hand);
	}



	@Override
	public ActionResultType useOn(ItemUseContext context) {
		World world = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		BlockState blockState = world.getBlockState(clickedPos);
		PlayerEntity player = context.getPlayer();
		ItemStack itemstack = context.getItemInHand();
		if (!blockState.is(ModBlocks.WAYSTONE_BLOCK.get())) {
			return ActionResultType.sidedSuccess(world.isClientSide);
		} else {
			if(blockState.getValue(WaystoneStoneBlock.SLOT_FILLED)) {
				world.playSound((PlayerEntity) null, clickedPos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
				
				ItemStack itemstack1 = new ItemStack(ModItems.RIFT_PEARL.get(), 1);
				CompoundNBT compoundnbt = itemstack.hasTag() ? itemstack.getTag().copy() : new CompoundNBT();
				compoundnbt = addLinkedPositionTags(world.dimension(), clickedPos);
				itemstack1.setTag(compoundnbt);
				itemstack.shrink(1);

				if (!player.inventory.add(itemstack1)) {
					player.drop(itemstack1, false);
				}
			}
			return ActionResultType.sidedSuccess(world.isClientSide);
		}
	}
	
	public static CompoundNBT addLinkedPositionTags(RegistryKey<World> world, BlockPos clickedPos) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("LinkedX", clickedPos.getX());
		nbt.putDouble("LinkedY", clickedPos.getY());
		nbt.putDouble("LinkedZ", clickedPos.getZ());
		nbt.putString("LinkedDimension", world.getRegistryName().getNamespace());
		return nbt;
	}
	
	public static BlockPos getLinkedPosition(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if(nbt != null && nbt.contains("LinkedX") && nbt.contains("LinkedY") && nbt.contains("LinkedZ")) {
			BlockPos blockPos = new BlockPos(nbt.getDouble("LinkedX"), nbt.getDouble("LinkedY"), nbt.getDouble("LinkedZ"));
			return blockPos;
		}else return null;
	}
	
	public static String getDimension(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if(nbt != null && nbt.contains("LinkedDimension")) {
			return nbt.getString("LinkedDimension");
		}else return null;
	}

	public static boolean isLinked(ItemStack itemStack) {
		CompoundNBT compoundnbt = itemStack.getTag();
		return compoundnbt != null && (compoundnbt.contains("LinkedX"));
	}

	public boolean isFoil(ItemStack itemStack) {
		return isLinked(itemStack);
	}
	

}
