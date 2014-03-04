package openblocks.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IAttackFlimFlam {

	/**
	 * 
	 * @param attacker
	 * @param target
	 * @param flimFlammers
	 */
	public void execute(EntityPlayer source, EntityPlayer target);

	public String name();

	// TODO: to be implemented
	public float weight();

}
