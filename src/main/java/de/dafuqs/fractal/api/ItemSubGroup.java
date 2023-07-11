package de.dafuqs.fractal.api;

import de.dafuqs.fractal.quack.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ItemSubGroup extends ItemGroup {
	
	public static final List<ItemSubGroup> SUB_GROUPS = new ArrayList<>();
	
	private final ItemGroup parent;
	private final int indexInParent;
	private final @Nullable Identifier backgroundTexture;
	
	protected ItemSubGroup(ItemGroup parent, Text displayName, @Nullable Identifier backgroundTexture, EntryCollector entryCollector) {
		super(parent.getRow(), parent.getColumn(), parent.getType(), displayName, () -> ItemStack.EMPTY, entryCollector);
		this.backgroundTexture = backgroundTexture;
		this.parent = parent;
		ItemGroupParent igp = (ItemGroupParent) parent;
		this.indexInParent = igp.fractal$getChildren().size();
		igp.fractal$getChildren().add(this);
		if (igp.fractal$getSelectedChild() == null) {
			igp.fractal$setSelectedChild(this);
		}
	}
	
	public ItemGroup getParent() {
		return parent;
	}
	
	public int getIndexInParent() {
		return indexInParent;
	}
	
	@Override
	public ItemStack getIcon() {
		return ItemStack.EMPTY;
	}
	
	public @Nullable Identifier getBackgroundTexture() {
		return this.backgroundTexture;
	}
	
	public static class Builder {
		
		protected ItemGroup parent;
		protected Text displayName;
		protected Identifier backgroundTexture;
		private EntryCollector entryCollector;
		
		public Builder(ItemGroup parent, Text displayName) {
			this.parent = parent;
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
			ItemSubGroup subGroup = new ItemSubGroup(parent, displayName, backgroundTexture, entryCollector);
			SUB_GROUPS.add(subGroup);
			return subGroup;
		}
	}
	
}
