package com.sleepingbags_upport_cmt.core;

import static org.lwjgl.opengl.GL11.glRotatef;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public class PlayerHook {
	//Tweak the players angle when sleeping, as by default this will check for a block.
	public static float getBedOrientationInDegrees(ISleepingbags_hookIF target)
	{
		//If we do not have other reason to suspect, then we will use our safe value from vanilla.
		float _result;
		_result = target.localGetBedOrientationInDegrees();
		
		//Works fine, just disabling for now.
		if (target instanceof EntityPlayerSP)
		{
			EntityPlayerSP _playerSP=(EntityPlayerSP)target;
			ItemStack IS_armor = _playerSP.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			Item I_armor = IS_armor.getItem();
			
			if (IS_armor != null)
			{
				if (IS_armor.getTagCompound() != null)
				{
					NBTTagCompound nbt_compound=IS_armor.getTagCompound();
					
					if (nbt_compound.hasKey("Rest_Angle"))
					{
						//System.out.println("Player rest angle : " + nbt_compound.getFloat("Rest_Angle"));
						float rest_angle=_playerSP.prevRenderYawOffset + 270F;
						return rest_angle;
						//return nbt_compound.getFloat("Rest_Angle") + 180F; //TODO: Needs adjusting(Can adjust bed cam angle for a temp time to figure out)
					}
				}
			}
		}
		
		return _result;
	}
	
	private static Class<?> clsForgeClientHooks=null;
	private static Method hlocalorientatBedCamera=null;
	private static boolean cacheOriginalOrientBedCameraHook()
	{
		//Find the class and cache it.
		if (clsForgeClientHooks == null) 
		{ 
			try
			{
				clsForgeClientHooks = Class.forName(ForgeHooksClientVisitor.targetClassName);
			} 
			catch (ClassNotFoundException e) 
			{
				System.out.println("Could not find class : " + ForgeHooksClientVisitor.targetClassName);
				return false;
			} 
		}
		if (hlocalorientatBedCamera == null)
		{
			try {
				hlocalorientatBedCamera = clsForgeClientHooks.getMethod("localorientBedCamera", IBlockAccess.class, BlockPos.class, IBlockState.class, Entity.class);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				System.out.println("Failed to obtain handle to method : localorientBedCamera");
				return false;
			}
		}
		
		return true;
	}
	
	//Tweak the players camera angle when sleeping.
	//This is called within buq,EntityRenderer | func_78467_g,orientCamera
	public static void orientBedCamera(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity)
    {
		//Default handler.
		cacheOriginalOrientBedCameraHook();
		int handled=0;
		
		//Works fine, just disabling for now.
		if (entity instanceof EntityPlayerSP)
		{
			EntityPlayerSP _playerSP=(EntityPlayerSP)entity;
			ItemStack IS_armor = _playerSP.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			Item I_armor = IS_armor.getItem();
					
			if (IS_armor != null)
			{
				if (IS_armor.getTagCompound() != null)
				{
					NBTTagCompound nbt_compound=IS_armor.getTagCompound();
						
					if (nbt_compound.hasKey("Rest_Angle"))
					{
						//System.out.println("Player rest angle : " + nbt_compound.getFloat("Rest_Angle"));
						//return nbt_compound.getFloat("Rest_Angle");
						float rest_angle=nbt_compound.getFloat("Rest_Angle");
						
						//Note: Not perfect on the angle for all angles.
						//Pitch, Yaw, Roll
						glRotatef((float)(rest_angle+180), 0.0F, 1.0F, 0.0F);
						GlStateManager.translate(0.0F, 0.1F, 0.0F);
						//Temp: Adjust this a bit so we can see how to align our self.
						handled=1;
					}
				}
			}
		}
		
		if (handled == 0)
		{
			try {
				hlocalorientatBedCamera.invoke(null, world, pos, state, entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.out.println("Invoke failed for localorientBedCamera");
			}
		}
    }
}
