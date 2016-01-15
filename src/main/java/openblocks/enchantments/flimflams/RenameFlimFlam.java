package openblocks.enchantments.flimflams;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;
import openblocks.rubbish.LoreGenerator;
import openmods.utils.WorldUtils;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

public class RenameFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {
		World world = target.worldObj;
		AxisAlignedBB around = target.getEntityBoundingBox().expand(20, 20, 20);
		List<EntityLiving> living = world.getEntitiesWithinAABB(EntityLiving.class, around, WorldUtils.NON_PLAYER);

		Collections.shuffle(living);
		for (EntityLiving e : living) {
			if (Strings.isNullOrEmpty(e.getCustomNameTag())) {
				e.setCustomNameTag(StringUtils.abbreviate(LoreGenerator.generateName(), 64));
				return true;
			}
		}

		return false;
	}

}
