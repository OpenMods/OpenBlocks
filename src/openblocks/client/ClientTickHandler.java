package openblocks.client;

import java.util.EnumSet;

import openblocks.common.entity.EntityHangGlider;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.RENDER))) {
			if (Minecraft.getMinecraft().theWorld != null) {
				preRenderTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld, ((Float)tickData[0]).floatValue());
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "OpenBlocksClientTick";
	}

	public void preRenderTick(Minecraft mc, World world, float renderTick) {
		for (int id : EntityHangGlider.gliderMap.values()) {
			EntityHangGlider glider = (EntityHangGlider)world.getEntityByID(id);
			if (glider != null) {
				glider.fixPositions();
			}
		}
	}
}
