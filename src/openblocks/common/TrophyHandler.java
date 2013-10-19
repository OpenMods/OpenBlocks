package openblocks.common;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import openblocks.Config;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.trophy.*;
import openblocks.utils.BlockUtils;

public class TrophyHandler {

	public static HashMap<Trophy, Entity> entityCache = new HashMap<Trophy, Entity>();

	public static Entity getEntityFromCache(Trophy trophy) {
		Entity entity = entityCache.get(trophy);
		if (entity == null) {
			entity = trophy.createEntity();
			entityCache.put(trophy, entity);
		}
		return entity;
	}

	public enum Trophy {
		Wolf(),
		Chicken(new ItemDropBehavior(10000, Item.egg.itemID, "mob.chicken.plop")),
		Cow(new ItemDropBehavior(20000, Item.leather.itemID)),
		Creeper(new CreeperBehavior()),
		Skeleton(new SkeletonBehavior()),
		PigZombie(new ItemDropBehavior(20000, Item.goldNugget.itemID)),
		Bat(1.0, -0.3),
		Zombie(),
		Witch(0.35),
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
		Slime(0.4) {
			@Override
			protected Entity createEntity() {
				Entity entity = super.createEntity();

				try {
					Method slimeSizeMethod = null;
					try {
						slimeSizeMethod = EntitySlime.class.getDeclaredMethod("setSlimeSize", int.class);
					} catch (NoSuchMethodException e) {
						try {
							slimeSizeMethod = EntitySlime.class.getDeclaredMethod("func_70799_a", int.class);
						} catch (NoSuchMethodException f) {
							Log.warn("setSlimeSize cannot be found");
						}
					}
					if (slimeSizeMethod != null) {
						slimeSizeMethod.setAccessible(true);
						slimeSizeMethod.invoke(entity, 1);
					}
				} catch (Exception e) {
					Log.warn(e, "Can't update slime size");
				}

				return entity;
			}
		},
		Ghast(0.1, 0.2),
		Enderman(0.3, new EndermanBehavior()),
		LavaSlime(0.8),
		Squid(0.3, 0.5, new SquidBehavior()),
		MushroomCow(new MooshroomBehavior()),
		VillagerGolem(0.3),
		SnowMan(new SnowmanBehavior()),
		Pig(new ItemDropBehavior(20000, Item.porkRaw.itemID));

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
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("entity", toString());
			stack.setTagCompound(tag);
			return stack;
		}

		public void playSound(World world, double x, double y, double z) {
			Entity e = getEntity();
			e.posX = x;
			e.posY = y;
			e.posZ = z;
			e.worldObj = world;
			if (e instanceof EntityLiving) {
				((EntityLiving)e).playLivingSound();
			}
		}

		public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
			if (behavior != null) {
				behavior.executeActivateBehavior(tile, player);
			}
		}

		public void executeTickBehavior(TileEntityTrophy tile) {
			if (behavior != null) {
				behavior.executeTickBehavior(tile);
			}
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
