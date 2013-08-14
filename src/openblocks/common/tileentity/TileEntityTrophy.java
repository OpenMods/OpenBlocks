package openblocks.common.tileentity;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import openblocks.api.IAwareTile;
import openblocks.trophy.CaveSpiderBehavior;
import openblocks.trophy.EndermanBehavior;
import openblocks.trophy.ITrophyBehavior;
import openblocks.trophy.SkeletonBehavior;
import openblocks.trophy.SnowmanBehavior;

public class TileEntityTrophy extends OpenTileEntity implements IAwareTile {

	
	public enum Trophy {
		Wolf(),
		Chicken(),
		Cow(),
		Creeper(),
		Skeleton(new SkeletonBehavior()),
		PigZombie(),
		Bat(1.0, -0.3),
		Zombie(),
		Witch(0.35),
		Villager(),
		Ozelot(),
		Sheep(),
		Blaze(),
		Silverfish(),
		Spider(),
		CaveSpider(new CaveSpiderBehavior()),
		Slime(0.4),
		Ghast(0.1, 0.2),
		Enderman(0.3, new EndermanBehavior()),
		LavaSlime(0.8),
		Squid(0.3, 0.5),
		MushroomCow(),
		VillagerGolem(0.3),
		SnowMan(new SnowmanBehavior());
		
		private double scale = 0.4;
		private double verticalOffset = 0.0;
		private ITrophyBehavior behavior;
		
		Trophy() {	
		}
		
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
		
		public void executeActivateBehavior(TileEntity tile, EntityPlayer player) {
			if (behavior != null) {
				behavior.executeActivateBehavior(tile, player);
			}
		}
		
		public void executeTickBehavior(TileEntity tile) {
			if (behavior != null) {
				behavior.executeTickBehavior(tile);
			}
		}
	}
	

	public static Trophy debugTrophy = Trophy.Wolf;
	
	private Trophy trophyType;
	
	public static HashMap<Trophy, Entity> entityCache = new HashMap<Trophy, Entity>();

	public static Entity getEntityFromCache(Trophy trophy) {
		Entity entity = entityCache.get(trophy);
		if (entity == null) {
			entity = EntityList.createEntityByName(trophy.toString(), null);
			if (entity instanceof EntitySlime) {
				try {
					Method slimeSizeMethod = EntitySlime.class.getDeclaredMethod("setSlimeSize", int.class);
					if (slimeSizeMethod == null) {
						slimeSizeMethod = EntitySlime.class.getDeclaredMethod("func_70799_a", int.class);
					}
					if (slimeSizeMethod != null) {
						slimeSizeMethod.setAccessible(true);
						slimeSizeMethod.invoke(entity, 1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			entityCache.put(trophy, entity);
		}
		return entity;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		Packet132TileEntityData packet = new Packet132TileEntityData();
		packet.actionType = 0;
		packet.xPosition = xCoord;
		packet.yPosition = yCoord;
		packet.zPosition = zCoord;
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		packet.customParam1 = nbt;
		return packet;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.customParam1);
	}
	
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			trophyType.executeTickBehavior(this);
		}
	}
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			trophyType.playSound(worldObj, xCoord, yCoord, zCoord);
			trophyType.executeActivateBehavior(this, player);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub
		
	}
	
	public Trophy getTrophyType() {
		return trophyType;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
	    /**
	     * Debug only. These will be dropped randomly with mobs!
	     */
		if (!worldObj.isRemote) {
		    int next = (debugTrophy.ordinal() + 1) % Trophy.values().length; 
		    debugTrophy = Trophy.values()[next];
		    trophyType = debugTrophy;
		    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	    }
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("trophytype")) {
			trophyType = Trophy.valueOf(tag.getString("trophytype"));
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("trophytype", trophyType.toString());
	}
	
}
