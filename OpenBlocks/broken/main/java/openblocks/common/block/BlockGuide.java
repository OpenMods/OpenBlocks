package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.events.GuideActionEvent;
import openmods.OpenMods;
import openmods.api.ISelectionAware;
import openmods.block.BlockRotationMode;
import openmods.block.IBlockRotationMode;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Hitbox;
import openmods.geometry.IHitboxSupplier;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockGuide extends OpenBlock implements ISelectionAware {

	@Override
	public IBlockRotationMode getRotationMode() {
		return BlockRotationMode.THREE_FOUR_DIRECTIONS;
	}

	private AxisAlignedBB selection;

	private final IHitboxSupplier buttonsHitbox = OpenMods.proxy.getHitboxes(OpenBlocks.location("guide_buttons"));

	public BlockGuide() {
		super(Material.ROCK);
		setLightLevel(0.6f);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World world, BlockPos pos) {
		return selection != null? selection : super.getSelectedBoundingBox(state, world, pos);
	}

	private Hitbox findClickBox(Vec3d pos) {
		for (Hitbox h : buttonsHitbox.asList())
			if (h.aabb().contains(pos))
				return h;

		return null;
	}

	protected boolean areButtonsActive(PlayerEntity player) {
		return true;
	}

	protected boolean onItemUse(ServerPlayerEntity player, TileEntityGuide guide, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public boolean onSelected(World world, BlockPos pos, DrawBlockHighlightEvent evt) {
		if (areButtonsActive(evt.getPlayer())) {
			final Vec3d hitVec = evt.getTarget().hitVec;

			final Orientation orientation = getOrientation(world, pos);
			final Vec3d localHit = BlockSpaceTransform.instance.mapWorldToBlock(orientation, hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
			final Hitbox clickBox = findClickBox(localHit);
			selection = clickBox != null? BlockSpaceTransform.instance.mapBlockToWorld(orientation, clickBox.aabb()).offset(pos.getX(), pos.getY(), pos.getZ()) : null;
		} else selection = null;

		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (hand != Hand.MAIN_HAND) return false;

		if (world.isRemote) {
			if (areButtonsActive(player)) {
				final Orientation orientation = getOrientation(world, pos);
				final Vec3d localHit = BlockSpaceTransform.instance.mapWorldToBlock(orientation, hitX, hitY, hitZ);
				final Hitbox clickBox = findClickBox(localHit);
				if (clickBox != null) {
					new GuideActionEvent(world.provider.getDimension(), pos, clickBox.name).sendToServer();
				}
			}
			return true;
		} else if (player instanceof ServerPlayerEntity) {
			final ItemStack heldStack = player.getHeldItemMainhand();
			if (!heldStack.isEmpty()) {
				TileEntityGuide guide = getTileEntity(world, pos, TileEntityGuide.class);
				if (guide.onItemUse((ServerPlayerEntity)player, heldStack, side, hitX, hitY, hitZ)) return true;
			}
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
		final float x = pos.getX() + 0.5f;
		final float y = pos.getY() + 0.7f;
		final float z = pos.getZ() + 0.5f;

		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
	}
}
