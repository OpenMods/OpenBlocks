package openblocks.rpc;

import openmods.utils.TranslationUtils;

public interface IStencilCrafter {
	public enum Mode {
		STENCILS("openblocks.gui.drawingtable.stencils"),
		GLYPHS("openblocks.gui.drawingtable.glyphs");

		public final String name;

		private Mode(final String name) {
			this.name = name;
		}

		public String getTranslatedName() {
			return TranslationUtils.translateToLocal(name);
		}
	}

	public void selectionUp();

	public void selectionDown();

	public void cycleMode();

	public void printGlyphs(String text);
}