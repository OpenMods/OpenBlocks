package openblocks.common.entity;

import com.google.common.base.Strings;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.lang.ref.WeakReference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityAssistant extends EntitySmoothMove implements IEntityAdditionalSpawnData {

	private String owner;
	private WeakReference<EntityPlayer> cachedOwner;
	protected double ownerOffsetX;
	protected double ownerOffsetY;
	protected double ownerOffsetZ;

	public EntityAssistant(World world, EntityPlayer owner) {
		super(world);
		this.cachedOwner = new WeakReference<EntityPlayer>(owner);

		if (owner != null) this.owner = owner.getCommandSenderName();
	}

	public EntityPlayer findOwner() {
		if (owner == null || owner.isEmpty()) return null;

		EntityPlayer result = worldObj.getPlayerEntityByName(owner);

		if (result != null) cachedOwner = new WeakReference<EntityPlayer>(result);

		return result;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		owner = tag.getString("Owner");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		if (owner != null) tag.setString("Owner", owner);
	}

	@Override
	public void onUpdate() {
		if (!worldObj.isRemote) {
			EntityPlayer owner = cachedOwner.get();

			if (owner == null) owner = findOwner();

			if (owner != null) smoother.setTarget(
					owner.posX + ownerOffsetX,
					owner.posY + owner.getEyeHeight() + ownerOffsetY,
					owner.posZ + ownerOffsetZ
					);

		}

		updatePrevPosition();
		smoother.update();
	}

	@Override
	protected void dealFireDamage(int par1) {}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
		if (!isDead && !worldObj.isRemote) entityDropItem(toItemStack(), 0.5f);
		setDead();
		return true;
	}

	public abstract ItemStack toItemStack();

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (player instanceof EntityPlayerMP && player.isSneaking() && getDistanceToEntity(player) < 3) {
			System.out.println("Interact: " + player);
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		ByteBufUtils.writeUTF8String(data, Strings.nullToEmpty(owner));
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		owner = ByteBufUtils.readUTF8String(data);
	}

	public void setSpawnPosition(Entity owner) {
		setPosition(owner.posX + 1, owner.posY + owner.getEyeHeight(), owner.posZ);
	}

}
