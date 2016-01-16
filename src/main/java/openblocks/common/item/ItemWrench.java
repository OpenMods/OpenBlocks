package openblocks.common.item;

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openmods.infobook.BookDocumentation;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@BookDocumentation
public class ItemWrench extends Item {

	private final Set<Class<? extends Block>> sneakOnly = Sets.newIdentityHashSet();

	public ItemWrench() {
		setMaxStackSize(1);

		sneakOnly.add(BlockLever.class);
		sneakOnly.add(BlockButton.class);
		sneakOnly.add(BlockChest.class);
	}

	@Override
	public boolean doesSneakBypassUse(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	private boolean requiresSneaking(final Block block) {
		return Iterables.any(sneakOnly, new Predicate<Class<? extends Block>>() {
			@Override
			public boolean apply(@Nullable Class<? extends Block> input) {
				return input.isInstance(block);
			}
		});
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		final Block block = world.getBlockState(pos).getBlock();

		if (requiresSneaking(block) && !player.isSneaking()) return false;

		final EnumFacing[] rotations = block.getValidRotations(world, pos);
		if (ArrayUtils.contains(rotations, side)) {
			if (block.rotateBlock(world, pos, side)) {
				player.swingItem();
				return !world.isRemote;
			}
		}

		return false;
	}

}
