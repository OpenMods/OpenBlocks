package openblocks.common.tileentity;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import openblocks.common.item.ItemFlagBlock;
import openmods.api.IActivateAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.model.eval.EvalModelState;
import openmods.sync.SyncMap;
import openmods.sync.SyncableFloat;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityFlag extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile {

	private SyncableFloat angle;

	private EvalModelState clipsState = EvalModelState.EMPTY;

	public TileEntityFlag() {}

	@Override
	protected void createSyncedFields() {
		angle = new SyncableFloat();
	}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addUpdateListener(changes -> {
			if (changes.contains(angle)) {
				setStateAngle(angle.get());
				markBlockForRenderUpdate(getPos());
			}
		});
	}

	public float getAngle() {
		return angle.get();
	}

	@Override
	public boolean onBlockActivated(PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && hand == Hand.MAIN_HAND) {
			ItemStack heldItem = player.getHeldItemMainhand();
			if (heldItem.getItem() instanceof ItemFlagBlock) return false;

			if (getOrientation().down() == Direction.DOWN) {
				angle.set(angle.get() + (player.isSneaking()? -10f : +10f));
				sync();
				return true;
			}
		}

		return false;
	}

	@Override
	public void onBlockPlacedBy(BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
		final Direction rotation = getOrientation().up();
		if (rotation == Direction.UP) {
			final float playerAngle = placer.rotationYawHead;
			final int angle = MathHelper.floor(playerAngle / 10) * 10;
			this.angle.set(angle);
			setStateAngle(angle);
		}
	}

	public EvalModelState getRenderState() {
		return clipsState;
	}

	private void setStateAngle(float angle) {
		clipsState = clipsState.withArg("rotation", angle);
	}
}
