package openblocks.common.block;

import openblocks.Config;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockCreativeItemSpawner extends OpenBlock {

	public BlockCreativeItemSpawner() {
		super(Config.blockCreativeItemSpawnerId, Material.rock);
	}
	
	@Override
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:creativeitemspawner");
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

}
