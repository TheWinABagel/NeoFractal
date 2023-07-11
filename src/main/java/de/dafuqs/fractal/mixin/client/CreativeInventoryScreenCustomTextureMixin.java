package de.dafuqs.fractal.mixin.client;

import com.mojang.blaze3d.systems.*;
import de.dafuqs.fractal.quack.*;
import de.dafuqs.fractal.api.*;
import net.fabricmc.api.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.util.math.*;
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
	@Final
	private static Identifier TEXTURE;
	
	@Shadow
	private static ItemGroup selectedTab;
	
	@Shadow protected abstract boolean hasScrollbar();
	
	@Unique
	private ItemSubGroup fractal$renderedItemGroup;
	
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 1))
	private Identifier injectCustomGroupTexture(Identifier original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null || subGroup.getBackgroundTexture() == null) return original;
		return subGroup.getBackgroundTexture();
	}
	
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 2))
	private Identifier injectCustomScrollbarTexture(Identifier original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null) return original;
		this.fractal$renderedItemGroup = subGroup;
		
		return subGroup.getBackgroundTexture() == null ? original : subGroup.getBackgroundTexture();
	}
	
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1), index = 3)
	private int injectCustomScrollbarTextureU(int original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null || subGroup.getBackgroundTexture() == null) return original;
		return this.hasScrollbar() ? 0 : 12;
	}
	
	@ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1), index = 4)
	private int injectCustomScrollbarTextureV(int original) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null || subGroup.getBackgroundTexture() == null) return original;
		return 136;
	}
	
	@Inject(method = "drawBackground", at = @At("RETURN"))
	private void releaseGroupInstance(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		fractal$renderedItemGroup = null;
	}
	
	@Inject(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;getIcon()Lnet/minecraft/item/ItemStack;"))
	private void injectCustomTabTexture(MatrixStack matrices, ItemGroup group, CallbackInfo ci) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null) return;
		
		fractal$renderedItemGroup = subGroup;
		
		if (subGroup.getBackgroundTexture() != null) {
			RenderSystem.setShaderTexture(0, subGroup.getBackgroundTexture());
		}
	}
	
	@ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"), index = 3)
	private int injectCustomTabTextureLocation(int original) {
		if (fractal$renderedItemGroup == null || fractal$renderedItemGroup.getBackgroundTexture() == null)
			return original;
		return selectedTab.getColumn() == 0 ? 195 : 223;
	}
	
	@Inject(method = "renderTabIcon", at = @At("RETURN"))
	private void restoreTabTexture(MatrixStack matrices, ItemGroup group, CallbackInfo ci) {
		ItemSubGroup subGroup = getSelectedSubGroup();
		if (subGroup == null) return;
		
		fractal$renderedItemGroup = null;
		RenderSystem.setShaderTexture(0, TEXTURE);
	}
	
	@Unique
	private @Nullable ItemSubGroup getSelectedSubGroup() {
		return selectedTab instanceof ItemGroupParent itemGroupParent ? itemGroupParent.fractal$getSelectedChild() : null;
	}
	
}