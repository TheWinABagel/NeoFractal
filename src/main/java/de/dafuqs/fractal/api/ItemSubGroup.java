package de.dafuqs.fractal.api;

import de.dafuqs.fractal.interfaces.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayBuilder;
import net.minecraft.world.item.ItemStack;

public class ItemSubGroup extends CreativeModeTab {
	
	public static final List<ItemSubGroup> SUB_GROUPS = new ArrayList<>();
	
	protected final CreativeModeTab parent;
	protected final int indexInParent;
	protected final Style style;
	
	public record Style(@Nullable ResourceLocation backgroundTexture,
						
						@Nullable ResourceLocation selectedSubtabTexture,
						@Nullable ResourceLocation unselectedSubtabTexture,
						
						@Nullable ResourceLocation enabledScrollbarTexture,
						@Nullable ResourceLocation disabledScrollbarTexture,
						 
						@Nullable ResourceLocation tabTopFirstSelectedTexture,
						@Nullable ResourceLocation tabTopSelectedTexture,
						@Nullable ResourceLocation tabTopLastSelectedTexture,
						@Nullable ResourceLocation tabTopFirstUnselectedTexture,
						@Nullable ResourceLocation tabTopUnselectedTexture,
						@Nullable ResourceLocation tabTopLastUnselectedTexture,
						@Nullable ResourceLocation tabBottomFirstSelectedTexture,
						@Nullable ResourceLocation tabBottomSelectedTexture,
						@Nullable ResourceLocation tabBottomLastSelectedTexture,
						@Nullable ResourceLocation tabBottomFirstUnselectedTexture,
						@Nullable ResourceLocation tabBottomUnselectedTexture,
						@Nullable ResourceLocation tabBottomLastUnselectedTexture) {
		
		public static class Builder {
			
			protected @Nullable ResourceLocation backgroundTexture;
			
			protected @Nullable ResourceLocation selectedSubtabTexture;
			protected @Nullable ResourceLocation unselectedSubtabTexture;
			
			protected @Nullable ResourceLocation enabledScrollbarTexture;
			protected @Nullable ResourceLocation disabledScrollbarTexture;
			
			protected @Nullable ResourceLocation tabTopFirstSelectedTexture;
			protected @Nullable ResourceLocation tabTopSelectedTexture;
			protected @Nullable ResourceLocation tabTopLastSelectedTexture;
			protected @Nullable ResourceLocation tabTopFirstUnselectedTexture;
			protected @Nullable ResourceLocation tabTopUnselectedTexture;
			protected @Nullable ResourceLocation tabTopLastUnselectedTexture;
			
			protected @Nullable ResourceLocation tabBottomFirstSelectedTexture;
			protected @Nullable ResourceLocation tabBottomSelectedTexture;
			protected @Nullable ResourceLocation tabBottomLastSelectedTexture;
			protected @Nullable ResourceLocation tabBottomFirstUnselectedTexture;
			protected @Nullable ResourceLocation tabBottomUnselectedTexture;
			protected @Nullable ResourceLocation tabBottomLastUnselectedTexture;
			
			public Builder() { }
			
			public Builder background(ResourceLocation backgroundTexture) { // texture
				this.backgroundTexture = backgroundTexture;
				return this;
			}
			
			public Builder scrollbar(ResourceLocation enabledTexture, ResourceLocation disabledTexture) { // sprite
				this.enabledScrollbarTexture = enabledTexture;
				this.disabledScrollbarTexture = disabledTexture;
				return this;
			}
			
			public Builder subtab(ResourceLocation selectedSubtabTexture, ResourceLocation unselectedSubtabTexture) { // sprite
				this.selectedSubtabTexture = selectedSubtabTexture;
				this.unselectedSubtabTexture = unselectedSubtabTexture;
				return this;
			}
			
			public Builder tab(ResourceLocation topFirstSelectedTexture, ResourceLocation topSelectedTexture, ResourceLocation topLastSelectedTexture, ResourceLocation topFirstUnselectedTexture, ResourceLocation topUnselectedTexture, ResourceLocation topLastUnselectedTexture,
									 ResourceLocation bottomFirstSelectedTexture, ResourceLocation bottomSelectedTexture, ResourceLocation bottomLastSelectedTexture, ResourceLocation bottomFirstUnselectedTexture, ResourceLocation bottomUnselectedTexture, ResourceLocation bottomLastUnselectedTexture) { // sprite
				
				this.tabTopFirstSelectedTexture = topFirstSelectedTexture;
				this.tabTopSelectedTexture = topSelectedTexture;
				this.tabTopLastSelectedTexture = topLastSelectedTexture;
				this.tabTopFirstUnselectedTexture = topFirstUnselectedTexture;
				this.tabTopUnselectedTexture = topUnselectedTexture;
				this.tabTopLastUnselectedTexture = topLastUnselectedTexture;
				
				this.tabBottomFirstSelectedTexture = bottomFirstSelectedTexture;
				this.tabBottomSelectedTexture = bottomSelectedTexture;
				this.tabBottomLastSelectedTexture = bottomLastSelectedTexture;
				this.tabBottomFirstUnselectedTexture = bottomFirstUnselectedTexture;
				this.tabBottomUnselectedTexture = bottomUnselectedTexture;
				this.tabBottomLastUnselectedTexture = bottomLastUnselectedTexture;
				
				return this;
			}
			
			public Style build() {
				return new Style(backgroundTexture,
						selectedSubtabTexture, unselectedSubtabTexture,
						enabledScrollbarTexture, disabledScrollbarTexture,
						tabTopFirstSelectedTexture, tabTopSelectedTexture, tabTopLastSelectedTexture, tabTopFirstUnselectedTexture, tabTopUnselectedTexture, tabTopLastUnselectedTexture,
						tabBottomFirstSelectedTexture, tabBottomSelectedTexture, tabBottomLastSelectedTexture, tabBottomFirstUnselectedTexture, tabBottomUnselectedTexture, tabBottomLastUnselectedTexture);
			}
		}
		
	}
	
