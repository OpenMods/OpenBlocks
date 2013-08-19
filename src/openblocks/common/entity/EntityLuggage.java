package openblocks.common.entity;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.common.entity.ai.EntityAICollectItem;

public class EntityLuggage extends EntityTameable {

	private GenericInventory inventory = new GenericInventory("luggage", false, 27);

	public EntityLuggage(World world) {
		super(world);
		this.texture = OpenBlocks.getTexturesPath("models/luggage.png");
		this.setSize(0.6F, 0.8F);
		this.moveSpeed = 0.4F;
		setTamed(true);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAICollectItem(this));
		this.tasks.addTask(3, new EntityAIFollowOwner(this, this.moveSpeed, 5.0F, 2.0F));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(5, new EntityAILookIdle(this));
	}

	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public int getMaxHealth() {
		return 100;
	}

	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return null;
	}

	@Override
	public boolean interact(EntityPlayer player) {
		if (!worldObj.isRemote) {
			if (player.isSneaking()) {
				setDead();
			} else {
				player.openGui(OpenBlocks.instance, OpenBlocks.Gui.Luggage.ordinal(), player.worldObj, entityId, 0, 0);
			}
		}
		return false;
	}

	@Override
	public boolean isEntityInvulnerable() {
		return true;
	}
}
