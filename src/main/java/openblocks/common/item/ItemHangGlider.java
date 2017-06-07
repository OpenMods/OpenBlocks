package openblocks.common.item;

import com.google.common.collect.MapMaker;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import openblocks.common.entity.EntityHangGlider;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class ItemHangGlider extends Item {

	private static Map<EntityPlayer, EntityHangGlider> spawnedGlidersMap = new MapMaker().weakKeys().weakValues().makeMap();

	public ItemHangGlider() {
		addPropertyOverride(new ResourceLocation("hidden"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return EntityHangGlider.isEntityWearingGlider(entityIn)? 2 : 0;
			}
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote && player != null) {
			EntityHangGlider glider = spawnedGlidersMap.get(player);
			if (glider != null) despawnGlider(player, glider);
			else spawnGlider(player);

			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	private static void despawnGlider(EntityPlayer player, EntityHangGlider glider) {
		glider.setDead();
		spawnedGlidersMap.remove(player);
	}

	private static void spawnGlider(EntityPlayer player) {
		EntityHangGlider glider = new EntityHangGlider(player.worldObj, player);
		glider.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationPitch, player.rotationYaw);
		player.worldObj.spawnEntityInWorld(glider);
		spawnedGlidersMap.put(player, glider);
	}
}
