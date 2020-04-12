package openblocks.integration;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;
import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Items;
import openperipheral.api.ApiAccess;
import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;
import org.apache.commons.lang3.tuple.Pair;

public class MagnetTurtleUpgrade implements ITurtleUpgrade {

	@Override
	public ResourceLocation getUpgradeID() {
		return OpenBlocks.location("magnet_turtle");
	}

	@Override
	public int getLegacyUpgradeID() {
		return TurtleIds.MAGNET_TURTLE_ID;
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "openblocks.turtle.magnet";
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingItem() {
		return new ItemStack(Items.miracleMagnet);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return ApiAccess.getApi(IComputerCraftObjectsFactory.class).createPeripheral(new MagnetControlAdapter(turtle, side));
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, Direction direction) {
		return null;
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
		IPeripheral peripheral = turtle.getPeripheral(side);
		if (peripheral instanceof ITickingTurtle) ((ITickingTurtle)peripheral).onPeripheralTick();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Pair<IBakedModel, Matrix4f> getModel(ITurtleAccess turtle, TurtleSide side) {
		final Minecraft mc = Minecraft.getMinecraft();
		final ModelManager modelManager = mc.getRenderItem().getItemModelMesher().getModelManager();

		// TODO: actual models
		ModelResourceLocation location = new ModelResourceLocation(side == TurtleSide.Left? "computercraft:turtle_crafting_table_left" : "computercraft:turtle_crafting_table_right", "inventory");
		return Pair.of(modelManager.getModel(location), null);
	}

}
