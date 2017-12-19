package openblocks.common.item;

import com.google.common.collect.MapMaker;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
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
import openmods.model.itemstate.IStateItem;
import openmods.state.State;
import openmods.state.StateContainer;

@BookDocumentation(hasVideo = true)
public class ItemHangGlider extends Item implements IStateItem {

	private static Map<EntityPlayer, EntityHangGlider> spawnedGlidersMap = new MapMaker().weakKeys().weakValues().makeMap();

	public ItemHangGlider() {
		addPropertyOverride(new ResourceLocation("hidden"), new IItemPropertyGetter() {
			@Override
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return EntityHangGlider.isHeldStackDeployedGlider(entityIn, stack)? 2 : 0;
			}
		});
	}

	public static final IProperty<Boolean> deployedProperty = PropertyBool.create("deployed");

	private final StateContainer stateContainer = new StateContainer(deployedProperty);

	private final State deployedState = stateContainer.getBaseState().withProperty(deployedProperty, true);

	private final State inHandState = stateContainer.getBaseState().withProperty(deployedProperty, false);

	@Override
	public StateContainer getStateContainer() {
		return stateContainer;
	}

	@Override
	public State getState(ItemStack stack, World world, EntityLivingBase entity) {
		return EntityHangGlider.isHeldStackDeployedGlider(entity, stack)? deployedState : inHandState;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			EntityHangGlider glider = spawnedGlidersMap.get(player);
			if (glider != null && !glider.isDead) {
				if (glider.getHandHeld() == hand) despawnGlider(player, glider);
				// if deployed glider is in other hand, ignore
			} else spawnGlider(player, hand);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	private static void despawnGlider(EntityPlayer player, EntityHangGlider glider) {
		glider.setDead();
		spawnedGlidersMap.remove(player);
	}

	private static void spawnGlider(EntityPlayer player, EnumHand hand) {
		EntityHangGlider glider = new EntityHangGlider(player.world, player, hand);
		glider.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationPitch, player.rotationYaw);
		player.world.spawnEntity(glider);
		spawnedGlidersMap.put(player, glider);
	}
}
