package openblocks.common.item;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.common.CraneRegistry;
import openblocks.common.entity.EntityMagnet;
import openmods.OpenMods;
import openmods.infobook.BookDocumentation;

import com.google.common.collect.MapMaker;

@BookDocumentation(customName = "crane_control", hasVideo = true)
public class ItemCraneControl extends Item {

	public ItemCraneControl() {
		setMaxStackSize(1);
	}

	private static final Map<EntityLivingBase, Long> debouncerTime = new MapMaker().weakKeys().makeMap();

	private static boolean hasClicked(EntityLivingBase entity) {
		long currentTime = OpenMods.proxy.getTicks(entity.worldObj);
		Long lastClick = debouncerTime.get(entity);
		if (lastClick == null || currentTime - lastClick > 5) {
			debouncerTime.put(entity, currentTime);
			return true;
		}

		return false;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if (entityLiving instanceof EntityPlayer && hasClicked(entityLiving)) {
			final EntityPlayer player = (EntityPlayer)entityLiving;
			final EntityMagnet magnet = CraneRegistry.instance.getMagnetForPlayer(player);
			if (magnet != null) magnet.toggleMagnet();

		}
		return true;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		CraneRegistry.Data data = CraneRegistry.instance.getData(player, false);

		if (data != null) {
			data.isExtending = Config.craneShiftControl? player.isSneaking() : !data.isExtending;
		}

		player.setItemInUse(stack, getMaxItemUseDuration(stack));
		return stack;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		if (player instanceof EntityPlayerMP
				&& ItemCraneBackpack.isWearingCrane(player)) {
			CraneRegistry.Data data = CraneRegistry.instance.getData(player, true);
			data.updateLength();
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000; // quite long time!
	}

	public enum State {
		NONE,
		DOWN,
		UP,
		LOCKED,
		DETECTED
	}

	// TODO 1.8.9 temporary. Enum probably only for port reasons
	public State getState(ItemStack stack, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (player != null && ItemCraneBackpack.isWearingCrane(player)) {
			CraneRegistry.Data data = CraneRegistry.instance.getData(player, false);
			if (data != null) {
				if (usingItem == stack) { return data.isExtending? State.DOWN : State.UP; }

				EntityMagnet magnet = CraneRegistry.instance.getMagnetForPlayer(player);

				if (magnet != null) {
					if (magnet.isLocked()) return State.LOCKED;
					else if (magnet.isAboveTarget()) return State.DETECTED;
				}
			}
		}

		return State.LOCKED;
	}

}
