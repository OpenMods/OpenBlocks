package openblocks.enchantments.flimflams;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamEffect;
import openblocks.rubbish.LoreGenerator;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

public class RenameFlimFlam implements IFlimFlamEffect {

	private static final IEntitySelector NON_PLAYER = new IEntitySelector() {
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return !(entity instanceof EntityPlayer);
		}
	};

	@Override
	public boolean execute(EntityPlayer target) {
		World world = target.worldObj;
		AxisAlignedBB around = target.boundingBox.expand(20, 20, 20);
		@SuppressWarnings("unchecked")
		List<EntityLiving> living = world.selectEntitiesWithinAABB(EntityLiving.class, around, NON_PLAYER);

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
	public float weight() {
		return 1;
	}

	@Override
	public float cost() {
		return 10;
	}

}
