package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.utils.BlockUtils;

public class BlockTank extends OpenBlock {

	public static class Icons {
		public static Icon xpJuiceStill;
		public static Icon xpJuiceFlowing;
	}

	public BlockTank() {
		super(Config.blockTankId, Material.ground);
		setupBlock(this, "tank", TileEntityTank.class, ItemTankBlock.class);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		super.registerIcons(registry);
		Icons.xpJuiceFlowing = registry.registerIcon(String.format("%s:%s", modKey, "xpjuiceflowing"));
		Icons.xpJuiceStill = registry.registerIcon(String.format("%s:%s", modKey, "xpjuicestill"));
	}

	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!world.isRemote
				&& world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			ItemStack itemStack = new ItemStack(OpenBlocks.Blocks.tank);
			TileEntityTank tank = getTileEntity(world, x, y, z, TileEntityTank.class);
			if (tank != null && tank.getAmount() > 10) {
				NBTTagCompound nbt = new NBTTagCompound();
				NBTTagCompound tankTag = tank.getItemNBT();
				nbt.setCompoundTag("tank", tankTag);
				itemStack.setTagCompound(nbt);
			}
			BlockUtils.dropItemStackInWorld(world, x, y, z, itemStack);
		}
		return world.setBlockToAir(x, y, z);
	}

	@Override
	protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack itemStack) {

	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (!Config.tanksEmitLight) return 0;
		TileEntityTank tile = getTileEntity(world, x, y, z, TileEntityTank.class);
		if (tile != null) { return tile.getFluidLightLevel(); }
		return 0;
	}
}
