package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAutoEnchantmentTable extends OpenBlock {

	private Icon iconTop;
	private Icon iconBottom;

	public BlockAutoEnchantmentTable() {
		super(Config.blockAutoEnchantmentTableId, Material.ground);
		setupBlock(this, "autoenchantmenttable", TileEntityAutoEnchantmentTable.class);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		super.randomDisplayTick(world, x, y, z, rand);

		for (int l = x - 2; l <= x + 2; ++l) {
			for (int i1 = z - 2; i1 <= z + 2; ++i1) {
				if (l > x - 2 && l < x + 2 && i1 == z - 1) {
					i1 = z + 2;
				}

				if (rand.nextInt(16) == 0) {
					for (int j1 = y; j1 <= y + 1; ++j1) {
						if (world.getBlockId(l, j1, i1) == Block.bookShelf.blockID) {
							if (!world.isAirBlock((l - x) / 2 + x, j1, (i1 - z)
									/ 2 + z)) {
								break;
							}

							world.spawnParticle("enchantmenttable", x + 0.5D, y + 2.0D, z + 0.5D, l
									- x + rand.nextFloat() - 0.5D, j1 - y
									- rand.nextFloat() - 1.0F, i1 - z
									+ rand.nextFloat() - 0.5D);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public void registerIcons(IconRegister registry) {
		super.registerIcons(registry);
		iconTop = registry.registerIcon("openblocks:autoenchantmenttable_top");
		iconBottom = registry.registerIcon("openblocks:autoenchantmenttable_bottom");
	}

	@Override
	public Icon getIcon(int side, int meta) {
		return side == 0? iconBottom : side == 1? iconTop : this.blockIcon;
	}

}
