package openblocks.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import openmods.utils.AsmUtils;

import org.objectweb.asm.ClassVisitor;

public class OpenBlocksClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(final String name, String transformedName, byte[] bytes) {
		if (transformedName.equals("net.minecraft.entity.player.EntityPlayer")) return AsmUtils.applyVisitor(bytes, new AsmUtils.TransformContext() {
			@Override
			public ClassVisitor createVisitor(ClassVisitor cv) {
				return new EntityPlayerVisitor(name, cv);
			}
		});

		if (transformedName.equals("net.minecraft.world.gen.structure.MapGenStructure")) return AsmUtils.applyVisitor(bytes, new AsmUtils.TransformContext() {
			@Override
			public ClassVisitor createVisitor(ClassVisitor cv) {
				return new MapGenStructureVisitor(name, cv);
			}
		});

		return bytes;
	}
}
