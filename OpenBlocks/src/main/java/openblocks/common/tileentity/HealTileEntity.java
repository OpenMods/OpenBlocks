package openblocks.common.tileentity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import openblocks.OpenBlocks;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.BlockUtils;
import java.util.List;

public class HealTileEntity extends OpenTileEntity implements ITickableTileEntity {
    public HealTileEntity() {
        super(OpenBlocks.TileEntities.heal);
    }

    @Override
    public void tick() {
        if (world.isRemote) return;

        if (world.getGameTime() % 10 == 0) {
            List<ServerPlayerEntity> playersOnTop = world.getEntitiesWithinAABB(ServerPlayerEntity.class, BlockUtils.expandAround(pos, 1, 2, 1));
            for (ServerPlayerEntity player : playersOnTop) {
                if (player.interactionManager.getGameType().isSurvivalOrAdventure()) {
                    //Just for the effectDisplay
                    player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 15, 1));
                    player.addPotionEffect(new EffectInstance(Effects.SATURATION, 15, -1));
                    //Change only happens here
                    player.getFoodStats().addStats(1,1);
                    player.heal(1);
                }
            }
        }
    }
}
