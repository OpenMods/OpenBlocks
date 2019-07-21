package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class SkeletonBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		final BlockPos pos = tile.getPos();
		double pX = pos.getX() + 0.5;
		final int pZ = pos.getY() + 1;
		double pY = pos.getZ() + 0.5;
		final World world = tile.getWorld();

		AbstractArrowEntity entityarrow = new ArrowEntity(world, pX, pZ, pY);
		entityarrow.setDamage(0.1);
		entityarrow.shoot(world.rand.nextInt(10) - 5, 40, world.rand.nextInt(10) - 5, 1.0f, 6.0f);
		world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + 0.5F);
		world.spawnEntity(entityarrow);

		return 0;
	}
}
