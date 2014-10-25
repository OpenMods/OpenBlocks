package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.Config;
import openmods.utils.BlockNotifyFlags;

public class BlockSponge extends OpenBlock {

	private static final int TICK_RATE = 20 * 5;
	private static final Random RANDOM = new Random();

	public BlockSponge() {
		super(Material.sponge);
		setStepSound(soundTypeCloth);
		setTickRandomly(true);
		setHarvestLevel("axe", 1);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		clearupLiquid(world, x, y, z);
	}

	@Override
	public int tickRate(World par1World) {
		return TICK_RATE;
	}

	@Override
	public void onBlockPlacedBy(World world, EntityPlayer player, ItemStack stack, int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ, int meta) {
		clearupLiquid(world, x, y, z);
		world.scheduleBlockUpdate(x, y, z, this, TICK_RATE + RANDOM.nextInt(5));
	}

	private void clearupLiquid(World world, int x, int y, int z) {
		if (world.isRemote) { return; }
		boolean hitLava = false;
		for (int dx = -Config.spongeRange; dx <= Config.spongeRange; dx++) {
			for (int dy = -Config.spongeRange; dy <= Config.spongeRange; dy++) {
				for (int dz = -Config.spongeRange; dz <= Config.spongeRange; dz++) {
					Block block = world.getBlock(x + dx, y + dy, z + dz);
					Material material = block.getMaterial();
					if (material.isLiquid()) {
						hitLava |= material == Material.lava;
						world.setBlockToAir(x + dx, y + dy, z + dz);
					}
				}
			}
		}
		if (hitLava) world.addBlockEvent(x, y, z, this, 0, 0);
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
			world.setBlock(x, y, z, Blocks.fire, 0, BlockNotifyFlags.ALL);
		}
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		clearupLiquid(world, x, y, z);
		world.scheduleBlockUpdate(x, y, z, this, TICK_RATE + RANDOM.nextInt(5));
	}

}
