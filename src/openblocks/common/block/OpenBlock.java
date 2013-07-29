package openblocks.common.block;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.UP;
import static net.minecraftforge.common.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.api.ISurfaceAttachment;
import openblocks.common.item.ItemOpenBlock;
import openblocks.utils.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public abstract class OpenBlock extends BlockContainer {

	private String uniqueBlockId;
	private Class<? extends TileEntity> teClass = null;
	protected String modKey = "";

	protected OpenBlock(int id, Material material) {
		super(id, material);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHardness(3.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		try {
			if (teClass != null) {
				return teClass.getConstructor(new Class[0]).newInstance();
			}
		} catch (NoSuchMethodException nsm) {
			System.out
					.println("Notice: Cannot create TE automatically due to constructor requirements");
		} catch (Exception ex) {
			System.out.println("Notice: Error creating tile entity");
			ex.printStackTrace();
		}
		return null;
	}

	public void registerIcons(IconRegister registry) {
		this.blockIcon = registry.registerIcon(String.format("%s:%s", modKey, uniqueBlockId));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		BlockUtils.dropTileInventory(tile);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	public void breakBlockDontDrop(World world, int x, int y, int z, int par5, int par6) {
		super.breakBlock(world, x, y, z, par5, par6);
	}

	public void setupBlock(Block instance, String uniqueName) {
		setupBlock(instance, uniqueName, null);
	}
	
	public void setupBlock(Block instance, String uniqueName, Class<? extends TileEntity> tileEntity) {
		setupBlock(instance, uniqueName, tileEntity, ItemOpenBlock.class);
	}

	public void setupBlock(Block instance, String uniqueName, Class<? extends TileEntity> tileEntity, Class<? extends ItemOpenBlock> itemClass) {
		uniqueBlockId = uniqueName;
		modKey = OpenBlocks.proxy.getModId().toLowerCase();
		
		GameRegistry.registerBlock(instance, itemClass, String.format("%s_%s", modKey, uniqueName));
		instance.setUnlocalizedName(String.format("%s.%s", modKey, uniqueName));
		
		if (tileEntity != null) {
			GameRegistry.registerTileEntity(tileEntity, String.format("%s_%s", modKey, uniqueName));
			this.teClass = tileEntity;
		}
	}
	
	/**
	 * Can we place the block on this
	 * @param world
	 * @param x of the block we're placing
	 * @param y of the block we're placing
	 * @param z of the block we're placing
	 * @param side the side of the block that's attached to the other block
	 * @return
	 */
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		x += side.offsetX;
		y += side.offsetY;
		z += side.offsetZ;
		return world.isBlockSolidOnSide(x, y, z, side.getOpposite());
	}

	public boolean canPlaceBlockOnSides(World world, int x, int y, int z, ForgeDirection ... sides) {
		for (ForgeDirection side : sides) {
			if (canPlaceBlockOnSide(world, x, y, z, side)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof ISurfaceAttachment) {
			ForgeDirection direction = ((ISurfaceAttachment) te).getSurfaceDirection();
			if (!canPlaceBlockOnSide(world, x, y, z, direction)) {
		        dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		        world.setBlockToAir(x, y, z);
			}
		}
	}
	
	protected void setupDimensionsFromCenter(float x, float y, float z, float width, float height, float depth) {
		setupDimensions(x - width, y, z - depth, x + width, y + height, z + depth);
	}
	
	protected void setupDimensions(float minX, float minY, float minZ, float maxX, float maxY, float maxZ){
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;		
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x,
			int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
			int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return canPlaceBlockOnSides(world, x, y, z, EAST, WEST, SOUTH, NORTH, UP, DOWN );
	}

	public <U extends TileEntity> U getTileEntity(IBlockAccess world, int x, int y, int z, Class<U> T) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && T.isAssignableFrom(te.getClass())) {
			return (U) te;
		}
		return null;
	}
	
	public void onBlockPlacedBy(World world, EntityPlayer player,
			ItemStack stack, int x, int y, int z, ForgeDirection side,
			float hitX, float hitY, float hitZ, int meta) {
		
	}
}
