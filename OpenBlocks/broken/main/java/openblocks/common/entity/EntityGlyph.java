package openblocks.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import openblocks.OpenBlocks.Items;
import openblocks.OpenBlocks.Sounds;
import openblocks.common.item.ItemGlyph;

public class EntityGlyph extends HangingEntity implements IEntityAdditionalSpawnData {

	private static final String TAG_CHAR_INDEX = "CharIndex";

	private static final String TAG_OFFSET_X = "OffsetX";

	private static final String TAG_OFFSET_Y = "OffsetY";

	private int charIndex;

	private byte offsetX;

	private byte offsetY;

	private ItemStack itemStack = ItemStack.EMPTY;

	public EntityGlyph(World world) {
		super(world);
	}

	public EntityGlyph(World world, BlockPos pos, Direction facing, int charIndex, byte offsetX, byte offsetY) {
		super(world, pos);
		setCharIndex(charIndex);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		updateFacingWithBoundingBox(facing);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		final Direction normal = this.facingDirection;
		if (normal != null) {
			final Direction left = normal.rotateY();
			if (left.getAxis() == Direction.Axis.Z) {
				z -= (offsetX - 8) / 16.0;
			} else {
				x -= (offsetX - 8) / 16.0;
			}
			y -= (offsetY - 8) / 16.0;
		}

		super.setPosition(x, y, z);
	}

	private static final double DEPTH = 0.5D;

	@Override
	protected void updateBoundingBox() {
		final Direction normal = this.facingDirection;
		if (normal != null) {
			double centerX = this.hangingPosition.getX() + (8.0 / 16.0);
			double centerY = this.hangingPosition.getY() + (8.0 / 16.0);
			double centerZ = this.hangingPosition.getZ() + (8.0 / 16.0);

			// move towards wall
			centerX -= normal.getFrontOffsetX() * (16 - DEPTH) / 16 / 2;
			centerZ -= normal.getFrontOffsetZ() * (16 - DEPTH) / 16 / 2;

			final Direction left = normal.rotateY();
			centerY += (offsetY - 8) / 16.0;

			if (left.getAxis() == Direction.Axis.Z) {
				centerZ += (offsetX - 8) / 16.0;
			} else {
				centerX += (offsetX - 8) / 16.0;
			}

			this.posX = centerX;
			this.posY = centerY;
			this.posZ = centerZ;

			final double halfSizeX;
			final double halfSizeY = getHeightPixels() / 16.0 / 2.0;
			final double halfSizeZ;

			if (normal.getAxis() == Direction.Axis.Z) {
				halfSizeX = getWidthPixels() / 16.0 / 2.0;
				halfSizeZ = DEPTH / 16 / 2;
			} else {
				halfSizeX = DEPTH / 16 / 2;
				halfSizeZ = getWidthPixels() / 16.0 / 2;
			}

			setEntityBoundingBox(new AxisAlignedBB(centerX - halfSizeX, centerY - halfSizeY, centerZ - halfSizeZ, centerX + halfSizeX, centerY + halfSizeY, centerZ + halfSizeZ));
		}
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return itemStack.copy();
	}

	@Override
	public int getWidthPixels() {
		return 8;
	}

	@Override
	public int getHeightPixels() {
		return 8;
	}

	private void setCharIndex(int charIndex) {
		this.charIndex = charIndex;
		itemStack = ItemGlyph.createStack(Items.glyph, charIndex);
	}

	@Override
	public void writeEntityToNBT(CompoundNBT compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger(TAG_CHAR_INDEX, charIndex);
		compound.setByte(TAG_OFFSET_X, offsetX);
		compound.setByte(TAG_OFFSET_Y, offsetY);
	}

	@Override
	public void readEntityFromNBT(CompoundNBT compound) {
		setCharIndex((char)compound.getInteger(TAG_CHAR_INDEX));
		offsetX = compound.getByte(TAG_OFFSET_X);
		offsetY = compound.getByte(TAG_OFFSET_Y);
		super.readEntityFromNBT(compound);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(charIndex);
		buffer.writeByte(facingDirection.ordinal());

		// can't trust received entity position, since it includes offsets, so send raw values
		buffer.writeInt(hangingPosition.getX());
		buffer.writeInt(hangingPosition.getY());
		buffer.writeInt(hangingPosition.getZ());

		buffer.writeByte(offsetX);
		buffer.writeByte(offsetY);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		setCharIndex(buffer.readInt());
		final int facing = buffer.readByte();

		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		this.hangingPosition = new BlockPos(x, y, z);

		offsetX = buffer.readByte();
		offsetY = buffer.readByte();

		updateFacingWithBoundingBox(Direction.VALUES[facing]);
	}

	public ItemStack getStack() {
		return itemStack;
	}

	@Override
	public void onBroken(Entity brokenEntity) {
		if (world.getGameRules().getBoolean("doEntityDrops")) {
			playSound(Sounds.ENTITY_GLYPH_BREAK, 1.0F, 1.0F);

			if (brokenEntity instanceof PlayerEntity) {
				final PlayerEntity entityplayer = (PlayerEntity)brokenEntity;
				if (entityplayer.capabilities.isCreativeMode) return;
			}

			entityDropItem(itemStack.copy(), 0.0F);
		}
	}

	@Override
	public void playPlaceSound() {
		playSound(Sounds.ENTITY_GLYPH_PLACE, 1.0F, 1.0F);
	}

}
