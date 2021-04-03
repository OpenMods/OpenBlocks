package openblocks.common.tileentity;

import net.minecraft.entity.player.PlayerEntity;
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
                    /*
                     * TODO: the potion effects are set to 1 tick only to give
                     * enough time for the player to regenerate, but without
                     * having any overkill However, this does have the
                     * side-effect of not showing particle effects. Personally,
                     * I wish that the player could see effects, but I think
                     * someone else should ultimately decide if it should be
                     * done (you know who you are) (Was it me? yk)
                     */
                    //Just for the effectDisplay
                    player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 15, 1));
                    player.addPotionEffect(new EffectInstance(Effects.SATURATION, 15, -1));
                    //Change only happens hear
                    player.getFoodStats().addStats(1,1);
                    player.heal(1);
                }
            }
        }
    }
}
