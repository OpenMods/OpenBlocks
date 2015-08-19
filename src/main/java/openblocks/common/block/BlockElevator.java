package openblocks.common.block;

import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.api.IElevatorBlock;
import openmods.infobook.BookDocumentation;
import openmods.utils.*;
import openmods.utils.ColorUtils.ColorMeta;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class BlockElevator extends OpenBlock implements IElevatorBlock {

	public BlockElevator() {
		super(Material.rock);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return ColorUtils.vanillaBlockToColor(world.getBlockMetadata(x, y, z)).rgb;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int damage) {
		return ColorUtils.vanillaBlockToColor(damage).rgb;
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta != colour) {
			world.setBlockMetadataWithNotify(x, y, z, colour, BlockNotifyFlags.ALL);
			return true;
		}

		return false;
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem();
		if (stack != null) {
			Set<ColorMeta> metas = ColorUtils.stackToColor(stack);
			if (!metas.isEmpty()) {
				ColorMeta meta = CollectionUtils.getRandom(metas);
				world.setBlockMetadataWithNotify(x, y, z, meta.vanillaBlockId, BlockNotifyFlags.ALL);
				return true;
			}
		}
		return false;
	}

	@Override
	public int getColor(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public PlayerRotation getRotation(World world, int x, int y, int z) {
		return PlayerRotation.NONE;
	}

}
