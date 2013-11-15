package openblocks.common.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.common.api.*;
import openblocks.common.item.ItemOpenBlock;
import openblocks.common.tileentity.SyncedTileEntity;
import openblocks.common.tileentity.OpenTileEntity;
import openblocks.sync.SyncableDirection;
import openblocks.utils.BlockUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class OpenBlock extends Block {

	/***
	 * The block rotation mode. Defines how many levels of rotation
	 * a block can have
	 */
	public enum BlockRotationMode {
		NONE,
		FOUR_DIRECTIONS,
		SIX_DIRECTIONS,
		TWENTYFOUR_DIRECTIONS
	}

	/***
	 * The block placement mode. Does it rotate based on the surface
	 * it's placed on, or the
	 */
	public enum BlockPlacementMode {
		ENTITY_ANGLE,
		SURFACE
	}

	/**
	 * The unique ID of this block
	 */
	private String uniqueBlockId;

	/**
	 * The tile entity class associated with this block
	 */
	private Class<? extends OpenTileEntity> teClass = null;
	protected BlockRotationMode blockRotationMode;
	protected BlockPlacementMode blockPlacementMode;
	protected ForgeDirection inventoryRenderRotation = ForgeDirection.WEST;

	public Icon[] textures = new Icon[6];

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

	protected void setInventoryRenderRotation(ForgeDirection rotation) {
		inventoryRenderRotation = rotation;
	}

	@Override
	public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (world.isRemote) return false;

		if ((!player.capabilities.isCreativeMode)) {
			int metadata = world.getBlockMetadata(x, y, z);
			ArrayList<ItemStack> items = getBlockDropped(world, x, y, z, metadata, 0);
			Iterator<ItemStack> it = items.iterator();
			while (it.hasNext()) {
				ItemStack item = it.next();
				dropBlockAsItem_do(world, x, y, z, item);
			}
		}
		return super.removeBlockByPlayer(world, player, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public ForgeDirection getInventoryRenderRotation() {
		return inventoryRenderRotation;
	}

	/**
	 * Set block bounds based on rotation
	 * 
	 * @param direction
	 *            direction to apply bounds to
	 */
	public void setBoundsBasedOnRotation(ForgeDirection direction) {

	}

	/**
	 * Helper function to get the OpenBlock class for a block in the world
	 * 
	 * @param world
	 *            world to get the block from
	 * @param x
	 *            X coord
	 * @param y
	 *            Y coord
	 * @param z
	 *            Z coord
	 * @return OpenBlock instance of the block, or null if invalid
	 */
	public static OpenBlock getOpenBlock(IBlockAccess world, int x, int y, int z) {
		if (world == null) return null;
		int id = world.getBlockId(x, y, z);
		if (id < 0 || id >= Block.blocksList.length) return null;
		Block block = Block.blocksList[id];
		if (block instanceof OpenBlock) return (OpenBlock)block;
		return null;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		OpenTileEntity te = null;
		try {
			if (teClass != null) {
				te = teClass.getConstructor(new Class[0]).newInstance();
			}
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

	public Class<? extends TileEntity> getTileClass() {
		return teClass;
	}

	@Override
	public void registerIcons(IconRegister registry) {
		this.blockIcon = registry.registerIcon(String.format("%s:%s", "openblocks", uniqueBlockId));
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

		GameRegistry.registerBlock(instance, itemClass, String.format("%s_%s", "openblocks", uniqueName));
		instance.setUnlocalizedName(String.format("%s.%s", "openblocks", uniqueName));

		if (tileEntity != null) {
			GameRegistry.registerTileEntity(tileEntity, String.format("%s_%s", "openblocks", uniqueName));
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
	public boolean renderAsNormalBlock() {
		return isOpaqueCube();
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventParam) {
		super.onBlockEventReceived(world, x, y, z, eventId, eventParam);
		TileEntity te = getTileEntity(world, x, y, z, TileEntity.class);
		if (te != null) {
			return te.receiveClientEvent(eventId, eventParam);
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

		switch (getPlacementMode()) {
			case SURFACE:
				meta = side.getOpposite().ordinal();
				break;
			default:
				switch (getRotationMode()) {
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
			SyncedTileEntity nTe = getTileEntity(world, x, y, z, SyncedTileEntity.class);
			if (nTe != null) {
				nTe.addSyncedObject("_rotation2", new SyncableDirection(additionalRotation));
				nTe.sync();
			} else {
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

	public void setTexture(ForgeDirection direction, Icon icon) {
		textures[direction.ordinal()] = icon;
	}

	/**
	 * This method should be overriden if needed. We're getting the texture for the UNROTATED block
	 * for a particular side (direction). Feel free to look up data in the TileEntity to grab
	 * additional information here
	 * @param direction
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Icon getUnrotatedTexture(ForgeDirection direction, IBlockAccess world, int x, int y, int z) {
		if (direction != ForgeDirection.UNKNOWN) {
			if (textures[direction.ordinal()] != null) { return textures[direction.ordinal()]; }
		}
		return blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	/**
	 * Get the texture, but rotate the block around the metadata rotation first
	 */
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		ForgeDirection direction = rotateSideByMetadata(side, world.getBlockMetadata(x, y, z));
		return getUnrotatedTexture(direction, world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	/***
	 * I'm sure there's a better way of doing this, but the idea is that we rotate the
	 * block based on the metadata (rotation), so when we try to get a texture we're
	 * referencing the side when 'unrotated'
	 */
	public ForgeDirection rotateSideByMetadata(int side, int metadata) {
		ForgeDirection rotation = ForgeDirection.getOrientation(metadata);
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		switch (getRotationMode()) {
			case FOUR_DIRECTIONS:
			case NONE:
				switch (rotation) {
					case EAST:
						dir = dir.getRotation(ForgeDirection.DOWN);
						break;
					case SOUTH:
						dir = dir.getRotation(ForgeDirection.UP);
						dir = dir.getRotation(ForgeDirection.UP);
						break;
					case WEST:
						dir = dir.getRotation(ForgeDirection.UP);
						break;
					default:
						break;
				}
				return dir;
			default:
				switch (rotation) {
					case DOWN:
						dir = dir.getRotation(ForgeDirection.SOUTH);
						dir = dir.getRotation(ForgeDirection.SOUTH);
						break;
					case EAST:
						dir = dir.getRotation(ForgeDirection.NORTH);
						break;
					case NORTH:
						dir = dir.getRotation(ForgeDirection.WEST);
						break;
					case SOUTH:
						dir = dir.getRotation(ForgeDirection.EAST);
						break;
					case WEST:
						dir = dir.getRotation(ForgeDirection.SOUTH);
						break;
					default:
						break;

				}
		};
		return dir;
	}

	@Override
	@SideOnly(Side.CLIENT)
	/***
	 * This is called by the blockrenderer when rendering an item into the inventory.
	 * We'll return the block, rotated as we wish, but without any additional texture
	 * changes that are caused by the blocks current state
	 */
	public Icon getIcon(int side, int metadata) {
		ForgeDirection newRotation = rotateSideByMetadata(side, metadata);
		if (newRotation != ForgeDirection.UNKNOWN) {
			int index = newRotation.ordinal();
			if (textures[index] != null) { return textures[index]; }
		}
		return blockIcon;
	}

	public void setDefaultTexture(Icon icon) {
		this.blockIcon = icon;
	}

	public abstract boolean shouldRenderBlock();

	public boolean useTESRForInventory() {
		return true;
	}

	@SuppressWarnings({ "rawtypes" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		
	}
}
