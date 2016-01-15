package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import openblocks.api.IPointable;
import openmods.utils.ItemUtils;
import openmods.utils.NbtUtils;

/**
 * Pointer item is used for.. pointing
 *
 * @author Mikee
 *
 */
public class MetaPointer extends MetaGeneric {

	public MetaPointer(String name, Object... recipes) {
		super(name, recipes);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, EntityPlayer player, World world) {
		if (!world.isRemote) {
			Vec3 posVec = new Vec3(player.posX, player.posY + 1.62F, player.posZ);
			Vec3 lookVec = player.getLook(1.0f);
			Vec3 targetVec = posVec.addVector(lookVec.xCoord * 10f, lookVec.yCoord * 10f, lookVec.zCoord * 10f);

			MovingObjectPosition movingObject = world.rayTraceBlocks(posVec, targetVec);
			NBTTagCompound tag = ItemUtils.getItemTag(itemStack);

			if (movingObject != null && movingObject.typeOfHit.equals(MovingObjectType.BLOCK)) {
				final BlockPos targetPos = movingObject.getBlockPos();
				final TileEntity pointedTileEntity = world.getTileEntity(targetPos);
				if (pointedTileEntity instanceof IPointable) {
					NBTTagCompound linkTag = new NBTTagCompound();
					NbtUtils.store(linkTag, targetPos);
					linkTag.setInteger("Dimension", world.provider.getDimensionId());
					tag.setTag("lastPoint", linkTag);
					((IPointable)pointedTileEntity).onPointingStart(itemStack, player);
				} else if (tag.hasKey("lastPoint")) {
					NBTTagCompound cannonTag = tag.getCompoundTag("lastPoint");
					BlockPos sourcePos = NbtUtils.readBlockPos(cannonTag);
					int d = cannonTag.getInteger("Dimension");
					if (world.provider.getDimensionId() == d && world.isBlockLoaded(sourcePos)) {
						TileEntity tile = world.getTileEntity(sourcePos);
						if (tile instanceof IPointable) {
							((IPointable)tile).onPointingEnd(itemStack, player, targetPos);
						}
					}
				}
			}

		}
		return itemStack;
	}
}
