package openblocks.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class ClassTransformerEntityPlayer implements IClassTransformer {

	private static final String isInBedDesc = "()Z";
	private static final String isInBedNameMcp = "isInBed";
	private static final String isInBedNameSrg = "func_71065_l";

	public static boolean IsInBedHookSuccess = false;

	private static boolean shouldHook(String clsName, String methodName, String methodDesc) {
		if (!methodDesc.equals(isInBedDesc)) return false;
		if (methodName.equals(isInBedNameMcp)) return true;

		String mapped = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clsName, methodName, methodDesc);
		return mapped.equals(isInBedNameSrg);
	}

	private static class HookMethodVisitor extends MethodVisitor {
		public HookMethodVisitor(MethodVisitor mv) {
			super(Opcodes.ASM4, mv);
		}

		@Override
		public void visitCode() {
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0); // EntityPlayer this
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "openblocks/Hooks", "isInBed", "(Lnet/minecraft/entity/player/EntityPlayer;)Z");
			Label skipReturn = new Label();
			mv.visitJumpInsn(Opcodes.IFEQ, skipReturn);
			mv.visitInsn(Opcodes.ICONST_1);
			mv.visitInsn(Opcodes.IRETURN);
			mv.visitLabel(skipReturn);
			IsInBedHookSuccess = true;
		}
	}

	public class HookClassVisitor extends ClassVisitor {
		private final String obfClassName;

		public HookClassVisitor(String obfClassName, ClassVisitor cv) {
			super(Opcodes.ASM4, cv);
			this.obfClassName = obfClassName;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
			return shouldHook(obfClassName, name, desc)? new HookMethodVisitor(parent) : parent;
		}
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (!transformedName.equals("net.minecraft.entity.player.EntityPlayer")) { return bytes; }

		ClassReader cr = new ClassReader(bytes);
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
		HookClassVisitor mod = new HookClassVisitor(name, cw);
		cr.accept(mod, 0);
		return cw.toByteArray();
	}

}
