package com.sleepingbags_upport_cmt.core;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ISleepingbags_ForgeHooksClient {
	//public void localorientBedCamera(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity);
	public void localorientBedCamera(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity);
}
