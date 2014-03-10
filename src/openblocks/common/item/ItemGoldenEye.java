package openblocks.common.item;

import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.StructureRegistry;
import openblocks.common.entity.EntityGoldenEye;
import openmods.Log;
import openmods.utils.ItemUtils;

import com.google.common.base.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGoldenEye extends Item {

	public static final int MAX_DAMAGE = 100;
	private static final String TAG_STRUCTURE = "Structure";

	public ItemGoldenEye() {
		super(Config.itemGoldenEyeId);
		setMaxDamage(MAX_DAMAGE);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world instanceof WorldServer && player instanceof EntityPlayerMP) {
			EntityPlayerMP betterPlayer = (EntityPlayerMP)player;
			if (player.isSneaking()) tryLearnStructure(stack, (WorldServer)world, betterPlayer);
			else {
				if (trySpawnEntity(stack, (WorldServer)world, betterPlayer)) stack.stackSize = 0;
			}
		}

		return stack;
	}

	private static void tryLearnStructure(ItemStack stack, WorldServer world, EntityPlayerMP player) {
		Map<String, ChunkPosition> nearbyStructures = StructureRegistry.instance.getNearestStructures(world, (int)player.posX, (int)player.posY, (int)player.posZ);

		String structureName = "";
		double max = Double.MAX_VALUE;

		for (Map.Entry<String, ChunkPosition> e : nearbyStructures.entrySet()) {
			ChunkPosition pos = e.getValue();
			if (Config.eyeDebug) player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(
					"openblocks.misc.structure_pos", e.getKey(), pos.x, pos.y, pos.z));

			double dx = pos.x - player.posX;
			double dy = pos.y - player.posY;
			double dz = pos.z - player.posZ;

			double dist = (dx * dx) + (dy * dy) + (dz * dz);

			if (dist < max) {
				max = dist;
				structureName = e.getKey();
			}
		}

		if (!Strings.isNullOrEmpty(structureName)) {
			Log.info("Learned structure %s, d = %f", structureName, max);
			NBTTagCompound tag = ItemUtils.getItemTag(stack);
			tag.setString(TAG_STRUCTURE, structureName);
		}
	}

	private static boolean trySpawnEntity(ItemStack stack, WorldServer world, EntityPlayerMP player) {
		int damage = stack.getItemDamage();
		if (damage >= stack.getMaxDamage()) return false;

		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		String structureName = tag.getString(TAG_STRUCTURE);

		if (Strings.isNullOrEmpty(structureName)) return false;

		Map<String, ChunkPosition> nearbyStructures = StructureRegistry.instance.getNearestStructures(world, (int)player.posX, (int)player.posY, (int)player.posZ);

		ChunkPosition structurePos = nearbyStructures.get(structureName);
		if (structurePos != null) {
			if (Config.eyeDebug) player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(
					"openblocks.misc.structure_pos", structureName, structurePos.x, structurePos.y, structurePos.z));

			stack.setItemDamage(damage + 1);
			EntityGoldenEye eye = new EntityGoldenEye(world, stack, player, structurePos);
			world.spawnEntityInWorld(eye);
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int id, CreativeTabs tab, List result) {
		result.add(new ItemStack(id, 1, 0));
		result.add(new ItemStack(id, 1, getMaxDamage()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:golden_eye");
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addInformation(ItemStack stack, EntityPlayer player, List result, boolean expanded) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		if (tag.hasKey(TAG_STRUCTURE)) result.add(StatCollector.translateToLocal("openblocks.misc.locked"));
	}

}
