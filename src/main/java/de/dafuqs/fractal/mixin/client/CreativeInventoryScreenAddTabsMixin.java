package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenAddTabsMixin extends EffectRenderingInventoryScreen<ItemPickerMenu> implements SubTabLocation, CreativeInventoryScreenAccessor {
	
	@Unique
	private static ResourceLocation TINYFONT_TEXTURE = new ResourceLocation("fractal", "textures/gui/tinyfont.png");
	
	public CreativeInventoryScreenAddTabsMixin(ItemPickerMenu screenHandler, Inventory playerInventory, Component text) {
		super(screenHandler, playerInventory, text);
	}
	
	@Shadow
	private float scrollOffs;
	
	@Shadow
	private static CreativeModeTab selectedTab;
	
	@Unique
	private int fractal$x, fractal$y, fractal$w, fractal$h;
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;checkTabHovering(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/CreativeModeTab;II)Z"))
	public void fractal$render(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (selectedTab instanceof ItemGroupParent parent && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
			if (!selectedTab.showTitle()) {
				CreativeModeTab child = parent.fractal$getSelectedChild();
				int x = context.drawString(font, selectedTab.getDisplayName(), this.leftPos + 8, this.topPos + 6, 4210752, false);
				if (child != null) {
					x = context.drawString(font, " ", x, this.topPos + 6, 4210752, false);
					x = context.drawString(font, child.getDisplayName(), x, this.topPos + 6, 4210752, false);
				}
			}
			int ofs = 5;
			int x = this.leftPos - ofs;
			int y = this.topPos + 6;
			int tw = 57;
			fractal$x = x - tw;
			fractal$y = y;
			for (ItemSubGroup child : parent.fractal$getChildren()) {
				context.setColor(1, 1, 1, 1);
				
				boolean thisChildSelected = child == parent.fractal$getSelectedChild();
				@Nullable ItemSubGroup.Style style = child.getStyle();
				@Nullable ResourceLocation subtabTextureID = style == null
						? (thisChildSelected ? ItemSubGroup.SUBTAB_SELECTED_TEXTURE : ItemSubGroup.SUBTAB_UNSELECTED_TEXTURE)
						: (thisChildSelected ? style.selectedSubtabTexture() : style.unselectedSubtabTexture());
				
				context.blitSprite(subtabTextureID, x - tw, y, 70, 11);
				
				String str = child.getDisplayName().getString();
				for (int i = str.length() - 1; i >= 0; i--) {
					char c = str.charAt(i);
					if (c > 0x7F) continue;
					int u = (c % 16) * 4;
					int v = (c / 16) * 6;
					context.setColor(0, 0, 0, 1);
					context.blit(TINYFONT_TEXTURE, x, y + 3, u, v, 4, 6, 64, 48);
					x -= 4;
				}
				x = this.leftPos - ofs;
				y += 10;
			}
			fractal$w = tw + ofs;
			fractal$h = y - fractal$y;
			context.setColor(1, 1, 1, 1);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
	public void fractal$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
		CreativeModeTab selected = selectedTab;
		if (selected instanceof ItemGroupParent parent && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
			int x = fractal$x;
			int y = fractal$y;
			int w = fractal$w;
			for (ItemSubGroup child : parent.fractal$getChildren()) {
				if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 11) {
					parent.fractal$setSelectedChild(child);
					
					menu.items.clear();
					menu.items.addAll(selected.getDisplayItems());
					
					this.scrollOffs = 0.0F;
					menu.scrollTo(0.0F);
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
