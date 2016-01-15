package openblocks.common.tileentity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import openblocks.rpc.IGuideAnimationTrigger;
import openmods.shapes.IShapeable;
import openmods.utils.render.GeometryUtils;

public class TileEntityBuilderGuide extends TileEntityGuide implements IGuideAnimationTrigger {

	private static final Random RANDOM = new Random();

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0 || (pass == 1 && shouldRender());
	}

	private int ticks;

	@Override
	public boolean onItemUse(EntityPlayerMP player, ItemStack heldStack, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (active.get()) {
			final Item heldItem = heldStack.getItem();
			if (heldItem instanceof ItemBlock) {
				final ItemBlock itemBlock = (ItemBlock)heldItem;
				final Block block = itemBlock.getBlock();
				final int blockMeta = itemBlock.getMetadata(heldStack.getItemDamage());

				if (player.capabilities.isCreativeMode && isInFillMode()) {
					creativeReplaceBlocks(block, blockMeta);
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
		if (worldObj.isRemote) ticks++;
	}

	private void creativeReplaceBlocks(Block block, int blockMeta) {
		final IBlockState state = block.getStateFromMeta(blockMeta);
		for (BlockPos coord : getShapeSafe())
			worldObj.setBlockState(pos.add(coord), state);
	}

	@Override
	protected boolean canAddCoord(int x, int y, int z) {
		// create safe space around builder, so it's always accesible
		return Math.abs(x) > 1 || Math.abs(y) > 1 || Math.abs(z) > 1;
	}

	private boolean survivalPlaceBlocks(EntityPlayerMP player, ItemStack heldItem, Block block, int blockMeta, EnumFacing side, float hitX, float hitY, float hitZ) {
		for (BlockPos relCoord : getShapeSafe()) {
			BlockPos absPos = pos.add(relCoord);
			if (worldObj.isBlockLoaded(absPos) && worldObj.isAirBlock(absPos)) {
				boolean hasPlaced = player.theItemInWorldManager.activateBlockOrUseItem(player, worldObj, heldItem, absPos, side, hitX, hitY, hitZ);
				if (hasPlaced) {
					final int stateId = Block.getStateId(block.getStateFromMeta(blockMeta));
					createServerRpcProxy(IGuideAnimationTrigger.class).trigger(absPos, stateId);
					return true;
				}
			}

		}

		return false;
	}

	private boolean isInFillMode() {
		return worldObj.getBlockState(pos.up()).getBlock() == Blocks.obsidian;
	}

	public float getTicks() {
		return ticks;
	}

	@Override
	public void trigger(BlockPos pos, final int stateId) {
		GeometryUtils.line3D(this.pos, pos, new IShapeable() {
			@Override
			public void setBlock(int x, int y, int z) {
				final double dx = x + 0.5;
				final double dy = y + 0.5;
				final double dz = z + 0.5;
				for (int i = 0; i < 5; i++) {
					double px = dx + 0.3 * RANDOM.nextFloat();
					double py = dy + 0.3 * RANDOM.nextFloat();
					double pz = dz + 0.3 * RANDOM.nextFloat();
					worldObj.spawnParticle(EnumParticleTypes.PORTAL, px, py, pz, 0, 0, 0);
					worldObj.spawnParticle(EnumParticleTypes.BLOCK_DUST, px, py, pz, 0, 0, 0, stateId);
				}
			}
		});
	}
}
