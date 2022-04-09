package com.psilocke.riftbound.common.item;

import com.psilocke.riftbound.common.block.waystone.WaystoneCore;
import com.psilocke.riftbound.common.block.waystone.WaystoneStoneBlock;
import com.psilocke.riftbound.common.tileentity.MonolithCoreTileEntity;
import com.psilocke.riftbound.registry.ModBlocks;
import com.psilocke.riftbound.registry.ModItems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
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

	public RiftPearlItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		/*
		if(!world.isClientSide) {
			player.sendMessage(new StringTextComponent("Stored Pos: "+getLinkedPosition(player.getItemInHand(hand).getStack())), Util.NIL_UUID);
			player.sendMessage(new StringTextComponent("Stored Dim: "+getDimension(player.getItemInHand(hand).getStack())), Util.NIL_UUID);
			player.sendMessage(new StringTextComponent("Active Dim: "+world.dimension().getRegistryName().getNamespace()), Util.NIL_UUID);
		}
		*/
		return super.use(world, player, hand);
	}



	@Override
	public ActionResultType useOn(ItemUseContext context) {
		/*
		World world = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		BlockState blockState = world.getBlockState(clickedPos);
		ItemStack itemStack = context.getItemInHand();
		if (!blockState.is(ModBlocks.WAYSTONE_BLOCK.get())) {
			return ActionResultType.sidedSuccess(world.isClientSide);
		} else {
			if(blockState.getValue(WaystoneCore.SLOT_FILLED)) {
				if(blockState.getValue(WaystoneCore.HALF)==DoubleBlockHalf.UPPER) {
					clickedPos = clickedPos.below();
					blockState = world.getBlockState(clickedPos);
				}
				
				world.playSound((PlayerEntity) null, clickedPos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
				
				linkPosition(world, clickedPos, itemStack);
			}
			return ActionResultType.sidedSuccess(world.isClientSide);
		}
		*/
		return ActionResultType.sidedSuccess(context.getLevel().isClientSide);
	}
	
	public static void linkPosition(World world, BlockPos clickedPos, ItemStack heldItem) {
		ItemStack clickedPositionPearl = ((MonolithCoreTileEntity)world.getBlockEntity(clickedPos)).getSlot();
		CompoundNBT clickedPearlNBT = clickedPositionPearl.getOrCreateTag();
		if(clickedPearlNBT != null && !clickedPearlNBT.contains("uniqueIndex")) {
			int uniqueIndex = (int) Math.round(Math.random()*100000);
			
			CompoundNBT heldItemNBT = compilelinkedPosition(world.dimension(), clickedPos);
			heldItem.setTag(heldItemNBT);
			heldItem.getTag().putInt("uniqueIndex", uniqueIndex);
			
			clickedPearlNBT.putInt("uniqueIndex", uniqueIndex);
		}
		
	}
	
	public static CompoundNBT compilelinkedPosition(RegistryKey<World> world, BlockPos clickedPos) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("LinkedX", clickedPos.getX());
		nbt.putDouble("LinkedY", clickedPos.getY());
		nbt.putDouble("LinkedZ", clickedPos.getZ());
		nbt.putString("LinkedDimension", world.getRegistryName().getNamespace());
		return nbt;
	}
	
	public static int getUniqueIndex(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if(nbt != null && nbt.contains("uniqueIndex")) {
			return nbt.getInt("uniqueIndex");
		}else return -1;
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
