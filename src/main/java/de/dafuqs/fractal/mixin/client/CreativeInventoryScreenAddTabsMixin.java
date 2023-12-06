package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.fabricmc.api.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenAddTabsMixin extends AbstractInventoryScreen<CreativeScreenHandler> implements SubTabLocation, CreativeInventoryScreenAccessor {
	
	@Unique
	private static Identifier TINYFONT_TEXTURE = new Identifier("fractal", "textures/gui/tinyfont.png");
	
	public CreativeInventoryScreenAddTabsMixin(CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}
	
	@Shadow
	private float scrollPosition;
	
	@Shadow
	private static ItemGroup selectedTab;
	
	@Unique
	private int fractal$x, fractal$y, fractal$w, fractal$h;
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTabTooltipIfHovered(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/item/ItemGroup;II)Z"))
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
			int x = this.x - ofs;
			int y = this.y + 6;
			int tw = 57;
			fractal$x = x - tw;
			fractal$y = y;
			for (ItemSubGroup child : parent.fractal$getChildren()) {
				context.setShaderColor(1, 1, 1, 1);
				
				boolean thisChildSelected = child == parent.fractal$getSelectedChild();
				@Nullable ItemSubGroup.Style style = child.getStyle();
				@Nullable Identifier subtabTextureID = style == null
						? (thisChildSelected ? ItemSubGroup.SUBTAB_SELECTED_TEXTURE : ItemSubGroup.SUBTAB_UNSELECTED_TEXTURE)
						: (thisChildSelected ? style.selectedSubtabTexture() : style.unselectedSubtabTexture());
				
				context.drawGuiTexture(subtabTextureID, x - tw, y, 70, 11);
				
				String str = child.getDisplayName().getString();
				for (int i = str.length() - 1; i >= 0; i--) {
					char c = str.charAt(i);
					if (c > 0x7F) continue;
					int u = (c % 16) * 4;
					int v = (c / 16) * 6;
					context.setShaderColor(0, 0, 0, 1);
					context.drawTexture(TINYFONT_TEXTURE, x, y + 3, u, v, 4, 6, 64, 48);
					x -= 4;
				}
				x = this.x - ofs;
				y += 10;
			}
			fractal$w = tw + ofs;
			fractal$h = y - fractal$y;
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
