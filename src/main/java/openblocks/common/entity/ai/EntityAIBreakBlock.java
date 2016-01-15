package openblocks.common.entity.ai;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import openmods.OpenMods;
import openmods.fakeplayer.BreakBlockAction;
import openmods.fakeplayer.FakePlayerPool;

public class EntityAIBreakBlock extends EntityAIBase {

	private EntityLiving entity;
	private PathNavigate pathFinder;
	private BlockPos blockCoord;
	private int tickOffset = 0;
	private Random rand;

	public EntityAIBreakBlock(EntityLiving minime) {
		this.entity = minime;
		this.pathFinder = minime.getNavigator();
		setMutexBits(3);
		rand = new Random(minime.getEntityId());
		tickOffset = rand.nextInt(10);
	}

	@Override
	public boolean shouldExecute() {
		if (!pathFinder.noPath()) return false;
		boolean hasTicked = (OpenMods.proxy.getTicks(entity.worldObj) + tickOffset) % 4 == 0;
		if (hasTicked && entity.worldObj != null && !entity.worldObj.isRemote) {
			for (int i = 0; i < 20; i++) {
				int x = rand.nextInt(16) - 8;
				int y = rand.nextInt(3) - 1;
				int z = rand.nextInt(16) - 8;
				blockCoord = new BlockPos(
						x + entity.posX,
						y + entity.posY,
						z + entity.posZ
						);
				if (canHarvestBlock(blockCoord)) { return true; }
				blockCoord = null;
			}
		}
		return false;
	}

	@Override
	public void resetTask() {
		pathFinder.clearPathEntity();
		blockCoord = null;
	}

	@Override
	public boolean continueExecuting() {
		return entity.isEntityAlive() &&
				!pathFinder.noPath() &&
				blockCoord != null &&
				canHarvestBlock(blockCoord);
	}

	@Override
	public void startExecuting() {
		if (blockCoord != null) {
			final PathEntity pathentity = pathFinder.getPathToPos(blockCoord);
			pathFinder.setPath(pathentity, 1.0);
		}
	}

	@Override
	public void updateTask() {
		super.updateTask();
		final World world = entity.worldObj;
		if ((world instanceof WorldServer) && blockCoord != null && canHarvestBlock(blockCoord)) {
			if (entity.getDistanceSqToCenter(blockCoord) < 1.0) {
				FakePlayerPool.instance.executeOnPlayer((WorldServer)world, new BreakBlockAction(world, blockCoord).setStackToUse(null));
				blockCoord = null;
			}
		}
	}

	public boolean canHarvestBlock(BlockPos coord) {
		final Block block = entity.worldObj.getBlockState(coord).getBlock();
		return block instanceof BlockFlower ||
				block == Blocks.torch;
	}
}
