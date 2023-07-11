package de.dafuqs.fractal.api;

import de.dafuqs.fractal.quack.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

public class ItemSubGroup extends ItemGroup {
	
	private final ItemGroup parent;
	private final int indexInParent;
	private @Nullable Identifier backgroundTexture;
	
	protected ItemSubGroup(ItemGroup parent, Identifier id, Identifier backgroundTexture) {
		super(0, id.getNamespace() + "." + id.getPath());
		this.backgroundTexture = backgroundTexture;
		this.parent = parent;
		ItemGroupParent igp = (ItemGroupParent) parent;
		this.indexInParent = igp.fractal$getChildren().size();
		igp.fractal$getChildren().add(this);
		if (igp.fractal$getSelectedChild() == null) {
			igp.fractal$setSelectedChild(this);
		}
	}
	
	public static ItemSubGroup create(ItemGroup parent, Identifier id, Identifier backgroundTexture) {
		return new ItemSubGroup(parent, id, backgroundTexture);
	}
	
	public static ItemSubGroup create(ItemGroup parent, Identifier id) {
		return new ItemSubGroup(parent, id, null);
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
	
	// unmapped as it's missing from the intersection, but that's ok
	@Override
	public ItemStack createIcon() {
		return ItemStack.EMPTY;
	}
	
	public @Nullable Identifier getBackgroundTexture() {
		return this.backgroundTexture;
	}
	
}
