package de.dafuqs.fractal.mixin;

import com.google.common.collect.*;
import de.dafuqs.fractal.interfaces.*;
import de.dafuqs.fractal.api.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(ItemGroup.class)
public class MixinItemGroup implements ItemGroupParent {
	
	private final List<ItemSubGroup> fractal$children = Lists.newArrayList();
	private ItemSubGroup fractal$selectedChild = null;
	
	@Inject(at = @At("HEAD"), method = "getDisplayStacks", cancellable = true)
	public void getDisplayStacks(CallbackInfoReturnable<Collection<ItemStack>> cir) {
		if (fractal$selectedChild != null) {
			cir.setReturnValue(fractal$selectedChild.getDisplayStacks());
		}
	}
	
	@Override
	public List<ItemSubGroup> fractal$getChildren() {
		return fractal$children;
	}
	
	@Override
	public ItemSubGroup fractal$getSelectedChild() {
		return fractal$selectedChild;
	}
	
	@Override
	public void fractal$setSelectedChild(ItemSubGroup group) {
		fractal$selectedChild = group;
	}
	
}
