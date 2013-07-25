package openblocks.common.block;

import java.util.List;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityDropBlock;
import openblocks.common.tileentity.TileEntityGuide;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDrop extends OpenBlock {

	public static final int[] colors = new int[] {
			0xe0e0e0, // 15
			0xc54a4a, // 1
			0x2b6631, // 2
			0x5d4b3f, // 3
			0x494c68, // 4
			0xaa55b2, // 9
			0x608696, // 6
			0xb0b0b0, // 7
			0x595959, // 8
			0xd490b6, // 9
			0x81c57c, // 10
			0x8c8f2e, // 11
			0x728abb, // 12
			0xaf60b6, // 13
			0xbd6c36, // 14
			0x252525, // 0

	};

	public BlockDrop() {
		super(OpenBlocks.Config.blockDropId, Material.ground);
		setupBlock(this, "drop", "Drop Block", TileEntityDropBlock.class);
	}

	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return colors[world.getBlockMetadata(x, y, z)];
	}

	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		TileEntityDropBlock drop = (TileEntityDropBlock) te;
		return drop.onActivated(player);
	}
}
