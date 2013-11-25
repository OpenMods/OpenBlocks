package openblocks.utils;

import openblocks.OpenBlocksCorePlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class AsmUtils {

	public static class MethodMatcher {
		private final String clsName;
		private final String description;
		private final String srgName;
		private final String mcpName;

		public MethodMatcher(String clsName, String description, String mcpName, String srgName) {
			this.clsName = clsName;
			this.description = description;
			this.srgName = srgName;
			this.mcpName = mcpName;
		}

		public boolean match(String methodName, String methodDesc) {
			if (!methodDesc.equals(description)) return false;
			if (methodName.equals(mcpName)) return true;
			if (!OpenBlocksCorePlugin.isRuntimeDeobfuscated) return false;
			String mapped = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clsName, methodName, methodDesc);
			return mapped.equals(srgName);
		}
	}

	public static interface TransformContext {
		public ClassVisitor createVisitor(ClassVisitor cv);
	}

	public static byte[] applyVisitor(byte[] bytes, TransformContext context) {
		ClassReader cr = new ClassReader(bytes);
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
		ClassVisitor mod = context.createVisitor(cw);
		cr.accept(mod, 0);
		return cw.toByteArray();
	}

}
