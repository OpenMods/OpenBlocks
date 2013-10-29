package openblocks.client;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import openblocks.common.entity.EntityHangGlider;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	private static int ticks = 0;

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

	/**
	 * @param mc
	 * @param renderTick
	 */
	public void preRenderTick(Minecraft mc, World world, float renderTick) {
		EntityHangGlider.updateGliders(world);
	}

	public void clientTick() {
		if (SoundEventsManager.isPlayerWearingGlasses()) {
			SoundEventsManager.instance.tickUpdate();
		}
		ticks++;
	}

	public static int getTicks() {
		return ticks;
	}
}
