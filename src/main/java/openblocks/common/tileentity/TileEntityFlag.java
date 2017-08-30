package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import openblocks.OpenBlocks;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomHarvestDrops;
import openmods.api.IPlaceAwareTile;
import openmods.colors.ColorMeta;
import openmods.model.eval.EvalModelState;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncMap;
import openmods.sync.SyncableEnum;
import openmods.sync.SyncableFloat;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityFlag extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile, ICustomHarvestDrops {

	private SyncableFloat angle;
	private SyncableEnum<ColorMeta> colorIndex;

	private EvalModelState clipsState = EvalModelState.EMPTY;

	public TileEntityFlag() {}

	@Override
	protected void createSyncedFields() {
		angle = new SyncableFloat();
		colorIndex = SyncableEnum.create(ColorMeta.GREEN);
	}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addUpdateListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				if (changes.contains(angle)) {
					setStateAngle(angle.get());
					markBlockForRenderUpdate(getPos());
				}
			}
		});
	}

	public ColorMeta getColor() {
		return colorIndex.get();
	}

	public float getAngle() {
		return angle.get();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
			ItemStack heldItem = player.getHeldItemMainhand();
			if (heldItem.getItem() == Item.getItemFromBlock(OpenBlocks.Blocks.flag)) return false;

			if (getOrientation().down() == EnumFacing.DOWN) {
				angle.set(angle.get() + (player.isSneaking()? -10f : +10f));
				sync();
				return true;
			}
		}

		return false;
	}

	@Override
	public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
		final EnumFacing rotation = getOrientation().up();
		if (rotation == EnumFacing.UP) {
			final float playerAngle = placer.rotationYawHead;
			final int angle = MathHelper.floor(playerAngle / 10) * 10;
			this.angle.set(angle);
			setStateAngle(angle);
		}

		colorIndex.set(ColorMeta.fromBlockMeta(stack.getItemDamage() & 0xF));
	}

	public EvalModelState getRenderState() {
		return clipsState;
	}

	private void setStateAngle(float angle) {
		clipsState = clipsState.withArg("rotation", angle);
	}

	@Override
	public boolean suppressBlockHarvestDrops() {
		return true;
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops, IBlockState blockState, int fortune, boolean isSilkTouch) {
		drops.add(new ItemStack(OpenBlocks.Blocks.flag, 1, colorIndex.get().vanillaBlockId));
	}
}
