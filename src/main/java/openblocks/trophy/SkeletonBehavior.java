package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class SkeletonBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		final BlockPos pos = tile.getPos();
		double pX = pos.getX() + 0.5;
		final int pZ = pos.getY() + 1;
		double pY = pos.getZ() + 0.5;
		final World world = tile.getWorld();

		EntityArrow entityarrow = new EntityTippedArrow(world, pX, pZ, pY);
		entityarrow.setDamage(0.1);
		entityarrow.setThrowableHeading(world.rand.nextInt(10) - 5, 40, world.rand.nextInt(10) - 5, 1.0f, 6.0f);
		world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + 0.5F);
		world.spawnEntity(entityarrow);

		return 0;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
