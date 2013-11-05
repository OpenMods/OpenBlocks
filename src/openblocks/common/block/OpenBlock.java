package openblocks.common.block;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
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
import openblocks.common.api.*;
import openblocks.common.item.ItemOpenBlock;
import openblocks.common.tileentity.NetworkedTileEntity;
import openblocks.common.tileentity.OpenTileEntity;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableDirection;
import openblocks.utils.BlockUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class OpenBlock extends Block {

	private String uniqueBlockId;
	private Class<? extends OpenTileEntity> teClass = null;
	protected String modKey = "";
	protected BlockRotationMode blockRotationMode;
	protected BlockPlacementMode blockPlacementMode;
	protected ForgeDirection inventortyRenderDirection = ForgeDirection.WEST;
	
	protected OpenBlock(int id, Material material) {
		super(id, material);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHardness(1.0F);
		setRotationMode(BlockRotationMode.NONE);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		
		// I dont think vanilla actually uses this..
		isBlockContainer = false;
	}
	
	protected void setPlacementMode(BlockPlacementMode mode) {
		this.blockPlacementMode = mode;
	}

	protected void setRotationMode(BlockRotationMode mode) {
		this.blockRotationMode = mode;
	}
	
	public BlockRotationMode getRotationMode() {
		return this.blockRotationMode;
	}
	
	protected BlockPlacementMode getPlacementMode() {
		return this.blockPlacementMode;
	}
	
	@SideOnly(Side.CLIENT)
	protected void setInventoryRenderDirection(ForgeDirection inventoryRenderDirection) {
		inventortyRenderDirection = inventoryRenderDirection;
	}
	
	@SideOnly(Side.CLIENT)
	public ForgeDirection getInventoryRenderDirection() {
		return inventortyRenderDirection;
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
	public TileEntity createTileEntity(World world, int metadata) {
		OpenTileEntity te = null;
		try {
			if (teClass != null) { te = teClass.getConstructor(new Class[0]).newInstance(); }
		} catch (NoSuchMethodException nsm) {
			Log.warn(nsm, "Notice: Cannot create TE automatically due to constructor requirements");
		} catch (Exception ex) {
			Log.warn(ex, "Notice: Error creating tile entity");
		}
		if (te != null) {
			te.blockType = this;
			te.setup();
		}
		return te;
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
        world.removeBlockTileEntity(x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	public void setupBlock(Block instance, String uniqueName) {
		setupBlock(instance, uniqueName, null);
	}

	public void setupBlock(Block instance, String uniqueName, Class<? extends OpenTileEntity> tileEntity) {
		setupBlock(instance, uniqueName, tileEntity, ItemOpenBlock.class);
	}

	public void setupBlock(Block instance, String uniqueName, Class<? extends OpenTileEntity> tileEntity, Class<? extends ItemOpenBlock> itemClass) {
		uniqueBlockId = uniqueName;
		modKey = OpenBlocks.getModId().toLowerCase();

		GameRegistry.registerBlock(instance, itemClass, String.format("%s_%s", modKey, uniqueName));
		instance.setUnlocalizedName(String.format("%s.%s", modKey, uniqueName));

		if (tileEntity != null) {
			GameRegistry.registerTileEntity(tileEntity, String.format("%s_%s", modKey, uniqueName));
			this.teClass = tileEntity;
			isBlockContainer = true;
		}
	}
	
    public boolean hasTileEntity(int metadata) {
        return teClass != null;
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
		super.onBlockEventReceived(world, x, y, z, eventId, eventParam);
		IAwareTile te = getTileEntity(world, x, y, z, IAwareTile.class);
		if (te != null) {
			TileEntity tile = (TileEntity) te;
			return tile.receiveClientEvent(eventId, eventParam);
		}
		return false;
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
				nTe.addSyncedObject("_rotation2", new SyncableDirection(additionalRotation));
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
	
	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
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
