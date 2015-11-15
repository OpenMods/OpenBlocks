package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.Log;
import openmods.block.BlockRotationMode;

import org.apache.logging.log4j.Level;

public class BlockGrave extends OpenBlock {

	public BlockGrave() {
		super(Material.ground);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
		setResistance(2000.0F);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return false;
	}

	private static Level debugLevel() {
		return Config.debugGraves? Level.INFO : Level.DEBUG;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		super.breakBlock(world, x, y, z, block, meta);
		Log.log(debugLevel(), "Grave @ (%d,%d,%d) dimension = %d destroyed", x, y, z, world.provider.dimensionId);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		Log.log(debugLevel(), "Grave @ (%d,%d,%d) dimension = %d destroyed by player %s", x, y, z, world.provider.dimensionId, player);
		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
		super.onBlockDestroyedByExplosion(world, x, y, z, explosion);
		Log.log(debugLevel(), "Grave @ (%d,%d,%d) dimension = %d destroyed by explosion", x, y, z, world.provider.dimensionId);
	}

}
