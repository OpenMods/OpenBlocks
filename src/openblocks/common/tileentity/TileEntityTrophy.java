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
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import openblocks.api.IAwareTile;

public class TileEntityTrophy extends OpenTileEntity implements IAwareTile {

	
	public enum Trophy {
		
		Wolf(),
		Chicken(),
		Cow(),
		Creeper(),
		Skeleton(),
		PigZombie(),
		Bat(1.0),
		Zombie(),
		Witch(0.35),
		Villager(),
		Ozelot(),
		Sheep(),
		Blaze(),
		Silverfish(),
		Spider(),
		CaveSpider(),
		Slime(0.4),
		Ghast(0.1, 0.2),
		Enderman(0.3, 0),
		LavaSlime(0.8),
		Squid(0.3, 0.5),
		MushroomCow(),
		VillagerGolem(0.3);
		
		private double scale = 0.4;
		private double verticalOffset = 0.0;
		
		
		Trophy() {	
		}
		
		Trophy(double scale) {
			this.scale = scale;
		}
		
		Trophy(double scale, double verticalOffset) {
			this(scale);
			this.verticalOffset = verticalOffset;
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
			if (trophyType == Trophy.Enderman) {
				teleportRandomly(player);
			}
		}
		return false;
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
	

    protected boolean teleportRandomly(EntityPlayer player)
    {
        double d0 = player.posX + (worldObj.rand.nextDouble() - 0.5D) * 32.0D;
        double d1 = player.posY + (double)(worldObj.rand.nextInt(64) - 16);
        double d2 = player.posZ + (worldObj.rand.nextDouble() - 0.5D) * 32.0D;
        return this.teleportTo(player, d0, d1, d2);
    }
    /**
     * Teleport the enderman
     */
    protected boolean teleportTo(EntityPlayer player, double par1, double par3, double par5)
    {

        double d3 = player.posX;
        double d4 = player.posY;
        double d5 = player.posZ;
        player.posX = par1;
        player.posY = par3;
        player.posZ = par5;
        boolean flag = false;
        int i = MathHelper.floor_double(player.posX);
        int j = MathHelper.floor_double(player.posY);
        int k = MathHelper.floor_double(player.posZ);
        int l;

        if (this.worldObj.blockExists(i, j, k))
        {
            boolean flag1 = false;

            while (!flag1 && j > 0)
            {
                l = this.worldObj.getBlockId(i, j - 1, k);

                if (l != 0 && Block.blocksList[l].blockMaterial.blocksMovement())
                {
                    flag1 = true;
                }
                else
                {
                    --player.posY;
                    --j;
                }
            }

            if (flag1)
            {
            	player.setPositionAndUpdate(player.posX, player.posY, player.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(player, player.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(player.boundingBox))
                {
                    flag = true;
                }
            }
        }

        if (!flag)
        {
        	player.setPositionAndUpdate(d3, d4, d5);
            return false;
        }
        else
        {
            short short1 = 128;

            for (l = 0; l < short1; ++l)
            {
                double d6 = (double)l / ((double)short1 - 1.0D);
                float f = (worldObj.rand.nextFloat() - 0.5F) * 0.2F;
                float f1 = (worldObj.rand.nextFloat() - 0.5F) * 0.2F;
                float f2 = (worldObj.rand.nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (player.posX - d3) * d6 + (worldObj.rand.nextDouble() - 0.5D) * (double)player.width * 2.0D;
                double d8 = d4 + (player.posY - d4) * d6 + worldObj.rand.nextDouble() * (double)player.height;
                double d9 = d5 + (player.posZ - d5) * d6 + (worldObj.rand.nextDouble() - 0.5D) * (double)player.width * 2.0D;
                this.worldObj.spawnParticle("portal", d7, d8, d9, (double)f, (double)f1, (double)f2);
            }

            this.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
            player.playSound("mob.endermen.portal", 1.0F, 1.0F);
            return true;
        }
    }
}
