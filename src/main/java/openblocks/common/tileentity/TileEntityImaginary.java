package openblocks.common.tileentity;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.block.BlockImaginary;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginationGlasses;
import openmods.OpenMods;
import openmods.api.ICustomPickItem;
import openmods.tileentity.OpenTileEntity;

public class TileEntityImaginary extends OpenTileEntity implements ICustomPickItem {

	public enum Property {
		VISIBLE,
		SELECTABLE,
		SOLID
	}

	public enum LegacyCollisionData {
		BLOCK {
			@Override
			public BlockImaginary.Shape readFromNBT(NBTTagCompound tag) {
				return BlockImaginary.Shape.BLOCK;
			}
		},
		PANEL {

			@Override
			public BlockImaginary.Shape readFromNBT(NBTTagCompound tag) {
				final float height = tag.getFloat("PanelHeight");
				return (height < 0.75)? BlockImaginary.Shape.HALF_PANEL : BlockImaginary.Shape.PANEL;
			}

		},
		STAIRS {
			@Override
			public BlockImaginary.Shape readFromNBT(NBTTagCompound tag) {
				return BlockImaginary.Shape.STAIRS;
			}

		};

		public static final LegacyCollisionData[] VALUES = values();

		public abstract BlockImaginary.Shape readFromNBT(NBTTagCompound tag);
	}

	public interface ILegacyCollisionData {
		public BlockImaginary.Shape readFromNBT(NBTTagCompound tag);
	}

	@SideOnly(Side.CLIENT)
	public float visibility;

	public TileEntityImaginary() {
		shape = BlockImaginary.Shape.BLOCK;
	}

	public TileEntityImaginary(Integer color, boolean isInverted, BlockImaginary.Shape shape) {
		Preconditions.checkNotNull(shape, "Bad idea! Rejected!");
		this.color = color;
		this.isInverted = isInverted;
		this.shape = shape;
	}

	public Integer color;
	public boolean isInverted;
	private BlockImaginary.Shape shape;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readShapeData(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);

		writeShapeData(tag);

		return tag;
	}

	private void readShapeData(NBTTagCompound tag) {
		color = tag.hasKey("Color")? tag.getInteger("Color") : null;
		isInverted = tag.getBoolean("IsInverted");

		if (tag.hasKey("Type", Constants.NBT.TAG_BYTE)) {
			LegacyCollisionData data = LegacyCollisionData.VALUES[tag.getByte("Type")];
			shape = data.readFromNBT(tag);
		} else {
			byte shapeId = tag.getByte("Shape");
			shape = BlockImaginary.Shape.VALUES[shapeId];
		}
	}

	private NBTTagCompound writeShapeData(NBTTagCompound tag) {
		if (color != null) tag.setInteger("Color", color);
		tag.setBoolean("IsInverted", isInverted);
		tag.setByte("Shape", (byte)shape.ordinal());
		return tag;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 42, writeShapeData(new NBTTagCompound()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readShapeData(pkt.getNbtCompound());
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	public boolean isPencil() {
		return color == null;
	}

	public boolean isInverted() {
		return isInverted;
	}

	public boolean is(Property what, EntityPlayer player) {
		if (what == Property.VISIBLE && player.isSpectator()) return true;
		if (what == Property.SOLID && isPencil()) return true;

		ItemStack helmet = player.inventory.armorItemInSlot(3);

		if (helmet == null) return isInverted();

		Item item = helmet.getItem();

		if (item instanceof ItemImaginationGlasses) return ((ItemImaginationGlasses)item).checkBlock(what, helmet, this);

		return isInverted();
	}

	public boolean is(Property what, Entity e) {
		return (e instanceof EntityPlayer) && is(what, (EntityPlayer)e);
	}

	public boolean is(Property what) {
		EntityPlayer player = OpenMods.proxy.getThePlayer();
		return player != null && is(what, player);
	}

	@Override
	@Nonnull
	public ItemStack getPickBlock(EntityPlayer player) {
		int dmg = isPencil()? ItemImaginary.DAMAGE_PENCIL : ItemImaginary.DAMAGE_CRAYON;
		return ItemImaginary.setupValues(new ItemStack(getBlockType(), 1, dmg), color, shape, isInverted);
	}

	public BlockImaginary.Shape getShape() {
		return shape;
	}

	public BlockImaginary.Type getType() {
		return isPencil()? BlockImaginary.Type.PENCIL : BlockImaginary.Type.CRAYON;
	}

	@Override
	public boolean hasFastRenderer() {
		return true;
	}
}
