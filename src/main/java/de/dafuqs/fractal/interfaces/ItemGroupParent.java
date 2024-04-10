package de.dafuqs.fractal.interfaces;

import de.dafuqs.fractal.api.*;

import java.util.List;

public interface ItemGroupParent {
	
	List<ItemSubGroup> fractal$getChildren();
	ItemSubGroup fractal$getSelectedChild();
	void fractal$setSelectedChild(ItemSubGroup group);
	
}
