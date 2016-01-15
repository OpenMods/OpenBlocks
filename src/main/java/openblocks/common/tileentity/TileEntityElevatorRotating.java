package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import openblocks.OpenBlocks.Blocks;
import openmods.api.*;
import openmods.colors.ColorMeta;
import openmods.sync.SyncableEnum;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.CollectionUtils;

public class TileEntityElevatorRotating extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile, ICustomHarvestDrops, ICustomPickItem {

	private SyncableEnum<ColorMeta> color;

	public TileEntityElevatorRotating() {
		syncMap.addUpdateListener(createRenderUpdateListener());
	}

	@Override
	protected void createSyncedFields() {
		this.color = new SyncableEnum<ColorMeta>(ColorMeta.BLACK);
	}

	public ColorMeta getColor() {
		return color.get();
	}

	public void setColor(ColorMeta next) {
		color.set(next);
		sync();
	}

	@Override
	public boolean suppressBlockHarvestDrops() {
		return true;
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops, int fortune, boolean isSilkTouch) {
		drops.add(createStack());
	}

	@Override
	public ItemStack getPickBlock(EntityPlayer player) {
		return createStack();
	}

	private ItemStack createStack() {
		final int colorMeta = color.get().vanillaBlockId;
		return new ItemStack(Blocks.elevatorRotating, 1, colorMeta);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote) return false;

		ItemStack stack = player.getHeldItem();
		if (stack != null) {
			Set<ColorMeta> metas = ColorMeta.fromStack(stack);
			if (!metas.isEmpty()) {
				ColorMeta meta = CollectionUtils.getRandom(metas);
				color.set(meta);
				sync();
				return true;
			}
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
		// TODO 1.8.9 verify colors
		ColorMeta colorMeta = ColorMeta.fromBlockMeta(stack.getItemDamage());
		color.set(colorMeta);
	}

}
