package openblocks.rpc;

import openmods.utils.TranslationUtils;

public interface IStencilCrafter {
	enum Mode {
		STENCILS("openblocks.gui.drawingtable.stencils"),
		GLYPHS("openblocks.gui.drawingtable.glyphs");

		public final String name;

		Mode(final String name) {
			this.name = name;
		}

		public String getTranslatedName() {
			return TranslationUtils.translateToLocal(name);
		}
	}

	void selectionUp();

	void selectionDown();

	void cycleMode();

	void printGlyphs(String text);
}