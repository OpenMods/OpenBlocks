package openblocks.common.tileentity;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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
		trophyIndex = new SyncableEnum<Trophy>(Trophy.PigZombie);
	}

	public Trophy getTrophy() {
		return trophyIndex.get();
	}

	@Override
	public void update() {
		if (!worldObj.isRemote) {
			Trophy trophy = getTrophy();
			if (trophy != null) trophy.executeTickBehavior(this);
			if (cooldown > 0) cooldown--;
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			Trophy trophyType = getTrophy();
			if (trophyType != null) {
				trophyType.playSound(worldObj, pos);
				if (cooldown <= 0) cooldown = trophyType.executeActivateBehavior(this, player);
			}
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
		Trophy trophy = ItemTrophyBlock.getTrophy(stack);
		if (trophy != null) trophyIndex.set(trophy);

		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			this.cooldown = tag.getInteger(TAG_COOLDOWN);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.cooldown = tag.getInteger(TAG_COOLDOWN);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setInteger("cooldown", cooldown);
		return tag;
	}

	@Override
	public boolean suppressBlockHarvestDrops() {
		return true;
	}

	private ItemStack getAsItem() {
		final Trophy trophy = getTrophy();
		if (trophy != null) {
			ItemStack stack = trophy.getItemStack();
			NBTTagCompound tag = ItemUtils.getItemTag(stack);
			tag.setInteger(TAG_COOLDOWN, cooldown);
			return stack;
		}

		return null;
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops, int fortune, boolean isSilkTouch) {
		ItemStack stack = getAsItem();
		if (stack != null) drops.add(stack);
	}

	@Override
	public ItemStack getPickBlock(EntityPlayer player) {
		final Trophy trophy = getTrophy();
		return trophy != null? trophy.getItemStack() : null;
	}

}
