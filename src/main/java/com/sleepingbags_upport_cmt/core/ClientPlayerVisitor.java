package com.sleepingbags_upport_cmt.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientPlayerVisitor extends ClassVisitor {
	public static final String targetClassName = "net.minecraft.client.entity.EntityPlayerSP";
	
	//Obfuscation mapping.
	public static final String obs_gBOID="db";
	public static final String obs_ABSPlayerClass="bua";
	
	private boolean hadLocalGetBedOrientationInDegrees;
	
	public static byte[] transform(byte[] bytes, boolean isObfuscated)
	{
		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			ClassReader cr = new ClassReader(in);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			ClientPlayerVisitor p = new ClientPlayerVisitor(cw, isObfuscated);

			cr.accept(p, 0);

			byte[] result = cw.toByteArray();
			in.close();
			return result;
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	private final boolean isObfuscated;

	public ClientPlayerVisitor(ClassVisitor classVisitor, boolean isObfuscated)
	{
		super(Opcodes.ASM4, classVisitor);
		this.isObfuscated = isObfuscated;
	}

	//TODO: Strip these to the minimal needed to work.
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		String[] newInterfaces = new String[interfaces.length + 1];
		for(int i=0; i<interfaces.length; i++)
			newInterfaces[i] = interfaces[i];
		newInterfaces[interfaces.length] = "com/sleepingbags_upport_cmt/core/ISleepingbags_hookIF";
		super.visit(version, access, name, signature, superName, newInterfaces);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		if(name.equals(isObfuscated ? obs_gBOID : "getBedOrientationInDegrees") && desc.equals("()F"))
		{
			hadLocalGetBedOrientationInDegrees = true;
			//Notes : This renames the function.
			return super.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "localGetBedOrientationInDegrees", desc, signature, exceptions);
		}
		
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
	
	@Override
	public void visitEnd()
	{
		MethodVisitor mv;
		
		//Notes : This redirects getBedOrientationInDegrees in EntityPlayerSP to our hook.
		mv = cv.visitMethod(Opcodes.ACC_PUBLIC, isObfuscated ? obs_gBOID : "getBedOrientationInDegrees", "()F", null, null);
		mv.visitVarInsn(Opcodes.ALOAD, 0); //Self
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/sleepingbags_upport_cmt/core/PlayerHook", "getBedOrientationInDegrees", "(Lcom/sleepingbags_upport_cmt/core/ISleepingbags_hookIF;)F", false);
		mv.visitInsn(Opcodes.FRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
		if(!hadLocalGetBedOrientationInDegrees)
		{
			//Notes : This redirects the abstract player class to our hook.
			mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "localGetBedOrientationInDegrees", "()F", null, null);
			mv.visitVarInsn(Opcodes.ALOAD, 0); //Self
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, isObfuscated ? obs_ABSPlayerClass : "net/minecraft/client/entity/AbstractClientPlayer", isObfuscated ? obs_gBOID : "getBedOrientationInDegrees", "()F", false);
			mv.visitInsn(Opcodes.FRETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
	}
}