	public static final ResourceLocation SUBTAB_SELECTED_TEXTURE = new ResourceLocation("fractal", "container/creative_inventory/subtab_selected");
	public static final ResourceLocation SUBTAB_UNSELECTED_TEXTURE = new ResourceLocation("fractal", "container/creative_inventory/subtab_unselected");
	
	public static final Style VANILLA_STYLE = new Style.Builder().build();
	
	protected ItemSubGroup(CreativeModeTab parent, Component displayName, DisplayItemsGenerator entryCollector) {
		this(parent, displayName, entryCollector, VANILLA_STYLE);
	}
	
	protected ItemSubGroup(CreativeModeTab parent, Component displayName, DisplayItemsGenerator entryCollector, Style style) {
		super(getBuilder(parent, displayName, entryCollector));
		this.style = style;
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
	
	/**
	 * 100 % the vanilla code, but the check for registered item groups was removed
	 * (we do not want to register our subgroups, so other mods do not pick them up)
	 */
	@Override
	public void buildContents(ItemDisplayParameters displayContext) {
		ItemDisplayBuilder entriesImpl = new ItemDisplayBuilder(this, displayContext.enabledFeatures());
		this.displayItemsGenerator.accept(displayContext, entriesImpl);
		this.displayItems = entriesImpl.tabContents;
		this.displayItemsSearchTab = entriesImpl.searchTabContents;
		this.rebuildSearchTree();
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
	
	public Style getStyle() {
		return style;
	}
	
	public static class Builder {
		
		protected CreativeModeTab parent;
		protected Component displayName;
		protected Style style;
		private DisplayItemsGenerator entryCollector;
		
		public Builder(CreativeModeTab parent, Component displayName) {
			this.parent = parent;
			this.displayName = displayName;
		}
		
		public Builder styled(Style style) {
			this.style = style;
			return this;
		}
		
		public Builder entries(DisplayItemsGenerator entryCollector) {
			this.entryCollector = entryCollector;
			return this;
		}
		
		public ItemSubGroup build() {
			ItemSubGroup subGroup = new ItemSubGroup(parent, displayName, entryCollector, style);
			SUB_GROUPS.add(subGroup);
			return subGroup;
		}
	}
	
}
