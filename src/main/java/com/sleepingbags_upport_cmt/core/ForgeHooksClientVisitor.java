package com.sleepingbags_upport_cmt.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class ForgeHooksClientVisitor extends ClassVisitor {
	//											  net.minecraftforge.client.ForgeHooksClient
	public static final String targetClassName = "net.minecraftforge.client.ForgeHooksClient";
	
	private static final String OBC_sig="(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/Entity;)V";
	private static final String OBC_redir_sig="(Lcom/sleepingbags_upport_cmt/core/ISleepingbags_ForgeHooksClient;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/Entity;)V";
	
	private boolean hadorientBedCamera;
	
	public static byte[] transform(byte[] bytes, boolean isObfuscated)
	{
		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			ClassReader cr = new ClassReader(in);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			ForgeHooksClientVisitor p = new ForgeHooksClientVisitor(cw, isObfuscated);

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

	public ForgeHooksClientVisitor(ClassVisitor classVisitor, boolean isObfuscated)
	{
		super(Opcodes.ASM4, classVisitor);
		this.isObfuscated = isObfuscated;
	}
	
	//TODO: Stub
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		String[] newInterfaces = new String[interfaces.length + 1];
		for(int i=0; i<interfaces.length; i++)
			newInterfaces[i] = interfaces[i];
		newInterfaces[interfaces.length] = "com/sleepingbags_upport_cmt/core/ISleepingbags_ForgeHooksClient"; //Push the interface so we know what we are targetting.
		super.visit(version, access, name, signature, superName, newInterfaces);
	}

	//TODO: Stub
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		//Desc returned.
		//(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/Entity;)V

		if(name.equals("orientBedCamera") && desc.equals(OBC_sig))
		{ 
			//System.out.println("orientBedCamera sig | " + desc);
			hadorientBedCamera=true;
			return super.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "localorientBedCamera", desc, signature, exceptions);
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
	
	//TODO: Stub
	@Override
	public void visitEnd()
	{
		MethodVisitor mv;
			
		mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "orientBedCamera", OBC_sig, null, null);
		//Static functions do not push self/this onto the stack.
		//mv.visitVarInsn(Opcodes.ALOAD, 0); //Self
		
		mv.visitVarInsn(Opcodes.ALOAD, 0); //World worldIn
		mv.visitVarInsn(Opcodes.ALOAD, 1); //BlockPos pos
		mv.visitVarInsn(Opcodes.ALOAD, 2); //IBlockState state
		mv.visitVarInsn(Opcodes.ALOAD, 3); //Entity entity
		//mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/sleepingbags_upport_cmt/core/PlayerHook", "orientBedCamera", OBC_redir_sig, false);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/sleepingbags_upport_cmt/core/PlayerHook", "orientBedCamera", OBC_sig, false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
}
