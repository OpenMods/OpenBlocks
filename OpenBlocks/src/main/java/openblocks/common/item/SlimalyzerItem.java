package openblocks.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import openblocks.OpenBlocks;

public class SlimalyzerItem extends Item {

    private static final String TAG_ACTIVE = "active";

    public SlimalyzerItem(Item.Properties properties) {
        super(properties);
    }
    public static boolean isActive(ItemStack stack) {
        final CompoundNBT itemTag = stack.getTag();
        return itemTag != null && itemTag.getBoolean(TAG_ACTIVE);
    }

    private static boolean isInSlimeChunk(ServerWorld world, Entity entity) {
        if (world == null || entity == null || world.getDimensionKey() != World.OVERWORLD) return false;
        //Reference SlimeEntity#func_223366_c
        ChunkPos chunkpos = new ChunkPos(entity.getPosition());
        return SharedSeedRandom.seedSlimeChunk(chunkpos.x, chunkpos.z, world.getSeed(), 987234911L).nextInt(10) == 0;
    }

    private static boolean update(ItemStack stack, ServerWorld world, Entity entity) {
        final boolean isActive = isActive(stack);
        final boolean isInSlimeChunk = isInSlimeChunk(world, entity);
        if (isActive != isInSlimeChunk) {
            if (isInSlimeChunk && entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)entity).playSound( OpenBlocks.Sounds.ITEM_SLIMALYZER_PING, SoundCategory.PLAYERS,1F, 1F);
            }
            stack.getOrCreateTag().putBoolean(TAG_ACTIVE, isInSlimeChunk);
            return true;
        }
        return false;
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!world.isRemote) update(stack, (ServerWorld) world, entity);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entityItem) {
        final World world = entityItem.world;
        if (!world.isRemote) {
            if (update(stack, (ServerWorld) world, entityItem)) entityItem.setItem(stack);
        }
        return super.onEntityItemUpdate(stack, entityItem);
    }
}
