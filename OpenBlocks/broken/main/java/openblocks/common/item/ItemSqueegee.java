package openblocks.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class ItemSqueegee extends Item {

	public ItemSqueegee() {
		setMaxStackSize(1);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;
			if (player.isSneaking()) canvas.removePaint(Direction.VALUES);
			else canvas.removePaint(facing);
			world.playSound(null, player.getPosition(), OpenBlocks.Sounds.ITEM_SQUEEGEE_ACTION, SoundCategory.PLAYERS, 1, 1);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}
}
