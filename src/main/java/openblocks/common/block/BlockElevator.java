package openblocks.common.block;

import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityElevator;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.CollectionUtils;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;

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
		super(Material.rock);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return colors[world.getBlockMetadata(x, y, z)];
	}

	private String getLocalisedDirection(ForgeDirection direction) {
		if (direction == ForgeDirection.UNKNOWN) {
			return "openblocks.misc.default";
		} else {
			return "openblocks.misc.side." + direction.name().toLowerCase();
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem();
		if (stack != null) {
			Set<ColorMeta> metas = ColorUtils.stackToColor(stack);
			if (!metas.isEmpty()) {
				ColorMeta meta = CollectionUtils.getRandom(metas);
				int dmg = meta.vanillaId;
				// temp hack, dont tell anyone
				// NOTE: someone was to lazy to create custom item to make sure
				// default meta is 15 (white). And now we have to support that
				// weird stuff
				if (dmg == 15) dmg = 0;
				else if (dmg == 0) dmg = 15;
				world.setBlockMetadataWithNotify(x, y, z, dmg, BlockNotifyFlags.ALL);
				return true;
			}
		} else if (!world.isRemote) {
			TileEntityElevator te = getTileEntity(world, x, y, z, TileEntityElevator.class);
			if (te != null) {
				te.setNextDirection();
				player.addChatMessage(new ChatComponentTranslation("openblocks.misc.direction").appendSibling(new ChatComponentTranslation(getLocalisedDirection(te.getDirection()))));
			}
		}
		return false;
	}

}
