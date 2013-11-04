package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.client.renderer.ItemRenderState;
import openblocks.common.api.*;
import openblocks.common.item.ItemOpenBlock;
import openblocks.common.tileentity.NetworkedTileEntity;
import openblocks.sync.SyncableDirection;
import openblocks.utils.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class OpenBlock extends BlockContainer {

	private String uniqueBlockId;
	private Class<? extends TileEntity> teClass = null;
	protected String modKey = "";
	protected BlockRotationMode blockRotationMode;
	protected BlockPlacementMode blockPlacementMode;
	
	@SideOnly(Side.CLIENT)
	private ItemRenderState itemRenderState;
	
	
	protected OpenBlock(int id, Material material) {
		super(id, material);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHardness(1.0F);
		setRotationMode(BlockRotationMode.NONE);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
	}
	
	protected void setPlacementMode(BlockPlacementMode mode) {
		this.blockPlacementMode = mode;
	}

	protected void setRotationMode(BlockRotationMode mode) {
		this.blockRotationMode = mode;
	}
	
	protected BlockRotationMode getRotationMode() {
		return this.blockRotationMode;
	}
	
	protected BlockPlacementMode getPlacementMode() {
		return this.blockPlacementMode;
	}
	
	@SideOnly(Side.CLIENT)
	private void ensureRenderState() {
		if(itemRenderState == null) itemRenderState = new ItemRenderState();
	}
	
	@SideOnly(Side.CLIENT)
	protected void setRenderDirection(ForgeDirection inventoryRenderDirection) {
		ensureRenderState();
		this.itemRenderState.rotation = inventoryRenderDirection;
	}
	
	@SideOnly(Side.CLIENT)
	public ForgeDirection getRenderDirection() {
		if(this.itemRenderState == null) return BlockUtils.DEFAULT_BLOCK_DIRECTION;
		return this.itemRenderState.rotation;
	}
	
	/**
	 * Helper function to get the OpenBlock class for a block in the world
	 * @param world world to get the block from 
	 * @param x X coord
	 * @param y Y coord
	 * @param z Z coord
	 * @return OpenBlock instance of the block, or null if invalid
	 */
	public static OpenBlock getOpenBlock(IBlockAccess world, int x, int y, int z) {
		if(world == null) return null;
		int id = world.getBlockId(x, y, z);
		if(id < 0 || id >= Block.blocksList.length) return null;
		Block block = Block.blocksList[id];
		if(block instanceof OpenBlock) return (OpenBlock)block;
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		try {
			if (teClass != null) { return teClass.getConstructor(new Class[0]).newInstance(); }
		} catch (NoSuchMethodException nsm) {
			Log.warn(nsm, "Notice: Cannot create TE automatically due to constructor requirements");
		} catch (Exception ex) {
			Log.warn(ex, "Notice: Error creating tile entity");
		}
		return null;
	}

	@Override
	public void registerIcons(IconRegister registry) {
		this.blockIcon = registry.registerIcon(String.format("%s:%s", modKey, uniqueBlockId));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IInventory) {
			BlockUtils.dropTileInventory(te);
		}
		if (te instanceof IAwareTile) {
			((IAwareTile)te).onBlockBroken();
		}
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
		modKey = OpenBlocks.getModId().toLowerCase();

		GameRegistry.registerBlock(instance, itemClass, String.format("%s_%s", modKey, uniqueName));
		instance.setUnlocalizedName(String.format("%s.%s", modKey, uniqueName));

		if (tileEntity != null) {
			GameRegistry.registerTileEntity(tileEntity, String.format("%s_%s", modKey, uniqueName));
			this.teClass = tileEntity;
		}
	}

	public final static boolean isNeighborBlockSolid(World world, int x, int y, int z, ForgeDirection side) {
		x += side.offsetX;
		y += side.offsetY;
		z += side.offsetZ;
		return world.isBlockSolidOnSide(x, y, z, side.getOpposite());
	}

	public final static boolean areNeighborBlocksSolid(World world, int x, int y, int z, ForgeDirection... sides) {
		for (ForgeDirection side : sides) {
			if (isNeighborBlockSolid(world, x, y, z, side)) { return true; }
		}
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof INeighbourAwareTile) {
			((INeighbourAwareTile)te).onNeighbourChanged(blockId);
		}
		if (te instanceof ISurfaceAttachment) {
			ForgeDirection direction = ((ISurfaceAttachment)te).getSurfaceDirection();
			if (!isNeighborBlockSolid(world, x, y, z, direction)) {
				dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockToAir(x, y, z);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		IActivateAwareTile te = getTileEntity(world, x, y, z, IActivateAwareTile.class);
		if (te != null) { return te.onBlockActivated(player, side, hitX, hitY, hitZ); }
		return false;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		IAwareTile te = getTileEntity(world, x, y, z, IAwareTile.class);
		if (te != null) {
			te.onBlockAdded();
		}
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventParam) {
		IAwareTile te = getTileEntity(world, x, y, z, IAwareTile.class);
		if (te != null) { return te.onBlockEventReceived(eventId, eventParam); }
		return super.onBlockEventReceived(world, x, y, z, eventId, eventParam);
	}

	protected void setupDimensionsFromCenter(float x, float y, float z, float width, float height, float depth) {
		setupDimensions(x - width, y, z - depth, x + width, y + height, z
				+ depth);
	}

	protected void setupDimensions(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@SuppressWarnings("unchecked")
	public <U> U getTileEntity(IBlockAccess world, int x, int y, int z, Class<U> T) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && T.isAssignableFrom(te.getClass())) { return (U)te; }
		return null;
	}
	
	/***
	 * An extended block placement function which includes ALL the details
	 * you'll ever need.
	 * This is called if your ItemBlock extends ItemOpenBlock
	 * 
	 * @param world
	 * @param player
	 * @param stack
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @param hitX
	 * @param hitY
	 * @param hitZ
	 * @param meta
	 */
	public void onBlockPlacedBy(World world, EntityPlayer player, ItemStack stack, int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ, int meta) {
		ForgeDirection additionalRotation = null;
		
		// We use both for 24's, so force to angle
		if (getRotationMode() == BlockRotationMode.TWENTYFOUR_DIRECTIONS) {
			setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		}
		
		switch(getPlacementMode()) {
			case SURFACE:
				meta = side.getOpposite().ordinal();
				break;
			default:
				switch(getRotationMode()) {
					case FOUR_DIRECTIONS:
						meta = BlockUtils.get2dOrientation(player).ordinal();
						break;
					case SIX_DIRECTIONS:
						meta = BlockUtils.get3dOrientation(player).ordinal();
						break;
					case TWENTYFOUR_DIRECTIONS:
						meta = side.getOpposite().ordinal();
						additionalRotation = BlockUtils.get2dOrientation(player);
						break;
					default:
						break;
				}
		}
		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
		IPlaceAwareTile te = null;
		if (additionalRotation != null) {
			NetworkedTileEntity nTe = getTileEntity(world, x, y, z, NetworkedTileEntity.class);
			if (nTe != null) {
				nTe.addSpecialObject(new SyncableDirection(additionalRotation));
				nTe.sync();
			}else {
				new Exception("For 6+ levels of rotation you need to use a NetworkedTileEntity").printStackTrace();
			}
		}
		te = getTileEntity(world, x, y, z, IPlaceAwareTile.class);
		if (te != null) {
			te.onBlockPlacedBy(player, side, stack, hitX, hitY, hitZ);
		}
	}

	@Override
	public final boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		return canPlaceBlockOnSide(world, x, y, z, ForgeDirection.getOrientation(side).getOpposite());
	}

	/***
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @return
	 */
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return canPlaceBlockAt(world, x, y, z); // default to vanilla rules
	}

	protected boolean isOnTopOfSolidBlock(World world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.DOWN
				&& isNeighborBlockSolid(world, x, y, z, ForgeDirection.DOWN);
	}
	
	public enum BlockRotationMode {
		NONE,
		FOUR_DIRECTIONS,
		SIX_DIRECTIONS,
		TWENTYFOUR_DIRECTIONS
	}
	
	public enum BlockPlacementMode {
		ENTITY_ANGLE,
		SURFACE
	}
}
