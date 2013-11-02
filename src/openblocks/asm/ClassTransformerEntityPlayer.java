package openblocks.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;

public class ClassTransformerEntityPlayer implements IClassTransformer {

	private static final String isInBedNameMcp = "isInBed";
	private static final String isInBedNameSrg = "func_71065_l";
	private static final String bedHookCls = "openblocks/Hooks";

	public static boolean IsInBedHookSuccess = false;

	private static boolean shouldHook(String methodName) {
		return methodName.equals(isInBedNameMcp) || methodName.equals(isInBedNameSrg);
	}

	private static class HookMethodVisitor extends MethodVisitor {
		public HookMethodVisitor(MethodVisitor mv) {
			super(Opcodes.ASM4, mv);
		}

		@Override
		public void visitCode() {
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0); // EntityPlayer this
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, bedHookCls, "isInBed", "(Lnet/minecraft/entity/player/EntityPlayer;)Z");
			Label skipReturn = new Label();
			mv.visitJumpInsn(Opcodes.IFEQ, skipReturn);
			mv.visitInsn(Opcodes.ICONST_0);
			mv.visitInsn(Opcodes.IRETURN);
			mv.visitLabel(skipReturn);
		}
	}

	public class HookClassVistor extends ClassVisitor {
		public HookClassVistor(ClassVisitor cv) {
			super(Opcodes.ASM4, cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
			return shouldHook(name)? new HookMethodVisitor(parent) : parent;
		}
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (!transformedName.equals("net.minecraft.entity.player.EntityPlayer")) { return bytes; }

		ClassReader cr = new ClassReader(bytes);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		HookClassVistor mod = new HookClassVistor(cw);
		cr.accept(mod, 0);
		return cw.toByteArray();
	}

}
