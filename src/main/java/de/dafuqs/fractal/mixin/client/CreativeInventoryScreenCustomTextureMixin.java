package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.interfaces.*;
import de.dafuqs.fractal.api.*;
import net.fabricmc.api.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenCustomTextureMixin {
	
	@Shadow
	private static ItemGroup selectedTab;
	
	@Shadow protected abstract boolean hasScrollbar();
	
	@Unique
	private ItemSubGroup fractal$renderedItemSubGroup;
	
	@Unique
	private ItemGroup fractal$renderedItemGroup;
	
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"))
	private Identifier injectCustomGroupTexture(Identifier original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null || subGroup.getBackgroundTexture() == null) return original;
		return subGroup.getBackgroundTexture();
	}
	
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"))
	private Identifier injectCustomScrollbarTexture(Identifier original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null) return original;
		this.fractal$renderedItemSubGroup = subGroup;
		
		return subGroup.getBackgroundTexture() == null ? original : subGroup.getBackgroundTexture();
	}
	
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1), index = 3)
	private int injectCustomScrollbarTextureU(int original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null || subGroup.getBackgroundTexture() == null) return original;
		return this.hasScrollbar() ? 0 : 12;
	}
	
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1), index = 4)
	private int injectCustomScrollbarTextureV(int original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null || subGroup.getBackgroundTexture() == null) return original;
		return 136;
	}
	
	@Inject(method = "drawBackground", at = @At("RETURN"))
	private void releaseGroupInstance(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		fractal$renderedItemSubGroup = null;
	}
	
	@Inject(method = "renderTabIcon", at = @At(value = "HEAD"))
	private void injectCustomTabTexture(DrawContext context, ItemGroup group, CallbackInfo ci) {
		fractal$renderedItemGroup = group;
		fractal$renderedItemSubGroup = getRenderedSubGroup();
	}
	
	@ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"))
	private Identifier injectCustomTabTexture(Identifier texture) {
		ItemSubGroup subGroup = fractal$renderedItemGroup instanceof ItemGroupParent itemGroupParent ? itemGroupParent.fractal$getSelectedChild() : null;
		return subGroup == null || subGroup.getBackgroundTexture() == null ? texture : subGroup.getBackgroundTexture();
	}
	
	@ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"), index = 3)
	private int injectCustomTabTextureLocation(int original) {
		if (fractal$renderedItemSubGroup == null || fractal$renderedItemSubGroup.getBackgroundTexture() == null)
			return original;
		return selectedTab.getColumn() == 0 ? 195 : 223;
	}
	
	@Inject(method = "renderTabIcon", at = @At("RETURN"))
	private void restoreTabTexture(DrawContext context, ItemGroup group, CallbackInfo ci) {
		fractal$renderedItemGroup = null;
		fractal$renderedItemSubGroup = null;
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