package openblocks.common;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import info.openmods.calc.Environment;
import info.openmods.calc.ExprType;
import info.openmods.calc.SingleExprEvaluator;
import info.openmods.calc.SingleExprEvaluator.EnvironmentConfigurator;
import info.openmods.calc.types.fp.DoubleCalculatorFactory;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTrophyBlock;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.trophy.BlazeBehavior;
import openblocks.trophy.CaveSpiderBehavior;
import openblocks.trophy.CreeperBehavior;
import openblocks.trophy.EndermanBehavior;
import openblocks.trophy.GuardianBehavior;
import openblocks.trophy.ITrophyBehavior;
import openblocks.trophy.ItemDropBehavior;
import openblocks.trophy.MooshroomBehavior;
import openblocks.trophy.SkeletonBehavior;
import openblocks.trophy.SnowmanBehavior;
import openblocks.trophy.SquidBehavior;
import openblocks.trophy.WitchBehavior;
import openmods.Log;
import openmods.config.properties.ConfigurationChange;
import openmods.reflection.ReflectionHelper;

public class TrophyHandler {

	private static final Map<Trophy, Entity> ENTITY_CACHE = Maps.newHashMap();

	private final Random fallbackDropChance = new Random();

	private final SingleExprEvaluator<Double, ExprType> dropChanceCalculator = SingleExprEvaluator.create(DoubleCalculatorFactory.createDefault());

	{
		updateDropChanceFormula();
	}

	@SubscribeEvent
	public void onConfigChange(ConfigurationChange.Post evt) {
		if (evt.check("trophy", "trophyDropChanceFormula"))
			updateDropChanceFormula();
	}

	private void updateDropChanceFormula() {
		dropChanceCalculator.setExpr(ExprType.INFIX, Config.trophyDropChanceFormula);

		if (!dropChanceCalculator.isExprValid())
			Log.info("Invalid trophyDropChanceFormula formula: ", Config.trophyDropChanceFormula);
	}

	public static Entity getEntityFromCache(Trophy trophy) {
		Entity entity = ENTITY_CACHE.get(trophy);
		if (entity == null) {
			if (!ENTITY_CACHE.containsKey(trophy)) {
				try {
					entity = trophy.createEntity();
				} catch (Throwable t) {
					Log.severe(t, "Failed to create dummy entity for trophy %s", trophy);
				}
			}
			ENTITY_CACHE.put(trophy, entity);
		}
		return entity;
	}

	private static Entity setSlimeSize(Entity entity, int size) {
		try {
			ReflectionHelper.call(entity, new String[] { "func_70799_a", "setSlimeSize" }, ReflectionHelper.primitive(size));
		} catch (Exception e) {
			Log.warn(e, "Can't update slime size");
		}
		return entity;
	}

	public enum Trophy {
		Wolf(),
		Chicken(new ItemDropBehavior(10000, new ItemStack(Items.EGG), SoundEvents.ENTITY_CHICKEN_EGG)),
		Cow(new ItemDropBehavior(20000, new ItemStack(Items.LEATHER))),
		Creeper(new CreeperBehavior()),
		Skeleton(new SkeletonBehavior()),
		PigZombie(new ItemDropBehavior(20000, new ItemStack(Items.GOLD_NUGGET))),
		Bat(1.0, -0.3),
		Zombie(),
		Witch(0.35, new WitchBehavior()),
		Villager(),
		Ozelot() {
			@Override
			protected Entity createEntity() {
				Entity entity = super.createEntity();

				try {
					((EntityOcelot)entity).setTamed(true);
				} catch (ClassCastException e) {
					Log.warn("Invalid cat entity class: %s", entity.getClass());
				}
				return entity;
			}
		},
		Sheep(),
		Blaze(new BlazeBehavior()),
		Silverfish(),
		Spider(),
		CaveSpider(new CaveSpiderBehavior()),
		Slime(0.6) {
			@Override
			protected Entity createEntity() {
				return setSlimeSize(super.createEntity(), 1);
			}
		},
		Ghast(0.1, 0.3),
		Enderman(0.3, new EndermanBehavior()),
		LavaSlime(0.6) {
			@Override
			protected Entity createEntity() {
				return setSlimeSize(super.createEntity(), 1);
			}
		},
		Squid(0.3, 0.5, new SquidBehavior()),
		MushroomCow(new MooshroomBehavior()),
		VillagerGolem(0.3),
		SnowMan(new SnowmanBehavior()),
		Pig(new ItemDropBehavior(20000, new ItemStack(Items.PORKCHOP))),
		Endermite(),
		Guardian(new GuardianBehavior()),
		Rabbit(new ItemDropBehavior(20000, new ItemStack(Items.CARROT)));
		// Skipped: Horse (needs world in ctor), Wither (renders boss bar)
		// TODO bear, shulker

