package com.sleepingbags_upport_cmt.core;

import net.minecraft.launchwrapper.IClassTransformer;

public class SleepingbagsTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if(transformedName.equals(ClientPlayerVisitor.targetClassName))
			return ClientPlayerVisitor.transform(bytes, SleepingbagsPlugin.isObfuscated);
		else if (transformedName.equals(ForgeHooksClientVisitor.targetClassName))
			return ForgeHooksClientVisitor.transform(bytes, SleepingbagsPlugin.isObfuscated);
		else
			return bytes;
	}
}
