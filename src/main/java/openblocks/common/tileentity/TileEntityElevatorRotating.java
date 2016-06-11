package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import openblocks.OpenBlocks.Blocks;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomHarvestDrops;
import openmods.api.ICustomPickItem;
import openmods.api.IPlacerAwareTile;
import openmods.sync.SyncableEnum;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.CollectionUtils;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;

public class TileEntityElevatorRotating extends SyncedTileEntity implements IPlacerAwareTile, IActivateAwareTile, ICustomHarvestDrops, ICustomPickItem {

	private SyncableEnum<ColorUtils.ColorMeta> color;

	public TileEntityElevatorRotating() {
		syncMap.addUpdateListener(createRenderUpdateListener());
	}

	@Override
	protected void createSyncedFields() {
		this.color = new SyncableEnum<ColorUtils.ColorMeta>(ColorUtils.ColorMeta.BLACK);
	}

	public ColorMeta getColor() {
		return color.get();
	}

	public void setColor(ColorMeta next) {
		color.set(next);
		sync();
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public boolean suppressNormalHarvestDrops() {
		return true;
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops) {
		drops.add(createStack());
	}

	@Override
	public ItemStack getPickBlock() {
		return createStack();
	}

	private ItemStack createStack() {
		final int colorMeta = color.get().vanillaBlockId;
		return new ItemStack(Blocks.elevatorRotating, 1, colorMeta);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote) return false;

		ItemStack stack = player.getHeldItem();
		if (stack != null) {
			Set<ColorMeta> metas = ColorUtils.stackToColor(stack);
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
	public void onBlockPlacedBy(EntityLivingBase placer, ItemStack stack) {
		ColorMeta colorMeta = ColorUtils.vanillaBlockToColor(stack.getItemDamage());
		color.set(colorMeta);
	}

}
