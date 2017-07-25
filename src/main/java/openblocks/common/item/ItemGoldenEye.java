package openblocks.common.item;

import com.google.common.base.Strings;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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

	public ItemGoldenEye() {
		setMaxDamage(MAX_DAMAGE);
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		if (hand == EnumHand.MAIN_HAND && world instanceof WorldServer && player instanceof EntityPlayerMP) {
			final EntityPlayerMP betterPlayer = (EntityPlayerMP)player;
			final WorldServer betterWorld = (WorldServer)world;
			if (player.isSneaking()) tryLearnStructure(stack, betterWorld, betterPlayer);
			else if (trySpawnEntity(stack, betterWorld, betterPlayer)) stack.setCount(0);

			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	private static void tryLearnStructure(@Nonnull ItemStack stack, WorldServer world, EntityPlayerMP player) {
		Map<String, BlockPos> nearbyStructures = StructureRegistry.instance.getNearestStructures(world, player.getPosition());

		String newStructureName = "";
		double max = Double.MAX_VALUE;

		for (Map.Entry<String, BlockPos> e : nearbyStructures.entrySet()) {
			BlockPos pos = e.getValue();
			if (Config.eyeDebug) player.sendMessage(new TextComponentTranslation(
					"openblocks.misc.structure_pos", e.getKey(), pos.getX(), pos.getY(), pos.getZ()));

			double distSq = player.getDistanceSqToCenter(pos);

			if (distSq < max) {
				max = distSq;
				newStructureName = e.getKey();
			}
		}

		if (!Strings.isNullOrEmpty(newStructureName)) {
			player.sendMessage(new TextComponentTranslation("openblocks.misc.locked_on_structure", new TextComponentTranslation(StructureRegistry.structureNameLocalizationKey(newStructureName))));
			NBTTagCompound tag = ItemUtils.getItemTag(stack);
			tag.setString(TAG_STRUCTURE, newStructureName);
		} else {
			player.sendMessage(new TextComponentTranslation("openblocks.misc.no_nearby_structures"));
		}
	}

	private static boolean trySpawnEntity(@Nonnull ItemStack stack, WorldServer world, EntityPlayerMP player) {
		int damage = stack.getItemDamage();
		if (damage >= stack.getMaxDamage()) return false;

		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		String structureName = tag.getString(TAG_STRUCTURE);

		if (Strings.isNullOrEmpty(structureName)) {
			player.sendMessage(new TextComponentTranslation("openblocks.misc.structure_not_locked"));
			return false;
		}

		Map<String, BlockPos> nearbyStructures = StructureRegistry.instance.getNearestStructures(world, player.getPosition());

		BlockPos structurePos = nearbyStructures.get(structureName);
		if (structurePos != null) {
			if (Config.eyeDebug) player.sendMessage(new TextComponentTranslation(
					"openblocks.misc.structure_pos", structureName, structurePos.getX(), structurePos.getY(), structurePos.getZ()));

			stack.setItemDamage(damage + 1);
			EntityGoldenEye eye = new EntityGoldenEye(world, stack, player, structurePos);
			world.spawnEntity(eye);
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDEREYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> result) {
		result.add(new ItemStack(item, 1, 0));
		result.add(new ItemStack(item, 1, MAX_DAMAGE));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, EntityPlayer player, List<String> result, boolean expanded) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		if (tag.hasKey(TAG_STRUCTURE, Constants.NBT.TAG_STRING)) {
			final String structure = tag.getString(TAG_STRUCTURE);
			final String localizedStructure = TranslationUtils.translateToLocal(StructureRegistry.structureNameLocalizationKey(structure));
			result.add(TranslationUtils.translateToLocalFormatted("openblocks.misc.locked_on_structure", localizedStructure));
		}
	}

}
