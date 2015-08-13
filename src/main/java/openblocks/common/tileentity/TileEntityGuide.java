package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.Config;
import openblocks.common.item.ItemGuide;
import openblocks.shapes.GuideShape;
import openmods.api.IActivateAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.shapes.IShapeable;
import openmods.sync.*;
import openmods.sync.drops.DroppableTileEntity;
import openmods.sync.drops.StoreOnDrop;
import openmods.utils.*;
import openmods.utils.ColorUtils.ColorMeta;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityGuide extends DroppableTileEntity implements IShapeable, IActivateAwareTile, ISyncListener, INeighbourAwareTile {

	private Set<Coord> shape;
	private Set<Coord> previousShape;
	private float timeSinceChange = 0;

	@StoreOnDrop(name = ItemGuide.TAG_WIDTH)
	protected SyncableInt width;

	@StoreOnDrop(name = ItemGuide.TAG_HEIGHT)
	protected SyncableInt height;

	@StoreOnDrop(name = ItemGuide.TAG_DEPTH)
	protected SyncableInt depth;

	@StoreOnDrop(name = ItemGuide.TAG_SHAPE)
	protected SyncableEnum<GuideShape> mode;

	@StoreOnDrop(name = ItemGuide.TAG_COLOR)
	protected SyncableInt color;

	protected SyncableBoolean active;

	public TileEntityGuide() {
		syncMap.addUpdateListener(this);
	}

	@Override
	protected void createSyncedFields() {
		width = new SyncableInt(8);
		height = new SyncableInt(8);
		depth = new SyncableInt(8);
		mode = SyncableEnum.create(GuideShape.Sphere);
		color = new SyncableInt(0xFFFFFF);
		active = new SyncableBoolean();
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.NUMBER)
	public int getWidth() {
		return width.get();
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.NUMBER)
	public int getHeight() {
		return height.get();
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.NUMBER)
	public int getDepth() {
		return depth.get();
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.NUMBER)
	public int getColor() {
		return color.get() & 0x00FFFFFF;
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.NUMBER)
	public int getCount() {
		if (shape == null) recreateShape();
		return shape.size();
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.STRING, name = "getShape")
	public GuideShape getCurrentMode() {
		return mode.get();
	}

	@ScriptCallable
	public void setWidth(@Arg(name = "width") int w) {
		Preconditions.checkArgument(w > 0, "Width must be > 0");
		width.set(w);

		if (mode.getValue().fixedRatio) {
			height.set(w);
			depth.set(w);
		}

		recreateShape();
		sync();
	}

	@ScriptCallable
	public void setDepth(@Arg(name = "depth") int d) {
		Preconditions.checkArgument(d > 0, "Depth must be > 0");
		depth.set(d);

		if (mode.getValue().fixedRatio) {
			width.set(d);
			depth.set(d);
		}

		recreateShape();
		sync();
	}

	@ScriptCallable
	public void setHeight(@Arg(name = "height") int h) {
		Preconditions.checkArgument(h > 0, "Height must be > 0");
		height.set(h);

		if (mode.getValue().fixedRatio) {
			width.set(h);
			depth.set(h);
		}

		recreateShape();
		sync();
	}

	@ScriptCallable
	public void setShape(@Arg(name = "shape") GuideShape shape) {
		mode.set(shape);

		if (mode.getValue().fixedRatio) {
			final int width = getWidth();
			height.set(width);
			depth.set(width);
		}

		recreateShape();
		sync();
	}

	@ScriptCallable
	public void setColor(@Arg(name = "color") int color) {
		this.color.set(color & 0x00FFFFFF);
		sync();
	}

	public boolean shouldRender() {
		return Config.guideRedstone == 0 || ((Config.guideRedstone < 0) ^ active.get());
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			if (timeSinceChange < 1.0) {
				timeSinceChange = (float)Math.min(1.0f, timeSinceChange + 0.1);
			}
		}
	}

	public float getTimeSinceChange() {
		return timeSinceChange;
	}

	private void recreateShape() {
		previousShape = shape;
		shape = Sets.newHashSet();
		getCurrentMode().generator.generateShape(getWidth(), getHeight(), getDepth(), this);
	}

	@Override
	public void setBlock(int x, int y, int z) {
		shape.add(new Coord(x, y, z));
	}

	public Set<Coord> getShape() {
		return shape;
	}

	public Set<Coord> getPreviousShape() {
		return previousShape;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = super.getRenderBoundingBox();
		return box.expand(getWidth(), getHeight(), getDepth());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return Config.guideRenderRangeSq;
	}

	private void switchMode(EntityPlayer player) {
		switchMode();
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_mode", getCurrentMode().getLocalizedName()));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	@ScriptCallable(returnTypes = ReturnType.STRING, name = "cycleShape")
	public GuideShape switchMode() {
		final GuideShape shape = mode.increment();

		if (shape.fixedRatio) {
			final int width = getWidth();
			height.set(width);
			depth.set(width);
		}

		recreateShape();
		if (!worldObj.isRemote) sync();
		return shape;
	}

	private void changeDimensions(EntityPlayer player, ForgeDirection orientation) {
		changeDimensions(orientation);
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_size", width.get(), height.get(), depth.get()));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	private static void inc(SyncableInt v) {
		v.modify(+1);
	}

	private static void dec(SyncableInt v) {
		if (v.get() > 0) v.modify(-1);
	}

	private void changeDimensions(ForgeDirection orientation) {
		switch (orientation) {
			case EAST:
				dec(width);
				break;
			case WEST:
				inc(width);
				break;

			case SOUTH:
				dec(depth);
				break;
			case NORTH:
				inc(depth);
				break;

			case DOWN:
				dec(height);
				break;
			case UP:
				inc(height);
				break;

			default:
				return;
		}

		if (getCurrentMode().fixedRatio) {
			int h = getHeight();
			int w = getWidth();
			int d = getDepth();
			if (w != h && w != d) {
				height.set(w);
				depth.set(w);
			} else if (h != w && h != d) {
				depth.set(h);
				width.set(h);
			} else if (d != w && d != h) {
				width.set(d);
				height.set(d);
			}
		}
		recreateShape();
		if (!worldObj.isRemote) sync();
	}

	@Override
	public void onSync(Set<ISyncableObject> changes) {
		if (changes.contains(depth) || changes.contains(height) || changes.contains(width) || changes.contains(mode)) {
			recreateShape();
			timeSinceChange = 0;
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote) return true;

		if (player.isSneaking()) switchMode(player);
		else {
			ItemStack heldStack = player.getHeldItem();
			if (heldStack == null || !tryUseItem(player, heldStack)) changeDimensions(player, ForgeDirection.getOrientation(side));
		}

		return true;
	}

	protected boolean tryUseItem(EntityPlayer player, ItemStack heldStack) {
		if (player.capabilities.isCreativeMode && isInFillMode()) {
			final Item heldItem = heldStack.getItem();
			if (heldItem instanceof ItemBlock) {
				replaceBlocks(heldStack, heldItem);
				return true;
			}
		}

		Set<ColorMeta> colors = ColorUtils.stackToColor(heldStack);
		if (!colors.isEmpty()) {
			ColorMeta selected = CollectionUtils.getRandom(colors);
			color.set(selected.rgb);
			if (!worldObj.isRemote) sync();
			return true;
		}

		return false;
	}

	protected void replaceBlocks(ItemStack heldStack, final Item heldItem) {
		if (shape == null) recreateShape();

		final ItemBlock itemBlock = (ItemBlock)heldItem;
		final Block block = itemBlock.field_150939_a;
		final int blockMeta = itemBlock.getMetadata(heldStack.getItemDamage());
		for (Coord coord : shape)
			worldObj.setBlock(xCoord + coord.x, yCoord + coord.y, zCoord + coord.z, block, blockMeta, BlockNotifyFlags.ALL);
	}

	private boolean isInFillMode() {
		return worldObj.getBlock(xCoord, yCoord + 1, zCoord) == Blocks.obsidian;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public void onNeighbourChanged(Block block) {
		if (Config.guideRedstone != 0) {
			boolean redstoneState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			active.set(redstoneState);
			sync();
		}
	}
}
