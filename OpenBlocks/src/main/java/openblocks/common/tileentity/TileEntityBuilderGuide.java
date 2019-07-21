package openblocks.common.tileentity;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import openblocks.rpc.IGuideAnimationTrigger;
import openmods.utils.render.GeometryUtils;

public class TileEntityBuilderGuide extends TileEntityGuide implements IGuideAnimationTrigger {

	private static final Random RANDOM = new Random();

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0 || (pass == 1 && shouldRender());
	}

	private int ticks;

	@Override
	public boolean onItemUse(ServerPlayerEntity player, @Nonnull ItemStack heldStack, Direction side, float hitX, float hitY, float hitZ) {
		if (active.get()) {
			final Item heldItem = heldStack.getItem();
			if (heldItem instanceof BlockItem) {
				final BlockItem itemBlock = (BlockItem)heldItem;
				final Block block = itemBlock.getBlock();
				final int blockMeta = itemBlock.getMetadata(heldStack.getItemDamage());

				if (player.capabilities.isCreativeMode && isInFillMode()) {
					creativeReplaceBlocks(player, heldStack, block, blockMeta, side, hitX, hitY, hitZ);
					return true;
				} else {
					return survivalPlaceBlocks(player, heldStack, block, blockMeta, side, hitX, hitY, hitZ);
				}
			}
		}

		return super.onItemUse(player, heldStack, side, hitX, hitY, hitZ);
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) ticks++;
	}

	private void creativeReplaceBlocks(ServerPlayerEntity player, ItemStack heldStack, Block block, int blockMeta, Direction side, float hitX, float hitY, float hitZ) {
		// TODO verify
		for (BlockPos coord : getShapeSafe().getCoords()) {
			final BlockPos clickPos = pos.add(coord);
			final BlockState state = block.getStateForPlacement(world, clickPos, side, hitX, hitY, hitZ, blockMeta, player, Hand.MAIN_HAND);
			world.setBlockState(clickPos, state);
		}
	}

	@Override
	protected boolean canAddCoord(int x, int y, int z) {
		// create safe space around builder, so it's always accesible
		return Math.abs(x) > 1 || Math.abs(y) > 1 || Math.abs(z) > 1;
	}

	private boolean survivalPlaceBlocks(ServerPlayerEntity player, @Nonnull ItemStack heldItem, Block block, int blockMeta, Direction side, float hitX, float hitY, float hitZ) {
		for (BlockPos relCoord : getShapeSafe().getCoords()) {
			BlockPos absPos = pos.add(relCoord);
			if (world.isBlockLoaded(absPos) && world.isAirBlock(absPos) && absPos.getY() >= 0 && absPos.getY() < 256) {
				final ActionResultType placeResult = player.interactionManager.processRightClickBlock(player, world, heldItem, Hand.MAIN_HAND, absPos, side, hitX, hitY, hitZ);

				if (placeResult == ActionResultType.SUCCESS) {
					final int stateId = Block.getStateId(world.getBlockState(absPos));
					createServerRpcProxy(IGuideAnimationTrigger.class).trigger(absPos, stateId);
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
	public void trigger(BlockPos pos, final int stateId) {
		GeometryUtils.line3D(this.pos, pos, (x, y, z) -> {
			final double dx = x + 0.5;
			final double dy = y + 0.5;
			final double dz = z + 0.5;
			for (int i = 0; i < 5; i++) {
				double px = dx + 0.3 * RANDOM.nextFloat();
				double py = dy + 0.3 * RANDOM.nextFloat();
				double pz = dz + 0.3 * RANDOM.nextFloat();
				world.spawnParticle(EnumParticleTypes.PORTAL, px, py, pz, 0, 0, 0);
				world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px, py, pz, 0, 0, 0, stateId);
			}
		});
	}
}
