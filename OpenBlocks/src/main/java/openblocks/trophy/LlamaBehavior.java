package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class LlamaBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		final BlockPos pos = tile.getPos();
		final double pX = pos.getX() + 0.5;
		final double pY = pos.getY() + 1;
		final double pZ = pos.getZ() + 0.5;
		final World world = tile.getWorld();

		LlamaSpitEntity entityllamaspit = new LlamaSpitEntity(player.world);
		entityllamaspit.setPosition(pX, pY, pZ);

		double dX = player.posX - pX;
		double dy = player.getEntityBoundingBox().minY + player.height / 3.0F - entityllamaspit.posY;
		double dZ = player.posZ - pZ;
		float f = MathHelper.sqrt(dX * dX + dZ * dZ) * 0.2F;
		entityllamaspit.shoot(dX, dy + f, dZ, 1.5F, 10.0F);
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_LLAMA_SPIT, SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F);
		world.spawnEntity(entityllamaspit);

		return 0;
	}
}
