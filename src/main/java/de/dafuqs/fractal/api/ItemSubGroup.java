package de.dafuqs.fractal.api;

import de.dafuqs.fractal.quack.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import java.util.*;

public class ItemSubGroup extends ItemGroup {
	
	public static final List<ItemSubGroup> SUB_GROUPS = new ArrayList<>();
	
	protected final ItemGroup parent;
	protected final int indexInParent;
	protected final Style style;
	
	public record Style(@Nullable Identifier backgroundTexture,
						
						@Nullable Identifier selectedSubtabTexture,
						@Nullable Identifier unselectedSubtabTexture,
						
						@Nullable Identifier enabledScrollbarTexture,
						@Nullable Identifier disabledScrollbarTexture,
						 
						@Nullable Identifier tabTopFirstSelectedTexture,
						@Nullable Identifier tabTopSelectedTexture,
						@Nullable Identifier tabTopLastSelectedTexture,
						@Nullable Identifier tabTopFirstUnselectedTexture,
						@Nullable Identifier tabTopUnselectedTexture,
						@Nullable Identifier tabTopLastUnselectedTexture,
						@Nullable Identifier tabBottomFirstSelectedTexture,
						@Nullable Identifier tabBottomSelectedTexture,
						@Nullable Identifier tabBottomLastSelectedTexture,
						@Nullable Identifier tabBottomFirstUnselectedTexture,
						@Nullable Identifier tabBottomUnselectedTexture,
						@Nullable Identifier tabBottomLastUnselectedTexture) {
		
		public static class Builder {
			
			protected @Nullable Identifier backgroundTexture;
			
			protected @Nullable Identifier selectedSubtabTexture;
			protected @Nullable Identifier unselectedSubtabTexture;
			
			protected @Nullable Identifier enabledScrollbarTexture;
			protected @Nullable Identifier disabledScrollbarTexture;
			
			protected @Nullable Identifier tabTopFirstSelectedTexture;
			protected @Nullable Identifier tabTopSelectedTexture;
			protected @Nullable Identifier tabTopLastSelectedTexture;
			protected @Nullable Identifier tabTopFirstUnselectedTexture;
			protected @Nullable Identifier tabTopUnselectedTexture;
			protected @Nullable Identifier tabTopLastUnselectedTexture;
			
			protected @Nullable Identifier tabBottomFirstSelectedTexture;
			protected @Nullable Identifier tabBottomSelectedTexture;
			protected @Nullable Identifier tabBottomLastSelectedTexture;
			protected @Nullable Identifier tabBottomFirstUnselectedTexture;
			protected @Nullable Identifier tabBottomUnselectedTexture;
			protected @Nullable Identifier tabBottomLastUnselectedTexture;
			
			public Builder() { }
			
			public Style.Builder background(Identifier backgroundTexture) { // texture
				this.backgroundTexture = backgroundTexture;
				return this;
			}
			
			public Style.Builder scrollbar(Identifier enabledTexture, Identifier disabledTexture) { // sprite
				this.enabledScrollbarTexture = enabledTexture;
				this.disabledScrollbarTexture = disabledTexture;
				return this;
			}
			
			public Style.Builder subtab(Identifier selectedSubtabTexture, Identifier unselectedSubtabTexture) { // sprite
				this.selectedSubtabTexture = selectedSubtabTexture;
				this.unselectedSubtabTexture = unselectedSubtabTexture;
				return this;
			}
			
			public Style.Builder tab(Identifier topFirstSelectedTexture, Identifier topSelectedTexture, Identifier topLastSelectedTexture, Identifier topFirstUnselectedTexture, Identifier topUnselectedTexture, Identifier topLastUnselectedTexture,
									 Identifier bottomFirstSelectedTexture, Identifier bottomSelectedTexture, Identifier bottomLastSelectedTexture, Identifier bottomFirstUnselectedTexture, Identifier bottomUnselectedTexture, Identifier bottomLastUnselectedTexture) { // sprite
				
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
	
	public static final Identifier SUBTAB_SELECTED_TEXTURE = new Identifier("fractal", "container/creative_inventory/subtab_selected");
	public static final Identifier SUBTAB_UNSELECTED_TEXTURE = new Identifier("fractal", "container/creative_inventory/subtab_unselected");
	
	public static final Style VANILLA_STYLE = new Style.Builder().build();
	
	protected ItemSubGroup(ItemGroup parent, Text displayName, EntryCollector entryCollector) {
		this(parent, displayName, entryCollector, VANILLA_STYLE);
	}
	
	protected ItemSubGroup(ItemGroup parent, Text displayName, EntryCollector entryCollector, Style style) {
		super(parent.getRow(), parent.getColumn(), parent.getType(), displayName, () -> ItemStack.EMPTY, entryCollector);
		this.style = style;
		this.parent = parent;
		ItemGroupParent igp = (ItemGroupParent) parent;
		this.indexInParent = igp.fractal$getChildren().size();
		igp.fractal$getChildren().add(this);
		if (igp.fractal$getSelectedChild() == null) {
			igp.fractal$setSelectedChild(this);
		}
	}
	
	/**
	 * 100 % the vanilla code, but the check for registered item groups was removed
	 * (we do not want to register our subgroups, so other mods do not pick them up)
	 */
	@Override
	public void updateEntries(DisplayContext displayContext) {
		EntriesImpl entriesImpl = new EntriesImpl(this, displayContext.enabledFeatures);
		this.entryCollector.accept(displayContext, entriesImpl);
		this.displayStacks = entriesImpl.parentTabStacks;
		this.searchTabStacks = entriesImpl.searchTabStacks;
		this.reloadSearchProvider();
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
	
	public Style getStyle() {
		return style;
	}
	
	public static class Builder {
		
		protected ItemGroup parent;
		protected Text displayName;
		protected Style style;
		private EntryCollector entryCollector;
		
		public Builder(ItemGroup parent, Text displayName) {
			this.parent = parent;
			this.displayName = displayName;
		}
		
		public Builder styled(Style style) {
			this.style = style;
			return this;
		}
		
		public Builder entries(EntryCollector entryCollector) {
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
