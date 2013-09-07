package openblocks.client;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityHangGlider;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.contains(TickType.RENDER)) {
			if (Minecraft.getMinecraft().theWorld != null) {
				preRenderTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld, ((Float)tickData[0]).floatValue());
			}
		}

		if (type.contains(TickType.CLIENT)) {
			clientTick();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER, TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "OpenBlocksClientTick";
	}

	public void preRenderTick(Minecraft mc, World world, float renderTick) {
		Iterator<Entry<EntityPlayer, EntityHangGlider>> it = OpenBlocks.proxy.gliderClientMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<EntityPlayer, EntityHangGlider> next = it.next();
			EntityPlayer player = next.getKey();
			EntityHangGlider glider = next.getValue();
			if (player == null
					|| glider == null
					|| glider.isDead
					|| player.isDead
					|| player.getHeldItem() == null
					|| player.getHeldItem().getItem() != OpenBlocks.Items.hangGlider) {
				it.remove();
			} else {
				if (glider != null) {
					glider.fixPositions(Minecraft.getMinecraft().thePlayer);
				}
			}
		}
	}

	public void clientTick() {
		if (SoundEventsManager.isPlayerWearingGlasses()) {
			SoundEventsManager.instance.tickUpdate();
		}
	}
}
