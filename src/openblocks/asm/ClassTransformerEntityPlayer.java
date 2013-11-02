package openblocks.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ClassTransformerEntityPlayer implements IClassTransformer {

	private static final String isInBedNameMcp = "isInBed";
	private static final String isInBedNameSrg = "func_71065_l";
	private static final String bedHook = "openblocks/Hooks";
	
	public static boolean IsInBedHookSuccess = false;

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (!transformedName.equals("net.minecraft.entity.player.EntityPlayer")) { return bytes; }

		ClassReader classReader = new ClassReader(bytes);
		ClassNode classNode = new ClassNode();

		classReader.accept(classNode, 0);

		for (MethodNode methodNode : classNode.methods) {
			if (methodNode.name.equals(isInBedNameMcp)
					|| methodNode.name.equals(isInBedNameSrg)) {

				LabelNode skipReturn = new LabelNode();
				methodNode.instructions.insert(skipReturn);
				methodNode.instructions.insert(new InsnNode(Opcodes.IRETURN));
				methodNode.instructions.insert(new LdcInsnNode(true));
				methodNode.instructions.insert(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
				methodNode.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, bedHook, "isInBed", "(Lnet/minecraft/entity/player/EntityPlayer;)Z"));
				methodNode.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 0));
				IsInBedHookSuccess = true;
			}
		}

		ClassWriter classWriter = new ClassWriter(0);

		classNode.accept(classWriter);

		return classWriter.toByteArray();
	}

}
