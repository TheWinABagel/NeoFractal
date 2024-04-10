package de.dafuqs.fractal.compat;

import de.dafuqs.fractal.interfaces.*;
import de.dafuqs.fractal.mixin.client.*;
import dev.emi.emi.api.*;
import dev.emi.emi.api.widget.*;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

public class FractalEMIPlugin implements EmiPlugin {
	
	@Override
	public void register(EmiRegistry registry) {
		registry.addExclusionArea(CreativeModeInventoryScreen.class, (screen, out) -> {
			if (screen != null) {
				CreativeModeTab selected = CreativeInventoryScreenAccessor.fractal$getSelectedTab();
				if (selected instanceof ItemGroupParent parent && screen instanceof SubTabLocation stl && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
					out.accept(new Bounds(stl.fractal$getX(), stl.fractal$getY(), stl.fractal$getW(), stl.fractal$getH()));
				}
			}
		});
	}
	
}
