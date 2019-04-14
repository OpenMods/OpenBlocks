package openblocks.common.tileentity;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
	public boolean onItemUse(EntityPlayerMP player, @Nonnull ItemStack heldStack, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (active.get()) {
			final Item heldItem = heldStack.getItem();
			if (heldItem instanceof ItemBlock) {
				final ItemBlock itemBlock = (ItemBlock)heldItem;
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

	private void creativeReplaceBlocks(EntityPlayerMP player, ItemStack heldStack, Block block, int blockMeta, EnumFacing side, float hitX, float hitY, float hitZ) {
		// TODO verify
		for (BlockPos coord : getShapeSafe().getCoords()) {
			final BlockPos clickPos = pos.add(coord);
			final IBlockState state = block.getStateForPlacement(world, clickPos, side, hitX, hitY, hitZ, blockMeta, player, EnumHand.MAIN_HAND);
			world.setBlockState(clickPos, state);
		}
	}

	@Override
	protected boolean canAddCoord(int x, int y, int z) {
		// create safe space around builder, so it's always accesible
		return Math.abs(x) > 1 || Math.abs(y) > 1 || Math.abs(z) > 1;
	}

	private boolean survivalPlaceBlocks(EntityPlayerMP player, @Nonnull ItemStack heldItem, Block block, int blockMeta, EnumFacing side, float hitX, float hitY, float hitZ) {
		for (BlockPos relCoord : getShapeSafe().getCoords()) {
			BlockPos absPos = pos.add(relCoord);
			if (world.isBlockLoaded(absPos) && world.isAirBlock(absPos) && absPos.getY() >= 0 && absPos.getY() < 256) {
				final EnumActionResult placeResult = player.interactionManager.processRightClickBlock(player, world, heldItem, EnumHand.MAIN_HAND, absPos, side, hitX, hitY, hitZ);

				if (placeResult == EnumActionResult.SUCCESS) {
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
