package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.api.IAwareTile;
import openblocks.integration.ModuleBuildCraft;
import openblocks.utils.BlockUtils;
import openblocks.utils.InventoryUtils;
import openblocks.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

public class TileEntityBlockBreaker extends OpenTileEntity
    implements IAwareTile, IPipeConnection {
    private boolean _redstoneSignal;

    public void setRedstoneSignal(boolean redstoneSignal) {
        if(redstoneSignal != _redstoneSignal) {
            _redstoneSignal = redstoneSignal;
            if(_redstoneSignal) {
                breakBlock();
            }
        }
    }

    private void breakBlock() {
        if(worldObj.isRemote) return;

        ForgeDirection direction = ForgeDirection.getOrientation(getMetadata());
        int x = xCoord + direction.offsetX,
            y = yCoord + direction.offsetY,
            z = zCoord + direction.offsetZ;
        
        if(worldObj.blockExists(x, y, z)) {
        	int blockId = worldObj.getBlockId(x, y, z);
        	if(blockId > 0) {
        		Block block = Block.blocksList[blockId];
        		
        		int metadata = worldObj.getBlockMetadata(x, y, z);
        		worldObj.playAuxSFX(2001, x, y, z, blockId + (metadata << 12));
        		
        		ArrayList<ItemStack> items = block.getBlockDropped(worldObj, x, y, z, metadata, 0);
        		
        		worldObj.setBlock(x, y, z, 0, 0, 3);
        		
        		ForgeDirection back = direction.getOpposite();
        		ejectAt(worldObj,
    				xCoord + back.offsetX,
    				yCoord + back.offsetY,
    				zCoord + back.offsetZ,
    				back, items);
        	}
        }
    }

    static void ejectAt(World world, int x, int y, int z, ForgeDirection direction, ArrayList<ItemStack> itemStacks) {
        IInventory inventory = InventoryUtils.getInventory(world, x, y, z); //getInventoryAt(world, x, y, z);
        if(inventory != null) {
            insertInto(world, x, y, z, inventory, direction, itemStacks);
            return;
        }

        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if(tileEntity != null) {
            ArrayList<ItemStack> restItems = new ArrayList<ItemStack>();
            for(int i = 0, l = itemStacks.size(); i < l; i++) {
                ItemStack stack = itemStacks.get(i);
                int submittedItems = ModuleBuildCraft.tryAcceptIntoPipe(tileEntity, stack, true, direction);
                if(submittedItems == 0) {
                    restItems.add(stack);
                } else if(submittedItems < stack.stackSize) {
                    ItemStack rest = new ItemStack(stack.getItem(), stack.stackSize - submittedItems, stack.getItemDamage());
                    restItems.add(rest);
                }
            }
            itemStacks = restItems;
        }

        if(itemStacks.size() > 0) {
            ejectItemsAt(world, x, y, z, direction, itemStacks);
        }
    }

    static void insertInto(World world, int x, int y, int z, IInventory inventory, ForgeDirection direction, ArrayList<ItemStack> itemStacks) {
        ArrayList<ItemStack> rest = new ArrayList<ItemStack>();

        for(int i = 0, l = itemStacks.size(); i < l; i++) {
            ItemStack stack = itemStacks.get(i);
            InventoryUtils.insertItemIntoInventory(inventory, stack, direction);
            if(stack.stackSize > 0)
                rest.add(stack);
        }

        if(rest.size() > 0)
        	ejectItemsAt(world, x, y, z, direction, rest);
    }

    static void ejectItemsAt(World world, int x, int y, int z, ForgeDirection direction, ArrayList<ItemStack> itemStacks) {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            for(int i = 0, l = itemStacks.size(); i < l; i++) {
                EntityItem item = BlockUtils.dropItemStackInWorld(world, x, y, z, itemStacks.get(i));
                item.motionX = ((float)direction.offsetX) / 5F;
                item.motionY = ((float)direction.offsetY) / 5F;
                item.motionZ = ((float)direction.offsetZ) / 5F;
            }
        }
    }

    @Override
    public void onBlockBroken() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onBlockAdded() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void onNeighbourChanged(int blockId) {
        if(!worldObj.isRemote) {
            setRedstoneSignal(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
        }
    }

    @Override
    public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
        worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, BlockUtils.get3dOrientation(player).ordinal(), 2);
    }

    @Override
    public boolean onBlockEventReceived(int eventId, int eventParam) {
        return false;
    }

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		if(type == PipeType.ITEM && with.ordinal() == ForgeDirection.getOrientation(getMetadata()).getOpposite().ordinal())
			return ConnectOverride.CONNECT;
		return ConnectOverride.DISCONNECT;
	}
}
