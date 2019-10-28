package com.sleepingbags_upport_cmt.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class SleepingbagsContainer extends DummyModContainer
{	
	public SleepingbagsContainer()
	{
		super(createMetadata());
		
		//Static defineing the classes here should let us obtain their true name via the compile process.
		Class r_sp_player=EntityPlayerSP.class;
		Class r_abs_player=AbstractClientPlayer.class;
		Class r_render_man=RenderManager.class;
		
		//Should be able to then get the identity of the class from these calls:
		//Ref: https://www.geeksforgeeks.org/object-class-in-java/
		//r_sp_player.getName()
		//r_abs_player.getName()
		System.out.println("EntityPlayerSP -> " + r_sp_player.getName());
		System.out.println("AbstractClientPlayer -> " + r_abs_player.getName());
		System.out.println("RenderManager -> " + r_render_man.getName());
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		System.out.println("Registering bus.");
		bus.register(this);
		return true;
	}
	
	//Loading preinit stage stuff.
	@Subscribe
    public void modInitialization(FMLInitializationEvent evt)
    {
		System.out.println("Replacing player render list.");
		//RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new RendererPlayer_ESFixup(Minecraft.getMinecraft().getRenderManager()));
		
		//Grab a few values from RenderManager, as players are handled different.
		Field playerRenderer = ReflectionHelper.findField(RenderManager.class, "playerRender", "playerRenderer");
		Field skinMap = ReflectionHelper.findField(RenderManager.class, "skinMap", "skinMap");
		playerRenderer.setAccessible(true);
		skinMap.setAccessible(true);
		
        Field modifiers;
		try {
			modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
	        try 
	        {
				modifiers.setInt(playerRenderer, playerRenderer.getModifiers() & ~Modifier.FINAL);
				modifiers.setInt(skinMap, skinMap.getModifiers() & ~Modifier.FINAL);
			} 
	        catch (IllegalArgumentException | IllegalAccessException e1) 
	        {
				e1.printStackTrace();
			}
		} 
		catch (NoSuchFieldException | SecurityException e2) 
		{
			e2.printStackTrace();
		}
        
		try 
		{
			playerRenderer.set(Minecraft.getMinecraft().getRenderManager(), new RendererPlayer_ESFixup(Minecraft.getMinecraft().getRenderManager()));
			((Map<String, RenderPlayer>)skinMap.get(Minecraft.getMinecraft().getRenderManager())).put("default", new RendererPlayer_ESFixup(Minecraft.getMinecraft().getRenderManager()));
			((Map<String, RenderPlayer>)skinMap.get(Minecraft.getMinecraft().getRenderManager())).put("slim", new RendererPlayer_ESFixup(Minecraft.getMinecraft().getRenderManager(), true));
		} 
		catch (IllegalArgumentException | IllegalAccessException e) 
		{
			e.printStackTrace();
		}
		
    }
	
	private static ModMetadata createMetadata()
	{
		ModMetadata meta = new ModMetadata();

		meta.modId = SleepingbagsPlugin.MODID;
		meta.name = "Sleeping bags vanilla core tweaks";
		meta.version = SleepingbagsPlugin.VERSION;
		meta.description = "Provides tweaks to make sleeping bags work correctly.";
		meta.url = "";
		meta.authorList = Arrays.asList(new String[] { "mathew_653" });

		return meta;
	}
	
	public static class Ev_Bus {
	}
}