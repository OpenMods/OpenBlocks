package openblocks.common;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.JukeboxBlock.TileEntityJukebox;
import net.minecraft.block.SandBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import openblocks.Config;
import openmods.utils.ITester;
import openmods.utils.ITester.Result;
import openmods.utils.ObjectTester;
import openmods.utils.ObjectTester.ClassNameTester;
import openmods.utils.ObjectTester.ClassTester;

public class MagnetWhitelists {
	public final static MagnetWhitelists instance = new MagnetWhitelists();

	public static class BlockCoords extends BlockPos {
		public final BlockState blockState;
		public final World world;

		BlockCoords(BlockState blockState, World world, Vec3i pos) {
			super(pos);
			this.blockState = blockState;
			this.world = world;
		}
	}

	private MagnetWhitelists() {}

	public final ObjectTester<Entity> entityWhitelist = ObjectTester.create();
	public final ObjectTester<BlockCoords> blockWhitelist = ObjectTester.create();
	public final ObjectTester<TileEntity> tileEntityWhitelist = ObjectTester.create();

	public static class WhitelistRegisterEvent<T> extends Event {
		protected final ObjectTester<T> tester;

		protected WhitelistRegisterEvent(ObjectTester<T> tester) {
			this.tester = tester;
		}

		public void addClass(Class<? extends T> cls) {
			tester.addTester(new ClassTester<>(cls));
		}

		@SuppressWarnings("unchecked")
		public void addClassNames(Class<? extends T>... cls) {
			tester.addTester(new ClassNameTester<T>().addClasses(cls));
		}

		public void addClassNames(String... names) {
			tester.addTester(new ClassNameTester<T>().addClasses(names));
		}
	}

	public static class EntityRegisterEvent extends
			WhitelistRegisterEvent<Entity> {
		protected EntityRegisterEvent(ObjectTester<Entity> tester) {
			super(tester);
		}
	}

	public static class BlockRegisterEvent extends WhitelistRegisterEvent<BlockCoords> {
		protected BlockRegisterEvent(ObjectTester<BlockCoords> tester) {
			super(tester);
		}
	}

	public static class TileEntityRegisterEvent extends
			WhitelistRegisterEvent<TileEntity> {
		protected TileEntityRegisterEvent(ObjectTester<TileEntity> tester) {
			super(tester);
		}
	}

	public static ITester<BlockCoords> createBlockIdentityTester(final Block template) {
		return o -> (o.blockState.getBlock() == template)? Result.ACCEPT : Result.CONTINUE;
	}

	public static ITester<BlockCoords> createBlockClassTester(final Class<? extends Block> cls) {
		return o -> (cls.isInstance(o.blockState.getBlock()))? Result.ACCEPT : Result.CONTINUE;
	}

	public void initTesters() {
		MinecraftForge.EVENT_BUS.post(new EntityRegisterEvent(entityWhitelist));

		entityWhitelist.addTester(new ClassTester<>(ItemEntity.class));
		entityWhitelist.addTester(new ClassTester<>(BoatEntity.class));
		entityWhitelist.addTester(new ClassTester<>(AbstractMinecartEntity.class));

		{
			final Set<ResourceLocation> allowedEntityLocations = toResourceLocationSet(Config.magnetEntityWhitelist);
			entityWhitelist.addTester(e -> allowedEntityLocations.contains(EntityList.getKey(e))? Result.ACCEPT : Result.CONTINUE);
		}

		MinecraftForge.EVENT_BUS.post(new BlockRegisterEvent(blockWhitelist));

		blockWhitelist.addTester(o -> {
			float hardness = o.blockState.getBlockHardness(o.world, o);
			return (hardness < 0)? Result.REJECT : Result.CONTINUE;
		});

		blockWhitelist.addTester(o -> o.blockState.getRenderType() == BlockRenderType.MODEL? Result.ACCEPT : Result.CONTINUE);

		blockWhitelist.addTester(createBlockClassTester(SandBlock.class));
		blockWhitelist.addTester(createBlockClassTester(StairsBlock.class));
		blockWhitelist.addTester(createBlockClassTester(FenceBlock.class));
		blockWhitelist.addTester(createBlockClassTester(FenceGateBlock.class));
		blockWhitelist.addTester(createBlockIdentityTester(Blocks.CACTUS));

		{
			final Set<ResourceLocation> allowedBlockLocations = toResourceLocationSet(Config.magnetBlockWhitelist);
			blockWhitelist.addTester(e -> allowedBlockLocations.contains(Block.REGISTRY.getNameForObject(e.blockState.getBlock()))? Result.ACCEPT : Result.CONTINUE);
		}

		MinecraftForge.EVENT_BUS.post(new TileEntityRegisterEvent(tileEntityWhitelist));

		tileEntityWhitelist
				.addTester(new ClassTester<>(BeaconTileEntity.class))
				.addTester(new ClassTester<>(BrewingStandTileEntity.class))
				.addTester(new ClassTester<>(ChestTileEntity.class))
				.addTester(new ClassTester<>(CommandBlockTileEntity.class))
				.addTester(new ClassTester<>(DispenserTileEntity.class))
				.addTester(new ClassTester<>(EnchantingTableTileEntity.class))
				.addTester(new ClassTester<>(EnderChestTileEntity.class))
				.addTester(new ClassTester<>(FurnaceTileEntity.class))
				.addTester(new ClassTester<>(HopperTileEntity.class))
				.addTester(new ClassTester<>(TileEntityNote.class))
				.addTester(new ClassTester<>(TileEntityJukebox.class));

		{
			final Set<ResourceLocation> allowedTileEntityLocations = toResourceLocationSet(Config.magnetTileEntityWhitelist);
			tileEntityWhitelist.addTester(e -> allowedTileEntityLocations.contains(TileEntity.getKey(e.getClass()))? Result.ACCEPT : Result.CONTINUE);
		}
	}

	private static ImmutableSet<ResourceLocation> toResourceLocationSet(final String[] names) {
		return Arrays.stream(names).map(ResourceLocation::new).collect(ImmutableSet.toImmutableSet());
	}

	public boolean testBlock(World world, BlockPos pos) {
		final BlockState blockState = world.getBlockState(pos);
		final Block block = blockState.getBlock();

		if (blockState.getBlock().isAir(blockState, world, pos)) return false;

		if (block instanceof ContainerBlock) {
			TileEntity te = world.getTileEntity(pos);
			return te != null && tileEntityWhitelist.check(te);
		}

		return blockWhitelist.check(new BlockCoords(blockState, world, pos));
	}
}
