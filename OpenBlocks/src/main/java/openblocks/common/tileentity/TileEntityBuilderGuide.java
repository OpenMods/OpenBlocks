package openblocks.common.tileentity;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import openblocks.OpenBlocks;
import openblocks.rpc.IGuideAnimationTrigger;
import openmods.utils.render.GeometryUtils;

public class TileEntityBuilderGuide extends TileEntityGuide implements IGuideAnimationTrigger {
	private static final Random RANDOM = new Random();

	public TileEntityBuilderGuide() {
		super(OpenBlocks.TileEntities.builderGuide);
	}

	private int ticks;

	@Override
	public boolean onItemUse(ServerPlayerEntity player, ItemStack heldStack, final BlockRayTraceResult hit) {
		if (active.get()) {
			final Item heldItem = heldStack.getItem();
			if (heldItem instanceof BlockItem) {
				final BlockItem itemBlock = (BlockItem)heldItem;
				final Block block = itemBlock.getBlock();

				if (player.abilities.isCreativeMode && isInFillMode()) {
					creativeReplaceBlocks(block);
					return true;
				} else {
					return survivalPlaceBlocks(player, heldStack, hit);
				}
			}
		}

		return super.onItemUse(player, heldStack, hit);
	}

	@Override
	public void tick() {
		super.tick();
		if (world.isRemote) {
			ticks++;
		}
	}

	private void creativeReplaceBlocks(final Block block) {
		// TODO verify
		for (BlockPos coord : getShapeSafe().getCoords()) {
			final BlockPos clickPos = pos.add(coord);
			world.setBlockState(clickPos, block.getDefaultState());
		}
	}

	@Override
	protected boolean canAddCoord(int x, int y, int z) {
		// create safe space around builder, so it's always accesible
		return Math.abs(x) > 1 || Math.abs(y) > 1 || Math.abs(z) > 1;
	}

	private boolean survivalPlaceBlocks(ServerPlayerEntity player, ItemStack heldItem, final BlockRayTraceResult hit) {
		for (BlockPos relCoord : getShapeSafe().getCoords()) {
			BlockPos absPos = pos.add(relCoord);
			if (world.isBlockLoaded(absPos) && world.isAirBlock(absPos) && absPos.getY() >= 0 && absPos.getY() < 256) {
				final BlockRayTraceResult fakeHit = new BlockRayTraceResult(hit.getHitVec(), hit.getFace(), absPos, hit.isInside());
				final ActionResultType placeResult = player.interactionManager.func_219441_a(player, world, heldItem, Hand.MAIN_HAND, fakeHit);

				if (placeResult.isSuccessOrConsume()) {
					createServerRpcProxy(IGuideAnimationTrigger.class).trigger(absPos, world.getBlockState(absPos));
					return true;
				}
			}

		}

		return false;
	}

	private boolean isInFillMode() {
		return world.getBlockState(pos.up()).getBlock() == Blocks.OBSIDIAN;
	}

	public float getTicks() {
		return ticks;
	}

	@Override
	public void trigger(BlockPos pos, final BlockState state) {
		GeometryUtils.line3D(this.pos, pos, (x, y, z) -> {
			final double dx = x + 0.5;
			final double dy = y + 0.5;
			final double dz = z + 0.5;
			for (int i = 0; i < 5; i++) {
				final double px = dx + 0.3 * RANDOM.nextFloat();
				final double py = dy + 0.3 * RANDOM.nextFloat();
				final double pz = dz + 0.3 * RANDOM.nextFloat();
				world.addParticle(ParticleTypes.PORTAL, px, py, pz, 0, 0, 0);
				world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, state), px, py, pz, 0, 0, 0);
			}
		});
	}
}
