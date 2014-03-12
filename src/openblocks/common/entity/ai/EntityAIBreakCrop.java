package openblocks.common.entity.ai;

import java.util.List;
import java.util.Random;

import openblocks.common.entity.EntityLuggage;
import openblocks.common.entity.EntityMiniMe;
import openmods.OpenMods;
import openmods.utils.Coord;
import openmods.utils.InventoryUtils;
import openmods.utils.OpenModsFakePlayer;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

public class EntityAIBreakCrop extends EntityAIBase {

	private EntityMiniMe minime;
	private PathNavigate pathFinder;
	private Coord blockCoord;
	private boolean validCoord = false;
	private int tickOffset = 0;
	private Random rand;
	
	public EntityAIBreakCrop(EntityMiniMe minime) {
		this.minime = minime;
		this.pathFinder = minime.getNavigator();
		setMutexBits(3);
		rand = new Random(minime.entityId);
		tickOffset = rand.nextInt(10);
		blockCoord = new Coord();
	}
	
	@Override
	public boolean shouldExecute() {
		if (!pathFinder.noPath()) return false;
		boolean hasTicked = (OpenMods.proxy.getTicks(minime.worldObj) + tickOffset) % 10 == 0;
		if (hasTicked && minime.worldObj != null && !minime.worldObj.isRemote) {
			for (int i = 0; i < 10; i++) {
				int x = rand.nextInt(12) - 6;
				int y = rand.nextInt(3) - 1;
				int z = rand.nextInt(12) - 6;
				blockCoord.x = (int)(x + minime.posX);
				blockCoord.y = (int)(y + minime.posY);
				blockCoord.z = (int)(z + minime.posZ);
				validCoord = canHarvestBlock(blockCoord);
				if (validCoord) {
					return true;
				}
			}
		}		
		return false;
	}

	@Override
	public void resetTask() {
		pathFinder.clearPathEntity();
	}

	@Override
	public boolean continueExecuting() {
		return minime.isEntityAlive() &&
				!pathFinder.noPath() &&
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
		World world = minime.worldObj;
		if (!world.isRemote && canHarvestBlock(blockCoord)) {
			if (minime.getDistance(0.5 + blockCoord.x, 0.5 + blockCoord.y, 0.5 + blockCoord.z) < 1.0) {
				
				EntityPlayer fakePlayer = new OpenModsFakePlayer(world);
				fakePlayer.inventory.currentItem = 0;
				
				Block block = Block.blocksList[blockCoord.getBlockID(minime.worldObj)];
				int meta = blockCoord.getBlockMetadata(world);
				
				BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(
						blockCoord.x,
						blockCoord.y,
						blockCoord.z,
						minime.worldObj,
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
			}
		}
	}

	private boolean canHarvestBlock(Coord coord) {
		return coord.isFlower(minime.worldObj);
	}
}
