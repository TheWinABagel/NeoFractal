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
						@Nullable Identifier enabledScrollbarTexture,
						@Nullable Identifier disabledScrollbarTexture,
						@Nullable Identifier tabTopFirstSelectedTexture,
						@Nullable Identifier tabTopSelectedTexture,
						@Nullable Identifier tabTopUnselectedTexture,
						@Nullable Identifier tabBottomFirstSelectedTexture,
						@Nullable Identifier tabBottomSelectedTexture,
						@Nullable Identifier tabBottomUnselectedTexture,
						@Nullable Identifier subtabSelectedTexture,
						@Nullable Identifier subtabUnselectedTexture) {
		
	}
	
	public static final Identifier SUBTAB_SELECTED_TEXTURE = new Identifier("fractal", "container/creative_inventory/subtab_selected");
	public static final Identifier SUBTAB_UNSELECTED_TEXTURE = new Identifier("fractal", "container/creative_inventory/subtab_unselected");
	
	public static final Style VANILLA_STYLE = new Style(null, null, null, null, null, null, null, null, null, null, null);
	
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
