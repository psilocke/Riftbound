package com.psilocke.riftbound.common.tileentity;

import com.psilocke.riftbound.build.ModTileEntityTypes;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

public class MonolithCoreTileEntity extends TileEntity {

	private ItemStack slot = ItemStack.EMPTY;
	
	public MonolithCoreTileEntity() {
		super(ModTileEntityTypes.MONOLITH_BLOCK.get());
	}
	
	public ItemStack getSlot() {
		return this.slot;
	}

	public boolean setSlot(ItemStack item) {
		this.slot = item;
		this.setChanged();
		return slot.equals(item);
	}

	public boolean clearContent() {
		return this.setSlot(ItemStack.EMPTY);
	}
}
