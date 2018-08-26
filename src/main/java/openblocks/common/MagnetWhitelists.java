package openblocks.common;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.EnumBlockRenderType;
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
		public final IBlockState blockState;
		public final World world;

		BlockCoords(IBlockState blockState, World world, Vec3i pos) {
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
			tester.addTester(new ClassTester<T>(cls));
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

		entityWhitelist.addTester(new ClassTester<Entity>(EntityItem.class));
		entityWhitelist.addTester(new ClassTester<Entity>(EntityBoat.class));
		entityWhitelist.addTester(new ClassTester<Entity>(EntityMinecart.class));

		{
			final Set<ResourceLocation> allowedEntityLocations = toResourceLocationSet(Config.magnetEntityWhitelist);
			entityWhitelist.addTester(e -> allowedEntityLocations.contains(EntityList.getKey(e))? Result.ACCEPT : Result.CONTINUE);
		}

		MinecraftForge.EVENT_BUS.post(new BlockRegisterEvent(blockWhitelist));

		blockWhitelist.addTester(o -> {
			float hardness = o.blockState.getBlockHardness(o.world, o);
			return (hardness < 0)? Result.REJECT : Result.CONTINUE;
		});

		blockWhitelist.addTester(o -> o.blockState.getRenderType() == EnumBlockRenderType.MODEL? Result.ACCEPT : Result.CONTINUE);

		blockWhitelist.addTester(createBlockClassTester(BlockSand.class));
		blockWhitelist.addTester(createBlockClassTester(BlockStairs.class));
		blockWhitelist.addTester(createBlockClassTester(BlockFence.class));
		blockWhitelist.addTester(createBlockClassTester(BlockFenceGate.class));
		blockWhitelist.addTester(createBlockIdentityTester(Blocks.CACTUS));

		{
			final Set<ResourceLocation> allowedBlockLocations = toResourceLocationSet(Config.magnetBlockWhitelist);
			blockWhitelist.addTester(e -> allowedBlockLocations.contains(Block.REGISTRY.getNameForObject(e.blockState.getBlock()))? Result.ACCEPT : Result.CONTINUE);
		}

		MinecraftForge.EVENT_BUS.post(new TileEntityRegisterEvent(tileEntityWhitelist));

		tileEntityWhitelist
				.addTester(new ClassTester<TileEntity>(TileEntityBeacon.class))
				.addTester(new ClassTester<TileEntity>(TileEntityBrewingStand.class))
				.addTester(new ClassTester<TileEntity>(TileEntityChest.class))
				.addTester(new ClassTester<TileEntity>(TileEntityCommandBlock.class))
				.addTester(new ClassTester<TileEntity>(TileEntityDispenser.class))
				.addTester(new ClassTester<TileEntity>(TileEntityEnchantmentTable.class))
				.addTester(new ClassTester<TileEntity>(TileEntityEnderChest.class))
				.addTester(new ClassTester<TileEntity>(TileEntityFurnace.class))
				.addTester(new ClassTester<TileEntity>(TileEntityHopper.class))
				.addTester(new ClassTester<TileEntity>(TileEntityNote.class))
				.addTester(new ClassTester<TileEntity>(TileEntityJukebox.class));

		{
			final Set<ResourceLocation> allowedTileEntityLocations = toResourceLocationSet(Config.magnetTileEntityWhitelist);
			tileEntityWhitelist.addTester(e -> allowedTileEntityLocations.contains(TileEntity.getKey(e.getClass()))? Result.ACCEPT : Result.CONTINUE);
		}
	}

	private static ImmutableSet<ResourceLocation> toResourceLocationSet(final String[] names) {
		return Arrays.asList(names).stream().map(ResourceLocation::new).collect(ImmutableSet.toImmutableSet());
	}

	public boolean testBlock(World world, BlockPos pos) {
		final IBlockState blockState = world.getBlockState(pos);
		final Block block = blockState.getBlock();

		if (blockState.getBlock().isAir(blockState, world, pos)) return false;

		if (block instanceof BlockContainer) {
			TileEntity te = world.getTileEntity(pos);
			return (te != null)? tileEntityWhitelist.check(te) : false;
		}

		return blockWhitelist.check(new BlockCoords(blockState, world, pos));
	}
}
