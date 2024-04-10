package de.dafuqs.fractal.mixin.client;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@OnlyIn(Dist.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor {
	
	@Accessor("selectedTab")
	static CreativeModeTab fractal$getSelectedTab() {
		throw new AssertionError();
	}
	
}