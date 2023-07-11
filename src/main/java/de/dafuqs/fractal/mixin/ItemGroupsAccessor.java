package de.dafuqs.fractal.mixin;

import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(ItemGroup.class)
public interface ItemGroupsAccessor {
	
	@Accessor(value = "GROUPS")
	static void setGroups(ItemGroup[] groups) {
		throw new AssertionError();
	}
	
	@Accessor(value = "GROUPS")
	static ItemGroup[] getGroups() {
		throw new AssertionError();
	}
	
}