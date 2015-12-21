package openblocks.common.block;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openmods.infobook.BookDocumentation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class BlockBuilderGuide extends BlockGuide {

	public BlockBuilderGuide() {}

	@Override
	protected String getCenterIconName() {
		return "openblocks:guide_center_ender";
	}

	@Override
	protected boolean areButtonsActive(EntityPlayer player) {
		final ItemStack heldItem = player.getHeldItem();
		return heldItem == null || !(heldItem.getItem() instanceof ItemBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		world.spawnParticle("smoke", x + 0.5f, y + 0.8f, z + 0.5f, 0.0D, 0.0D, 0.0D);
		final EntityFX flame = Minecraft.getMinecraft().renderGlobal.doSpawnParticle("flame", x + 0.5f, y + 0.7f, z + 0.5f, 0.0D, 0.0D, 0.0D);
		if (flame != null) flame.setRBGColorF(0, 1, 1);
	}
}
