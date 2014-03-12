package openblocks.enchantments.flimflams;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamEffect;
import openblocks.rubbish.LoreGenerator;
import openmods.utils.WorldUtils;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

public class RenameFlimFlam implements IFlimFlamEffect {

	@Override
	public boolean execute(EntityPlayer target) {
		World world = target.worldObj;
		AxisAlignedBB around = target.boundingBox.expand(20, 20, 20);
		List<EntityLiving> living = WorldUtils.getEntitiesWithinAABB(world, EntityLiving.class, around, WorldUtils.NON_PLAYER);

		Collections.shuffle(living);
		for (EntityLiving e : living) {
			if (Strings.isNullOrEmpty(e.getCustomNameTag())) {
				e.setCustomNameTag(StringUtils.abbreviate(LoreGenerator.generateName(), 64));
				return true;
			}
		}

		return false;
	}

	@Override
	public String name() {
		return "living-rename";
	}

	@Override
	public int weight() {
		return 30;
	}

	@Override
	public int cost() {
		return 10;
	}

	@Override
	public boolean isSilent() {
		return false;
	}

}
