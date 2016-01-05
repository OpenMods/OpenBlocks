package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.block.BlockRotationMode;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockItemDropper extends OpenBlock {
	public BlockItemDropper() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.NONE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);
		setTexture(ForgeDirection.DOWN, registry.registerIcon("openblocks:itemDropper_down"));
	}

}
