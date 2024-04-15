package de.dafuqs.fractal.api;

import de.dafuqs.fractal.interfaces.ItemGroupParent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayBuilder;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ItemSubGroup extends CreativeModeTab {
	
	public static final List<ItemSubGroup> SUB_GROUPS = new ArrayList<>();
	
	protected final CreativeModeTab parent;
	protected final ResourceLocation identifier;
	protected final int indexInParent;
	protected final @Nullable ResourceLocation backgroundTexture;
	
	protected ItemSubGroup(CreativeModeTab parent, ResourceLocation identifier, Component displayName, @Nullable ResourceLocation backgroundTexture, DisplayItemsGenerator entryCollector) {
		super(getBuilder(parent, displayName, entryCollector));
		this.identifier = identifier;
		this.backgroundTexture = backgroundTexture;
		this.parent = parent;
		ItemGroupParent igp = (ItemGroupParent) parent;
		this.indexInParent = igp.fractal$getChildren().size();
		igp.fractal$getChildren().add(this);
		if (igp.fractal$getSelectedChild() == null) {
			igp.fractal$setSelectedChild(this);
		}
	}

	private static CreativeModeTab.Builder getBuilder(CreativeModeTab parent, Component displayName, DisplayItemsGenerator entryCollector) {
		return CreativeModeTab.builder(parent.row(), parent.column()).title(displayName).icon(() -> ItemStack.EMPTY).displayItems(entryCollector);
	}
	
	public ResourceLocation getIdentifier() {
		return identifier;
	}
	
	/**
	 * 100 % the vanilla code, but the check for registered item groups was removed
	 * (we do not want to register our subgroups, so other mods do not pick them up)
	 */
	@Override
	public void buildContents(ItemDisplayParameters context) {
		ItemDisplayBuilder entries = new ItemDisplayBuilder(this, context.enabledFeatures());
		this.displayItemsGenerator.accept(context, entries);
		this.displayItems = entries.tabContents;
		this.displayItemsSearchTab = entries.searchTabContents;
		
		triggerEntryUpdateEvent(context);
		
		this.parent.displayItemsSearchTab.addAll(this.displayItemsSearchTab);
		this.parent.displayItems.addAll(this.displayItems);
		
		this.rebuildSearchTree();
	}
	
	// Custom impl of the default fabric event trigger at
	// https://github.com/FabricMC/fabric/blob/95a137205b0b47b97b1ab35ac09a3430641137de/fabric-item-group-api-v1/src/main/java/net/fabricmc/fabric/mixin/itemgroup/ItemGroupMixin.java#L55
	protected void triggerEntryUpdateEvent(ItemDisplayParameters context) {
		final ResourceKey<CreativeModeTab> registryKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(parent).orElseThrow(() -> new IllegalStateException("Unregistered parent item group : " + parent));
		
		// Do not modify special item groups (except Operator Blocks) at all.
		// Special item groups include Saved Hotbars, Search, and Survival Inventory.
		// Note, search gets modified as part of the parent item group.
		if (parent.isAlignedRight() && registryKey != CreativeModeTabs.OP_BLOCKS) return;
		
		// Sanity check for the injection point. It should be after these fields are set.
		Objects.requireNonNull(displayItems, "displayStacks");
		Objects.requireNonNull(displayItemsSearchTab, "searchTabStacks");
		
		// Convert the entries to lists
		List<ItemStack> mutableDisplayStacks = new LinkedList<>(displayItems);
		List<ItemStack> mutableSearchTabStacks = new LinkedList<>(displayItemsSearchTab);
//		FabricItemGroupEntries entries = new FabricItemGroupEntries(context, mutableDisplayStacks, mutableSearchTabStacks); // scary ApiStatus.Internal usage
//
//		final Event<ItemSubGroupEvents.ModifyEntries> modifyEntriesEvent = ItemSubGroupEvents.modifyEntriesEvent(identifier);
//
//		if (modifyEntriesEvent != null) {
//			modifyEntriesEvent.invoker().modifyEntries(entries);
//		}
//
//		// Now trigger the global event
//		if (registryKey != CreativeModeTabs.OP_BLOCKS || context.hasPermissions()) {
//			ItemSubGroupEvents.MODIFY_ENTRIES_ALL.invoker().modifyEntries(this, entries);
//		}
		
		// Convert the stacks back to sets after the events had a chance to modify them
		displayItems.clear();
		displayItems.addAll(mutableDisplayStacks);
		
		displayItemsSearchTab.clear();
		displayItemsSearchTab.addAll(mutableSearchTabStacks);
	}
	
	@Override
	public ItemStack getIconItem() {
		return ItemStack.EMPTY;
	}
	
	public CreativeModeTab getParent() {
		return parent;
	}
	
	public int getIndexInParent() {
		return indexInParent;
	}
	
	public @Nullable ResourceLocation getBackgroundTexture() {
		return this.backgroundTexture;
	}
	
	public static class Builder {
		
		protected CreativeModeTab parent;
		protected final ResourceLocation identifier;
		protected Component displayName;
		protected ResourceLocation backgroundTexture;
		private DisplayItemsGenerator entryCollector;
		
		public Builder(CreativeModeTab parent, ResourceLocation identifier, Component displayName) {
			this.parent = parent;
			this.identifier = identifier;
			this.displayName = displayName;
		}
		
		public de.dafuqs.fractal.api.ItemSubGroup.Builder backgroundTexture(ResourceLocation texture) {
			this.backgroundTexture = texture;
			return this;
		}
		
		public de.dafuqs.fractal.api.ItemSubGroup.Builder entries(DisplayItemsGenerator entryCollector) {
			this.entryCollector = entryCollector;
			return this;
		}
		
		public ItemSubGroup build() {
			ItemSubGroup subGroup = new ItemSubGroup(parent, identifier, displayName, backgroundTexture, entryCollector);
			SUB_GROUPS.add(subGroup);
			return subGroup;
		}
	}
	
}
