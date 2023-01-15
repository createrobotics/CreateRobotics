package com.workert.robotics.client.screens;

import com.google.common.collect.Lists;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Arrays;
import java.util.List;

public class CodeEditorScreen extends AbstractSimiScreen {

	protected ItemStack program;

	protected AllGuiTextures background;
	private IconButton confirmButton;

	// private EditBox codeTextBox;

	private long lastClickTime;
	private int lastIndex = -1;

	private DisplayCache displayCache = new DisplayCache("", new CodeEditorScreen.Pos2i(0, 0), true, new int[] {0},
			new CodeEditorScreen.LineInfo[] {new CodeEditorScreen.LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
	private String code = "";

	private int frameTick;

	private final TextFieldHelper codeEdit = new TextFieldHelper(this::getCode, this::setCode, this::getClipboard,
			this::setClipboard,
			p_98179_ -> (p_98179_.length() < 1024 && this.font.wordWrapHeight(p_98179_, 114) <= 128));

	public CodeEditorScreen(ItemStack program) {
		super(Component.literal("Code Editor"));
		this.program = program;
		this.background = AllGuiTextures.STATION;
	}

	@Override
	protected void init() {
		this.setWindowSize(this.background.width, this.background.height);
		super.init();
		this.clearWidgets();

		int x = this.guiLeft;
		int y = this.guiTop;

		this.confirmButton = new IconButton(x + this.background.width - 33, y + this.background.height - 24,
				AllIcons.I_CONFIRM);
		this.confirmButton.withCallback((mouseX, mouseY) -> {
			this.program.getOrCreateTag().putString("code", this.code); //this.codeTextBox.getValue();
			this.onClose();
		});
		this.addRenderableWidget(this.confirmButton);

		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

		this.setCode(this.program.getOrCreateTag().getString("code"));

		/*this.codeTextBox = new EditBox(this.font, x + 23, y + 44, this.background.width - 54, 44,
				new TextComponent("Code Editor"));
		this.codeTextBox.setBordered(true); // Maybe false looks better?
		this.codeTextBox.setMaxLength(23);
		this.codeTextBox.setTextColor(0xFFFFFF);
		this.codeTextBox.setValue(this.drone.droneCode);
		this.codeTextBox.mouseClicked(0, 0, 0);

		this.addRenderableWidget(this.codeTextBox);*/
	}

	@Override
	protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		int x = this.guiLeft;
		int y = this.guiTop;

		this.background.render(ms, x, y, this);

		ms.pushPose();
		TransformStack msr = TransformStack.cast(ms);
		msr.pushPose().translate(x + this.background.width + 4, y + this.background.height + 4, 100).scale(40)
				.rotateX(-22).rotateY(63);

		ms.popPose();
	}

	@Override
	public void render(PoseStack ms, int pMouseX, int pMouseY, float pPartialTick) {
		GuiComponent.drawCenteredString(ms, new NoShadowFontWrapper(this.font), "Drone Programmer", this.width / 2,
				this.guiTop + 4, 0x442000);

		this.renderBackground(ms);
		this.setFocused(null);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BookViewScreen.BOOK_LOCATION);
		int i = (this.width - 192) / 2;
		int j = 2;
		this.blit(ms, i, 2, 0, 0, 192, 192);

		int j1 = this.font.width(this.code);
		this.font.draw(ms, this.code, i - j1 + 192 - 44, 18.0F, 0);
		DisplayCache displaycache = this.getDisplayCache();

		for (LineInfo lineinfo : displaycache.lines) {
			this.font.draw(ms, lineinfo.asComponent, lineinfo.x, lineinfo.y, -16777216);
		}

		//this.renderHighlight(displaycache.selection); // Weird things...
		this.renderCursor(ms, displaycache.cursor, displaycache.cursorAtEnd);

		super.render(ms, pMouseX, pMouseY, pPartialTick);
	}

	private void renderCursor(PoseStack pPoseStack, Pos2i pCursorPos, boolean pIsEndOfText) {
		if (this.frameTick / 6 % 2 == 0) {
			pCursorPos = this.convertLocalToScreen(pCursorPos);
			if (!pIsEndOfText) {
				GuiComponent.fill(pPoseStack, pCursorPos.x, pCursorPos.y - 1, pCursorPos.x + 1, pCursorPos.y + 9,
						-16777216);
			} else {
				this.font.draw(pPoseStack, "_", pCursorPos.x, pCursorPos.y, 0);
			}
		}

	}

