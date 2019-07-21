package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockBlockManpulatorBase extends OpenBlock.SixDirections {

	private static final int MASK_POWERED = 0x8;
	public static final IProperty<Boolean> POWERED = PropertyBool.create("powered");

	public BlockBlockManpulatorBase() {
		super(Material.ROCK);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getPropertyOrientation(), POWERED);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta)
				.withProperty(POWERED, (meta & MASK_POWERED) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return super.getMetaFromState(state) | (state.getValue(POWERED)? MASK_POWERED : 0);
	}

}
