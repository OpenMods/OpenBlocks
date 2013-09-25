package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityElevator;

public class BlockElevator extends OpenBlock {

	public static final int[] colors = new int[] { 0xe0e0e0, // 15
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

	public BlockElevator() {
		super(Config.blockElevatorId, Material.ground);
		setupBlock(this, "elevator", TileEntityElevator.class);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return colors[world.getBlockMetadata(x, y, z)];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		TileEntityElevator drop = (TileEntityElevator) te;
		return drop.onActivated(player);
	}
}
