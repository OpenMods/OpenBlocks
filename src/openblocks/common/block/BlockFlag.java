package openblocks.common.block;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AABBPool;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityFlag;

public class BlockFlag extends OpenBlock {

	public BlockFlag() {
		super(OpenBlocks.Config.blockFlagId, Material.ground);
		setupBlock(this, "flag", "Flag", TileEntityFlag.class);
		setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1/16f, 1f, 1/16f);
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

	/**
	 * Same placement logic as torches
	 */
	private boolean canPlaceFlagOn(World par1World, int par2, int par3, int par4) {
		if (par1World.doesBlockHaveSolidTopSurface(par2, par3, par4)) {
			return true;
		} else {
			int l = par1World.getBlockId(par2, par3, par4);
			return (Block.blocksList[l] != null && Block.blocksList[l]
					.canPlaceTorchOnTop(par1World, par2, par3, par4));
		}
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }
	
	 public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3)
	    {
	        int l = par1World.getBlockMetadata(par2, par3, par4) & 7;
	        float depth = 0.45F;
	        float width = 1/16f;
	        float height = 1f;
	        if (l == 1)
	        {
	            this.setBlockBounds(0.0F, 0.2F, 0.5F - width, depth * 2.0F, height, 0.5F + width);
	        }
	        else if (l == 2)
	        {
	            this.setBlockBounds(1.0F - depth * 2.0F, 0.2F, 0.5F - width, 1.0F, height, 0.5F + width);
	        }
	        else if (l == 3)
	        {
	            this.setBlockBounds(0.5F - width, 0.2F, 0.0F, 0.5F + width, height, depth * 2.0F);
	        }
	        else if (l == 4)
	        {
	            this.setBlockBounds(0.5F - width, 0.2F, 1.0F - depth * 2.0F, 0.5F + width, height, 1.0F);
	        }
	        else
	        {
	            depth = 1/16f;
	            setupDimensionsFromCenter(0.5f, 0f, 0.5f, depth, 1f, depth);
	        }

	        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
	    }

	
	@Override
	public boolean canBlockStay(World par1World, int par2, int par3, int par4) {
		return canPlaceFlagOn(par1World, par2, par3 - 1, par4);
	}

	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		return par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST,  true) ||
	               par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST,  true) ||
	               par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH, true) ||
	               par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH, true) ||
	               canPlaceFlagOn(par1World, par2, par3 - 1, par4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		return Block.planks.getIcon(par1, par2);
	}

	/**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     * returns metadata
     */
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int side, float par6, float par7, float par8, int par9)
    {
        int metadata = par9;

        if (side == 1 && this.canPlaceFlagOn(par1World, par2, par3 - 1, par4))
        {
            metadata = 5;
        }

        if (side == 2 && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH, true))
        {
            metadata = 4;
        }

        if (side == 3 && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH, true))
        {
            metadata = 3;
        }

        if (side == 4 && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST, true))
        {
            metadata = 2;
        }

        if (side == 5 && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST, true))
        {
            metadata = 1;
        }

        return metadata;
    }
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entity, ItemStack itemstack) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x,y,z);
		if (tile != null && tile instanceof TileEntityFlag) {
			TileEntityFlag flag = (TileEntityFlag) tile;
			if(meta == 5){
				flag.setRotation(entity.rotationYawHead + 90f);
			}else{
				flag.setRotation(getRotationForMeta(meta));
			}				
		}
	}

	public float getRotationForMeta(int meta) {
		switch(meta){
		case 1: return 270f;
		case 2: return 90f;
		case 3: return 0f;
		case 4: return 180f;
		default: return 0f;
		}		
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityFlag) {
			if (!world.isRemote) {
				((TileEntityFlag) te).onActivated(player);
			}
			return true;
		}
		return false;
	}
}
