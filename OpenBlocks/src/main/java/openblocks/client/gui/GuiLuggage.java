package openblocks.client.gui;

import com.google.common.base.Strings;
import openblocks.common.container.ContainerLuggage;
import openblocks.common.entity.EntityLuggage;
import openmods.gui.BaseGuiContainer;

public class GuiLuggage extends BaseGuiContainer<ContainerLuggage> {

	private static String getInventoryName(EntityLuggage owner) {
		String customName = owner.getCustomNameTag();
		return Strings.isNullOrEmpty(customName)? "openblocks.gui.luggage" : customName;
	}

	private static int calculateHeight(EntityLuggage owner) {
		return owner.isSpecial()? 221 : 167;
	}

	public GuiLuggage(ContainerLuggage container) {
		super(container, 176, calculateHeight(container.getOwner()), getInventoryName(container.getOwner()));
	}
}
