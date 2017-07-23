package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockBuilderGuide extends BlockGuide {

	public BlockBuilderGuide() {}

	@Override
	protected boolean areButtonsActive(EntityPlayer player) {
		final ItemStack heldItem = player.getHeldItemMainhand();
		return heldItem == null || !(heldItem.getItem() instanceof ItemBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		super.randomDisplayTick(state, world, pos, rand);

		final float x = pos.getX() + 0.5f;
		final float y = pos.getY() + 0.7f;
		final float z = pos.getZ() + 0.5f;

		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
		spawnGreenFlameParticle(x, y, z, EnumParticleTypes.FLAME);
	}

	private static void spawnGreenFlameParticle(float x, float y, float z, EnumParticleTypes particleType) {
		final Minecraft mc = Minecraft.getMinecraft();
		if (mc.gameSettings.particleSetting == 0) {
			final Particle flame = mc.effectRenderer.spawnEffectParticle(particleType.getParticleID(), x, y, z, 0.0D, 0.0D, 0.0D);
			if (flame != null) flame.setRBGColorF(0, 1, 1);
		}
	}
}
