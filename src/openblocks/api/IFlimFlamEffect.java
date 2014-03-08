package openblocks.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IFlimFlamEffect {

	public boolean execute(EntityPlayer target);

	public String name();

	public float weight();

	public float cost();

}
