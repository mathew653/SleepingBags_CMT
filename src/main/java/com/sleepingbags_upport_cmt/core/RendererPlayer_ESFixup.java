package com.sleepingbags_upport_cmt.core;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

public class RendererPlayer_ESFixup extends RenderPlayer {
	public RendererPlayer_ESFixup(RenderManager renderManager) {
		super(renderManager);
	}
	
	public RendererPlayer_ESFixup(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
	}
	
	protected void ancestor_applyRotations(AbstractClientPlayer entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
    }

	protected void applyRotations(AbstractClientPlayer entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
		int handled=0;
        if (entityLiving.isEntityAlive() && entityLiving.isPlayerSleeping())
        {
        	if (entityLiving instanceof EntityPlayerSP)
    		{
    			EntityPlayerSP _playerSP=(EntityPlayerSP)entityLiving;
    			ItemStack IS_armor = _playerSP.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
    			Item I_armor = IS_armor.getItem();
    			
    			if (IS_armor != null)
    			{
    				if (IS_armor.getTagCompound() != null)
    				{
    					NBTTagCompound nbt_compound=IS_armor.getTagCompound();
    					
    					if (nbt_compound.hasKey("Rest_Angle"))
    					{
    						//TODO: Not perfectly working, unsure why..
    						float rest_angle=nbt_compound.getFloat("Rest_Angle");
    						System.out.println("Player yaw : " + rotationYaw);
    						
    						ancestor_applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
    						//GlStateManager.translate(0.0F, 0.0f, 0.0F);
    						
    						//GlStateManager.rotate(this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
    						//GlStateManager.rotate(1.0f * (-90.0F - entityLiving.rotationPitch), 1.0F, 0.0F, 0.0F);
    						
    						//ancestor_applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
    						GlStateManager.rotate(this.getDeathMaxRotation(entityLiving), 1.0F, 0.0F, 0.0F);
    			            //GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
    						
    						//GlStateManager.rotate(rest_angle, 0.0F, 1.0F, 0.0F);
    						//GlStateManager.rotate(this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
    						handled=1;
    					}
    				}
    			}
    		}
        }
        
        if (handled == 0)
        {
            super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
        }
    }
}
