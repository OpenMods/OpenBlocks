package openblocks.asm;

import openblocks.utils.AsmUtils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class EntityPlayerVisitor extends ClassVisitor {

	public static boolean IsInBedHookSuccess = false;

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

	private final AsmUtils.MethodMatcher isInBedMatcher;

	public EntityPlayerVisitor(String obfClassName, ClassVisitor cv) {
		super(Opcodes.ASM4, cv);
		isInBedMatcher = new AsmUtils.MethodMatcher(obfClassName, "()Z", "isInBed", "func_71065_l");
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
		return isInBedMatcher.match(name, desc)? new HookMethodVisitor(parent) : parent;
	}
}