package openblocks.common.tileentity;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import openblocks.Config;
import openblocks.common.item.ItemGuide;
import openblocks.shapes.GuideShape;
import openmods.api.IAddAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.shapes.IShapeGenerator;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityGuide extends DroppableTileEntity implements ISyncListener, INeighbourAwareTile, IAddAwareTile {

	public interface ShapeManipulator {
		public boolean activate(EntityPlayerMP player, TileEntityGuide te);

		public static final ShapeManipulator MIDDLE = new ShapeManipulator() {
			@Override
			public boolean activate(EntityPlayerMP player, TileEntityGuide te) {
				te.switchMode(player);
				return true;
			}
		};

		public static final ShapeManipulator X_PLUS = new ShapeManipulator() {
			@Override
			public boolean activate(EntityPlayerMP player, TileEntityGuide te) {
				return te.inc(player, te.width);
			}
		};

		public static final ShapeManipulator X_MINUS = new ShapeManipulator() {
			@Override
			public boolean activate(EntityPlayerMP player, TileEntityGuide te) {
				return te.dec(player, te.width);
			}
		};

		public static final ShapeManipulator Y_PLUS = new ShapeManipulator() {
			@Override
			public boolean activate(EntityPlayerMP player, TileEntityGuide te) {
				return te.inc(player, te.height);
			}
		};

		public static final ShapeManipulator Y_MINUS = new ShapeManipulator() {
			@Override
			public boolean activate(EntityPlayerMP player, TileEntityGuide te) {
				return te.dec(player, te.height);

			}
		};

		public static final ShapeManipulator Z_PLUS = new ShapeManipulator() {
			@Override
			public boolean activate(EntityPlayerMP player, TileEntityGuide te) {
				return te.inc(player, te.depth);
			}
		};

		public static final ShapeManipulator Z_MINUS = new ShapeManipulator() {
			@Override
			public boolean activate(EntityPlayerMP player, TileEntityGuide te) {
				return te.dec(player, te.depth);
			}
		};
	}

	private static final Comparator<Coord> COORD_COMPARATOR = new Comparator<Coord>() {
		@Override
		public int compare(Coord o1, Coord o2) {
			{
				// first, go from bottom to top
				int result = Ints.compare(o1.y, o2.y);
				if (result != 0) return result;
			}

			{
				// then sort by angle, to make placement more intuitive
				final double angle1 = Math.atan2(o1.z, o1.x);
				final double angle2 = Math.atan2(o2.z, o2.x);

				int result = Doubles.compare(angle1, angle2);
				if (result != 0) return result;
			}

			// then sort by x and z to make all unique coordinates are included
			{
				int result = Ints.compare(o1.x, o2.x);
				if (result != 0) return result;
			}

			{
				int result = Ints.compare(o1.z, o2.z);
				return result;
			}
		}
	};

	private List<Coord> shape;
	private List<Coord> previousShape;
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
		shape = generateShape(getCurrentMode().generator, getWidth(), getHeight(), getDepth());
	}

	private static List<Coord> generateShape(IShapeGenerator generator, int width, int height, int depth) {
		final Set<Coord> result = Sets.newTreeSet(COORD_COMPARATOR);

		generator.generateShape(width, height, depth, new IShapeable() {
			@Override
			public void setBlock(int x, int y, int z) {
				result.add(new Coord(x, y, z));
			}
		});

		return ImmutableList.copyOf(result);
	}

	public List<Coord> getShape() {
		return shape;
	}

	public List<Coord> getPreviousShape() {
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

	@ScriptCallable(returnTypes = ReturnType.STRING, name = "cycleShape")
	public GuideShape switchMode() {
		final GuideShape shape = mode.increment();

		if (shape.fixedRatio) {
			final int width = getWidth();
			height.set(width);
			depth.set(width);
		}

		recreateShape();
		sync();
		return shape;
	}

	private void switchMode(EntityPlayer player) {
		switchMode();

		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_mode", getCurrentMode().getLocalizedName()));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	private void ensureFixedRatio() {
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
	}

	protected void notifyPlayer(EntityPlayer player) {
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_size", width.get(), height.get(), depth.get()));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	private void afterDimensionsChange(EntityPlayer player) {
		ensureFixedRatio();
		recreateShape();

		sync();
		notifyPlayer(player);
	}

	private boolean inc(EntityPlayer player, SyncableInt v) {
		v.modify(+1);
		afterDimensionsChange(player);
		return true;
	}

	private boolean dec(EntityPlayer player, SyncableInt v) {
		if (v.get() > 0) {
			v.modify(-1);
			afterDimensionsChange(player);
			return true;
		}

		return false;
	}

	@Override
	public void onSync(Set<ISyncableObject> changes) {
		if (changes.contains(depth) || changes.contains(height) || changes.contains(width) || changes.contains(mode)) {
			recreateShape();
			timeSinceChange = 0;
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1 && shouldRender();
	}

	private void updateRedstone() {
		if (Config.guideRedstone != 0) {
			boolean redstoneState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			active.set(redstoneState);
			sync();
		}
	}

	@Override
	public void onNeighbourChanged(Block block) {
		updateRedstone();
	}

	@Override
	public void onAdded() {
		updateRedstone();
	}

	protected List<Coord> getShapeSafe() {
		if (shape == null) recreateShape();
		return shape;
	}

	public boolean onItemUse(EntityPlayerMP player, ItemStack heldStack, int side, float hitX, float hitY, float hitZ) {
		Set<ColorMeta> colors = ColorUtils.stackToColor(heldStack);
		if (!colors.isEmpty()) {
			ColorMeta selected = CollectionUtils.getRandom(colors);
			color.set(selected.rgb);
			if (!worldObj.isRemote) sync();
			return true;
		}

		return false;
	}
}
