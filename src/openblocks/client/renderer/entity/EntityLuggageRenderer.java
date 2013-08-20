package openblocks.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import openblocks.client.model.ModelLuggage;

public class EntityLuggageRenderer extends RenderLiving {

	public EntityLuggageRenderer() {
		super(new ModelLuggage(), 0.5F);
	}

}
