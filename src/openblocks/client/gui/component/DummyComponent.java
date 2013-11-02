package openblocks.client.gui.component;

public class DummyComponent extends BaseComponent {

	private final int width;
	private final int height;

	public DummyComponent(int x, int y, int width, int height) {
		super(x, y);
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}
