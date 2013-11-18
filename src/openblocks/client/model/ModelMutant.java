package openblocks.client.model;

import java.util.HashMap;

import com.google.common.collect.Maps;

import openblocks.api.IMutantDefinition;
import openblocks.api.IMutantRenderer;
import openblocks.api.MutantRegistry;
import openblocks.common.entity.EntityMutant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;


@SideOnly(Side.CLIENT)
public class ModelMutant extends ModelBase {

	public HashMap<IMutantDefinition, IMutantRenderer> rendererCache = Maps.newHashMap();
	
	public ModelMutant() {

    }

	private IMutantRenderer getRenderer(IMutantDefinition definition) {
		IMutantRenderer renderer = rendererCache.get(definition);
		if (renderer == null) {
			renderer = definition.createRenderer();
			renderer.initialize(this);
			rendererCache.put(definition, renderer);
		}
		return renderer;
	}
	
    public void render(Entity entity, float legSwing, float prevLegSwing,
                    float wingSwing, float yaw, float pitch, float scale) {

            EntityMutant mutant = (EntityMutant) entity;
            
            IMutantDefinition head = mutant.getHead();
            IMutantDefinition body = mutant.getBody();
            IMutantDefinition arms = mutant.getArms();
            IMutantDefinition legs = mutant.getLegs();
            IMutantDefinition wings = mutant.getWings();
            IMutantDefinition tail = mutant.getTail();
            
            if (head != null) {
            	getRenderer(head).renderHead(mutant, scale, yaw, pitch);
            }

            if (body != null) {
            	getRenderer(body).renderBody(mutant, scale);
            }
            
            if (arms != null) {
            	getRenderer(arms).renderArms(mutant, scale, legSwing, prevLegSwing);
            }
            
            if (legs != null) {
            	getRenderer(legs).renderLegs(mutant, scale, legSwing, prevLegSwing);
            }
            
            if (wings != null) {
            	getRenderer(wings).renderWings(mutant, scale, wingSwing);
            }
            
            if (tail != null) {
            	getRenderer(tail).renderTail(mutant, scale, legSwing, prevLegSwing);
            }

    }

    public void _setTextureOffset(String par1Str, int par2, int par3) {
    	setTextureOffset(par1Str, par2, par3);
    }
    
}