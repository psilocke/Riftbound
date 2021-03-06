package com.psilocke.riftbound.common.block.waystone;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.psilocke.riftbound.common.block.MonolithCore;
import com.psilocke.riftbound.common.item.RiftPearlItem;
import com.psilocke.riftbound.common.tileentity.MonolithCoreTileEntity;
import com.psilocke.riftbound.registry.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ICollisionReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLClientLaunchProvider;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;

@SuppressWarnings("deprecation")
public class WaystoneCore extends MonolithCore implements IWaterLoggable, ITileEntityProvider{
	
	public static final VoxelShape LOWER_SHAPE = VoxelShapes.or(
			Block.box(1, 0, 1, 15, 17, 15),
			Block.box(4, 0, 0, 12, 16, 16),
			Block.box(0, 0, 4, 16, 16, 12)
    ).optimize();

    public static final VoxelShape UPPER_SHAPE = VoxelShapes.or(
    		Block.box(2, 1, 2, 14, 5, 14),
    		Block.box(2, 9, 2, 14, 13, 14),
    		Block.box(3, 5, 3, 7, 9, 7),
    		Block.box(3, 5, 9, 7, 9, 13),
    		Block.box(9, 5, 9, 13, 9, 13),
    		Block.box(9, 5, 3, 13, 9, 7)
    ).optimize();
    
    //block properties
    public final static EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public final static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final ImmutableList<Vector3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vector3i(0, 0, -1), new Vector3i(-1, 0, 0), new Vector3i(0, 0, 1), new Vector3i(1, 0, 0), new Vector3i(-1, 0, -1), new Vector3i(1, 0, -1), new Vector3i(-1, 0, 1), new Vector3i(1, 0, 1));
    private static final ImmutableList<Vector3i> RESPAWN_OFFSETS = (new Builder<Vector3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::above).iterator()).build();
	
	public WaystoneCore(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
		    	.setValue(HALF, DoubleBlockHalf.LOWER)
		    	.setValue(POWERED, false)
				.setValue(WATERLOGGED, false)
				.setValue(SLOT_FILLED, false)
		    );
	}
	
	//constructor
	@Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    	builder
    	.add(HALF)
    	.add(POWERED)
    	.add(SLOT_FILLED)
    	.add(WATERLOGGED);
    }
	
	
	//placement methods
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getClickedPos();
		FluidState fluidstate = context.getLevel().getFluidState(blockpos);
		World world = context.getLevel();
		if (blockpos.getY() < world.getMaxBuildHeight()-1 && context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context)) {
			return this.defaultBlockState()
					.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
		}

		return null;
	}
	
	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity livingEntity, ItemStack itemStack) {
		super.setPlacedBy(world, pos, state, livingEntity, itemStack);
		FluidState fluidstate = world.getFluidState(pos);
		world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER), 0);
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader iworld, BlockPos pos) {
    	BlockState below = iworld.getBlockState(pos.below());
		return (below.getBlock() != Blocks.AIR && below.getBlock() != Blocks.WATER) || (below.getBlock() == this && below.getValue(HALF) == DoubleBlockHalf.LOWER);
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity) {
		if (!world.isClientSide && playerEntity.isCreative()) {
			preventCreativeDropFromBottomPart(world, pos, state, playerEntity);
		}
		super.playerWillDestroy(world, pos, state, playerEntity);
	}
	
	protected static void preventCreativeDropFromBottomPart(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
		if (doubleblockhalf == DoubleBlockHalf.UPPER) {
			BlockPos blockpos = pos.below();
			BlockState blockstate = world.getBlockState(blockpos);
			if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
				if(blockstate.getValue(WATERLOGGED)) {
					world.setBlock(blockpos, Blocks.WATER.defaultBlockState(), 35);
				}else world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
				world.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
			}
		}

	}
	
	
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		if(state.getValue(HALF)==DoubleBlockHalf.LOWER) {
			return true;
		}
		return super.hasTileEntity(state);
	}

	//model and lighting methods
	@Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if(state.getValue(HALF) == DoubleBlockHalf.LOWER) {
        	return LOWER_SHAPE;
        }else return UPPER_SHAPE;
    }
	

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, IBlockReader world, BlockPos pos) {
		if(state.getValue(HALF) == DoubleBlockHalf.LOWER) {
        	return VoxelShapes.block();
		}else return VoxelShapes.empty();
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		if(state.getValue(POWERED) == false) {
			return 15;
		}else return 1;
	}
	
	public boolean useShapeForLightOcclusion(BlockState state) {
		return false;
	}
	
	
	//state changing methods
	public BlockState updateShape(BlockState state1, Direction direction, BlockState state2, IWorld iworld, BlockPos pos, BlockPos directionPos) {
		if (state1.getValue(WATERLOGGED)) {
			iworld.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(iworld));
		}

		DoubleBlockHalf doubleblockhalf = state1.getValue(HALF);
		if (direction.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
			iworld.setBlock(pos.above(), state2, 0);
			return state2.is(this) && state2.getValue(HALF) != doubleblockhalf ? state1.setValue(POWERED, state2.getValue(POWERED)).setValue(SLOT_FILLED, state2.getValue(SLOT_FILLED)) : Blocks.AIR.defaultBlockState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state1.canSurvive(iworld, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state1, direction, state2, iworld, pos, directionPos);
		}
	}
	
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos1, Block block, BlockPos pos2, boolean bool) {
		this.checkPoweredState(world, pos1, state);
	}
	
	private void checkPoweredState(World world, BlockPos pos, BlockState state) {
		boolean flag = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (flag != state.getValue(POWERED)) {
			world.setBlockAndUpdate(pos, state.setValue(POWERED, Boolean.valueOf(flag)));
		}

	}
	
	
	//interaction methods
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos clickedPos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace) {
		if(state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			clickedPos = clickedPos.below();
			state = world.getBlockState(clickedPos);
		}
		
		ActionResultType result = super.use(state, world, clickedPos, player, hand, rayTrace);
		
		if(result == ActionResultType.SUCCESS) {
			teleport(player, clickedPos, world);
		}
		
		return result;
	}
	
	private void teleport(PlayerEntity player, BlockPos clickedPos, World world) {
		ItemStack itemStack = ((MonolithCoreTileEntity)world.getBlockEntity(clickedPos)).getSlot();
		if (RiftPearlItem.isLinked(itemStack)) {
			if(RiftPearlItem.getDimension(itemStack).matches(world.dimension().getRegistryName().getNamespace())) {
				BlockPos tpPos = RiftPearlItem.getLinkedPosition(itemStack);
				Vector3d standPos = RespawnAnchorBlock.findStandUpPosition(player.getType(), world, tpPos).orElseGet(() -> {
		               BlockPos blockpos = clickedPos.above();
		               return new Vector3d((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.1D, (double)blockpos.getZ() + 0.5D);
		            });
				double x = standPos.x;
				double y = standPos.y;
				double z = standPos.z;
				player.teleportTo(x, y, z);
				player.sendMessage(new StringTextComponent("Teleport Success."), Util.NIL_UUID);
			}else player.sendMessage(new StringTextComponent("Cannot Teleport Between Dimensions."), Util.NIL_UUID);
		} else player.sendMessage(new StringTextComponent("Not Linked."), Util.NIL_UUID);
	}

}