		private double scale = 0.4;
		private double verticalOffset = 0.0;
		private ITrophyBehavior behavior;

		Trophy() {}

		Trophy(ITrophyBehavior behavior) {
			this.behavior = behavior;
		}

		Trophy(double scale) {
			this.scale = scale;
		}

		Trophy(double scale, ITrophyBehavior behavior) {
			this.scale = scale;
			this.behavior = behavior;
		}

		Trophy(double scale, double verticalOffset) {
			this(scale);
			this.verticalOffset = verticalOffset;
		}

		Trophy(double scale, double verticalOffset, ITrophyBehavior behavior) {
			this(scale, verticalOffset);
			this.behavior = behavior;
		}

		public double getVerticalOffset() {
			return verticalOffset;
		}

		public double getScale() {
			return scale;
		}

		public Entity getEntity() {
			return getEntityFromCache(this);
		}

		public ItemStack getItemStack() {
			return ItemTrophyBlock.putMetadata(new ItemStack(OpenBlocks.Blocks.trophy), this);
		}

		public void playSound(World world, BlockPos pos) {
			if (world == null) return;

			Entity e = getEntity();
			if (e instanceof EntityLiving) {
				e.posX = pos.getX();
				e.posY = pos.getY();
				e.posZ = pos.getZ();

				synchronized (e) {
					e.worldObj = world;
					((EntityLiving)e).playLivingSound();
					e.worldObj = null;
				}
			}
		}

		public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
			if (behavior != null) return behavior.executeActivateBehavior(tile, player);
			return 0;
		}

		public void executeTickBehavior(TileEntityTrophy tile) {
			if (behavior != null) behavior.executeTickBehavior(tile);
		}

		protected Entity createEntity() {
			return EntityList.createEntityByName(name(), null);
		}

		static {

			ImmutableMap.Builder<String, Trophy> builder = ImmutableMap.builder();

			for (Trophy t : values())
				builder.put(t.name(), t);

			TYPES = builder.build();
		}

		public final static Map<String, Trophy> TYPES;

		public final static Trophy[] VALUES = values();
	}

	@SubscribeEvent
	public void onLivingDrops(final LivingDropsEvent event) {
		final Entity entity = event.getEntity();
		if (event.isRecentlyHit() && canDrop(entity)) {
			final Double result = dropChanceCalculator.evaluate(
					new EnvironmentConfigurator<Double>() {
						@Override
						public void accept(Environment<Double> env) {
							env.setGlobalSymbol("looting", Double.valueOf(event.getLootingLevel()));
							env.setGlobalSymbol("chance", Config.trophyDropChance);
						}
					}, new Supplier<Double>() {
						@Override
						public Double get() {
							final double bias = fallbackDropChance.nextDouble() / 4;
							final double selection = fallbackDropChance.nextDouble();
							return (event.getLootingLevel() + bias) * Config.trophyDropChance - selection;
						}
					});

			if (result > 0) {
				final String entityName = EntityList.getEntityString(entity);
				if (!Strings.isNullOrEmpty(entityName)) {
					Trophy mobTrophy = Trophy.TYPES.get(entityName);
					if (mobTrophy != null) {
						EntityItem drop = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, mobTrophy.getItemStack());
						drop.setDefaultPickupDelay();
						event.getDrops().add(drop);
					}
				}
			}
		}
	}

	private static boolean canDrop(Entity entity) {
		final World world = entity.worldObj;
		return world != null && world.getGameRules().getBoolean("doMobLoot");
	}

}
