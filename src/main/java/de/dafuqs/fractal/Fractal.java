package de.dafuqs.fractal;

import de.dafuqs.fractal.api.*;
import de.dafuqs.fractal.interfaces.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.itemgroup.v1.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.text.*;
import net.minecraft.util.*;

public class Fractal implements ModInitializer {
	
	public static final Identifier GROUP_ID = new Identifier("mymod", "main");
	public static final ItemGroup MAIN = FabricItemGroup.builder()
			.icon(() -> new ItemStack(Blocks.REDSTONE_BLOCK))
			.entries((displayContext, entries) -> {
				entries.add(Items.APPLE, ItemGroup.StackVisibility.PARENT_TAB_ONLY);
				ItemGroupParent parent = (ItemGroupParent) Fractal.MAIN;
				for (ItemSubGroup subGroup : parent.fractal$getChildren()) {
					entries.addAll(subGroup.getSearchTabStacks(), ItemGroup.StackVisibility.SEARCH_TAB_ONLY);
				}
			})
			.displayName(Text.translatable("mymod.1"))
			.noRenderedName()
			.build();
	
	public static final Identifier ITEM_GROUP_BACKGROUND_TEXTURE_IDENTIFIER = new Identifier("fractal", "textures/custom_item_group.png");
	
	public static final ItemGroup EQUIPMENT = new ItemSubGroup.Builder(MAIN, new Identifier("fractal", "equipment"), Text.translatable("itemGroup.mymod.equipment")).entries((displayContext, entries) -> entries.add(Items.APPLE)).build();
	public static final ItemGroup FUNCTIONAL = new ItemSubGroup.Builder(MAIN, new Identifier("fractal", "functional"), Text.translatable("itemGroup.mymod.functional")).entries((displayContext, entries) -> entries.add(Items.BAKED_POTATO)).build();
	public static final ItemGroup CUISINE = new ItemSubGroup.Builder(MAIN, new Identifier("fractal", "cuisine"), Text.translatable("itemGroup.mymod.cuisine")).entries((displayContext, entries) -> entries.add(Items.CACTUS)).build();
	public static final ItemGroup RESOURCES = new ItemSubGroup.Builder(MAIN, new Identifier("fractal", "resources"), Text.translatable("itemGroup.mymod.resources")).entries((displayContext, entries) -> entries.add(Items.DANDELION)).build();
	public static final ItemGroup RESOURCES2 = new ItemSubGroup.Builder(MAIN, new Identifier("fractal", "resources2"), Text.translatable("itemGroup.mymod.resources2")).entries((displayContext, entries) -> entries.add(Items.EGG)).build();
	public static final ItemGroup RESOURCES3 = new ItemSubGroup.Builder(MAIN, new Identifier("fractal", "resources3"), Text.translatable("itemGroup.mymod.resources3")).entries((displayContext, entries) -> entries.add(Items.GLASS)).build();
	
	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM_GROUP, GROUP_ID, MAIN);
	}
	
}