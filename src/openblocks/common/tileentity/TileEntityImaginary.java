package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemImaginationGlasses;

import com.google.common.base.Preconditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityImaginary extends OpenTileEntity {

	public static final double PANEL_HEIGHT = 0.1;

	public enum Property {
		VISIBLE, SELECTABLE, SOLID
	}

	private enum CollisionType {
		BLOCK {
			@Override
			public ICollisionData createData() {
				return DUMMY;
			}
		},
		PANEL {
			@Override
			public ICollisionData createData() {
				return new PanelData();
			}
		},
		STAIRS {
			@Override
			public ICollisionData createData() {
				return new StairsData();
			}
		};

		public abstract ICollisionData createData();

		public final static CollisionType[] VALUES = values();
	}

	public interface ICollisionData {
		public CollisionType getType();

		public void readFromNBT(NBTTagCompound tag);

		public void writeToNBT(NBTTagCompound tag);

		public void addCollisions(int x, int y, int z, AxisAlignedBB region, List<AxisAlignedBB> result);

		public AxisAlignedBB getBlockBounds();
	}

	public final static ICollisionData DUMMY = new ICollisionData() {

		@Override
		public void readFromNBT(NBTTagCompound tag) {}

		@Override
		public void writeToNBT(NBTTagCompound tag) {}

		@Override
		public CollisionType getType() {
			return CollisionType.BLOCK;
		}

		@Override
		public void addCollisions(int x, int y, int z, AxisAlignedBB region, List<AxisAlignedBB> result) {
			AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 1, z + 1);
			if (aabb != null && aabb.intersectsWith(region)) result.add(aabb);
		}

		@Override
		public AxisAlignedBB getBlockBounds() {
			return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 1, 1, 1);
		}
	};

	public static class PanelData implements ICollisionData {
		public float height;

		public PanelData() {}

		public PanelData(float height) {
			this.height = height;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			height = tag.getFloat("PanelHeight");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			tag.setFloat("PanelHeight", height);
		}

		@Override
		public CollisionType getType() {
			return CollisionType.PANEL;
		}

		@Override
		public void addCollisions(int x, int y, int z, AxisAlignedBB region, List<AxisAlignedBB> result) {
			AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(x, y
					+ height - PANEL_HEIGHT, z, x + 1, y + height, z + 1);
			if (aabb != null && aabb.intersectsWith(region)) result.add(aabb);
		}

		@Override
		public AxisAlignedBB getBlockBounds() {
			return AxisAlignedBB.getAABBPool().getAABB(0, height - PANEL_HEIGHT, 0, 1, height, 1);
		}
	}

	public static class StairsData implements ICollisionData {
		public float lowerPanelHeight;
		public float upperPanelHeight;
		public ForgeDirection orientation;

		public StairsData() {}

		public StairsData(float lowerPanelHeight, float upperPanelHeight, ForgeDirection orientation) {
			this.lowerPanelHeight = lowerPanelHeight;
			this.upperPanelHeight = upperPanelHeight;
			this.orientation = orientation;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			lowerPanelHeight = tag.getFloat("LowerPanelHeight");
			upperPanelHeight = tag.getFloat("UpperPanelHeight");
			orientation = ForgeDirection.getOrientation(tag.getByte("Orientation"));
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			tag.setFloat("LowerPanelHeight", lowerPanelHeight);
			tag.setFloat("UpperPanelHeight", upperPanelHeight);
			tag.setByte("Orientation", (byte)orientation.ordinal());
		}

		@Override
		public CollisionType getType() {
			return CollisionType.STAIRS;
		}

		@Override
		public void addCollisions(int x, int y, int z, AxisAlignedBB region, List<AxisAlignedBB> result) {
			AxisAlignedBB lower;
			AxisAlignedBB upper;

			final double lowerTop = y + lowerPanelHeight;
			final double lowerBottom = lowerTop - PANEL_HEIGHT;

			final double upperTop = y + upperPanelHeight;
			final double upperBottom = upperTop - PANEL_HEIGHT;

			switch (orientation) {
				case NORTH:
					lower = AxisAlignedBB.getAABBPool().getAABB(x, lowerBottom, z + 0.5, x + 1, lowerTop, z + 1.0);
					upper = AxisAlignedBB.getAABBPool().getAABB(x, upperBottom, z + 0.0, x + 1, upperTop, z + 0.5);
					break;
				case SOUTH:
					lower = AxisAlignedBB.getAABBPool().getAABB(x, lowerBottom, z + 0.0, x + 1, lowerTop, z + 0.5);
					upper = AxisAlignedBB.getAABBPool().getAABB(x, upperBottom, z + 0.5, x + 1, upperTop, z + 1.0);
					break;
				case WEST:
					lower = AxisAlignedBB.getAABBPool().getAABB(x + 0.5, lowerBottom, z, x + 1.0, lowerTop, z + 1);
					upper = AxisAlignedBB.getAABBPool().getAABB(x + 0.0, upperBottom, z, x + 0.5, upperTop, z + 1);
					break;
				case EAST:
					lower = AxisAlignedBB.getAABBPool().getAABB(x + 0.0, lowerBottom, z, x + 0.5, lowerTop, z + 1);
					upper = AxisAlignedBB.getAABBPool().getAABB(x + 0.5, upperBottom, z, x + 1.0, upperTop, z + 1);
					break;
				default:
					lower = upper = null;
					break;
			}

			if (lower != null && lower.intersectsWith(region)) result.add(lower);
			if (upper != null && upper.intersectsWith(region)) result.add(upper);
		}

		@Override
		public AxisAlignedBB getBlockBounds() {
			return AxisAlignedBB.getAABBPool().getAABB(0, lowerPanelHeight, 0, 1, upperPanelHeight, 1);
		}
	}

	@SideOnly(Side.CLIENT)
	public float visibility;

	public TileEntityImaginary() {
		collisionData = DUMMY;
	}

	public TileEntityImaginary(Integer color, boolean isInverted, ICollisionData collisionData) {
		Preconditions.checkNotNull(collisionData, "Bad idea! Rejected!");
		this.color = color;
		this.isInverted = isInverted;
		this.collisionData = collisionData;
	}

	public Integer color;
	public boolean isInverted;
	public ICollisionData collisionData;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		color = tag.hasKey("Color")? tag.getInteger("Color") : null;
		isInverted = tag.getBoolean("IsInverted");
		CollisionType type = CollisionType.VALUES[tag.getByte("Type")];
		collisionData = type.createData();
		collisionData.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		if (color != null) tag.setInteger("Color", color);
		tag.setBoolean("IsInverted", isInverted);
		tag.setByte("Type", (byte)collisionData.getType().ordinal());
		collisionData.writeToNBT(tag);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound data = new NBTTagCompound();
		writeToNBT(data);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 42, data);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		if (pkt.data != null) readFromNBT(pkt.data);
	}

	public boolean isPencil() {
		return color == null;
	}

	public boolean isInverted() {
		return isInverted;
	}

	public boolean is(Property what, EntityPlayer player) {
		if (what == Property.SOLID && isPencil()) return true;

		ItemStack helmet = player.inventory.armorItemInSlot(3);

		if (helmet == null) return isInverted();

		Item item = helmet.getItem();

		if (item instanceof ItemImaginationGlasses) return ((ItemImaginationGlasses)item).checkBlock(what, helmet, this);

		return false;
	}

	public boolean is(EntityPlayer player) {
		return player.getHeldItem() != null;
	}

	public boolean is(Property what, Entity e) {
		return (e instanceof EntityPlayer) && is(what, (EntityPlayer)e);
	}

	public boolean is(Property what) {
		EntityPlayer player = OpenBlocks.proxy.getThePlayer();
		return player != null && is(what, player);
	}

	public void addCollisions(AxisAlignedBB region, List<AxisAlignedBB> result) {
		collisionData.addCollisions(xCoord, yCoord, zCoord, region, result);
	}

	public AxisAlignedBB getSelectionBox() {
		return collisionData.getBlockBounds().offset(xCoord, yCoord, zCoord);
	}

	public AxisAlignedBB getBlockBounds() {
		return collisionData.getBlockBounds();
	}
}
