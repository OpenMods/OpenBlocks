package openblocks.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.render.RenderUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation(customName = "sky.normal")
public class BlockSky extends OpenBlock {

	private static final int MASK_INVERTED = 1 << 0;
	private static final int MASK_POWERED = 1 << 1;

	public BlockSky() {
		super(Material.iron);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:sky_inactive");
	}

	@Override
	public int damageDropped(int meta) {
		return meta & MASK_INVERTED;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor() {
		// randomness more or less intended
		return RenderUtils.getFogColor().getColor();
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!world.isRemote) {
			final int isPowered = world.isBlockIndirectlyGettingPowered(x, y, z)? MASK_POWERED : 0;
			final int isActive = world.getBlockMetadata(x, y, z) & MASK_POWERED;

			if (isPowered != isActive) world.scheduleBlockUpdate(x, y, z, this, 1);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		final int isPowered = world.isBlockIndirectlyGettingPowered(x, y, z)? MASK_POWERED : 0;
		final int isInverted = world.getBlockMetadata(x, y, z) & MASK_INVERTED;

		world.setBlockMetadataWithNotify(x, y, z, isPowered | isInverted, BlockNotifyFlags.ALL);
	}

	public static boolean isActive(int meta) {
		boolean isPowered = (meta & MASK_POWERED) != 0;
		boolean isInverted = (meta & MASK_INVERTED) != 0;
		return isPowered ^ isInverted;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return isActive(meta)? AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0) : super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> arr = new ArrayList<ItemStack>();
		arr.add(new ItemStack(OpenBlocks.Blocks.sky, 1, metadata == 0 || metadata == 2 ? 0 : 1));
		return arr;
	}
}
