package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Config;
import openblocks.common.entity.EntityGhost;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.utils.BlockUtils;

public class BlockGrave extends OpenBlock {

	public BlockGrave() {
		super(Config.blockGraveId, Material.anvil); /*
													 * Requires tool and
													 * immovable
													 */
		setupBlock(this, "grave", TileEntityGrave.class);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		if (OpenBlocks.proxy.isServer()) {
			TileEntityGrave tile = getTileEntity(world, x, y, z, TileEntityGrave.class);
			if (tile != null) {
				handleGhostSpawn(tile, world, x, y, z);
			}
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}

	private void updateOnSoilStatus(World worldObj, int x, int y, int z) {
		TileEntityGrave graveEnt = (TileEntityGrave)worldObj.getBlockTileEntity(x, y, z);
		if (graveEnt != null) {
			int id = 0;
			Block block = Block.blocksList[(id = worldObj.getBlockId(x, y - 1, z))];
			if (block != null) {
				graveEnt.onSoil = (block == Block.dirt || block == Block.grass);
			}
		}
	}

	@Override
	public void onNeighborBlockChange(World worldObj, int x, int y, int z, int changedBlockId) {
		super.onNeighborBlockChange(worldObj, x, y, z, changedBlockId);
		updateOnSoilStatus(worldObj, x, y, z);
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int par5) {

	}

	private boolean shouldSpawnGhost(World world) {
		if (world.difficultySetting == 0) return false;
		return OpenBlocks.Config.ghostSpawnProbability > world.rand.nextInt(100);
	}

	private void handleGhostSpawn(TileEntityGrave grave, World world, int x, int y, int z) {
		if (shouldSpawnGhost(world)) {
			EntityGhost ghost = new EntityGhost(world, grave.getUsername(), grave.getLoot());
			ghost.setPositionAndRotation(x, y, z, 0, 0);
			world.spawnEntityInWorld(ghost);
		} else {
			BlockUtils.dropInventory(grave.getLoot(), world, x, y, z);
		}
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		this.setBlockBounds(0, 0, 0, 1f, 0.1f, 1f);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving living, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, living, stack);
		updateOnSoilStatus(world, x, y, z);
		TileEntityGrave grave = (TileEntityGrave)world.getBlockTileEntity(x, y, z);
		if (living instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)living;
			grave.setUsername(player.username);
			grave.setLoot(player.inventory);
		}
	}

}
