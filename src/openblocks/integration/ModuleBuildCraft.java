package openblocks.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.transport.IPipeTile;

public class ModuleBuildCraft {

	public static int tryAcceptIntoPipe(TileEntity possiblePipe, ItemStack nextStack, ForgeDirection direction) {
		if (possiblePipe instanceof IPipeTile) { return ((IPipeTile)possiblePipe).injectItem(nextStack, true, direction.getOpposite()); }
		return 0;
	}
}
