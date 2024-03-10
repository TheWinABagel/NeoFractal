package de.dafuqs.fractal.api;

import de.dafuqs.fractal.interfaces.*;
import net.fabricmc.fabric.api.event.*;
import net.fabricmc.fabric.api.itemgroup.v1.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ItemSubGroup extends ItemGroup {
	
	public static final List<ItemSubGroup> SUB_GROUPS = new ArrayList<>();
	
	protected final ItemGroup parent;
	protected final Identifier identifier;
	protected final int indexInParent;
	protected final @Nullable Identifier backgroundTexture;
	
	protected ItemSubGroup(ItemGroup parent, Identifier identifier, Text displayName, @Nullable Identifier backgroundTexture, EntryCollector entryCollector) {
		super(parent.getRow(), parent.getColumn(), parent.getType(), displayName, () -> ItemStack.EMPTY, entryCollector);
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
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	/**
	 * 100 % the vanilla code, but the check for registered item groups was removed
	 * (we do not want to register our subgroups, so other mods do not pick them up)
	 */
	@Override
	public void updateEntries(DisplayContext context) {
		EntriesImpl entries = new EntriesImpl(this, context.enabledFeatures);
		this.entryCollector.accept(context, entries);
		this.displayStacks = entries.parentTabStacks;
		this.searchTabStacks = entries.searchTabStacks;
		
		triggerEntryUpdateEvent(context);
		
		this.parent.searchTabStacks.addAll(this.searchTabStacks);
		this.parent.displayStacks.addAll(this.displayStacks);
		
		this.reloadSearchProvider();
	}
	
	// Custom impl of the default fabric event trigger at
	// https://github.com/FabricMC/fabric/blob/95a137205b0b47b97b1ab35ac09a3430641137de/fabric-item-group-api-v1/src/main/java/net/fabricmc/fabric/mixin/itemgroup/ItemGroupMixin.java#L55
	protected void triggerEntryUpdateEvent(DisplayContext context) {
		final RegistryKey<ItemGroup> registryKey = Registries.ITEM_GROUP.getKey(parent).orElseThrow(() -> new IllegalStateException("Unregistered parent item group : " + parent));
		
		// Do not modify special item groups (except Operator Blocks) at all.
		// Special item groups include Saved Hotbars, Search, and Survival Inventory.
		// Note, search gets modified as part of the parent item group.
		if (parent.isSpecial() && registryKey != ItemGroups.OPERATOR) return;
		
		// Sanity check for the injection point. It should be after these fields are set.
		Objects.requireNonNull(displayStacks, "displayStacks");
		Objects.requireNonNull(searchTabStacks, "searchTabStacks");
		
		// Convert the entries to lists
		List<ItemStack> mutableDisplayStacks = new LinkedList<>(displayStacks);
		List<ItemStack> mutableSearchTabStacks = new LinkedList<>(searchTabStacks);
		FabricItemGroupEntries entries = new FabricItemGroupEntries(context, mutableDisplayStacks, mutableSearchTabStacks); // scary ApiStatus.Internal usage
		
		final Event<ItemSubGroupEvents.ModifyEntries> modifyEntriesEvent = ItemSubGroupEvents.modifyEntriesEvent(identifier);
		
		if (modifyEntriesEvent != null) {
			modifyEntriesEvent.invoker().modifyEntries(entries);
		}
		
		// Now trigger the global event
		if (registryKey != ItemGroups.OPERATOR || context.hasPermissions()) {
			ItemSubGroupEvents.MODIFY_ENTRIES_ALL.invoker().modifyEntries(this, entries);
		}
		
		// Convert the stacks back to sets after the events had a chance to modify them
		displayStacks.clear();
		displayStacks.addAll(mutableDisplayStacks);
		
		searchTabStacks.clear();
		searchTabStacks.addAll(mutableSearchTabStacks);
	}
	
	@Override
	public ItemStack getIcon() {
		return ItemStack.EMPTY;
	}
	
	public ItemGroup getParent() {
		return parent;
	}
	
	public int getIndexInParent() {
		return indexInParent;
	}
	
	public @Nullable Identifier getBackgroundTexture() {
		return this.backgroundTexture;
	}
	
	public static class Builder {
		
		protected ItemGroup parent;
		protected final Identifier identifier;
		protected Text displayName;
		protected Identifier backgroundTexture;
		private EntryCollector entryCollector;
		
		public Builder(ItemGroup parent, Identifier identifier, Text displayName) {
			this.parent = parent;
			this.identifier = identifier;
			this.displayName = displayName;
		}
		
		public Builder backgroundTexture(Identifier texture) {
			this.backgroundTexture = texture;
			return this;
		}
		
		public Builder entries(EntryCollector entryCollector) {
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
