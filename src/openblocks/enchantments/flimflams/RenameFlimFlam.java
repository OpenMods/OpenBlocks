package openblocks.enchantments.flimflams;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IAttackFlimFlam;
import openblocks.rubbish.LoreGenerator;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

public class RenameFlimFlam implements IAttackFlimFlam {

	private static final IEntitySelector NON_PLAYER = new IEntitySelector() {
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return !(entity instanceof EntityPlayer);
		}
	};

	@Override
	public void execute(EntityPlayer source, EntityPlayer target) {
		World world = target.worldObj;
		AxisAlignedBB around = target.boundingBox.expand(20, 20, 20);
		@SuppressWarnings("unchecked")
		List<EntityLiving> living = world.selectEntitiesWithinAABB(EntityLiving.class, around, NON_PLAYER);

		Collections.shuffle(living);
		for (EntityLiving e : living) {
			if (Strings.isNullOrEmpty(e.getCustomNameTag())) {
				e.setCustomNameTag(StringUtils.abbreviate(LoreGenerator.generateName(), 64));
				break;
			}
		}
	}

	@Override
	public String name() {
		return "living-rename";
	}

	@Override
	public float weight() {
		return 1;
	}

}
