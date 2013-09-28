package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.integration.ModuleBuildCraft;
import openblocks.utils.InventoryUtils;
import openblocks.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksander
 * Date: 25.09.13
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
public class TileEntityBlockBreaker extends OpenTileEntity {
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
        System.out.println("Breaking block");

        int direction = getMetadata();
        int x = xCoord,
            y = yCoord,
            z = zCoord;

        int backX = xCoord,
            backY = yCoord,
            backZ = zCoord;

        ForgeDirection backDirection;

        switch (direction) {
            case 2: z--; backZ++; backDirection = ForgeDirection.getOrientation(3); break;
            case 3: z++; backZ--; backDirection = ForgeDirection.getOrientation(2); break;
            case 4: x--; backX++; backDirection = ForgeDirection.getOrientation(5); break;
            case 5: x++; backX--; backDirection = ForgeDirection.getOrientation(4); break;
            default: return;
        }

        if(worldObj.blockExists(x, y, z)) {
            int blockId = worldObj.getBlockId(x, y, z);
            if(blockId > 0) {

                Block block = Block.blocksList[blockId];

                int metadata = worldObj.getBlockMetadata(x, y, z);
                worldObj.playAuxSFX(2001, x, y, z, blockId + (metadata << 12));

                ArrayList<ItemStack> items = block.getBlockDropped(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z), 0);

                worldObj.setBlock(x, y, z, 0, 0, 3);

                System.out.println("Dropped " + items.size() + " stacks");

                ejectAt(worldObj, backX, backY, backZ, backDirection, items);
            }
        }
    }

    static void ejectAt(World world, int x, int y, int z, ForgeDirection direction, ArrayList<ItemStack> itemStacks) {
        IInventory inventory = InventoryUtils.getInventory(world, x, y, z); //getInventoryAt(world, x, y, z);
        if(inventory != null) {
            insertInto(world, x, y, z, inventory, itemStacks);
            return;
        }

        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if(tileEntity != null) {
            ArrayList<ItemStack> restItems = new ArrayList<ItemStack>();
            for(int i = 0, l = itemStacks.size(); i < l; i++) {
                ItemStack stack = itemStacks.get(i);
                int submittedItems = ModuleBuildCraft.tryAcceptIntoPipe(tileEntity, stack, direction);
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
            dropItemsAt(world, x, y, z, itemStacks);
        }
    }

    static void insertInto(World world, int x, int y, int z, IInventory inventory, ArrayList<ItemStack> itemStacks) {
        ArrayList<ItemStack> rest = new ArrayList<ItemStack>();

        for(int i = 0, l = itemStacks.size(); i < l; i++) {
            ItemStack stack = itemStacks.get(i);
            InventoryUtils.insertItemIntoInventory(inventory, stack);
            if(stack.stackSize > 0)
                rest.add(stack);
        }

        if(rest.size() > 0)
            dropItemsAt(world, x, y, z, rest);
    }

    static void dropItemsAt(World world, int x, int y, int z, ArrayList<ItemStack> itemStacks) {
        float maxOffset = 0.7F;

        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            for(int i = 0, l = itemStacks.size(); i < l; i++) {
                double offsetX = (double)(world.rand.nextFloat() * maxOffset) + (double)(1.0F - maxOffset) * 0.5D;
                double offsetY = (double)(world.rand.nextFloat() * maxOffset) + (double)(1.0F - maxOffset) * 0.5D;
                double offsetZ = (double)(world.rand.nextFloat() * maxOffset) + (double)(1.0F - maxOffset) * 0.5D;

                EntityItem entityItem = new EntityItem(world, (double)x + offsetX, (double)y + offsetY, (double)z + offsetZ, itemStacks.get(i));
                entityItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityItem);
            }
        }
    }

    // stolen from TileEntityHopper
    static IInventory getInventoryAt(World world, int x, int y, int z) {
        IInventory inventory = null;

        TileEntity tileentity = world.getBlockTileEntity(x, y, z);

        if (tileentity != null && tileentity instanceof IInventory) {
            inventory = (IInventory)tileentity;

            if (inventory instanceof TileEntityChest) {
                int blockId = world.getBlockId(x, y, z);
                Block block = Block.blocksList[blockId];

                if(block instanceof BlockChest) {
                    inventory = ((BlockChest)block).getInventory(world, x, y, z);
                }
            }
        }

        if(inventory == null) {
            List list = world.getEntitiesWithinAABBExcludingEntity(
                    (Entity)null,
                    AxisAlignedBB.getAABBPool().getAABB(
                            (double)x,
                            (double)y,
                            (double)z,
                            (double)x + 1.0D,
                            (double)y + 1.0D,
                            (double)z + 1.0D),
                    IEntitySelector.selectInventories);

            if (list != null && list.size() > 0) {
                inventory = (IInventory)list.get(world.rand.nextInt(list.size()));
            }
        }

        return inventory;
    }
}
