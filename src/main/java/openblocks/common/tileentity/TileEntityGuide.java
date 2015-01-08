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

	public int getWidth() {
		return width.get();
	}

	public int getHeight() {
		return height.get();
	}

	public int getDepth() {
		return depth.get();
	}

	public int getColor() {
		return color.get();
	}

	public void setWidth(int w) {
		width.set(w);
	}

	public void setDepth(int d) {
		depth.set(d);
	}

	public void setHeight(int h) {
		height.set(h);
	}

	public GuideShape getCurrentMode() {
		return mode.get();
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

	public void switchMode() {
		final GuideShape shape = mode.increment();

		if (shape.fixedRatio) {
			setHeight(getWidth());
			setDepth(getWidth());
		}
		recreateShape();
		if (!worldObj.isRemote) sync();
	}

	public void switchMode(GuideShape shape) {
		if(shape == mode.get()) {
			return;
		}
		mode.set(shape);

		if (shape.fixedRatio) {
			setHeight(getWidth());
			setDepth(getWidth());
		}
		recreateShape();
		if (!worldObj.isRemote) sync();
	}

	private void changeDimensions(EntityPlayer player, ForgeDirection orientation) {
		changeDimensions(orientation);
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_size", width.get(), height.get(), depth.get()));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	private static void inc(SyncableInt v) {
		change(v, +1);
	}

	private static void dec(SyncableInt v) {
		change(v, -1);
	}

	private static void change(SyncableInt v, int amount) {
		if (v.get() + amount >= 0) v.modify(-1);
	}

	public void changeDimensions(ForgeDirection orientation) {
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
		updateDimensions();
	}

	public void changeWidth(int amount) {
		change(width, amount);
		updateDimensions();
	}

	public void changeHeight(int amount) {
		change(height, amount);
		updateDimensions();
	}

	public void changeDepth(int amount) {
		change(depth, amount);
		updateDimensions();
	}

	private void updateDimensions() {
		if (getCurrentMode().fixedRatio) {
			int h = getHeight();
			int w = getWidth();
			int d = getDepth();
			if (w != h && w != d) {
				setHeight(w);
				setDepth(w);
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
			changeColor(selected.rgb);
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

	public void changeColor(int color) {
		this.color.set(color);
		if (!worldObj.isRemote) sync();
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
