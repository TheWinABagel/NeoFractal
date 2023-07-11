package de.dafuqs.fractal.mixin.client;

import com.mojang.blaze3d.systems.*;
import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.quack.*;
import net.fabricmc.api.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenAddTabsMixin extends AbstractInventoryScreen<CreativeScreenHandler> implements SubTabLocation {
	
	public CreativeInventoryScreenAddTabsMixin(CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}
	
	@Shadow
	private float scrollPosition;
	
	@Shadow
	public abstract int getSelectedTab();
	
	@Unique
	private int fractal$x, fractal$y, fractal$w, fractal$h;
	
	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/ingame/CreativeInventoryScreen.drawMouseoverTooltip(Lnet/minecraft/client/util/math/MatrixStack;II)V"), method = "render")
	public void fractal$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		ItemGroup selected = ItemGroup.GROUPS[this.getSelectedTab()];
		if (selected instanceof ItemGroupParent parent && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
			if (!selected.shouldRenderName()) {
				ItemGroup child = parent.fractal$getSelectedChild();
				float x = textRenderer.draw(matrices, selected.getName(), this.x + 8, this.y + 6, 4210752);
				if (child != null) {
					x = textRenderer.draw(matrices, " ", x, this.y + 6, 4210752);
					x = textRenderer.draw(matrices, child.getName(), x, this.y + 6, 4210752);
				}
			}
			int ofs = 5;
			int x = this.x - ofs;
			int y = this.y + 6;
			int tw = 56;
			fractal$x = x - tw;
			fractal$y = y;
			for (ItemSubGroup child : parent.fractal$getChildren()) {
				RenderSystem.setShaderColor(1, 1, 1, 1);
				
				boolean thisChildSelected = child == parent.fractal$getSelectedChild();
				if(child.getBackgroundTexture() == null) {
					RenderSystem.setShaderTexture(0, new Identifier("fractal", "textures/subtab.png"));
					int bgV = thisChildSelected ? 11 : 0;
					drawTexture(matrices, x - tw, y, 0, bgV, tw + ofs, 11, 70, 22);
					drawTexture(matrices, this.x, y, 64, bgV, 6, 11, 70, 22);
				} else {
					RenderSystem.setShaderTexture(0, child.getBackgroundTexture());
					int bgV = thisChildSelected ? 136 + 11 : 136;
					drawTexture(matrices, x - tw, y, 24, bgV, tw + ofs, 11, 256, 256);
					drawTexture(matrices, this.x, y, 24 + 64, bgV, 6, 11, 256, 256);
				}
				
				RenderSystem.setShaderTexture(0, new Identifier("fractal", "textures/tinyfont.png"));
				String str = child.getName();
				for (int i = str.length() - 1; i >= 0; i--) {
					char c = str.charAt(i);
					if (c > 0x7F) continue;
					int u = (c % 16) * 4;
					int v = (c / 16) * 6;
					RenderSystem.setShaderColor(0, 0, 0, 1);
					drawTexture(matrices, x, y + 3, u, v, 4, 6, 64, 48);
					x -= 4;
				}
				x = this.x - ofs;
				y += 10;
			}
			fractal$w = tw + ofs;
			fractal$h = y - fractal$y;
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
	public void fractal$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
		ItemGroup selected = ItemGroup.GROUPS[this.getSelectedTab()];
		if (selected instanceof ItemGroupParent parent && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
			int x = fractal$x;
			int y = fractal$y;
			int w = fractal$w;
			for (ItemSubGroup child : parent.fractal$getChildren()) {
				if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 11) {
					parent.fractal$setSelectedChild(child);
					handler.itemList.clear();
					selected.appendStacks(handler.itemList);
					this.scrollPosition = 0.0F;
					handler.scrollItems(0.0F);
					ci.setReturnValue(true);
					return;
				}
				y += 10;
			}
		}
	}
	
	@Override
	public int fractal$getX() {
		return fractal$x;
	}
	
	@Override
	public int fractal$getY() {
		return fractal$y;
	}
	
	@Override
	public int fractal$getW() {
		return fractal$w;
	}
	
	@Override
	public int fractal$getH() {
		return fractal$h;
	}
	
}
