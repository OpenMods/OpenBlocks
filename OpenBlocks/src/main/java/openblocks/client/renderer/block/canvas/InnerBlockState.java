package openblocks.client.renderer.block.canvas;

import net.minecraft.block.BlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class InnerBlockState {

	public static final IUnlistedProperty<BlockState> PROPERTY = new IUnlistedProperty<BlockState>() {

		@Override
		public String valueToString(BlockState value) {
			return value.toString();
		}

		@Override
		public boolean isValid(BlockState value) {
			return true;
		}

		@Override
		public Class<BlockState> getType() {
			return BlockState.class;
		}

		@Override
		public String getName() {
			return "inner";
		}
	};

}
