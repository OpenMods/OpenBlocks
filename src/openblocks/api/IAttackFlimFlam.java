package openblocks.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IAttackFlimFlam {

	public enum FlimFlammer {
		ATTACKER,
		DEFENDER,
		BOTH;
		public static FlimFlammer getFlimFlam(boolean source, boolean target) {
			if (source) {
				if (target) {
					return FlimFlammer.BOTH;
				} else {
					return FlimFlammer.ATTACKER;
				}
			} else if (target) { return FlimFlammer.DEFENDER; }
			return null;
		}
	};

	/**
	 * 
	 * @param attacker
	 * @param target
	 * @param flimFlammers
	 * @return was it successful?
	 */
	public boolean execute(EntityPlayer attacker, EntityPlayer target, FlimFlammer flimFlammers);

}
