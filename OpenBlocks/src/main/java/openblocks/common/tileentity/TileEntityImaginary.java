package openblocks.common.tileentity;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.block.BlockImaginary;
import openblocks.common.item.ItemImaginationGlasses;
import openmods.OpenMods;
import openmods.api.ICustomPickItem;
import openmods.tileentity.OpenTileEntity;

public abstract class TileEntityImaginary extends OpenTileEntity implements ICustomPickItem {

	public enum Property {
		VISIBLE,
		SELECTABLE,
		SOLID
	}

	public enum LegacyCollisionData {
		BLOCK {
			@Override
			public BlockImaginary.Shape readFromNBT(CompoundNBT tag) {
				return BlockImaginary.Shape.BLOCK;
			}
		},
		PANEL {
			@Override
			public BlockImaginary.Shape readFromNBT(CompoundNBT tag) {
				final float height = tag.getFloat("PanelHeight");
				return (height < 0.75)? BlockImaginary.Shape.HALF_PANEL : BlockImaginary.Shape.PANEL;
			}

		},
		STAIRS {
			@Override
			public BlockImaginary.Shape readFromNBT(CompoundNBT tag) {
				return BlockImaginary.Shape.STAIRS;
			}

		};

		public static final LegacyCollisionData[] VALUES = values();

		public abstract BlockImaginary.Shape readFromNBT(CompoundNBT tag);
	}

	public interface ILegacyCollisionData {
		BlockImaginary.Shape readFromNBT(CompoundNBT tag);
	}

	@SideOnly(Side.CLIENT)
	public float visibility;

	public TileEntityImaginary() {
		shape = BlockImaginary.Shape.BLOCK;
	}

	public void setup(boolean isInverted, BlockImaginary.Shape shape) {
		this.isInverted = isInverted;
		this.shape = shape;
	}

	protected boolean isInverted;
	protected BlockImaginary.Shape shape;

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		readShapeData(tag);
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		writeShapeData(tag);
		return tag;
	}

	private void readShapeData(CompoundNBT tag) {
		isInverted = tag.getBoolean("IsInverted");

		if (tag.hasKey("Type", Constants.NBT.TAG_BYTE)) {
			LegacyCollisionData data = LegacyCollisionData.VALUES[tag.getByte("Type")];
			shape = data.readFromNBT(tag);
		} else {
			byte shapeId = tag.getByte("Shape");
			shape = BlockImaginary.Shape.VALUES[shapeId];
		}
	}

	private CompoundNBT writeShapeData(CompoundNBT tag) {
		tag.setBoolean("IsInverted", isInverted);
		tag.setByte("Shape", (byte)shape.ordinal());
		return tag;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return writeToNBT(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getPos(), 42, writeShapeData(new CompoundNBT()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readShapeData(pkt.getNbtCompound());
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	public boolean isInverted() {
		return isInverted;
	}

	public boolean is(Property what, PlayerEntity player) {
		if (what == Property.VISIBLE && player.isSpectator()) return true;
		if (what == Property.SOLID && isAlwaysSolid()) return true;

		final ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
		Item item = helmet.getItem();
		if (item instanceof ItemImaginationGlasses) return ((ItemImaginationGlasses)item).checkBlock(what, helmet, this);

		return isInverted();
	}

	public boolean is(Property what, Entity e) {
		return (e instanceof PlayerEntity) && is(what, (PlayerEntity)e);
	}

	public boolean is(Property what) {
		PlayerEntity player = OpenMods.proxy.getThePlayer();
		return player != null && is(what, player);
	}

	@Override
	@Nonnull
	public abstract ItemStack getPickBlock(PlayerEntity player);

	public BlockImaginary.Shape getShape() {
		return shape;
	}

	@Override
	public boolean hasFastRenderer() {
		return true;
	}

	public abstract boolean isAlwaysSolid();

	public abstract int getColor();
}
