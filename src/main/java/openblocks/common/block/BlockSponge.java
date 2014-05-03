package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openmods.utils.BlockNotifyFlags;

public class BlockSponge extends OpenBlock {

	private static final int TICK_RATE = 20 * 5;
	private static final Random RANDOM = new Random();

	public BlockSponge() {
		super(Config.blockSpongeId, Material.sponge);
		setStepSound(soundClothFootstep);
		setTickRandomly(true);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		clearupLiquid(world, x, y, z);
	}

	@Override
	public int tickRate(World par1World) {
		return TICK_RATE;
	}

	@Override
	public void onBlockPlacedBy(World world, EntityPlayer player, ItemStack stack, int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ, int meta) {
		clearupLiquid(world, x, y, z);
		world.scheduleBlockUpdate(x, y, z, blockID, TICK_RATE + RANDOM.nextInt(5));
	}

	private void clearupLiquid(World world, int x, int y, int z) {
		if (world.isRemote) { return; }
		boolean hitLava = false;
		for (int dx = -Config.spongeRange; dx <= Config.spongeRange; dx++) {
			for (int dy = -Config.spongeRange; dy <= Config.spongeRange; dy++) {
				for (int dz = -Config.spongeRange; dz <= Config.spongeRange; dz++) {
					Material material = world.getBlockMaterial(x + dx, y + dy, z + dz);
					if (material.isLiquid()) {
						hitLava |= material == Material.lava;
						world.setBlock(x + dx, y + dy, z + dz, 0, 0, BlockNotifyFlags.SEND_TO_CLIENTS);
					}
				}
			}
		}
		if (hitLava) world.addBlockEvent(x, y, z, blockID, 0, 0);
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventParam) {
		if (world.isRemote) {
			for (int i = 0; i < 20; i++) {
				double px = x + RANDOM.nextDouble() * 0.1;
				double py = y + 1.0 + RANDOM.nextDouble();
				double pz = z + RANDOM.nextDouble();
				world.spawnParticle("largesmoke", px, py, pz, 0.0D, 0.0D, 0.0D);
			}
		} else {
			world.setBlock(x, y, z, Block.fire.blockID, 0, BlockNotifyFlags.ALL);
		}
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		clearupLiquid(world, x, y, z);
		world.scheduleBlockUpdate(x, y, z, blockID, TICK_RATE + RANDOM.nextInt(5));
	}

}
