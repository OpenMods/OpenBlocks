package openblocks.common.entity;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBlock extends Entity implements IEntityAdditionalSpawnData {

	private static final int OBJECT_BLOCK_ID = 11;
	private static final int OBJECT_BLOCK_META = 12;
	private boolean hasGravity = false;

	public static final ForgeDirection[] PLACE_DIRECTIONS = { ForgeDirection.UNKNOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST, ForgeDirection.DOWN };

	public EntityBlock(World world) {
		super(world);
		setSize(0.925F, 0.925F);
	}

	private void setHeight(float height) {
		this.height = height;
		yOffset = 0;
	}
	
	public static EntityBlock create(World world, int x, int y, int z) {
		return create(world, x, y, z, EntityBlock.class);
	}
	
	public static EntityBlock create(World world, int x, int y, int z, Class<? extends EntityBlock> klazz) {
		int blockId = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockId];

		if (block == null) return null;

		int meta = world.getBlockMetadata(x, y, z);

		EntityBlock entity = null;
		try {
			entity = klazz.getConstructor(World.class).newInstance(world);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (entity == null) return null;
		
		entity.setBlockIdAndMeta(blockId, meta);

		if (block instanceof BlockContainer) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te != null) {
				entity.tileEntity = te;
				te.invalidate();
				world.removeBlockTileEntity(x, y, z);
			}
		}

		world.setBlockToAir(x, y, z);

		return entity;
	}

	public TileEntity tileEntity;

	@Override
	protected void entityInit() {
		dataWatcher.addObject(OBJECT_BLOCK_ID, Block.bedrock.blockID);
		dataWatcher.addObject(OBJECT_BLOCK_META, 0);
	}

	public void setBlockIdAndMeta(int id, int meta) {
		this.dataWatcher.updateObject(OBJECT_BLOCK_ID, id);
		this.dataWatcher.updateObject(OBJECT_BLOCK_META, meta);
	}

	public int getBlockId() {
		return dataWatcher.getWatchableObjectInt(OBJECT_BLOCK_ID);
	}
	
	public Block getBlock() {
		int blockId = getBlockId();
		return Block.blocksList[blockId];
	}

	public int getBlockMeta() {
		return dataWatcher.getWatchableObjectInt(OBJECT_BLOCK_META);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		int blockId = tag.getInteger("BlockId");

		if (Block.blocksList[blockId] == null) {
			setDead();
			return;
		}

		int blockMeta = tag.getInteger("BlockMeta");
		setBlockIdAndMeta(blockId, blockMeta);

		NBTBase teTag = tag.getTag("TileEntity");

		if (teTag instanceof NBTTagCompound) {
			tileEntity = TileEntity.createAndLoadEntity((NBTTagCompound)teTag);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setInteger("BlockId", getBlockId());
		tag.setInteger("BlockMeta", getBlockMeta());

		if (tileEntity != null) {
			NBTTagCompound teTag = new NBTTagCompound();
			tileEntity.writeToNBT(teTag);
			tag.setTag("TileEntity", teTag);
		}
	}

	@Override
	public void onUpdate() {
		if (posY < -500.0D) {
			setDead();
			return;
		}
		
		if (hasGravity) {
	        motionY -= 0.03999999910593033D;
			motionX *= 0.98;
			motionY *= 0.98;
			motionZ *= 0.98;
		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		extinguish();
		moveEntity(motionX, motionY, motionZ);

		Block block = getBlock();
		if (block == null) setDead();
		else setHeight((float)block.getBlockBoundsMaxY());

		if (shouldPlaceBlock()) {
			int x = MathHelper.floor_double(posX);
			int y = MathHelper.floor_double(posY);
			int z = MathHelper.floor_double(posZ);

			if (!tryPlaceBlock(x, y, z)) {
				dropBlock();
			}

			setDead();
		}
	}

	protected boolean shouldPlaceBlock() {
		return onGround;
	}

	private boolean tryPlaceBlock(int baseX, int baseY, int baseZ) {
		for (ForgeDirection dir : PLACE_DIRECTIONS) {
			int x = baseX + dir.offsetX;
			int y = baseY + dir.offsetY;
			int z = baseZ + dir.offsetZ;
			if (!worldObj.isAirBlock(x, y, z)) continue;

			worldObj.setBlock(x, y, z, getBlockId(), getBlockMeta(), 3);

			if (tileEntity != null) {
				tileEntity.xCoord = x;
				tileEntity.yCoord = y;
				tileEntity.zCoord = z;
				tileEntity.validate();
				worldObj.setBlockTileEntity(x, y, z, tileEntity);
			}
			return true;
		}
		return false;
	}

	private void dropBlock() {
		ItemStack item = new ItemStack(getBlockId(), 1, getBlockMeta());

		entityDropItem(item, 0.1f);

		if (tileEntity instanceof IInventory) {
			IInventory inv = (IInventory)tileEntity;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack is = inv.getStackInSlot(i);
				if (is != null) entityDropItem(is, 0.1f);
			}
		}
	}
	
	public void setHasGravity(boolean gravity) {
		this.hasGravity = gravity;
	}
	
	public boolean hasGravity() {
		return hasGravity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@Override
	public boolean canBePushed() {
		return !isDead;
	}

	@Override
	protected void dealFireDamage(int i) {}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeBoolean(hasGravity);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		hasGravity = data.readBoolean();
	}

}
