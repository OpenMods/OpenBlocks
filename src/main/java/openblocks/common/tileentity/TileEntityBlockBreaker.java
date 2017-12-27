package openblocks.common.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import openblocks.Config;
import openmods.fakeplayer.BreakBlockAction;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.utils.InventoryUtils;
import openmods.utils.ItemUtils;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityBlockBreaker extends TileEntityBlockManipulator {

	// DON'T remove this object, even though it seems unused. Without it Builcraft pipes won't connect. -B
	@IncludeInterface(IInventory.class)
	private final GenericInventory inventory = registerInventoryCallback(new GenericInventory("blockbreaker", true, 1) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}
	});

	public TileEntityBlockBreaker() {}

	@Override
	protected boolean canWork(IBlockState targetState, BlockPos target, EnumFacing direction) {
		final Block block = targetState.getBlock();
		return !block.isAir(targetState, world, target) && block != Blocks.BEDROCK && targetState.getBlockHardness(world, target) > -1.0F;
	}

	@Override
	protected void doWork(IBlockState targetState, BlockPos target, EnumFacing direction) {
		final List<EntityItem> drops = FakePlayerPool.instance.executeOnPlayer((WorldServer)world, new BreakBlockAction(world, target));

		if (drops.isEmpty()) return;

		final EnumFacing dropSide = direction.getOpposite();
		final IItemHandler targetInventory = InventoryUtils.tryGetHandler(world, pos.offset(dropSide), direction);
		if (targetInventory == null) return;

		for (EntityItem drop : drops) {
			ItemStack stack = drop.getItem();
			final ItemStack leftovers = ItemHandlerHelper.insertItem(targetInventory, stack, false);
			ItemUtils.setEntityItemStack(drop, leftovers);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory.getHandler();

		return super.getCapability(capability, facing);
	}

	@Override
	protected int getActionLimit() {
		return Config.blockBreakerActionLimit;
	}
}
