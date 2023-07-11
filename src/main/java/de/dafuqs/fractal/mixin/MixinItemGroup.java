package de.dafuqs.fractal.mixin;

import com.google.common.collect.*;
import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.quack.*;
import net.minecraft.item.*;
import net.minecraft.util.collection.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(ItemGroup.class)
public class MixinItemGroup implements ItemGroupParent {
	
	private final List<ItemSubGroup> fractal$children = Lists.newArrayList();
	private ItemSubGroup fractal$selectedChild = null;
	
	@Inject(at = @At("HEAD"), method = "appendStacks", cancellable = true)
	public void fractal$appendStacksHead(DefaultedList<ItemStack> stacks, CallbackInfo ci) {
		if (fractal$selectedChild != null) {
			fractal$selectedChild.appendStacks(stacks);
			ci.cancel();
		}
	}
	
	@Inject(at = @At("TAIL"), method = "appendStacks", cancellable = true)
	public void appendStacksTail(DefaultedList<ItemStack> stacks, CallbackInfo ci) {
		if (fractal$children != null) {
			for (ItemSubGroup child : fractal$children) {
				child.appendStacks(stacks);
			}
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
	
	// from Polymer
	private static ItemGroup[] FRACTAL$GROUPS_OLD;
	
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;GROUPS:[Lnet/minecraft/item/ItemGroup;", shift = Shift.BEFORE), method = "<init>", require = 1)
	private void fractal$replaceArrayToSkipAdding(int index, String id, CallbackInfo ci) {
		if ((Object) this instanceof ItemSubGroup) {
			FRACTAL$GROUPS_OLD = ItemGroupsAccessor.getGroups();
			ItemGroupsAccessor.setGroups(new ItemGroup[1]);
		}
	}
	
	@Inject(at = @At("TAIL"),
			method = "<init>")
	private void fractal$unreplaceArray(int index, String id, CallbackInfo ci) {
		if ((Object) this instanceof ItemSubGroup) {
			ItemGroupsAccessor.setGroups(FRACTAL$GROUPS_OLD);
		}
	}
	
}
