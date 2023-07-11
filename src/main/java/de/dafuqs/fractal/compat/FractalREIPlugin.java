package de.dafuqs.fractal.compat;

import de.dafuqs.fractal.quack.*;
import de.dafuqs.fractal.mixin.client.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.plugins.*;
import me.shedaniel.rei.api.client.registry.screen.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.item.*;

import java.util.*;

public class FractalREIPlugin implements REIClientPlugin {
	
	@Override
	public void registerExclusionZones(ExclusionZones zones) {
		zones.register(CreativeInventoryScreen.class, (screen) -> {
			ItemGroup selected = ((CreativeInventoryScreenAccessor) screen).fractal$getSelectedTab();
			if (selected instanceof ItemGroupParent parent && screen instanceof SubTabLocation stl && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
				return List.of(new Rectangle(stl.fractal$getX(), stl.fractal$getY(), stl.fractal$getW(), stl.fractal$getH()));
			}
			return List.of();
		});
	}
	
}
