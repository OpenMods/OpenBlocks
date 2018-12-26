package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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

	public MetaPointer(String name) {
		super(name);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (player.isSneaking()) {
			Vec3d posVec = new Vec3d(player.posX, player.posY + 1.62F, player.posZ);
			Vec3d lookVec = player.getLook(1.0f);
			Vec3d targetVec = posVec.addVector(lookVec.x * 10f, lookVec.y * 10f, lookVec.z * 10f);

			RayTraceResult movingObject = world.rayTraceBlocks(posVec, targetVec);

			if (!world.isRemote && movingObject != null && movingObject.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
				final BlockPos targetPos = movingObject.getBlockPos();
				final TileEntity pointedTileEntity = world.getTileEntity(targetPos);
				NBTTagCompound tag = ItemUtils.getItemTag(itemStack);
				if (pointedTileEntity instanceof IPointable) {
					NBTTagCompound linkTag = new NBTTagCompound();
					NbtUtils.store(linkTag, targetPos);
					linkTag.setInteger("Dimension", world.provider.getDimension());
					tag.setTag("lastPoint", linkTag);
					((IPointable)pointedTileEntity).onPointingStart(itemStack, player);
				} else if (tag.hasKey("lastPoint")) {
					NBTTagCompound cannonTag = tag.getCompoundTag("lastPoint");
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
			return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
		}

		return ActionResult.newResult(EnumActionResult.PASS, itemStack);
	}
}
