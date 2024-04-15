package de.dafuqs.fractal.mixin.client;

import de.dafuqs.fractal.api.ItemSubGroup;
import de.dafuqs.fractal.interfaces.ItemGroupParent;
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
	
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
	private ResourceLocation injectCustomGroupTexture(ResourceLocation original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null || subGroup.getBackgroundTexture() == null) return original;
		return subGroup.getBackgroundTexture();
	}
	
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
	private ResourceLocation injectCustomScrollbarTexture(ResourceLocation original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null) return original;
		this.fractal$renderedItemSubGroup = subGroup;
		
		return subGroup.getBackgroundTexture() == null ? original : subGroup.getBackgroundTexture();
	}
	
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), index = 3)
	private int injectCustomScrollbarTextureU(int original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null || subGroup.getBackgroundTexture() == null) return original;
		return this.canScroll() ? 0 : 12;
	}
	
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), index = 4)
	private int injectCustomScrollbarTextureV(int original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null || subGroup.getBackgroundTexture() == null) return original;
		return 136;
	}
	
	@Inject(method = "renderBg", at = @At("RETURN"))
	private void releaseGroupInstance(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		fractal$renderedItemSubGroup = null;
	}
	
	@Inject(method = "renderTabButton", at = @At(value = "HEAD"))
	private void injectCustomTabTexture(GuiGraphics context, CreativeModeTab group, CallbackInfo ci) {
		fractal$renderedItemGroup = group;
		fractal$renderedItemSubGroup = getRenderedSubGroup();
	}
	
	@ModifyArg(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
	private ResourceLocation injectCustomTabTexture(ResourceLocation texture) {
		ItemSubGroup subGroup = fractal$renderedItemGroup instanceof ItemGroupParent itemGroupParent ? itemGroupParent.fractal$getSelectedChild() : null;
		return subGroup == null || subGroup.getBackgroundTexture() == null ? texture : subGroup.getBackgroundTexture();
	}
	
	@ModifyArg(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"), index = 3)
	private int injectCustomTabTextureLocation(int original) {
		if (fractal$renderedItemSubGroup == null || fractal$renderedItemSubGroup.getBackgroundTexture() == null)
			return original;
		return selectedTab.column() == 0 ? 195 : 223;
	}
	
	@Inject(method = "renderTabButton", at = @At("RETURN"))
	private void restoreTabTexture(GuiGraphics context, CreativeModeTab group, CallbackInfo ci) {
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