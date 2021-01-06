package openblocks.common.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
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
public class BlockGuide extends OpenBlock.Orientable implements ISelectionAware {
	@Nullable
	private AxisAlignedBB selection;

	private final IHitboxSupplier buttonsHitbox = OpenMods.PROXY.getHitboxes(OpenBlocks.location("guide_buttons"));

	public BlockGuide(final Block.Properties properties) {
		super(properties);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public IBlockRotationMode getRotationMode() {
		return BlockRotationMode.THREE_FOUR_DIRECTIONS;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (context instanceof EntitySelectionContext && selection != null) {
			final VoxelShape voxelShape = VoxelShapes.create(selection);
			selection = null;
			return voxelShape;
		}
		return super.getShape(state, worldIn, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.fullCube();
	}

	@Nullable
	private Hitbox findClickBox(Vector3d pos) {
		for (Hitbox h : buttonsHitbox.asList()) {
			if (h.aabb().contains(pos)) {
				return h;
			}
		}

		return null;
	}

	protected boolean areButtonsActive(Entity entity) {
		return true;
	}

	@Override
	public boolean onSelected(World world, BlockPos pos, DrawHighlightEvent evt) {
		if (areButtonsActive(evt.getInfo().getRenderViewEntity())) {
			final Vector3d hitVec = evt.getTarget().getHitVec();

			final Orientation orientation = getOrientation(world, pos);
			final Vector3d localHit = BlockSpaceTransform.instance.mapWorldToBlock(orientation, hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
			final Hitbox clickBox = findClickBox(localHit);
			selection = clickBox != null? BlockSpaceTransform.instance.mapBlockToWorld(orientation, clickBox.aabb()) : null;
		} else {
			selection = null;
		}

		return false;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (hand != Hand.MAIN_HAND) {
			return ActionResultType.PASS;
		}

		if (world.isRemote) {
			if (areButtonsActive(player)) {
				final Orientation orientation = getOrientation(world, pos);
				final Vector3d hitVec = hit.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ());
				final Vector3d localHit = BlockSpaceTransform.instance.mapWorldToBlock(orientation, hitVec.getX(), hitVec.getY(), hitVec.getZ());
				final Hitbox clickBox = findClickBox(localHit);
				if (clickBox != null) {
					new GuideActionEvent(world.getDimensionKey(), pos, clickBox.name).sendToServer();
				}
			}
			return ActionResultType.SUCCESS;
		} else if (player instanceof ServerPlayerEntity) {
			final ItemStack heldStack = player.getHeldItemMainhand();
			if (!heldStack.isEmpty()) {
				final TileEntityGuide guide = getTileEntity(world, pos, TileEntityGuide.class);
				if (guide.onItemUse((ServerPlayerEntity)player, heldStack, hit)) {
					return ActionResultType.SUCCESS;
				}
			}
		}

		return ActionResultType.CONSUME;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		final float x = pos.getX() + 0.5f;
		final float y = pos.getY() + 0.7f;
		final float z = pos.getZ() + 0.5f;

		world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
		world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
	}
}
