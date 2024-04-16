package de.dafuqs.fractal.api;

import de.dafuqs.fractal.interfaces.ItemGroupParent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.util.MutableHashedLinkedMap;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
		this.displayItems = entries.tabContents;
		this.displayItemsSearchTab = entries.searchTabContents;
		final ResourceKey<CreativeModeTab> registryKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(parent).orElseThrow(() -> new IllegalStateException("Unregistered parent item group : " + parent));
		onCreativeModeTabBuildContents(registryKey, this.displayItemsGenerator, context, entries);
		
		this.parent.displayItemsSearchTab.addAll(this.displayItemsSearchTab);
		this.parent.displayItems.addAll(this.displayItems);
		
		this.rebuildSearchTree();
	}

	//Custom impl of EventHooks#onCreativeModeTabBuildContents
	public void onCreativeModeTabBuildContents(ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.DisplayItemsGenerator originalGenerator, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
		final var entries = new MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility>(ItemStackLinkedSet.TYPE_AND_TAG,
				(key, left, right) -> {
					//throw new IllegalStateException("Accidentally adding the same item stack twice " + key.getDisplayName().getString() + " to a Creative Mode Tab: " + tab.getDisplayName().getString());
					// Vanilla adds enchanting books twice in both visibilities.
					// This is just code cleanliness for them. For us lets just increase the visibility and merge the entries.
					return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
				});

		originalGenerator.accept(params, (stack, vis) -> {
			if (stack.getCount() != 1)
				throw new IllegalArgumentException("The stack count must be 1");
			entries.put(stack, vis);
		});

		ModLoader.get().postEvent(new BuildCreativeModeTabContentsEvent(this, tabKey, params, entries));

		final ItemSubGroupEvents.ModifyEntriesEvent modifyEntriesEvent = new ItemSubGroupEvents.ModifyEntriesEvent(identifier, output);
		ModLoader.get().postEvent(modifyEntriesEvent);

		// Now trigger the global event
		if (tabKey != CreativeModeTabs.OP_BLOCKS || params.hasPermissions()) {
			ModLoader.get().postEvent(new ItemSubGroupEvents.ModifyEntriesAllEvent(this, output));
		}

		for (var entry : entries)
			output.accept(entry.getKey(), entry.getValue());
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
