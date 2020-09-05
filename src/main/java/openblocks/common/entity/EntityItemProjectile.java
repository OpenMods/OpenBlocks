package openblocks.common.entity;

import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Ugly EntityItem holder thingy with no air resistance. Because physics is hard
 * enough as it is
 *
 */
public class EntityItemProjectile extends EntityItem {

	public EntityItemProjectile(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityItemProjectile(World world, double x, double y, double z, @Nonnull ItemStack stack) {
		super(world, x, y, z, stack);
	}

	public EntityItemProjectile(World world) {
		super(world);
	}

	public static void registerFixes(DataFixer fixer) {
		fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(EntityItemProjectile.class, "Item"));
	}

	private boolean firstUpdate = true;

	@Override
	public void onUpdate() {
		// Remove the air drag that EntityItem.onUpdate adds to our velocity
		if (!firstUpdate && !this.onGround) {
			float f = 0.98F;
			this.motionX = this.motionX / f;
			this.motionY = this.motionY / f;
			this.motionZ = this.motionZ / f;
		}
		firstUpdate = false;

		// let vanilla run
		super.onUpdate();
	}

}
