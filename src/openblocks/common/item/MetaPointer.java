package openblocks.common.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import openblocks.api.IPointable;

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
	public void registerIcons(IconRegister register) {
		registerIcon(register, "pointer");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, EntityPlayer player, World world) {
		if (!world.isRemote) {
			Vec3 posVec = player.worldObj.getWorldVec3Pool().getVecFromPool(player.posX, player.posY + 1.62F, player.posZ);
			Vec3 lookVec = player.getLook(1.0f);
			Vec3 targetVec = posVec.addVector(lookVec.xCoord * 10f, lookVec.yCoord * 10f, lookVec.zCoord * 10f);
			MovingObjectPosition movingObject = world.clip(posVec, targetVec);
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
				itemStack.setTagCompound(tag);
			}

			if (movingObject != null && movingObject.typeOfHit.equals(EnumMovingObjectType.TILE)) {
				if (world.getBlockTileEntity(movingObject.blockX, movingObject.blockY, movingObject.blockZ) instanceof IPointable) {
					NBTTagCompound linkTag = new NBTTagCompound();
					linkTag.setInteger("x", movingObject.blockX);
					linkTag.setInteger("y", movingObject.blockY);
					linkTag.setInteger("z", movingObject.blockZ);
					linkTag.setInteger("d", world.provider.dimensionId);
					tag.setCompoundTag("lastPoint", linkTag);
					player.sendChatToPlayer(ChatMessageComponent.createFromText("Selected block for linking"));
				} else if (tag.hasKey("lastPoint")) {
					NBTTagCompound cannonTag = tag.getCompoundTag("lastPoint");
					int x = cannonTag.getInteger("x");
					int y = cannonTag.getInteger("y");
					int z = cannonTag.getInteger("z");
					int d = cannonTag.getInteger("d");
					if (world.provider.dimensionId == d && world.blockExists(x, y, z)) {
						TileEntity tile = world.getBlockTileEntity(x, y, z);
						if (tile instanceof IPointable) {
							((IPointable)tile).onPoint(itemStack, player, movingObject.blockX, movingObject.blockY, movingObject.blockZ);
							tag.removeTag("lastPoint");
						}
					}
				}
			}

		}
		return itemStack;
	}
}
