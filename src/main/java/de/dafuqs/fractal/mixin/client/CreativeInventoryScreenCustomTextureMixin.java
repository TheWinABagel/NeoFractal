package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenCustomTextureMixin {
	
	@Shadow
	private static CreativeModeTab selectedTab;
	
	@Shadow protected abstract boolean canScroll();

	@Unique
	private ItemSubGroup fractal$renderedItemSubGroup;
	
	@Unique
	private CreativeModeTab fractal$renderedItemGroup;
	
	// BACKGROUND
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0))
	private ResourceLocation injectCustomGroupTexture(ResourceLocation original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		return (subGroup == null || subGroup.getStyle() == null || subGroup.getStyle().backgroundTexture() == null) ? original : subGroup.getStyle().backgroundTexture();
	}
	
	// SCROLLBAR
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
	private ResourceLocation injectCustomScrollbarTexturea(ResourceLocation old) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if(subGroup != null && subGroup.getStyle() != null) {
			ResourceLocation scrollbarTextureID = this.canScroll() ? subGroup.getStyle().enabledScrollbarTexture() : subGroup.getStyle().disabledScrollbarTexture();
			if(scrollbarTextureID != null) {
				return scrollbarTextureID;
			}
		}
		return old;
	}
//	@ModifyArgs(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
//	private void injectCustomScrollbarTexture(Args args) {
//		ItemSubGroup subGroup = getSelectedSubGroup();
//		if(subGroup != null && subGroup.getStyle() != null) {
//			ResourceLocation scrollbarTextureID = this.canScroll() ? subGroup.getStyle().enabledScrollbarTexture() : subGroup.getStyle().disabledScrollbarTexture();
//			if(scrollbarTextureID != null) {
//				args.set(0, scrollbarTextureID);
//			}
//		}
//	}
	
	// ICON
	@Inject(method = "renderTabButton", at = @At("HEAD"))
	private void captureContextGroup(GuiGraphics context, CreativeModeTab group, CallbackInfo ci) {
		this.fractal$renderedItemGroup = group;
	}
	
	@ModifyArg(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
	private ResourceLocation injectCustomTabTexture(ResourceLocation original) {
		ItemSubGroup subGroup = getRenderedSubGroup();
		if(subGroup == null) {
			return original;
		}
		ItemSubGroup.Style style = subGroup.getStyle();
		if(style == null) {
			return original;
		}
		
		boolean onTop = this.fractal$renderedItemGroup.row() == CreativeModeTab.Row.TOP;
		boolean isSelected = selectedTab == this.fractal$renderedItemGroup;
		
		ResourceLocation texture = onTop
				? isSelected ? this.fractal$renderedItemGroup.column() == 0 ? style.tabTopFirstSelectedTexture() : style.tabTopSelectedTexture() : style.tabTopUnselectedTexture()
				: isSelected ? this.fractal$renderedItemGroup.column() == 0 ? style.tabBottomFirstSelectedTexture() : style.tabBottomSelectedTexture() : style.tabBottomUnselectedTexture();
		
		return texture == null ? original : texture;
	}
	
	@Unique
	private @Nullable ItemSubGroup getRenderedSubGroup() {
		return fractal$renderedItemGroup instanceof ItemGroupParent itemGroupParent ? itemGroupParent.fractal$getSelectedChild() : null;
	}
	
	@Unique
	private @Nullable ItemSubGroup getSelectedSubGroup() {
		return selectedTab instanceof ItemGroupParent itemGroupParent ? itemGroupParent.fractal$getSelectedChild() : null;
	}
	
}