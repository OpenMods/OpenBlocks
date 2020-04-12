package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockBuilderGuide extends BlockGuide {

	public BlockBuilderGuide(final Block.Properties properties) {
		super(properties);
	}

	@Override
	protected boolean areButtonsActive(Entity entity) {
		if (entity instanceof PlayerEntity) {
			final PlayerEntity player = (PlayerEntity)entity;
			final ItemStack heldItem = player.getHeldItemMainhand();
			return heldItem.isEmpty() || !(heldItem.getItem() instanceof BlockItem);
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		final float x = pos.getX() + 0.5f;
		final float y = pos.getY() + 0.7f;
		final float z = pos.getZ() + 0.5f;

		world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
		spawnGreenFlameParticle(x, y, z);
	}

	private static void spawnGreenFlameParticle(float x, float y, float z) {
		final Minecraft mc = Minecraft.getInstance();
		if (mc.gameSettings.particles == ParticleStatus.ALL) {
			final Particle flame = mc.particles.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
			if (flame != null) { flame.setColor(0, 1, 1); }
		}
	}
}
