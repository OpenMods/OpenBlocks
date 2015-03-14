package openblocks.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import openmods.asm.VisitorHelper;
import openmods.asm.VisitorHelper.TransformProvider;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class OpenBlocksClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(final String name, String transformedName, byte[] bytes) {
		if (bytes == null) return bytes;

		if (transformedName.equals("net.minecraft.entity.player.EntityPlayer")) return VisitorHelper.apply(bytes, name, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
			@Override
			public ClassVisitor createVisitor(String name, ClassVisitor cv) {
				FMLRelaunchLog.info("[OpenBlocks] Trying to patch EntityPlayer.isInBed (class: %s)", name);
				return new EntityPlayerVisitor(name, cv);
			}
		});

		return bytes;
	}
}
