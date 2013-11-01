package openblocks.common.tileentity;

import java.lang.ref.WeakReference;
import java.util.List;

import openblocks.OpenBlocks;
import openblocks.common.api.IAwareTile;
import openblocks.utils.BlockUtils;
import openblocks.utils.EnchantmentUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityXPDrain extends OpenTileEntity implements IAwareTile {

	private WeakReference<TileEntity> targetTank;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (OpenBlocks.proxy.getTicks(worldObj) % 100 == 0) {
			searchForTank();
		}
		if (targetTank != null) {
			TileEntity tile = targetTank.get();
			if (!(tile instanceof IFluidHandler) || tile.isInvalid()) {
				targetTank = null;
			}else {
				if (!worldObj.isRemote) {
					IFluidHandler tank = (IFluidHandler) tile;
					for (EntityPlayer player : getPlayersOnGrid()) {
						FluidStack xpStack = OpenBlocks.XP_FLUID.copy();
						int xpToDrain = Math.min(4, player.experienceTotal);
						xpStack.amount =  EnchantmentUtils.XPToLiquidRatio(xpToDrain);
						int filled = tank.fill(ForgeDirection.UP, xpStack, true);
						if (filled > 0) {
							if (OpenBlocks.proxy.getTicks(worldObj) % 4 == 0) {
								worldObj.playSoundEffect(xCoord+0.5, yCoord +0.5, zCoord+0.5, "random.orb", 0.1F, 0.5F * ((worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.7F + 1.8F));
							}
							int xpDrained = EnchantmentUtils.liquidToXPRatio(filled);
							while (xpDrained > 0) {
								player.experienceTotal--;
								player.experienceLevel = EnchantmentUtils.getLevelForExperience(player.experienceTotal);
								int expForLevel = EnchantmentUtils.getExperienceForLevel(player.experienceLevel);
								player.experience = (float)(player.experienceTotal - expForLevel) / (float)player.xpBarCap();
								xpDrained--;
							}
						}
					}
				}
			}
		}
	}

	public void searchForTank() {
		targetTank = null;
		for (int y = yCoord - 1; y > 0; y--) {
			boolean isAir = worldObj.isAirBlock(xCoord, y, zCoord);
			if (!isAir) {
				TileEntity te = worldObj.getBlockTileEntity(xCoord, y, zCoord);
				if (!(te instanceof IFluidHandler) && te != null) {
					Block block = te.getBlockType();
					if (block.isOpaqueCube()) {
						return;
					}
				}else {
					targetTank = new WeakReference<TileEntity>(te);
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<EntityPlayer> getPlayersOnGrid() {
		AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
		return worldObj.getEntitiesWithinAABB(EntityPlayer.class, bb);
	}

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		setRotation(BlockUtils.get2dOrientation(player));
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}
}
