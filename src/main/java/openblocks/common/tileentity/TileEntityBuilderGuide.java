package openblocks.common.tileentity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import openblocks.rpc.IGuideAnimationTrigger;
import openmods.shapes.IShapeable;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.Coord;
import openmods.utils.render.GeometryUtils;

public class TileEntityBuilderGuide extends TileEntityGuide implements IGuideAnimationTrigger {

	private static final Random RANDOM = new Random();

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0 || (pass == 1 && shouldRender());
	}

	private int ticks;

	@Override
	public boolean onItemUse(EntityPlayerMP player, ItemStack heldStack, int side, float hitX, float hitY, float hitZ) {
		if (active.get()) {
			final Item heldItem = heldStack.getItem();
			if (heldItem instanceof ItemBlock) {
				final ItemBlock itemBlock = (ItemBlock)heldItem;
				final Block block = itemBlock.field_150939_a;
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
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) ticks++;
	}

	private void creativeReplaceBlocks(Block block, int blockMeta) {
		for (Coord coord : getShapeSafe())
			worldObj.setBlock(xCoord + coord.x, yCoord + coord.y, zCoord + coord.z, block, blockMeta, BlockNotifyFlags.ALL);
	}

	private boolean survivalPlaceBlocks(EntityPlayerMP player, ItemStack heldItem, Block block, int blockMeta, int side, float hitX, float hitY, float hitZ) {
		for (Coord relCoord : getShapeSafe()) {
			final int absX = relCoord.x + xCoord;
			final int absY = relCoord.y + yCoord;
			final int absZ = relCoord.z + zCoord;
			if (worldObj.blockExists(absX, absY, absZ) && worldObj.isAirBlock(absX, absY, absZ)) {
				boolean hasPlaced = player.theItemInWorldManager.activateBlockOrUseItem(player, worldObj, heldItem, absX, absY, absZ, side, hitX, hitY, hitZ);
				if (hasPlaced) {
					final String particle = "blockdust_" + Block.getIdFromBlock(block) + "_" + blockMeta;
					createServerRpcProxy(IGuideAnimationTrigger.class).trigger(absX, absY, absZ, particle);
					return true;
				}
			}

		}

		return false;
	}

	private boolean isInFillMode() {
		return worldObj.getBlock(xCoord, yCoord + 1, zCoord) == Blocks.obsidian;
	}

	public float getTicks() {
		return ticks;
	}

	@Override
	public void trigger(int x, int y, int z, final String particle) {
		GeometryUtils.line3D(xCoord, yCoord, zCoord, x, y, z, new IShapeable() {
			@Override
			public void setBlock(int x, int y, int z) {
				final double dx = x + 0.5;
				final double dy = y + 0.5;
				final double dz = z + 0.5;
				for (int i = 0; i < 5; i++) {
					double px = dx + 0.3 * RANDOM.nextFloat();
					double py = dy + 0.3 * RANDOM.nextFloat();
					double pz = dz + 0.3 * RANDOM.nextFloat();
					worldObj.spawnParticle("portal", px, py, pz, 0, 0, 0);
					worldObj.spawnParticle(particle, px, py, pz, 0, 0, 0);
				}
			}
		});
	}
}
