package openblocks.common.tileentity;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ITickable;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.item.ItemTrophyBlock;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomHarvestDrops;
import openmods.api.ICustomPickItem;
import openmods.api.IPlaceAwareTile;
import openmods.sync.SyncableEnum;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.ItemUtils;

public class TileEntityTrophy extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile, ICustomHarvestDrops, ICustomPickItem, ITickable {

	private final String TAG_COOLDOWN = "cooldown";

	private int cooldown = 0;
	private SyncableEnum<Trophy> trophyIndex;

	public TileEntityTrophy() {}

	@Override
	protected void createSyncedFields() {
		trophyIndex = new SyncableEnum<>(Trophy.PigZombie);
	}

	public Trophy getTrophy() {
		return trophyIndex.get();
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			Trophy trophy = getTrophy();
			if (trophy != null) trophy.executeTickBehavior(this);
			if (cooldown > 0) cooldown--;
		}
	}

	@Override
	public boolean onBlockActivated(PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && hand == Hand.MAIN_HAND) {
			Trophy trophyType = getTrophy();
			if (trophyType != null) {
				trophyType.playSound(world, pos);
				if (cooldown <= 0) cooldown = trophyType.executeActivateBehavior(this, player);
			}
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
		Trophy trophy = ItemTrophyBlock.getTrophy(stack);
		if (trophy != null) trophyIndex.set(trophy);

		if (stack.hasTagCompound()) {
			CompoundNBT tag = stack.getTagCompound();
			this.cooldown = tag.getInteger(TAG_COOLDOWN);
		}
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		this.cooldown = tag.getInteger(TAG_COOLDOWN);
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		tag.setInteger("cooldown", cooldown);
		return tag;
	}

	@Override
	public boolean suppressBlockHarvestDrops() {
		return true;
	}

	@Nonnull
	private ItemStack getAsItem() {
		final Trophy trophy = getTrophy();
		if (trophy != null) {
			ItemStack stack = trophy.getItemStack();
			if (!stack.isEmpty()) {
				CompoundNBT tag = ItemUtils.getItemTag(stack);
				tag.setInteger(TAG_COOLDOWN, cooldown);
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void addHarvestDrops(PlayerEntity player, List<ItemStack> drops, BlockState blockState, int fortune, boolean isSilkTouch) {
		ItemStack stack = getAsItem();
		if (!stack.isEmpty()) drops.add(stack);
	}

	@Override
	@Nonnull
	public ItemStack getPickBlock(PlayerEntity player) {
		final Trophy trophy = getTrophy();
		return trophy != null? trophy.getItemStack() : ItemStack.EMPTY;
	}

}
