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
import net.minecraft.world.item.ItemStackLinkedSet;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.util.MutableHashedLinkedMap;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.EventHooks;
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

		ResourceKey<CreativeModeTab> resourcekey = BuiltInRegistries.CREATIVE_MODE_TAB
				.getResourceKey(parent)
				.orElseThrow(() -> new IllegalStateException("Unregistered creative tab: " + parent));
		onCreativeModeTabBuildContents(this, resourcekey, this.displayItemsGenerator, context, entries);

		if (resourcekey != CreativeModeTabs.OP_BLOCKS || context.hasPermissions()) {
			ModLoader.get().postEvent(new ModifyEntriesAllEvent(this, entries));
//			ItemSubGroupEvents.MODIFY_ENTRIES_ALL.invoker().modifyEntries(this, entries);
		}

//		this.displayItems = entries.tabContents;
//		this.displayItemsSearchTab = entries.searchTabContents;
		this.parent.displayItemsSearchTab.addAll(this.displayItemsSearchTab);
		this.parent.displayItems.addAll(this.displayItems);

		this.rebuildSearchTree();
	}

	public static void onCreativeModeTabBuildContents(CreativeModeTab tab, ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.DisplayItemsGenerator originalGenerator, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
		final var entries = new MutableHashedLinkedMap<ItemStack, TabVisibility>(ItemStackLinkedSet.TYPE_AND_TAG,
				(key, left, right) -> {
					//throw new IllegalStateException("Accidentally adding the same item stack twice " + key.getDisplayName().getString() + " to a Creative Mode Tab: " + tab.getDisplayName().getString());
					// Vanilla adds enchanting books twice in both visibilities.
					// This is just code cleanliness for them. For us lets just increase the visibility and merge the entries.
					return TabVisibility.PARENT_AND_SEARCH_TABS;
				});

//		originalGenerator.accept(params, (stack, vis) -> {
//			if (stack.getCount() != 1)
//				throw new IllegalArgumentException("The stack count must be 1");
//			entries.put(stack, vis);
//		});

		ModLoader.get().postEvent(new BuildCreativeModeTabContentsEvent(tab, tabKey, params, entries));

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
