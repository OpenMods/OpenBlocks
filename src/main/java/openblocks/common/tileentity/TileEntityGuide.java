package openblocks.common.tileentity;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.common.item.ItemGuide;
import openblocks.shapes.GuideShape;
import openmods.api.IAddAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.colors.ColorMeta;
import openmods.geometry.HalfAxis;
import openmods.geometry.Orientation;
import openmods.shapes.IShapeGenerator;
import openmods.shapes.IShapeable;
import openmods.sync.*;
import openmods.sync.drops.DroppableTileEntity;
import openmods.sync.drops.StoreOnDrop;
import openmods.utils.CollectionUtils;
import openmods.utils.MathUtils;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.*;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.StructField;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class TileEntityGuide extends DroppableTileEntity implements ISyncListener, INeighbourAwareTile, IAddAwareTile, ITickable {

	private static final Comparator<BlockPos> BlockPos_COMPARATOR = new Comparator<BlockPos>() {
		@Override
		public int compare(BlockPos o1, BlockPos o2) {
			{
				// first, go from bottom to top
				int result = Ints.compare(o1.getX(), o2.getX());
				if (result != 0) return result;
			}

			{
				// then sort by angle, to make placement more intuitive
				final double angle1 = Math.atan2(o1.getZ(), o1.getX());
				final double angle2 = Math.atan2(o2.getZ(), o2.getX());

				int result = Doubles.compare(angle1, angle2);
				if (result != 0) return result;
			}

			{
				// then sort by distance, far ones first
				final double length1 = MathUtils.lengthSq(o1.getX(), o1.getZ());
				final double length2 = MathUtils.lengthSq(o2.getX(), o2.getZ());

				int result = Doubles.compare(length2, length1);
				if (result != 0) return result;
			}

			// then sort by x and z to make all unique BlockPosinates are included
			{
				int result = Ints.compare(o1.getX(), o2.getX());
				if (result != 0) return result;
			}

			{
				int result = Ints.compare(o1.getZ(), o2.getZ());
				return result;
			}
		}
	};

	private List<BlockPos> shape;
	private List<BlockPos> previousShape;
	private float timeSinceChange = 0;
	private AxisAlignedBB renderAABB;

	@StoreOnDrop(name = ItemGuide.TAG_POS_X)
	protected SyncableVarInt posX;

	@StoreOnDrop(name = ItemGuide.TAG_POS_Y)
	protected SyncableVarInt posY;

	@StoreOnDrop(name = ItemGuide.TAG_POS_Z)
	protected SyncableVarInt posZ;

	@StoreOnDrop(name = ItemGuide.TAG_NEG_X)
	protected SyncableVarInt negX;

	@StoreOnDrop(name = ItemGuide.TAG_NEG_Y)
	protected SyncableVarInt negY;

	@StoreOnDrop(name = ItemGuide.TAG_NEG_Z)
	protected SyncableVarInt negZ;

	@StoreOnDrop(name = ItemGuide.TAG_SHAPE)
	protected SyncableEnum<GuideShape> mode;

	@StoreOnDrop(name = ItemGuide.TAG_COLOR)
	protected SyncableInt color;

	protected SyncableBoolean active;

	private final Map<HalfAxis, SyncableVarInt> axisDimensions = Maps.newEnumMap(HalfAxis.class);

	public TileEntityGuide() {
		syncMap.addUpdateListener(this);

		axisDimensions.put(HalfAxis.NEG_X, negX);
		axisDimensions.put(HalfAxis.NEG_Y, negY);
		axisDimensions.put(HalfAxis.NEG_Z, negZ);

		axisDimensions.put(HalfAxis.POS_X, posX);
		axisDimensions.put(HalfAxis.POS_Y, posY);
		axisDimensions.put(HalfAxis.POS_Z, posZ);
	}

	@Override
	protected void createSyncedFields() {
		posX = new SyncableVarInt(8);
		posY = new SyncableVarInt(8);
		posZ = new SyncableVarInt(8);

		negX = new SyncableVarInt(8);
		negY = new SyncableVarInt(8);
		negZ = new SyncableVarInt(8);

		mode = SyncableEnum.create(GuideShape.Sphere);
		color = new SyncableInt(0xFFFFFF);
		active = new SyncableBoolean();
	}

	@ScriptStruct
	public static class ShapeSize {
		@StructField
		public int negX;
		@StructField
		public int negY;
		@StructField
		public int negZ;

		@StructField
		public int posX;
		@StructField
		public int posY;
		@StructField
		public int posZ;
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.TABLE)
	public ShapeSize getSize() {
		ShapeSize result = new ShapeSize();
		result.negX = negX.get();
		result.negY = negY.get();
		result.negZ = negZ.get();

		result.posX = posX.get();
		result.posY = posY.get();
		result.posZ = posZ.get();
		return result;
	}

	@ScriptCallable
	public void setSize(ShapeSize size) {
		Preconditions.checkArgument(size.negX > 0, "NegX must be > 0");
		negX.set(size.negX);

		Preconditions.checkArgument(size.negY > 0, "NegY must be > 0");
		negY.set(size.negY);

		Preconditions.checkArgument(size.negZ > 0, "NegZ must be > 0");
		negZ.set(size.negZ);

		Preconditions.checkArgument(size.posX > 0, "PosX must be > 0");
		posX.set(size.posX);

		Preconditions.checkArgument(size.negY > 0, "PosY must be > 0");
		posY.set(size.posY);

		Preconditions.checkArgument(size.negZ > 0, "PosZ must be > 0");
		posZ.set(size.posZ);

		recreateShape();
		sync();
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
	public void setShape(@Arg(name = "shape") GuideShape shape) {
		mode.set(shape);

		recreateShape();
		sync();
	}

	@ScriptCallable
	public void setColor(@Arg(name = "color") int color) {
		this.color.set(color & 0x00FFFFFF);
		sync();
	}

	public boolean incrementHalfAxis(HalfAxis axis, EntityPlayerMP player) {
		final SyncableVarInt v = axisDimensions.get(axis);
		v.modify(+1);
		afterDimensionsChange(player);
		return true;

	}

	public boolean decrementHalfAxis(HalfAxis axis, EntityPlayerMP player) {
		final SyncableVarInt v = axisDimensions.get(axis);
		if (v.get() > 0) {
			v.modify(-1);
			afterDimensionsChange(player);
			return true;
		}
		return false;
	}

	public boolean copyHalfAxis(HalfAxis from, HalfAxis to, EntityPlayerMP player) {
		final SyncableVarInt fromV = axisDimensions.get(from);
		final SyncableVarInt toV = axisDimensions.get(to);
		toV.set(fromV.get());
		afterDimensionsChange(player);
		return true;
	}

	public void incrementMode(EntityPlayer player) {
		incrementMode();

		displayModeChange(player);
		displayBlockCount(player);
	}

	public void decrementMode(EntityPlayer player) {
		decrementMode();

		displayModeChange(player);
		displayBlockCount(player);
	}

	private void displayModeChange(EntityPlayer player) {
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_mode", getCurrentMode().getLocalizedName()));
	}

	private void displayBlockCount(EntityPlayer player) {
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	public boolean shouldRender() {
		return Config.guideRedstone == 0 || ((Config.guideRedstone < 0) ^ active.get());
	}

	@Override
	public void update() {
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
		shape = generateShape();
		renderAABB = null;
	}

	private List<BlockPos> generateShape() {
		final IShapeGenerator generator = getCurrentMode().generator;

		final Set<BlockPos> uniqueResults = Sets.newHashSet();
		final IShapeable collector = new IShapeable() {
			@Override
			public void setBlock(int x, int y, int z) {
				if (canAddCoord(x, y, z)) uniqueResults.add(new BlockPos(x, y, z));
			}
		};
		generator.generateShape(-negX.get(), -negY.get(), -negZ.get(), posX.get(), posY.get(), posZ.get(), collector);

		final List<BlockPos> sortedResults = Lists.newArrayList(uniqueResults);
		Collections.sort(sortedResults, BlockPos_COMPARATOR);

		final List<BlockPos> rotatedResult = Lists.newArrayList();
		final Orientation orientation = getOrientation();

		for (BlockPos c : sortedResults) {
			final int tx = orientation.transformX(c.getX(), c.getY(), c.getZ());
			final int ty = orientation.transformY(c.getX(), c.getY(), c.getZ());
			final int tz = orientation.transformZ(c.getX(), c.getY(), c.getZ());

			rotatedResult.add(new BlockPos(tx, ty, tz));
		}

		return ImmutableList.copyOf(rotatedResult);
	}

	protected boolean canAddCoord(int x, int y, int z) {
		return true;
	}

	public List<BlockPos> getShape() {
		return shape;
	}

	public List<BlockPos> getPreviousShape() {
		return previousShape;
	}

	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		// remote world will be updated by desctiption packet from block rotate
		if (!worldObj.isRemote) recreateShape();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		if (renderAABB == null) renderAABB = createRenderAABB();
		return renderAABB;
	}

	private AxisAlignedBB createRenderAABB() {
		double minX = 0;
		double minY = 0;
		double minZ = 0;

		double maxX = 1;
		double maxY = 1;
		double maxZ = 1;

		if (shape != null) {
			for (BlockPos c : shape) {
				{
					final int x = c.getX();
					if (maxX < x) maxX = x;
					if (minX > x) minX = x;
				}

				{
					final int y = c.getY();
					if (maxY < y) maxY = y;
					if (minY > y) minY = y;
				}

				{
					final int z = c.getZ();
					if (maxZ < z) maxZ = z;
					if (minZ > z) minZ = z;
				}
			}

		}

		return new AxisAlignedBB(pos.add(minX, minY, minZ), pos.add(maxX, maxY, maxZ));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return Config.guideRenderRangeSq;
	}

	@Alias("cycleShape")
	@ScriptCallable(returnTypes = ReturnType.STRING)
	public GuideShape incrementMode() {
		final GuideShape shape = mode.increment();

		recreateShape();
		sync();
		return shape;
	}

	@ScriptCallable(returnTypes = ReturnType.STRING)
	public GuideShape decrementMode() {
		final GuideShape shape = mode.decrement();

		recreateShape();
		sync();
		return shape;
	}

	private void notifyPlayer(EntityPlayer player) {
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_box_size",
				-negX.get(), -negY.get(), -negZ.get(),
				+posX.get(), +posY.get(), +posZ.get()));
		displayBlockCount(player);
	}

	private void afterDimensionsChange(EntityPlayer player) {
		recreateShape();

		sync();
		notifyPlayer(player);
	}

	@Override
	public void onSync(Set<ISyncableObject> changes) {
		if (changes.contains(negX) || changes.contains(negY) || changes.contains(negZ) ||
				changes.contains(posX) || changes.contains(posY) || changes.contains(posZ) ||
				changes.contains(mode)) {
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
			boolean redstoneState = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
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

	protected List<BlockPos> getShapeSafe() {
		if (shape == null) recreateShape();
		return shape;
	}

	public boolean onItemUse(EntityPlayerMP player, ItemStack heldStack, EnumFacing side, float hitX, float hitY, float hitZ) {
		Set<ColorMeta> colors = ColorMeta.fromStack(heldStack);
		if (!colors.isEmpty()) {
			ColorMeta selected = CollectionUtils.getRandom(colors);
			color.set(selected.rgb);
			if (!worldObj.isRemote) sync();
			return true;
		}

		return false;
	}
}
