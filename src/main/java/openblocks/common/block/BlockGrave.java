package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.Log;
import openmods.block.OpenBlock;

import org.apache.logging.log4j.Level;

public class BlockGrave extends OpenBlock.FourDirections {

	public BlockGrave() {
		super(Material.ground);
		setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
		setResistance(2000.0F);
	}

	// TODO 1.8.9 model all the things
	@Override
	public int getRenderType() {
		return 2; // TESR only
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
	public boolean canEntityDestroy(IBlockAccess world, BlockPos pos, Entity entity) {
		return false;
	}

	private static Level debugLevel() {
		return Config.debugGraves? Level.INFO : Level.DEBUG;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		Log.log(debugLevel(), "Grave @ (%s) dimension = %d destroyed", pos, world.provider.getDimensionId());
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		Log.log(debugLevel(), "Grave @ (%s) dimension = %d destroyed by player %s", pos, world.provider.getDimensionId(), player);
		return super.removedByPlayer(world, pos, player, willHarvest);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
		Log.log(debugLevel(), "Grave @ (%s) dimension = %d destroyed by explosion", pos, world.provider.getDimensionId());
	}

}
