package openblocks.common.entity.ai;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import openmods.OpenMods;
import openmods.utils.BlockProperties;
import openmods.utils.Coord;
import openmods.utils.OpenModsFakePlayer;

public class EntityAIBreakBlock extends EntityAIBase {

	private EntityLiving entity;
	private PathNavigate pathFinder;
	private Coord blockCoord;
	private boolean validCoord = false;
	private int tickOffset = 0;
	private Random rand;

	public EntityAIBreakBlock(EntityLiving minime) {
		this.entity = minime;
		this.pathFinder = minime.getNavigator();
		setMutexBits(3);
		rand = new Random(minime.entityId);
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
				blockCoord = new Coord(
						(int)(x + entity.posX),
						(int)(y + entity.posY),
						(int)(z + entity.posZ)
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
			pathFinder.tryMoveToXYZ(blockCoord.x, blockCoord.y, blockCoord.z, 1f);
		}
	}

	@Override
	public void updateTask() {
		super.updateTask();
		World world = entity.worldObj;
		if (!world.isRemote && blockCoord != null && canHarvestBlock(blockCoord)) {
			if (entity.getDistance(0.5 + blockCoord.x, 0.5 + blockCoord.y, 0.5 + blockCoord.z) < 1.0) {

				EntityPlayer fakePlayer = new OpenModsFakePlayer(world);
				fakePlayer.inventory.currentItem = 0;

				Block block = BlockProperties.getBlock(blockCoord, world);
				int meta = BlockProperties.getBlockMetadata(blockCoord, world);

				BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(
						blockCoord.x,
						blockCoord.y,
						blockCoord.z,
						entity.worldObj,
						block,
						meta,
						fakePlayer);

				if (MinecraftForge.EVENT_BUS.post(event)) return;

				if (ForgeHooks.canHarvestBlock(block, fakePlayer, meta)) {
					block.harvestBlock(
							world,
							fakePlayer,
							blockCoord.x,
							blockCoord.y,
							blockCoord.z, meta);
					world.setBlockToAir(blockCoord.x, blockCoord.y, blockCoord.z);
				}
				fakePlayer.setDead();
				blockCoord = null;
			}
		}
	}

	public boolean canHarvestBlock(Coord coord) {
		return BlockProperties.isFlower(coord, entity.worldObj) ||
				BlockProperties.getBlock(coord, entity.worldObj) == Block.torchWood;
	}
}
