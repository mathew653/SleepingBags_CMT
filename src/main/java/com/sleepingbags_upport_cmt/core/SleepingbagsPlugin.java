package com.sleepingbags_upport_cmt.core;

import java.util.Arrays;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("Sleeping bags vanilla core tweaks")
@IFMLLoadingPlugin.TransformerExclusions({"com.sleepingbags_upport_cmt.core"})
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1000)
public class SleepingbagsPlugin implements IFMLLoadingPlugin
{
    public static final String MODID = "sleepingbags_upport_cmt";
    public static final String NAME = "sleepingbags_upport_cmt";
    public static final String VERSION = "0.1";
    
    //Coremod madness, lets see if our environment is Obfuscated
    public static boolean isObfuscated = false;
    
    //plain : EntityPlayer :: getBedOrientationInDegrees
    //obs	: db
    //plain : AbstractClientPlayer :: getBedOrientationInDegrees
    //obs	: bua
    
	@Override
	public String[] getASMTransformerClass() 
	{ return new String[] { "com.sleepingbags_upport_cmt.core.SleepingbagsTransformer" }; }

	@Override
	public String getModContainerClass() 
	{ return "com.sleepingbags_upport_cmt.core.SleepingbagsContainer"; }

	@Override
	public String getSetupClass() { return null; }

	@Override
	public String getAccessTransformerClass() { return null; }
    
    //We will need to detect this so we know what class and field we need to patch
    @Override
	public void injectData(Map<String, Object> data)
	{
    	//System.out.println("Loading sleeping bags tweak module..");
    	isObfuscated = (Boolean) data.get("runtimeDeobfuscationEnabled");	
    }
}
