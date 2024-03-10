package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.interfaces.*;
import de.dafuqs.fractal.api.*;
import net.fabricmc.api.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenAddTabsMixin extends AbstractInventoryScreen<CreativeScreenHandler> implements SubTabLocation, CreativeInventoryScreenAccessor {
	
	@Unique
	private static final Identifier SUBTAB_TEXTURE = new Identifier("fractal", "textures/subtab.png");
	@Unique
	private static final Identifier TINYFONT_TEXTURE = new Identifier("fractal", "textures/tinyfont.png");
	
	public CreativeInventoryScreenAddTabsMixin(CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}
	
	@Shadow
	private float scrollPosition;
	
	@Shadow
	private static ItemGroup selectedTab;
	
	@Unique
	private int fractal$x, fractal$y, fractal$w, fractal$h;
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V"))
	public void fractal$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (selectedTab instanceof ItemGroupParent parent && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
			if (!selectedTab.shouldRenderName()) {
				ItemGroup child = parent.fractal$getSelectedChild();
				int x = context.drawText(textRenderer, selectedTab.getDisplayName(), this.x + 8, this.y + 6, 4210752, false);
				if (child != null) {
					x = context.drawText(textRenderer, " ", x, this.y + 6, 4210752, false);
					x = context.drawText(textRenderer, child.getDisplayName(), x, this.y + 6, 4210752, false);
				}
			}
			int ofs = 5;

			int[] pos = {this.x - ofs, this.y + 6};
			int tw = 56;
			fractal$x = pos[0] - tw;
			fractal$y = pos[1];
			for (ItemSubGroup child : parent.fractal$getChildren()) {
				context.setShaderColor(1, 1, 1, 1);
				
				boolean thisChildSelected = child == parent.fractal$getSelectedChild();
				if (child.getBackgroundTexture() == null) {
					int bgV = thisChildSelected ? 11 : 0;
					
					context.drawTexture(SUBTAB_TEXTURE, pos[0] - tw, pos[1], 0, bgV, tw + ofs, 11, 70, 22);
					context.drawTexture(SUBTAB_TEXTURE, this.x, pos[1], 64, bgV, 6, 11, 70, 22);
				} else {
					Identifier backgroundTextureID = child.getBackgroundTexture();
					int bgV = thisChildSelected ? 136 + 11 : 136;
					context.drawTexture(backgroundTextureID,  pos[0] - tw, pos[1], 24, bgV, tw + ofs, 11, 256, 256);
					context.drawTexture(backgroundTextureID, this.x, pos[1], 24 + 64, bgV, 6, 11, 256, 256);
				}
				
				String str = child.getDisplayName().getString();
				context.draw(() -> {
					for (int i = str.length() - 1; i >= 0; i--) {
						char c = str.charAt(i);
						if (c > 0x7F) continue;
						int u = (c % 16) * 4;
						int v = (c / 16) * 6;
						context.setShaderColor(0, 0, 0, 1);
						context.drawTexture(TINYFONT_TEXTURE, pos[0], pos[1] + 3, u, v, 4, 6, 64, 48);
						pos[0] -= 4;
					}
				});
				pos[0] = this.x - ofs;
				pos[1] += 10;
			}
			fractal$w = tw + ofs;
			fractal$h = pos[1] - fractal$y;
			context.setShaderColor(1, 1, 1, 1);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
	public void fractal$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
		ItemGroup selected = selectedTab;
		if (selected instanceof ItemGroupParent parent && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
			int x = fractal$x;
			int y = fractal$y;
			int w = fractal$w;
			for (ItemSubGroup child : parent.fractal$getChildren()) {
				if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 11) {
					parent.fractal$setSelectedChild(child);
					
					handler.itemList.clear();
					handler.itemList.addAll(selected.getDisplayStacks());
					
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
