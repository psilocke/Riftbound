package com.psilocke.riftbound.common.block;

import com.psilocke.riftbound.common.item.RiftPearlItem;
import com.psilocke.riftbound.common.tileentity.MonolithCoreTileEntity;
import com.psilocke.riftbound.registry.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class MonolithCore extends Block {
	
	protected final static int SLOTTING = 0;
	protected final static int UNSLOTTED = 1;
	protected final static int INACTIVE = 2;
	protected final static int ACTIVE = 3;
	protected final static int LINK = 4;
	protected final static int EJECT = 5;
	protected final static int FAIL = -1;

    public final static BooleanProperty POWERED = BlockStateProperties.POWERED;
    public final static BooleanProperty SLOT_FILLED = BooleanProperty.create("slot_filled");

	public MonolithCore(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}
	
	public TileEntity newBlockEntity(IBlockReader reader) {
		return new MonolithCoreTileEntity();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity) {
		this.dropRiftPearl(world, pos, playerEntity);
		super.playerWillDestroy(world, pos, state, playerEntity);
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos clickedPos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace) {
		if(!world.isClientSide) {
			switch(interactWith(world,state,clickedPos,player)) {
				case ACTIVE:
					player.sendMessage(new StringTextComponent("Activation Success."), Util.NIL_UUID);
					return ActionResultType.SUCCESS;
				case INACTIVE:
					player.sendMessage(new StringTextComponent("Deactivated."), Util.NIL_UUID);
					return ActionResultType.FAIL;
				case SLOTTING:
					player.sendMessage(new StringTextComponent("Pearl Slotting."), Util.NIL_UUID);
					setSlot(world, clickedPos, (MonolithCoreTileEntity)world.getBlockEntity(clickedPos), player);
					return ActionResultType.CONSUME;
				case UNSLOTTED:
					player.sendMessage(new StringTextComponent("Empty."), Util.NIL_UUID);
					return ActionResultType.FAIL;
				case EJECT:
					player.sendMessage(new StringTextComponent("Ejecting."), Util.NIL_UUID);
					dropRiftPearl(world, clickedPos, player);
					return ActionResultType.CONSUME;
				case LINK:
					player.sendMessage(new StringTextComponent("Pearl Linking."), Util.NIL_UUID);
					return ActionResultType.PASS;
			}
		}else return ActionResultType.SUCCESS;
		
		return ActionResultType.CONSUME;
	}
	
	protected int interactWith(World world, BlockState state, BlockPos clickedPos, PlayerEntity player) {
		TileEntity tileEntity = world.getBlockEntity(clickedPos);
		if (tileEntity != null && tileEntity instanceof MonolithCoreTileEntity) {
			boolean playerHoldingPearl = player.getMainHandItem().getItem() == ModItems.RIFT_PEARL.get();
			if(state.getValue(SLOT_FILLED)) {
				if(player.isCrouching()) {
					return EJECT;
				}else if(!playerHoldingPearl) {
					if(!state.getValue(POWERED)) {
						return ACTIVE;
					}else {
						return INACTIVE;
					}
				}else return LINK;
			}else {
				if(!playerHoldingPearl) {
					return UNSLOTTED;
				}else {
					return SLOTTING;
				}
			}
		}
		return FAIL;
	}
	
	protected void setSlot(World world, BlockPos clickedPos, MonolithCoreTileEntity monolith, PlayerEntity player) {
		ItemStack item = player.getMainHandItem();
		if(item.getItem() instanceof RiftPearlItem) {
			if(monolith.setSlot(item)) {
				if(!world.isClientSide) {
					player.getMainHandItem().shrink(1);
				}
				if(monolith.getSlot() != ItemStack.EMPTY) {
					player.sendMessage(new StringTextComponent("Rift Pearl Slotted."), Util.NIL_UUID);
					world.setBlockAndUpdate(clickedPos, world.getBlockState(clickedPos).setValue(SLOT_FILLED, true));
				}
				syncSlot(world, monolith, clickedPos);
			}
		}
	}
	
	private void syncSlot(World world, MonolithCoreTileEntity monolith, BlockPos clickedPos) {
		BlockPos linkedPos = RiftPearlItem.getLinkedPosition(monolith.getSlot());
		int uniqueIndex = RiftPearlItem.getUniqueIndex(monolith.getSlot());
		
		if(world.getBlockState(linkedPos).is(this)) {
			MonolithCoreTileEntity refMonolith = ((MonolithCoreTileEntity)world.getBlockEntity(linkedPos));
			ItemStack itemstack = refMonolith.getSlot();
			
			if(RiftPearlItem.getUniqueIndex(itemstack) == uniqueIndex) {
				CompoundNBT compoundnbt = RiftPearlItem.compilelinkedPosition(world.dimension(), clickedPos);
				itemstack.setTag(compoundnbt);
				
				refMonolith.setSlot(itemstack);
			}
		}
	}

	protected void dropRiftPearl(World world, BlockPos pos, PlayerEntity player) {
		TileEntity tileentity = world.getBlockEntity(pos);
		if (tileentity instanceof MonolithCoreTileEntity) {
			MonolithCoreTileEntity monolith = (MonolithCoreTileEntity) tileentity;
			if(world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), monolith.getSlot()))) {
				monolith.clearContent();
				world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(SLOT_FILLED, false));
				player.sendMessage(new StringTextComponent("Ejected."), Util.NIL_UUID);
			}
		}
	}

}
