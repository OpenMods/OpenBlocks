package openblocks.api;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IFlimFlamEffect {

	public boolean execute(EntityPlayerMP target);

	public String name();

	public int weight();

	public int cost();

	public boolean isSilent();

}
