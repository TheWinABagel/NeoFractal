package de.dafuqs.fractal.api;

import net.minecraft.item.*;
import net.minecraft.resource.featuretoggle.*;

import java.util.*;

public class DefaultStackEntryCollector implements ItemGroup.Entries {
	
	public final Collection<ItemStack> parentTabStacks = ItemStackSet.create();
	public final Set<ItemStack> searchTabStacks = ItemStackSet.create();
	private final ItemGroup group;
	private final FeatureSet enabledFeatures;
	
	public DefaultStackEntryCollector(ItemGroup group, FeatureSet enabledFeatures) {
		this.group = group;
		this.enabledFeatures = enabledFeatures;
	}
	
	@Override
	public void add(ItemConvertible item, ItemGroup.StackVisibility visibility) {
		this.add(item.asItem().getDefaultStack(), visibility);
	}
	
	@Override
	public void add(ItemStack stack, ItemGroup.StackVisibility visibility) {
		if (stack.getCount() != 1) {
			throw new IllegalArgumentException("Stack size must be exactly 1");
		} else {
			if (this.parentTabStacks.contains(stack) && visibility != ItemGroup.StackVisibility.SEARCH_TAB_ONLY) {
				throw new IllegalStateException("Accidentally adding the same item stack twice " + stack.toHoverableText().getString() + " to a Creative Mode Tab: " + this.group.getDisplayName().getString());
			} else {
				if (stack.getItem().isEnabled(this.enabledFeatures)) {
					switch (visibility) {
						case PARENT_AND_SEARCH_TABS -> {
							this.parentTabStacks.add(stack);
							this.searchTabStacks.add(stack);
						}
						case PARENT_TAB_ONLY -> this.parentTabStacks.add(stack);
						case SEARCH_TAB_ONLY -> this.searchTabStacks.add(stack);
					}
				}
			}
		}
	}
	
}