package openblocks.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openblocks.api.IPointable;
import openmods.utils.ItemUtils;

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
	public void registerIcons(IIconRegister register) {
		registerIcon(register, "pointer");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, EntityPlayer player, World world) {
		if (!world.isRemote) {
			Vec3 posVec = Vec3.createVectorHelper(player.posX, player.posY + 1.62F, player.posZ);
			Vec3 lookVec = player.getLook(1.0f);
			Vec3 targetVec = posVec.addVector(lookVec.xCoord * 10f, lookVec.yCoord * 10f, lookVec.zCoord * 10f);

			MovingObjectPosition movingObject = world.rayTraceBlocks(posVec, targetVec);
			NBTTagCompound tag = ItemUtils.getItemTag(itemStack);

			if (movingObject != null && movingObject.typeOfHit.equals(MovingObjectType.BLOCK)) {
				final TileEntity pointedTileEntity = world.getTileEntity(movingObject.blockX, movingObject.blockY, movingObject.blockZ);
				if (pointedTileEntity instanceof IPointable) {
					NBTTagCompound linkTag = new NBTTagCompound();
					linkTag.setInteger("x", movingObject.blockX);
					linkTag.setInteger("y", movingObject.blockY);
					linkTag.setInteger("z", movingObject.blockZ);
					linkTag.setInteger("d", world.provider.dimensionId);
					tag.setTag("lastPoint", linkTag);
					((IPointable)pointedTileEntity).onPointingStart(itemStack, player);
				} else if (tag.hasKey("lastPoint")) {
					NBTTagCompound cannonTag = tag.getCompoundTag("lastPoint");
					int x = cannonTag.getInteger("x");
					int y = cannonTag.getInteger("y");
					int z = cannonTag.getInteger("z");
					int d = cannonTag.getInteger("d");
					if (world.provider.dimensionId == d && world.blockExists(x, y, z)) {
						TileEntity tile = world.getTileEntity(x, y, z);
						if (tile instanceof IPointable) {
							((IPointable)tile).onPointingEnd(itemStack, player, movingObject.blockX, movingObject.blockY, movingObject.blockZ);
						}
					}
				}
			}

		}
		return itemStack;
	}
}
