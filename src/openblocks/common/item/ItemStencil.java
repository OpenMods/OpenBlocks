package openblocks.common.item;

import java.math.BigInteger;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.stencils.Stencil;
import openblocks.client.stencils.StencilManager;
import openblocks.common.block.BlockCanvas;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.utils.ItemUtils;
import openmods.utils.render.PaintUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStencil extends Item {

	private static final String TAG_BITS = "Bits";

	public ItemStencil() {
		super(Config.itemStencilId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() {
		return 0;
	}

	public ItemStack createItemStack(BigInteger bits) {
		ItemStack stack = new ItemStack(this);
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		tag.setByteArray(TAG_BITS, bits.toByteArray());
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconIndex(ItemStack stack) {
		return getIcon(stack);
	}

	@Override
	public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		return getIcon(stack);
	}

	protected Icon getIcon(ItemStack stack) {
		BigInteger bits = getBitsFromStack(stack);
		return StencilManager.instance.getStencilIcon(bits).coverIcon;
	}

	private static BigInteger getBitsFromStack(ItemStack stack) {
		BigInteger bits;
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		NBTBase bytes = tag.getTag(TAG_BITS);
		if (bytes instanceof NBTTagByteArray) {
			bits = new BigInteger(((NBTTagByteArray)bytes).byteArray);
		} else {
			bits = Stencil.bitsFromLegacyStencil(stack.getItemDamage());
			tag.setByteArray(TAG_BITS, bits.toByteArray());
		}
		return bits;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (Stencil s : Stencil.VALUES)
			list.add(createItemStack(s.bits));
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		if (PaintUtils.instance.isAllowedToReplace(world, x, y, z)) {
			BlockCanvas.replaceBlock(world, x, y, z);
		}

		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;
			BigInteger bits = getBitsFromStack(stack);
			if (canvas.useStencil(side, bits)) stack.stackSize--;
			return true;
		}

		return false;
	}

}
