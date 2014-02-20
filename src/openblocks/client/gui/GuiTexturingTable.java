package openblocks.client.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.Resource;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import openblocks.client.wallpapers.ReplaceableIcon;
import openblocks.common.container.ContainerTexturingTable;
import openblocks.common.tileentity.TileEntityTexturingTable;
import openmods.gui.BaseGuiContainer;
import openmods.gui.component.*;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableIntArray;

public class GuiTexturingTable extends BaseGuiContainer<ContainerTexturingTable> implements IComponentListener {

	private GuiComponentColorPicker colorPicker;
	private GuiComponentPixelGrid pixelGrid;
	private GuiComponentTextButton buttonDraw;

	public GuiTexturingTable(ContainerTexturingTable container) {
		super(container, 176, 200, "openblocks.gui.texturingtable");

		SyncableInt color = container.getOwner().getClientColor();
		SyncableIntArray colorGrid = container.getOwner().getClientColorGrid();

		root.addComponent(pixelGrid = new GuiComponentPixelGrid(118, 56, 16, 16, 3, colorGrid, color) {
			@Override
			public void mouseClicked(int mouseX, int mouseY, int button) {
				/****
				 * Might go
				 */
				ItemStack stack = mc.thePlayer.inventory.getItemStack();
				if (stack != null) {
					Item item = stack.getItem();
					if (item instanceof ItemBlock) {
						int blockId = ((ItemBlock)item).getBlockID();
						Block block = Block.blocksList[blockId];
						Icon icon = block.getIcon(0, 0);
						String basePath = ((TextureMap)mc.renderEngine.getTexture(TextureMap.locationBlocksTexture)).basePath;
						ResourceLocation resourcelocation = new ResourceLocation(icon.getIconName());
						ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getResourceDomain(), String.format("%s/%s%s", new Object[] { basePath, resourcelocation.getResourcePath(), ".png" }));
						try {
							Resource resource = mc.getResourceManager().getResource(resourcelocation1);
							InputStream inputstream = resource.getInputStream();
							AnimationMetadataSection animationmetadatasection = (AnimationMetadataSection)resource.getMetadata("animation");
							if (animationmetadatasection != null && animationmetadatasection.getFrameCount() > 1) {
								super.mouseClicked(mouseX, mouseY, button);
								return;
							}
							BufferedImage bufferedimage = ImageIO.read(inputstream);
							int height = bufferedimage.getHeight();
							int width = bufferedimage.getWidth();

							if (height > ReplaceableIcon.SIZE || width > ReplaceableIcon.SIZE) {
								Image resized = bufferedimage.getScaledInstance(ReplaceableIcon.SIZE, ReplaceableIcon.SIZE, Image.SCALE_SMOOTH);
								bufferedimage = new BufferedImage(ReplaceableIcon.SIZE, ReplaceableIcon.SIZE, Image.SCALE_REPLICATE);
								bufferedimage.getGraphics().drawImage(resized, 0, 0, null);
							}

							int[] aint = new int[ReplaceableIcon.SIZE * ReplaceableIcon.SIZE];
							bufferedimage.getRGB(0, 0, ReplaceableIcon.SIZE, ReplaceableIcon.SIZE, aint, 0, ReplaceableIcon.SIZE);
							setColors(aint);

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					super.mouseClicked(mouseX, mouseY, button);
				}
			}
		});
		root.addComponent(colorPicker = new GuiComponentColorPicker(10, 20, color));
		root.addComponent((buttonDraw = new GuiComponentTextButton(69, 91, 40, 13, 0xFFFFFF))
				.setText("Draw")
				.setName("btnDraw")
				.addListener(this));
	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		if (component.equals(buttonDraw)) {
			TileEntityTexturingTable table = getContainer().getOwner();
			table.sendColorsToServer();
		}
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {
		// TODO Auto-generated method stub

	}

}
