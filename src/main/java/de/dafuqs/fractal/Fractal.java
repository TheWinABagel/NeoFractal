package de.dafuqs.fractal;

import de.dafuqs.fractal.api.*;
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
			.entries((displayContext, entries) -> entries.add(Items.APPLE))
			.displayName(Text.translatable("mymod.1"))
			.noRenderedName()
			.build();
	
	public static final Item I1 = new Item(new Item.Settings());
	public static final Item I2 = new Item(new Item.Settings());
	public static final Item I3 = new Item(new Item.Settings());
	public static final Item I4 = new Item(new Item.Settings());
	
	public static final Identifier ITEM_GROUP_BACKGROUND_TEXTURE_IDENTIFIER = new Identifier("fractal", "textures/custom_item_group.png");
	
	public static final ItemGroup EQUIPMENT = new ItemSubGroup.Builder(MAIN, Text.translatable("itemGroup.mymod.equipment")).backgroundTexture(ITEM_GROUP_BACKGROUND_TEXTURE_IDENTIFIER).entries((displayContext, entries) -> entries.add(I1)).build();
	public static final ItemGroup FUNCTIONAL = new ItemSubGroup.Builder(MAIN, Text.translatable("itemGroup.mymod.functional")).backgroundTexture(ITEM_GROUP_BACKGROUND_TEXTURE_IDENTIFIER).entries((displayContext, entries) -> entries.add(I2)).build();
	public static final ItemGroup CUISINE = new ItemSubGroup.Builder(MAIN, Text.translatable("itemGroup.mymod.cuisine")).entries((displayContext, entries) -> entries.add(I3)).build();
	public static final ItemGroup RESOURCES = new ItemSubGroup.Builder(MAIN, Text.translatable("itemGroup.mymod.resources")).entries((displayContext, entries) -> entries.add(I4)).build();

	
	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM_GROUP, GROUP_ID, MAIN);
		Registry.register(Registries.ITEM, new Identifier("mymod", "i1"), I1);
		Registry.register(Registries.ITEM, new Identifier("mymod", "i2"), I2);
		Registry.register(Registries.ITEM, new Identifier("mymod", "i3"), I3);
		Registry.register(Registries.ITEM, new Identifier("mymod", "i4"), I4);
	}
	
}