package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import openblocks.OpenBlocks;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomHarvestDrops;
import openmods.api.ICustomPickItem;
import openmods.api.IPlaceAwareTile;
import openmods.colors.ColorMeta;
import openmods.sync.SyncMap;
import openmods.sync.SyncableEnum;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.CollectionUtils;

public class TileEntityElevatorRotating extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile, ICustomHarvestDrops, ICustomPickItem {

	private SyncableEnum<ColorMeta> color;

	public TileEntityElevatorRotating() {}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addUpdateListener(createRenderUpdateListener());
	}

	@Override
	protected void createSyncedFields() {
		this.color = new SyncableEnum<>(ColorMeta.BLACK);
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
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops, IBlockState blockState, int fortune, boolean isSilkTouch) {
		drops.add(createStack());
	}

	@Override
	@Nonnull
	public ItemStack getPickBlock(EntityPlayer player) {
		return createStack();
	}

	@Nonnull
	private ItemStack createStack() {
		final int colorMeta = color.get().vanillaBlockId;
		return new ItemStack(OpenBlocks.Blocks.elevatorRotating, 1, colorMeta);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote || hand != EnumHand.MAIN_HAND) return false;

		final ItemStack heldItem = player.getHeldItemMainhand();
		if (!heldItem.isEmpty()) {
			Set<ColorMeta> metas = ColorMeta.fromStack(heldItem);
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
	public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
		ColorMeta colorMeta = ColorMeta.fromBlockMeta(stack.getItemDamage());
		color.set(colorMeta);
	}

}
