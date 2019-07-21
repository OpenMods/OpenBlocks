package openblocks.common.item;

import com.google.common.base.Strings;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.common.entity.EntityGoldenEye;
import openmods.utils.ItemUtils;
import openmods.utils.TranslationUtils;
import openmods.world.StructureRegistry;

public class ItemGoldenEye extends Item {

	public static final int MAX_DAMAGE = 100;
	private static final String TAG_STRUCTURE = "Structure";
	private static final String TAG_X = "X";
	private static final String TAG_Y = "Y";
	private static final String TAG_Z = "Z";

	public ItemGoldenEye() {
		setMaxDamage(MAX_DAMAGE);
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		final ItemStack stack = player.getHeldItem(hand);

		if (hand != Hand.MAIN_HAND) return ActionResult.newResult(ActionResultType.PASS, stack);

		if (world instanceof ServerWorld && player instanceof ServerPlayerEntity) {
			final ServerPlayerEntity betterPlayer = (ServerPlayerEntity)player;
			final ServerWorld betterWorld = (ServerWorld)world;
			if (player.isSneaking()) tryLearnStructure(stack, betterWorld, betterPlayer);
			else if (trySpawnEntity(stack, betterWorld, betterPlayer)) stack.setCount(0);

			return ActionResult.newResult(ActionResultType.SUCCESS, stack);
		}

		return ActionResult.newResult(ActionResultType.PASS, stack);
	}

	private static void tryLearnStructure(@Nonnull ItemStack stack, ServerWorld world, ServerPlayerEntity player) {
		Map<String, BlockPos> nearbyStructures = StructureRegistry.instance.getNearestStructures(world, player.getPosition());

		String newStructureName = "";
		BlockPos newStructurePos = null;
		double max = Double.MAX_VALUE;

		for (Map.Entry<String, BlockPos> e : nearbyStructures.entrySet()) {
			BlockPos pos = e.getValue();
			if (Config.eyeDebug) player.sendMessage(new TranslationTextComponent(
					"openblocks.misc.structure_pos", e.getKey(), pos.getX(), pos.getY(), pos.getZ()));

			double distSq = player.getDistanceSqToCenter(pos);

			if (distSq < max) {
				max = distSq;
				newStructureName = e.getKey();
				newStructurePos = pos;
			}
		}

		if (!Strings.isNullOrEmpty(newStructureName) && newStructurePos != null) {
			player.sendMessage(new TranslationTextComponent("openblocks.misc.locked_on_nearest_structure", new TranslationTextComponent(StructureRegistry.structureNameLocalizationKey(newStructureName))));
			CompoundNBT tag = ItemUtils.getItemTag(stack);
			tag.setString(TAG_STRUCTURE, newStructureName);
			tag.setInteger(TAG_X, newStructurePos.getX());
			tag.setInteger(TAG_Y, newStructurePos.getY());
			tag.setInteger(TAG_Z, newStructurePos.getZ());

			if (Config.eyeDebug) player.sendMessage(new TranslationTextComponent(
					"openblocks.misc.structure_pos", newStructureName, newStructurePos.getX(), newStructurePos.getY(), newStructurePos.getZ()));

		} else {
			player.sendMessage(new TranslationTextComponent("openblocks.misc.no_nearby_structures"));
		}
	}

	private static boolean trySpawnEntity(@Nonnull ItemStack stack, ServerWorld world, ServerPlayerEntity player) {
		final int damage = stack.getItemDamage();
		if (damage >= stack.getMaxDamage()) return false;

		final CompoundNBT tag = ItemUtils.getItemTag(stack);

		if (!tag.hasKey(TAG_X, Constants.NBT.TAG_ANY_NUMERIC) ||
				!tag.hasKey(TAG_Y, Constants.NBT.TAG_ANY_NUMERIC) ||
				!tag.hasKey(TAG_Z, Constants.NBT.TAG_ANY_NUMERIC)) {
			player.sendMessage(new TranslationTextComponent("openblocks.misc.structure_not_locked"));
			return false;
		}

		final int x = tag.getInteger(TAG_X);
		final int y = tag.getInteger(TAG_Y);
		final int z = tag.getInteger(TAG_Z);

		final BlockPos structurePos = new BlockPos(x, y, z);

		stack.setItemDamage(damage + 1);
		EntityGoldenEye eye = new EntityGoldenEye(world, stack, player, structurePos);
		world.spawnEntity(eye);
		world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> result) {
		if (isInCreativeTab(tab)) {
			result.add(new ItemStack(this, 1, 0));
			result.add(new ItemStack(this, 1, MAX_DAMAGE));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> result, ITooltipFlag flag) {
		CompoundNBT tag = ItemUtils.getItemTag(stack);
		if (tag.hasKey(TAG_STRUCTURE, Constants.NBT.TAG_STRING)) {
			final String structure = tag.getString(TAG_STRUCTURE);
			final String localizedStructure = TranslationUtils.translateToLocal(StructureRegistry.structureNameLocalizationKey(structure));
			result.add(TranslationUtils.translateToLocalFormatted("openblocks.misc.locked_on_structure", localizedStructure));
		}
	}

}
