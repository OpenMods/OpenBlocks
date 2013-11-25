package openblocks.asm;

import openblocks.OpenBlocksCorePlugin;
import openblocks.utils.AsmUtils.MethodMatcher;
import openmods.Log;

import org.objectweb.asm.*;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class MapGenStructureVisitor extends ClassVisitor {

	{
		if (Log.logger == null) Log.logger = OpenBlocksCorePlugin.log;
	}

	private final MethodMatcher modifiedMethod;
	private final MethodMatcher markerMethod;
	private String structureStartCls;

	private class FixerMethodVisitor extends MethodVisitor {
		public FixerMethodVisitor(MethodVisitor mv) {
			super(Opcodes.ASM4, mv);
		}

		private boolean checkcastFound;
		private Integer localVarId;
		private boolean markerMethodFound;

		/*
		 * Default compilator usually creates:
		 * checkcast class net/minecraft/world/gen/structure/StructureStart
		 * astore X
		 * aload X
		 * 
		 * We use that to get id of local variable that stores 'structurestart'
		 */

		@Override
		public void visitTypeInsn(int opcode, String type) {
			super.visitTypeInsn(opcode, type);
			if (opcode == Opcodes.CHECKCAST && type.equals(structureStartCls)) {
				checkcastFound = true;
				Log.info("MapGenFix: Found checkcast to '%s'", type);
			}
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			super.visitVarInsn(opcode, var);

			if (checkcastFound && opcode == Opcodes.ASTORE) {
				localVarId = var;
				checkcastFound = false;
				Log.info("MapGenFix: Found var: %d", localVarId);
			}
		}

		/*
		 * Here we are transforming condition
		 * if (structurestart.isSizeableStructure())
		 * to
		 * if (structurestart.isSizeableStructure() &&
		 * !structurestart.getComponents().isEmpty())
		 * 
		 * Again, we assume that compilator places IFEQ jump just after calling
		 * isSizeableStructure from first expression. We can then reuse label
		 * for second part
		 */

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			super.visitMethodInsn(opcode, owner, name, desc);
			if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals(structureStartCls) && markerMethod.match(name, desc)) {
				markerMethodFound = true;
				Log.info("MapGenFix: Found 'StructureStart.isSizeableStructure' (%s.%s) call", owner, name);
			}
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			super.visitJumpInsn(opcode, label);

			if (markerMethodFound && localVarId != null && opcode == Opcodes.IFEQ) {
				Log.info("MapGenFix: All conditions matched, inserting extra condition");
				super.visitVarInsn(Opcodes.ALOAD, localVarId); // hopefully
																// 'structurestart'
				String getComponentsMethodName = OpenBlocksCorePlugin.isRuntimeDeobfuscated? "func_75073_b" : "getComponents";
				super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, structureStartCls, getComponentsMethodName, "()Ljava/util/LinkedList;");
				super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/LinkedList", "isEmpty", "()Z");
				super.visitJumpInsn(Opcodes.IFNE, label);
				markerMethodFound = false;
			}
		}
	}

	public MapGenStructureVisitor(String obfClassName, ClassVisitor cv) {
		super(Opcodes.ASM4, cv);

		structureStartCls = "net/minecraft/world/gen/structure/StructureStart";
		String chunkPositionCls = "net/minecraft/world/ChunkPosition";
		String worldCls = "net/minecraft/world/World";

		if (OpenBlocksCorePlugin.isRuntimeDeobfuscated) {
			structureStartCls = FMLDeobfuscatingRemapper.INSTANCE.unmap(structureStartCls);
			chunkPositionCls = FMLDeobfuscatingRemapper.INSTANCE.unmap(chunkPositionCls);
			worldCls = FMLDeobfuscatingRemapper.INSTANCE.unmap(worldCls);
		}

		String descriptor = Type.getMethodDescriptor(
				Type.getObjectType(chunkPositionCls),
				Type.getObjectType(worldCls),
				Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE
				);

		modifiedMethod = new MethodMatcher(obfClassName, descriptor, "getNearestInstance", "func_75050_a");
		markerMethod = new MethodMatcher(structureStartCls, "()Z", "isSizeableStructure", "func_75069_d");
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
		return modifiedMethod.match(name, desc)? new FixerMethodVisitor(parent) : parent;
	}

}
