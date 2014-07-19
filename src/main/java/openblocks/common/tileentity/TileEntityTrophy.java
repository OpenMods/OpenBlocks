package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.TrophyHandler.Trophy;
import openmods.api.IActivateAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;

import com.google.common.base.Preconditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTrophy extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile {

	public static Trophy debugTrophy = Trophy.Wolf;
	private int cooldown = 0;
	private SyncableInt trophyIndex;

	public TileEntityTrophy() {}

	@Override
	protected void createSyncedFields() {
		trophyIndex = new SyncableInt(-1);
	}

	public Trophy getTrophy() {
		int trophyId = trophyIndex.getValue();
		return trophyId >= 0? Trophy.VALUES[trophyId] : null;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			Trophy trophy = getTrophy();
			if (trophy != null) trophy.executeTickBehavior(this);
			if (cooldown > 0) cooldown--;
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			Trophy trophyType = getTrophy();
			if (trophyType != null) {
				trophyType.playSound(worldObj, xCoord, yCoord, zCoord);
				if (cooldown <= 0) cooldown = trophyType.executeActivateBehavior(this, player);
			}
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		boolean set = false;
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey("entity")) {
				String entityKey = tag.getString("entity");
				trophyIndex.setValue(Trophy.valueOf(entityKey).ordinal());
				set = true;
			}
		}

		if (!worldObj.isRemote) {
			if (!set) {
				int next = (debugTrophy.ordinal() + 1) % Trophy.values().length;
				debugTrophy = Trophy.values()[next];
				trophyIndex.setValue(debugTrophy.ordinal());
			}

			sync();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		cooldown = tag.getInteger("cooldown");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("cooldown", cooldown);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void prepareForInventoryRender(Block block, int metadata) {
		Preconditions.checkElementIndex(metadata, Trophy.VALUES.length);
		super.prepareForInventoryRender(block, metadata);
		trophyIndex.setValue(metadata);
	}

}
