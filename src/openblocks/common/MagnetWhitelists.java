package openblocks.common;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import openblocks.utils.ITester;
import openblocks.utils.ObjectTester;
import openblocks.utils.ObjectTester.ClassNameTester;
import openblocks.utils.ObjectTester.ClassTester;

public class MagnetWhitelists {
	public final static MagnetWhitelists instance = new MagnetWhitelists();

	private MagnetWhitelists() {}

	public final ObjectTester<Entity> entityWhitelist = ObjectTester.create();
	public final ObjectTester<Block> blockWhitelist = ObjectTester.create();
	public final ObjectTester<TileEntity> tileEntityWhitelist = ObjectTester.create();

	public static class WhitelistRegisterEvent<T> extends Event {
		protected final ObjectTester<T> tester;

		protected WhitelistRegisterEvent(ObjectTester<T> tester) {
			this.tester = tester;
		}

		public void addClass(Class<? extends T> cls) {
			tester.addTester(new ClassTester<T>(cls));
		}

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

	public static class BlockRegisterEvent extends
			WhitelistRegisterEvent<Block> {
		protected BlockRegisterEvent(ObjectTester<Block> tester) {
			super(tester);
		}
	}

	public static class TileEntityRegisterEvent extends
			WhitelistRegisterEvent<TileEntity> {
		protected TileEntityRegisterEvent(ObjectTester<TileEntity> tester) {
			super(tester);
		}
	}

	public void initTesters() {
		entityWhitelist.addTester(new ClassTester<Entity>(EntityItem.class));
		entityWhitelist.addTester(new ClassTester<Entity>(EntityBoat.class));
		entityWhitelist.addTester(new ClassTester<Entity>(EntityMinecart.class));
		MinecraftForge.EVENT_BUS.post(new EntityRegisterEvent(entityWhitelist));

		blockWhitelist.addTester(new ITester<Block>() {
			@Override
			public Result test(Block o) {
				return (o.blockHardness < 0)? Result.REJECT : Result.CONTINUTE;
			}
		});

		blockWhitelist.addTester(new ITester<Block>() {
			@Override
			public openblocks.utils.ITester.Result test(Block o) {
				return o.getRenderType() == 0? Result.ACCEPT : Result.CONTINUTE;
			}
		});

		blockWhitelist.addTester(new ClassTester<Block>(BlockSand.class));
		blockWhitelist.addTester(new ClassTester<Block>(BlockStairs.class));
		blockWhitelist.addTester(new ClassTester<Block>(BlockFence.class));
		blockWhitelist.addTester(new ClassTester<Block>(BlockFenceGate.class));
		blockWhitelist.addTester(new ClassTester<Block>(BlockCactus.class));
		MinecraftForge.EVENT_BUS.post(new BlockRegisterEvent(blockWhitelist));

		tileEntityWhitelist.addTester(new ClassTester<TileEntity>(TileEntityBeacon.class)).addTester(new ClassTester<TileEntity>(TileEntityBrewingStand.class)).addTester(new ClassTester<TileEntity>(TileEntityChest.class)).addTester(new ClassTester<TileEntity>(TileEntityCommandBlock.class)).addTester(new ClassTester<TileEntity>(TileEntityDispenser.class)).addTester(new ClassTester<TileEntity>(TileEntityEnchantmentTable.class)).addTester(new ClassTester<TileEntity>(TileEntityEnderChest.class)).addTester(new ClassTester<TileEntity>(TileEntityFurnace.class)).addTester(new ClassTester<TileEntity>(TileEntityHopper.class)).addTester(new ClassTester<TileEntity>(TileEntityNote.class)).addTester(new ClassTester<TileEntity>(TileEntityRecordPlayer.class));
	}

	public boolean testBlock(World world, int x, int y, int z) {
		int blockId = world.getBlockId(x, y, z);
		Block /* block? */block /* block! */= Block/* blocky */.blocksList[blockId/*
																				 * blockety
																				 * block
																				 */];
		// semantic satiation FTW!

		if (block == null) return false;

		if (block instanceof BlockContainer) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			return (te != null)? tileEntityWhitelist.check(te) : false;
		}

		return blockWhitelist.check(block);
	}
}
