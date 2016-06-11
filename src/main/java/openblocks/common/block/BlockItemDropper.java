package openblocks.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.common.util.ForgeDirection;
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
