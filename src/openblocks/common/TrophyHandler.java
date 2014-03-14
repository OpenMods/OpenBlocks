package openblocks.common;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.trophy.*;
import openmods.Log;
import openmods.utils.BlockUtils;
import openmods.utils.ItemUtils;
import openmods.utils.ReflectionHelper;

import com.google.common.collect.Maps;

public class TrophyHandler {

	public static Map<Trophy, Entity> entityCache = Maps.newHashMap();

	public static Entity getEntityFromCache(Trophy trophy) {
		Entity entity = entityCache.get(trophy);
		if (entity == null) {
			entity = trophy.createEntity();
			entityCache.put(trophy, entity);
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
		Chicken(new ItemDropBehavior(10000, new ItemStack(Item.egg), "mob.chicken.plop")),
		Cow(new ItemDropBehavior(20000, new ItemStack(Item.leather))),
		Creeper(new CreeperBehavior()),
		Skeleton(new SkeletonBehavior()),
		PigZombie(new ItemDropBehavior(20000, new ItemStack(Item.goldNugget))),
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
		Ghast(0.1, 0.2),
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
		Pig(new ItemDropBehavior(20000, new ItemStack(Item.porkRaw)));

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
			ItemStack stack = new ItemStack(OpenBlocks.Blocks.trophy, 1, ordinal());
			NBTTagCompound tag = ItemUtils.getItemTag(stack);
			tag.setString("entity", toString());
			return stack;
		}

		public void playSound(World world, double x, double y, double z) {
			Entity e = getEntity();
			e.posX = x;
			e.posY = y;
			e.posZ = z;
			e.worldObj = world;
			if (e instanceof EntityLiving && world != null) {
				((EntityLiving)e).playLivingSound();
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
			return EntityList.createEntityByName(toString(), null);
		}

		public final static Trophy[] VALUES = values();
	}

	@ForgeSubscribe
	public void onLivingDeath(LivingDeathEvent event) {
		if (!event.entity.worldObj.isRemote) {
			if (Math.random() < Config.trophyDropChance) {
				Entity entity = event.entity;
				String entityName = EntityList.getEntityString(entity);
				if (entityName != null && !entityName.isEmpty()) {
					try {
						Trophy mobTrophy = Trophy.valueOf(entityName);
						BlockUtils.dropItemStackInWorld(entity.worldObj, entity.posX, entity.posY, entity.posZ, mobTrophy.getItemStack());
					} catch (IllegalArgumentException e) {}
				}
			}
		}
	}

}
