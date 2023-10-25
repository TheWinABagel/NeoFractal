package de.dafuqs.fractal.mixin;

import de.dafuqs.fractal.api.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemGroups.class)
public class MixinItemGroups {
	
	@Inject(at = @At("HEAD"), method = "updateEntries")
	private static void updateEntries(ItemGroup.DisplayContext displayContext, CallbackInfo ci) {
		ItemSubGroup.SUB_GROUPS.forEach((group) -> {
			group.updateEntries(displayContext);
		});
	}
	
}
