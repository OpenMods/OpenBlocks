package openblocks.client.renderer.block.canvas;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class InnerBlockState {

	public static final IUnlistedProperty<IBlockState> PROPERTY = new IUnlistedProperty<IBlockState>() {

		@Override
		public String valueToString(IBlockState value) {
			return value.toString();
		}

		@Override
		public boolean isValid(IBlockState value) {
			return true;
		}

		@Override
		public Class<IBlockState> getType() {
			return IBlockState.class;
		}

		@Override
		public String getName() {
			return "inner";
		}
	};

}
