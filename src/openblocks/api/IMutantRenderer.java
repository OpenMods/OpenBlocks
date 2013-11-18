package openblocks.api;

import net.minecraft.client.model.ModelBase;

public interface IMutantRenderer {

	public void initialize(ModelBase model);

	public void renderHead(IMutant mutant, float scale, float yaw, float pitch);

	public void renderLegs(IMutant mutant, float scale, float legSwing, float prevLegSwing);

	public void renderBody(IMutant mutant, float scale);

	public void renderWings(IMutant mutant, float scale);

	public void renderArms(IMutant mutant, float scale, float legSwing);

	public void renderTail(IMutant mutant, float scale);
}
