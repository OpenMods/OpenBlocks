package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.api.IPaintableBlock;
import openblocks.common.Stencil;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.infobook.BookDocumentation;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.IPlantable;
import net.minecraft.entity.player.EntityPlayer;

@BookDocumentation
public class BlockCanvas extends OpenBlock implements IPaintableBlock {

	public static final int RENDER_ALL_SIDES = -1;

	public static final int BASE_LAYER = -1;

	public static final int NO_LAYER = -2;

	private int layer = 0;
	private int renderSide = 0;
	public IIcon baseIcon;
	public IIcon wallpaper;

	public BlockCanvas() {
		this(Material.sponge);
	}

	public BlockCanvas(Material material) {
		super(material);
	}

	@Override
	public void registerBlockIcons(IIconRegister registry) {
		blockIcon = baseIcon = registry.registerIcon("openblocks:canvas");
		wallpaper = registry.registerIcon("openblocks:wallpaper");
		for (Stencil stencil : Stencil.values())
			stencil.registerBlockIcons(registry);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer A, World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getPlayerRelativeBlockHardness(A, world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getPlayerRelativeBlockHardness(A, world, x, y, z);
		return block.getPlayerRelativeBlockHardness(A, world, x, y, z);
	}
	
	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int A, EffectRenderer B){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.addDestroyEffects(world, x, y, z, A, B);
		Block block = tile.getPaintedBlock();
		int meta = tile.getPaintedBlockMeta();
		if (block == Blocks.air)
			return super.addDestroyEffects(world, x, y, z, meta, B);
		return block.addDestroyEffects(world, x, y, z, meta, B);
	}

	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.canBeReplacedByLeaves(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.canBeReplacedByLeaves(world, x, y, z);
		return block.canBeReplacedByLeaves(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.canBlockStay(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.canBlockStay(world, x, y, z);
		return block.canBlockStay(world, x, y, z);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.canEntityDestroy(world, x, y, z, entity);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.canEntityDestroy(world, x, y, z, entity);
		return block.canEntityDestroy(world, x, y, z, entity);
	}
	
	@Override
	public boolean canPlaceTorchOnTop(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.canPlaceTorchOnTop(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.canPlaceTorchOnTop(world, x, y, z);
		return block.canPlaceTorchOnTop(world, x, y, z);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.canPlaceBlockAt(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.canPlaceBlockAt(world, x, y, z);
		return block.canPlaceBlockAt(world, x, y, z);
	}

	@Override
	public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.canSustainLeaves(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.canSustainLeaves(world, x, y, z);
		return block.canSustainLeaves(world, x, y, z);
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection A, IPlantable B){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.canSustainPlant(world, x, y, z, A, B);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.canSustainPlant(world, x, y, z, A, B);
		return block.canSustainPlant(world, x, y, z, A, B);
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 A, Vec3 B){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.collisionRayTrace(world, x, y, z, A, B);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.collisionRayTrace(world, x, y, z, A, B);
		return block.collisionRayTrace(world, x, y, z, A, B);
	}
	
	@Override
	public boolean isLeaves(IBlockAccess world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.isLeaves(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block.isLeaves(world, x, y, z))
			return true;
		else
			return super.isLeaves(world, x, y, z);
	}
	
	@Override
	public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getBlocksMovement(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getBlocksMovement(world, x, y, z);
		return block.getBlocksMovement(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		return block.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getComparatorInputOverride(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getComparatorInputOverride(world, x, y, z, A);
		return block.getComparatorInputOverride(world, x, y, z, A);
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getDamageValue(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getDamageValue(world, x, y, z);
		return block.getDamageValue(world, x, y, z);
	}
	
	@Override
	public float getEnchantPowerBonus(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getEnchantPowerBonus(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getEnchantPowerBonus(world, x, y, z);
		return block.getEnchantPowerBonus(world, x, y, z);
	}

	@Override
	public float getExplosionResistance(Entity A, World world, int x, int y, int z, double B, double C, double D){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getExplosionResistance(A, world, x, y, z, B, C, D);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getExplosionResistance(A, world, x, y, z, B, C, D);
		return block.getExplosionResistance(A, world, x, y, z, B, C, D);
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getFireSpreadSpeed(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getFireSpreadSpeed(world, x, y, z, A);
		return block.getFireSpreadSpeed(world, x, y, z, A);
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getFlammability(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getFlammability(world, x, y, z, A);
		return block.getFlammability(world, x, y, z, A);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition A, World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getPickBlock(A, world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getPickBlock(A, world, x, y, z);
		return block.getPickBlock(A, world, x, y, z);
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getSelectedBoundingBoxFromPool(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getSelectedBoundingBoxFromPool(world, x, y, z);
		return block.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public ForgeDirection[] getValidRotations(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getValidRotations(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getValidRotations(world, x, y, z);
		return block.getValidRotations(world, x, y, z);
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getWeakChanges(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getWeakChanges(world, x, y, z);
		return block.getWeakChanges(world, x, y, z);
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.isBlockSolid(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.isBlockSolid(world, x, y, z, A);
		return block.isBlockSolid(world, x, y, z, A);
	}

	@Override
	public boolean isBurning(IBlockAccess world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.isBurning(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.isBurning(world, x, y, z);
		return block.isBurning(world, x, y, z);
	}

	@Override
	public boolean isFertile(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.isFertile(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.isFertile(world, x, y, z);
		return block.isFertile(world, x, y, z);
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.isFireSource(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.isFireSource(world, x, y, z, A);
		return block.isFireSource(world, x, y, z, A);
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.isFlammable(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.isFlammable(world, x, y, z, A);
		return block.isFlammable(world, x, y, z, A);
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.isProvidingStrongPower(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.isProvidingStrongPower(world, x, y, z, A);
		return block.isProvidingStrongPower(world, x, y, z, A);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.isProvidingWeakPower(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.isProvidingWeakPower(world, x, y, z, A);
		return block.isProvidingWeakPower(world, x, y, z, A);
	}

	@Override
	public boolean isWood(IBlockAccess world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.isWood(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.isWood(world, x, y, z);
		return block.isWood(world, x, y, z);
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int A, int B){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.onBlockEventReceived(world, x, y, z, A, B);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.onBlockEventReceived(world, x, y, z, A, B);
		return block.onBlockEventReceived(world, x, y, z, A, B);
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.rotateBlock(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.rotateBlock(world, x, y, z, A);
		return block.rotateBlock(world, x, y, z, A);
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.shouldCheckWeakPower(world, x, y, z, A);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.shouldCheckWeakPower(world, x, y, z, A);
		return block.shouldCheckWeakPower(world, x, y, z, A);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB A, java.util.List B, Entity C){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null){
			super.addCollisionBoxesToList(world, x, y, z, A, B, C);
			return;
		}
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air){
			super.addCollisionBoxesToList(world, x, y, z, A, B, C);
			return;
		}
		block.addCollisionBoxesToList(world, x, y, z, A, B, C);
		return;
	}

	@Override
	public void fillWithRain(World world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null){
			super.fillWithRain(world, x, y, z);
			return;
		}
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air){
			super.fillWithRain(world, x, y, z);
			return;
		}
		block.fillWithRain(world, x, y, z);
		return;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null){
			super.onEntityCollidedWithBlock(world, x, y, z, A);
			return;
		}
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air){
			super.onEntityCollidedWithBlock(world, x, y, z, A);
			return;
		}
		block.onEntityCollidedWithBlock(world, x, y, z, A);
		return;
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null){
			super.onEntityWalking(world, x, y, z, A);
			return;
		}
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air){
			super.onEntityWalking(world, x, y, z, A);
			return;
		}
		block.onEntityWalking(world, x, y, z, A);
		return;
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity A, float B){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null){
			super.onFallenUpon(world, x, y, z, A, B);
			return;
		}
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air){
			super.onFallenUpon(world, x, y, z, A, B);
			return;
		}
		block.onFallenUpon(world, x, y, z, A, B);
		return;
	}

	@Override
	public void onPlantGrow(World world, int x, int y, int z, int A, int B, int C){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null){
			super.onPlantGrow(world, x, y, z, A, B, C);
			return;
		}
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air){
			super.onPlantGrow(world, x, y, z, A, B, C);
			return;
		}
		block.onPlantGrow(world, x, y, z, A, B, C);
		return;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null){
			super.setBlockBoundsBasedOnState(world, x, y, z);
			return;
		}
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air){
			super.setBlockBoundsBasedOnState(world, x, y, z);
			return;
		}
		block.setBlockBoundsBasedOnState(world, x, y, z);
		return;
	}

	@Override
	public void velocityToAddToEntity(World world, int x, int y, int z, Entity A, Vec3 B){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null){
			super.velocityToAddToEntity(world, x, y, z, A, B);
			return;
		}
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air){
			super.velocityToAddToEntity(world, x, y, z, A, B);
			return;
		}
		block.velocityToAddToEntity(world, x, y, z, A, B);
		return;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, java.util.Random A){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null){
			super.updateTick(world, x, y, z, A);
			return;
		}
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air){
			super.updateTick(world, x, y, z, A);
			return;
		}
		block.updateTick(world, x, y, z, A);
		return;
	}
	
	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z){
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return super.getLightOpacity(world, x, y, z);
		Block block = tile.getPaintedBlock();
		if (block == Blocks.air)
			return super.getLightOpacity(world, x, y, z);
		return block.getLightOpacity(world, x, y, z);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean canRenderInPass(int pass){
		if (pass == 1)
			return true;
		return false;
	}
	
	@Override
	public int getRenderBlockPass(){
		return 1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public void setLayerForRender(int layer) {
		this.layer = layer;
	}

	public void setSideForRender(int side) {
		this.renderSide = side;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile == null)
			return (renderSide == RENDER_ALL_SIDES || side == renderSide) && super.shouldSideBeRendered(world, x, y, z, side);
		Block block = tile.getPaintedBlock();
		if (block == this || block == Blocks.glass || !block.isOpaqueCube() || block.canRenderInPass(1)) { return false; }
		return (renderSide == RENDER_ALL_SIDES || side == renderSide) && super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		Block block = tile.getPaintedBlock();
		if (layer != NO_LAYER) {
			if (tile != null) return tile.getColorForRender(renderSide, layer);
		}

		return block.colorMultiplier(world, x, y, z);
	}

	@Override
	public IIcon getUnrotatedTexture(ForgeDirection direction, IBlockAccess world, int x, int y, int z) {
		if (layer != NO_LAYER) {
			TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
			if (tile != null) return tile.getTextureForRender(renderSide, layer);
		}

		return super.getUnrotatedTexture(direction, world, x, y, z);

	}

	public static void replaceBlock(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		if (block.getMaterial() == Material.glass) {
			world.setBlock(x, y, z, OpenBlocks.Blocks.canvasGlass);
		} else {
			world.setBlock(x, y, z, OpenBlocks.Blocks.canvas);
		}
		TileEntityCanvas tile = (TileEntityCanvas)world.getTileEntity(x, y, z);
		tile.setPaintedBlockBlock(block, meta, block.colorMultiplier(world, x, y, z));
	}

	@Override
	public boolean recolourBlockRGB(World world, int x, int y, int z, ForgeDirection side, int color) {
		final TileEntity te = world.getTileEntity(x, y, z);
		return (te instanceof TileEntityCanvas)? ((TileEntityCanvas)te).applyPaint(color, side) : false;
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
		ColorMeta color = ColorUtils.vanillaBlockToColor(colour);
		return recolourBlockRGB(world, x, y, z, side, color.rgb);
	}

}
