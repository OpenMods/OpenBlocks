package openblocks.utils;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import java.util.HashMap;

public class OpenBlocksFakePlayer extends FakePlayer {
    static final HashMap<Integer, OpenBlocksFakePlayer> _players = new HashMap<Integer, OpenBlocksFakePlayer>();

    public static OpenBlocksFakePlayer getPlayerForWorld(World world) {
        int id = world.provider.dimensionId;
        if(!_players.containsKey(id)) {
            _players.put(id, new OpenBlocksFakePlayer(world));
        }
        return _players.get(id);
    }


    private OpenBlocksFakePlayer(World world) {
        super(world, "Open Blocks Fake Player");
    }

    public ItemStack equipWithAndRightClick(ItemStack itemStack, Vec3 currentPos, Vec3 hitVector, ForgeDirection side, boolean blockExists) {
        System.out.println("Place block at " + hitVector.xCoord + "," + hitVector.yCoord + "," + hitVector.zCoord + ". Block exist: " + blockExists);

        setPosition(currentPos.xCoord, currentPos.yCoord, currentPos.zCoord);

        if(blockExists) {
            hitVector.yCoord++;
        }

        // find rotations
        float deltaX = (float)(currentPos.xCoord - hitVector.xCoord);
        float deltaY = (float)(currentPos.yCoord - hitVector.yCoord);
        float deltaZ = (float)(currentPos.zCoord - hitVector.zCoord);
        float distanceInGroundPlain = (float)Math.sqrt((float)MathUtils.lengthSq(deltaX, deltaZ));

        float pitch = (float)(Math.atan2(deltaZ, deltaX) * 180 / Math.PI);
        float hue = (float)(Math.atan2(deltaY, distanceInGroundPlain) * 180 / Math.PI);

        setRotation(pitch, hue);

        inventory.clearInventory(-1, -1);
        inventory.addItemStackToInventory(itemStack);

        rightClick(
            inventory.getCurrentItem(),
            (int)Math.floor(hitVector.xCoord),
            (int)Math.floor(hitVector.yCoord),
            (int)Math.floor(hitVector.zCoord),
            side.ordinal(),
            deltaX, deltaY, deltaZ,
            blockExists);

        return ItemStack.copyItemStack(inventory.getCurrentItem());
    }

    public void dropItemAt(ItemStack itemStack, int count, int x, int y, int z, ForgeDirection direction) {
        setPosition(x + 0.5F, y, z + 0.5F);
        if(direction == ForgeDirection.DOWN) {
            setRotation(0, -90);
        } else {
            // "Other directions than down is not implemented"
            throw new IllegalStateException();
        }

        EntityItem entityItem = dropItemWithOffset(itemStack.itemID, count, -0.5F);
        entityItem.motionX = 0;
        entityItem.motionY = 0;
        entityItem.motionZ = 0;
    }

    private boolean rightClick(ItemStack itemStack, int x, int y, int z, int side, float deltaX, float deltaY, float deltaZ, boolean blockExists) {
        boolean flag = false;
        int blockId;

        if(itemStack != null && itemStack.getItem() != null
           && itemStack.getItem().onItemUseFirst(itemStack, this, worldObj, x, y, z, side, deltaX, deltaY, deltaZ)) {
            return true;
        }

        if(isSneaking() || (getHeldItem() == null || getHeldItem().getItem().shouldPassSneakingClickToBlock(worldObj, x, y, z))) {
            blockId = worldObj.getBlockId(x, y, z);

            if(blockId > 0 && Block.blocksList[blockId].onBlockActivated(worldObj, x, y, z, this, side, deltaX, deltaY, deltaZ)) {
                flag = true;
            }
        }

        if(!flag && itemStack != null && itemStack.getItem() instanceof ItemBlock) {
            if(blockExists) {
                return false;
            }

            ItemBlock itemblock = (ItemBlock)itemStack.getItem();

            if (!canPlaceItemBlockOnSide(itemblock, worldObj, x, y, z, side, itemStack)) {
                return false;
            }
        }

        if(flag) {
            return true;
        } else if(itemStack == null) {
            return false;
        } else {
            if (!itemStack.tryPlaceItemIntoWorld(this, worldObj, x, y, z, side, deltaX, deltaY, deltaZ))
            {
                return false;
            }
//            if (itemStack.stackSize <= 0)
//            {
//                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this, itemStack));
//            }
            return true;
        }
    }
    
    private boolean canPlaceItemBlockOnSide(ItemBlock itemBlock, World world, int x, int y, int z, int side, ItemStack itemStack) {
    	int blockId = world.getBlockId(x, y, z);
    	if (blockId == Block.snow.blockID) {
    		side = 1;
    	} else if (
			blockId != Block.vine.blockID &&
			blockId != Block.tallGrass.blockID &&
			blockId != Block.deadBush.blockID &&
			(Block.blocksList[blockId] == null || !Block.blocksList[blockId].isBlockReplaceable(world, x, y, z))) {
    		switch(side) {
    		case 0: --y; break;
    		case 1: ++y; break;
    		case 2: --z; break;
    		case 3: ++z; break;
    		case 4: --x; break;
    		case 5: ++x; break;
    		}
    	}
    	
    	return world.canPlaceEntityOnSide(itemBlock.getBlockID(), x, y, z, false, side, this, itemStack);
    }
}
