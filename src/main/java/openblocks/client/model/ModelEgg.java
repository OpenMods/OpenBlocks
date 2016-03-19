package openblocks.client.model;

import net.minecraft.client.model.ModelRenderer;

public class ModelEgg extends AbstractModel {
	ModelRenderer egg;

	public ModelEgg() {
		textureWidth = 128;
		textureHeight = 64;

		egg = new ModelRenderer(this);

		egg.setTextureOffset(0, 0);
		egg.addBox(-2F, 0F, -2F, 4, 1, 4);

		egg.setTextureOffset(0, 5);
		egg.addBox(-3F, 1, -3F, 6, 15, 6);

		egg.setTextureOffset(0, 26);
		egg.addBox(-4F, 2, -4F, 8, 1, 8);

		egg.setTextureOffset(0, 35);
		egg.addBox(-5F, 3, -5F, 10, 2, 10);

		egg.setTextureOffset(40, 0);
		egg.addBox(-6F, 5, -6F, 12, 10, 12);

		egg.setTextureOffset(40, 22);
		egg.addBox(-7F, 8, -7F, 14, 5, 14);
	}

	public void render() {
		egg.render(SCALE);
	}
}
