package openblocks.common.item;

import javax.annotation.Nonnull;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockNotifyFlags;

@BookDocumentation
public class ItemSpongeOnAStick extends Item {

	public ItemSpongeOnAStick() {
		setMaxStackSize(1);
		setMaxDamage(Config.spongeMaxDamage);
	}

	private static int getCleanupFlags() {
		return Config.spongeStickBlockUpdate? BlockNotifyFlags.ALL : BlockNotifyFlags.SEND_TO_CLIENTS;
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		final ItemStack stack = player.getHeldItem(hand);
		return soakUp(world, pos, player, stack)? ActionResultType.SUCCESS : ActionResultType.FAIL;

	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		boolean result = soakUp(world, player.getPosition(), player, stack);
		return ActionResult.newResult(result? ActionResultType.SUCCESS : ActionResultType.FAIL, stack);
	}

	private static boolean soakUp(World world, BlockPos pos, PlayerEntity player, @Nonnull ItemStack stack) {
		boolean absorbedAnything = false;
		boolean hitLava = false;
		int damage = stack.getItemDamage();

		final int cleanupFlags = getCleanupFlags();
		for (int x = -Config.spongeStickRange; x <= Config.spongeStickRange; x++) {
			for (int y = -Config.spongeStickRange; y <= Config.spongeStickRange; y++) {
				for (int z = -Config.spongeStickRange; z <= Config.spongeStickRange; z++) {
					final BlockPos targetPos = pos.add(x, y, z);

					Material material = world.getBlockState(targetPos).getMaterial();
					if (material.isLiquid()) {
						absorbedAnything = true;
						hitLava |= material == Material.LAVA;
						world.setBlockState(targetPos, Blocks.AIR.getDefaultState(), cleanupFlags);
						if (++damage >= Config.spongeMaxDamage) break;
					}

				}
			}
		}

		if (hitLava) {
			stack.setCount(0);
			player.setFire(6);
		}

		if (absorbedAnything) {
			stack.damageItem(1, player);
			return true;
		}

		return false;
	}

}
