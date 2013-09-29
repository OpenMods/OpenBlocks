package openblocks.utils;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksander
 * Date: 29.09.13
 * Time: 00:34
 * To change this template use File | Settings | File Templates.
 */
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

    public ItemStack equipWithAndRightClick(ItemStack itemStack, Vec3 currentPos, Vec3 hitVector, ForgeDirection side) {
        setPosition(currentPos.xCoord, currentPos.yCoord, currentPos.zCoord);

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
            deltaX, deltaY, deltaZ);

        return ItemStack.copyItemStack(inventory.getCurrentItem());
    }

    private boolean rightClick(ItemStack itemStack, int x, int y, int z, int side, float deltaX, float deltaY, float deltaZ) {
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
            ItemBlock itemblock = (ItemBlock)itemStack.getItem();

            if (!itemblock.canPlaceItemBlockOnSide(worldObj, x, y, z, side, this, itemStack))
            {
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
}
