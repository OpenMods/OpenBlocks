package openblocks.rubbish;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import openblocks.events.PlayerActionEvent;
import openblocks.events.PlayerActionEvent.Type;
import openmods.utils.ItemUtils;

public class BrickManager {

	public static final String BOWELS_PROPERTY = "Bowels";

	public static final Achievement brickAchievement = new Achievement(70997, "openblocks.droppedBrick", 13, 13, Item.brick, null).registerAchievement();
	public static final StatBase brickStat = (new StatBasic(70998, "stat.openblocks.bricksDropped")).registerStat();

	public static class BowelContents implements IExtendedEntityProperties {

		public int brickCount;

		@Override
		public void saveNBTData(NBTTagCompound entityTag) {
			entityTag.setInteger("Bricks", brickCount);
		}

		@Override
		public void loadNBTData(NBTTagCompound entityTag) {
			brickCount = entityTag.getInteger("Bricks");
		}

		@Override
		public void init(Entity entity, World world) {}
	}

	public static BowelContents getProperty(Entity entity) {
		IExtendedEntityProperties prop = entity.getExtendedProperties(BOWELS_PROPERTY);
		return (prop instanceof BowelContents)? (BowelContents)prop : null;
	}

	@ForgeSubscribe
	public void onEntityConstruct(EntityEvent.EntityConstructing evt) {
		if (evt.entity instanceof EntityPlayer) evt.entity.registerExtendedProperties(BOWELS_PROPERTY, new BowelContents());
	}

	@ForgeSubscribe
	public void onEntityDeath(LivingDropsEvent evt) {
		if (evt.entity.worldObj.isRemote) return;
		IExtendedEntityProperties prop = evt.entity.getExtendedProperties(BOWELS_PROPERTY);

		if (prop instanceof BowelContents) {
			BowelContents tag = (BowelContents)prop;

			for (int i = 0; i < Math.min(tag.brickCount, 16); i++) {
				EntityItem entityItem = createBrick(evt.entity);
				evt.drops.add(entityItem);
			}
		}
	}

	private static boolean canDropBrick(EntityPlayer player) {
		if (player.capabilities.isCreativeMode) return true;

		IExtendedEntityProperties prop = player.getExtendedProperties(BOWELS_PROPERTY);

		if (prop instanceof BowelContents) {
			BowelContents tag = (BowelContents)prop;
			if (tag.brickCount > 0) {
				tag.brickCount--;
				return true;
			}
		}

		return false;
	}

	@ForgeSubscribe
	public void onPlayerScared(PlayerActionEvent evt) {
		if (evt.type == Type.BOO) {
			EntityPlayer player = (EntityPlayer)evt.player;

			if (canDropBrick(player)) {
				EntityItem drop = createBrick(player);
				drop.delayBeforeCanPickup = 20;
				player.worldObj.spawnEntityInWorld(drop);

				player.triggerAchievement(brickAchievement);
				player.addStat(brickStat, 1);
			}
		}
	}

	private static EntityItem createBrick(Entity dropper) {
		ItemStack brick = new ItemStack(Item.brick);
		EntityItem drop = ItemUtils.createDrop(dropper, brick);
		double rotation = Math.toRadians(dropper.rotationYaw) - Math.PI / 2;
		double dx = Math.cos(rotation);
		double dz = Math.sin(rotation);

		drop.moveEntity(0.75 * dx, 0.5, 0.75 * dz);

		drop.motionX = 0.5 * dx;
		drop.motionY = 0.2;
		drop.motionZ = 0.5 * dz;

		return drop;
	}

}
