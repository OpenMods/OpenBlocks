package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiBigButton;
import openblocks.common.GenericInventory;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.api.ISurfaceAttachment;
import openblocks.common.container.ContainerBigButton;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFlags;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBigButton extends SyncedTileEntity implements IActivateAwareTile, ISurfaceAttachment, IInventory, IHasGui {

	private int tickCounter = 0;

	public enum Flags {
		active
	}
	
	private SyncableFlags flags = new SyncableFlags();
	
	public TileEntityBigButton() {
		setInventory(new GenericInventory("bigbutton", true, 1));
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (tickCounter > 0) {
				tickCounter--;
				if (tickCounter <= 0) {
					worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.click", 0.3F, 0.5F);
					flags.off(Flags.active);
					sync();
				}
			}
		}
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerBigButton(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiBigButton(new ContainerBigButton(player.inventory, this));
	}

	public int getTickTime() {
		ItemStack stack = inventory.getStackInSlot(0);
		return stack == null? 1 : stack.stackSize;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			if (player.isSneaking()) {
				openGui(player);
			} else {
				flags.on(Flags.active);
				tickCounter = getTickTime();
				worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.click", 0.3F, 0.6F);
				sync();
			}
		}
		return true;
	}

	@Override
	public void sync() {
		super.sync();
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, OpenBlocks.Blocks.bigButton.blockID);
		ForgeDirection rot = getRotation();
		worldObj.notifyBlocksOfNeighborChange(xCoord + rot.offsetX, yCoord
				+ rot.offsetY, zCoord + rot.offsetZ, OpenBlocks.Blocks.bigButton.blockID);

	}
	
	@Override
	public ForgeDirection getSurfaceDirection() {
		return getRotation();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void prepareForInventoryRender(Block block, int metadata) {
		super.prepareForInventoryRender(block, metadata);
		GL11.glTranslated(-0.5, 0, 0);
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		// TODO Auto-generated method stub
		
	}

	public boolean isButtonActive() {
		return flags.get(Flags.active);
	}
}