	@Override
	public void tick() {
		super.tick();
		++this.frameTick;
	}

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		if (super.mouseClicked(pMouseX, pMouseY, pButton)) {
			return true;
		} else {
			if (pButton == 0) {
				long i = Util.getMillis();
				DisplayCache displaycache = this.getDisplayCache();
				int j = displaycache.getIndexAtPosition(this.font,
						this.convertScreenToLocal(new Pos2i((int) pMouseX, (int) pMouseY)));
				if (j >= 0) {
					if (j == this.lastIndex && i - this.lastClickTime < 250L) {
						if (!this.codeEdit.isSelecting()) {
							this.selectWord(j);
						} else {
							this.codeEdit.selectAll();
						}
					} else {
						this.codeEdit.setCursorPos(j, Screen.hasShiftDown());
					}

					this.clearDisplayCache();
				}

				this.lastIndex = j;
				this.lastClickTime = i;
			}

			return true;
		}
	}

	private void selectWord(int pIndex) {
		String s = this.getCode();
		this.codeEdit.setSelectionRange(StringSplitter.getWordPosition(s, -1, pIndex, false),
				StringSplitter.getWordPosition(s, 1, pIndex, false));
	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		if (super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
			return true;
		} else {
			if (pButton == 0) {
				DisplayCache displaycache = this.getDisplayCache();
				int i = displaycache.getIndexAtPosition(this.font,
						this.convertScreenToLocal(new Pos2i((int) pMouseX, (int) pMouseY)));
				this.codeEdit.setCursorPos(i, true);
				this.clearDisplayCache();
			}

			return true;
		}
	}

	private void renderHighlight(Rect2i[] pSelected) {
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

		for (Rect2i rect2i : pSelected) {
			int i = rect2i.getX();
			int j = rect2i.getY();
			int k = i + rect2i.getWidth();
			int l = j + rect2i.getHeight();
			bufferbuilder.vertex(i, l, 0.0D).endVertex();
			bufferbuilder.vertex(k, l, 0.0D).endVertex();
			bufferbuilder.vertex(k, j, 0.0D).endVertex();
			bufferbuilder.vertex(i, j, 0.0D).endVertex();
		}

		tesselator.end();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}

	@Override
	public boolean charTyped(char pCodePoint, int pModifiers) {
		if (SharedConstants.isAllowedChatCharacter(pCodePoint)) {
			this.codeEdit.insertText(Character.toString(pCodePoint));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (Screen.isSelectAll(pKeyCode)) {
			this.codeEdit.selectAll();
			return true;
		} else if (Screen.isCopy(pKeyCode)) {
			this.codeEdit.copy();
			return true;
		} else if (Screen.isPaste(pKeyCode)) {
			this.codeEdit.paste();
			return true;
		} else if (Screen.isCut(pKeyCode)) {
			this.codeEdit.cut();
			return true;
		} else {
			switch (pKeyCode) {
				case InputConstants.KEY_RETURN:
				case InputConstants.KEY_NUMPADENTER:
					this.codeEdit.insertText("\n");
					return true;
				case InputConstants.KEY_BACKSPACE:
					this.codeEdit.removeCharsFromCursor(-1);
					return true;
				case InputConstants.KEY_DELETE:
					this.codeEdit.removeCharsFromCursor(1);
					return true;
				case InputConstants.KEY_RIGHT:
					this.codeEdit.moveByChars(1, Screen.hasShiftDown());
					return true;
				case InputConstants.KEY_LEFT:
					this.codeEdit.moveByChars(-1, Screen.hasShiftDown());
					return true;
				case InputConstants.KEY_DOWN:
					this.keyDown();
					return true;
				case InputConstants.KEY_UP:
					this.keyUp();
					return true;
				case InputConstants.KEY_HOME:
					this.keyHome();
					return true;
				case InputConstants.KEY_END:
					this.keyEnd();
					return true;
				default:
					return false;
			}
		}
	}

	private void keyUp() {
		this.changeLine(-1);
	}

	private void keyDown() {
		this.changeLine(1);
	}

	private void changeLine(int pYChange) {
		int i = this.codeEdit.getCursorPos();
		int j = this.getDisplayCache().changeLine(i, pYChange);
		this.codeEdit.setCursorPos(j, Screen.hasShiftDown());
	}

	private void keyHome() {
		int i = this.codeEdit.getCursorPos();
		int j = this.getDisplayCache().findLineStart(i);
		this.codeEdit.setCursorPos(j, Screen.hasShiftDown());
	}

	private void keyEnd() {
		DisplayCache CodeDroneScreen$displaycache = this.getDisplayCache();
		int i = this.codeEdit.getCursorPos();
		int j = CodeDroneScreen$displaycache.findLineEnd(i);
		this.codeEdit.setCursorPos(j, Screen.hasShiftDown());
	}

	private String getCode() {
		return this.code;
	}

	private void setCode(String code) {
		this.code = code;

	}

	private void setClipboard(String clipboard) {
		if (this.minecraft != null) {
			TextFieldHelper.setClipboardContents(this.minecraft, clipboard);
		}

	}

	private String getClipboard() {
		return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
	}

	static int findLineFromPos(int[] p_98150_, int p_98151_) {
		int i = Arrays.binarySearch(p_98150_, p_98151_);
		return i < 0 ? -(i + 2) : i;
	}

	private DisplayCache getDisplayCache() {
		if (this.displayCache == null) {
			this.displayCache = this.rebuildDisplayCache();
		}

		return this.displayCache;
	}

	private Pos2i convertLocalToScreen(Pos2i pLocalScreenPos) {
		return new Pos2i(pLocalScreenPos.x + (this.width - 192) / 2 + 36, pLocalScreenPos.y + 32);
	}

	private Pos2i convertScreenToLocal(Pos2i pScreenPos) {
		return new Pos2i(pScreenPos.x - (this.width - 192) / 2 - 36, pScreenPos.y - 32);
	}

	private Rect2i createPartialLineSelection(String p_98120_, StringSplitter p_98121_, int p_98122_, int p_98123_,
			int p_98124_, int p_98125_) {
		String s = p_98120_.substring(p_98125_, p_98122_);
		String s1 = p_98120_.substring(p_98125_, p_98123_);
		Pos2i pos2i = new Pos2i((int) p_98121_.stringWidth(s), p_98124_);
		Pos2i pos2i1 = new Pos2i((int) p_98121_.stringWidth(s1), p_98124_ + 9);
		return this.createSelection(pos2i, pos2i1);
	}

	private Rect2i createSelection(Pos2i p_98117_, Pos2i p_98118_) {
		Pos2i pos2i = this.convertLocalToScreen(p_98117_);
		Pos2i pos2i1 = this.convertLocalToScreen(p_98118_);
		int i = Math.min(pos2i.x, pos2i1.x);
		int j = Math.max(pos2i.x, pos2i1.x);
		int k = Math.min(pos2i.y, pos2i1.y);
		int l = Math.max(pos2i.y, pos2i1.y);
		return new Rect2i(i, k, j - i, l - k);
	}

	private DisplayCache rebuildDisplayCache() {
		String s = this.getCode();
		if (s.isEmpty()) {
			return DisplayCache.EMPTY;
		} else {
			int i = this.codeEdit.getCursorPos();
			int j = this.codeEdit.getSelectionPos();
			IntList intlist = new IntArrayList();
			List<CodeEditorScreen.LineInfo> list = Lists.newArrayList();
			MutableInt mutableint = new MutableInt();
			MutableBoolean mutableboolean = new MutableBoolean();
			StringSplitter stringsplitter = this.font.getSplitter();
			stringsplitter.splitLines(s, 114, Style.EMPTY, true, (p_98132_, p_98133_, p_98134_) -> {
				int k3 = mutableint.getAndIncrement();
				String s2 = s.substring(p_98133_, p_98134_);
				mutableboolean.setValue(s2.endsWith("\n"));
				String s3 = StringUtils.stripEnd(s2, " \n");
				int l3 = k3 * 9;
				CodeEditorScreen.Pos2i CodeDroneScreen$pos2i1 = this.convertLocalToScreen(
						new CodeEditorScreen.Pos2i(0, l3));
				intlist.add(p_98133_);
				list.add(new CodeEditorScreen.LineInfo(p_98132_, s3, CodeDroneScreen$pos2i1.x,
						CodeDroneScreen$pos2i1.y));
			});
			int[] aint = intlist.toIntArray();
			boolean flag = i == s.length();
			CodeEditorScreen.Pos2i CodeDroneScreen$pos2i;
			if (flag && mutableboolean.isTrue()) {
				CodeDroneScreen$pos2i = new CodeEditorScreen.Pos2i(0, list.size() * 9);
			} else {
				int k = findLineFromPos(aint, i);
				int l = this.font.width(s.substring(aint[k], i));
				CodeDroneScreen$pos2i = new CodeEditorScreen.Pos2i(l, k * 9);
			}

			List<Rect2i> list1 = Lists.newArrayList();
			if (i != j) {
				int l2 = Math.min(i, j);
				int i1 = Math.max(i, j);
				int j1 = findLineFromPos(aint, l2);
				int k1 = findLineFromPos(aint, i1);
				if (j1 == k1) {
					int l1 = j1 * 9;
					int i2 = aint[j1];
					list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i1, l1, i2));
				} else {
					int i3 = j1 + 1 > aint.length ? s.length() : aint[j1 + 1];
					list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i3, j1 * 9, aint[j1]));

					for (int j3 = j1 + 1; j3 < k1; ++j3) {
						int j2 = j3 * 9;
						String s1 = s.substring(aint[j3], aint[j3 + 1]);
						int k2 = (int) stringsplitter.stringWidth(s1);
						list1.add(this.createSelection(new CodeEditorScreen.Pos2i(0, j2),
								new CodeEditorScreen.Pos2i(k2, j2 + 9)));
					}

					list1.add(this.createPartialLineSelection(s, stringsplitter, aint[k1], i1, k1 * 9, aint[k1]));
				}
			}

			return new DisplayCache(s, CodeDroneScreen$pos2i, flag, aint,
					list.toArray(new CodeEditorScreen.LineInfo[0]), list1.toArray(new Rect2i[0]));
		}
	}

	private void clearDisplayCache() {
		this.displayCache = null;
	}

	@OnlyIn(Dist.CLIENT)
	static class DisplayCache {
		static final DisplayCache EMPTY = new DisplayCache("", new CodeEditorScreen.Pos2i(0, 0), true, new int[] {0},
				new CodeEditorScreen.LineInfo[] {new CodeEditorScreen.LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
		private final String fullText;
		final CodeEditorScreen.Pos2i cursor;
		final boolean cursorAtEnd;
		private final int[] lineStarts;
		final CodeEditorScreen.LineInfo[] lines;
		final Rect2i[] selection;

		public DisplayCache(String pFullText, CodeEditorScreen.Pos2i pCursor, boolean pCursorAtEnd, int[] pLineStarts,
				CodeEditorScreen.LineInfo[] pLines, Rect2i[] pSelection) {
			this.fullText = pFullText;
			this.cursor = pCursor;
			this.cursorAtEnd = pCursorAtEnd;
			this.lineStarts = pLineStarts;
			this.lines = pLines;
			this.selection = pSelection;
		}

		public int getIndexAtPosition(Font pFont, CodeEditorScreen.Pos2i pCursorPosition) {
			int i = pCursorPosition.y / 9;
			if (i < 0) {
				return 0;
			} else if (i >= this.lines.length) {
				return this.fullText.length();
			} else {
				CodeEditorScreen.LineInfo CodeDroneScreen$lineinfo = this.lines[i];
				return this.lineStarts[i] + pFont.getSplitter()
						.plainIndexAtWidth(CodeDroneScreen$lineinfo.contents, pCursorPosition.x,
								CodeDroneScreen$lineinfo.style);
			}
		}

		public int changeLine(int p_98211_, int p_98212_) {
			int i = CodeEditorScreen.findLineFromPos(this.lineStarts, p_98211_);
			int j = i + p_98212_;
			int k;
			if (0 <= j && j < this.lineStarts.length) {
				int l = p_98211_ - this.lineStarts[i];
				int i1 = this.lines[j].contents.length();
				k = this.lineStarts[j] + Math.min(l, i1);
			} else {
				k = p_98211_;
			}

			return k;
		}

		public int findLineStart(int p_98209_) {
			int i = CodeEditorScreen.findLineFromPos(this.lineStarts, p_98209_);
			return this.lineStarts[i];
		}

		public int findLineEnd(int p_98219_) {
			int i = CodeEditorScreen.findLineFromPos(this.lineStarts, p_98219_);
			return this.lineStarts[i] + this.lines[i].contents.length();
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class LineInfo {
		final Style style;
		final String contents;
		final Component asComponent;
		final int x;
		final int y;

		public LineInfo(Style pStyle, String pContents, int pX, int pY) {
			this.style = pStyle;
			this.contents = pContents;
			this.x = pX;
			this.y = pY;
			this.asComponent = (Component.literal(pContents)).setStyle(pStyle);
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class Pos2i {
		public final int x;
		public final int y;

		Pos2i(int pX, int pY) {
			this.x = pX;
			this.y = pY;
		}
	}

}