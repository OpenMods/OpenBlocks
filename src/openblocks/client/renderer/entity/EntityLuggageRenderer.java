package openblocks.client.renderer.entity;

import openblocks.client.model.ModelLuggage;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;

public class EntityLuggageRenderer extends RenderLiving {

	public EntityLuggageRenderer() {
		super(new ModelLuggage(), 0.5F);
	}

}
