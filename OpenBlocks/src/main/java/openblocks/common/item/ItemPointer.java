package openblocks.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import openblocks.api.IPointable;
import openmods.utils.ItemUtils;
import openmods.utils.NbtUtils;

public class ItemPointer extends Item {

	public ItemPointer() {
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		final ItemStack itemStack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			Vec3d posVec = new Vec3d(player.posX, player.posY + 1.62F, player.posZ);
			Vec3d lookVec = player.getLook(1.0f);
			Vec3d targetVec = posVec.addVector(lookVec.x * 10f, lookVec.y * 10f, lookVec.z * 10f);

			RayTraceResult movingObject = world.rayTraceBlocks(posVec, targetVec);

			if (!world.isRemote && movingObject != null && movingObject.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
				final BlockPos targetPos = movingObject.getBlockPos();
				final TileEntity pointedTileEntity = world.getTileEntity(targetPos);
				CompoundNBT tag = ItemUtils.getItemTag(itemStack);
				if (pointedTileEntity instanceof IPointable) {
					CompoundNBT linkTag = new CompoundNBT();
					NbtUtils.store(linkTag, targetPos);
					linkTag.setInteger("Dimension", world.provider.getDimension());
					tag.setTag("lastPoint", linkTag);
					((IPointable)pointedTileEntity).onPointingStart(itemStack, player);
				} else if (tag.hasKey("lastPoint")) {
					CompoundNBT cannonTag = tag.getCompoundTag("lastPoint");
					BlockPos sourcePos = NbtUtils.readBlockPos(cannonTag);
					int d = cannonTag.getInteger("Dimension");
					if (world.provider.getDimension() == d && world.isBlockLoaded(sourcePos)) {
						TileEntity tile = world.getTileEntity(sourcePos);
						if (tile instanceof IPointable) {
							((IPointable)tile).onPointingEnd(itemStack, player, targetPos);
						}
					}
				}
			}
			return ActionResult.newResult(ActionResultType.SUCCESS, itemStack);
		}

		return ActionResult.newResult(ActionResultType.PASS, itemStack);
	}
}
