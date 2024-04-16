package de.dafuqs.fractal;

import de.dafuqs.fractal.api.ItemSubGroup;
import de.dafuqs.fractal.api.ItemSubGroupEvents;
import de.dafuqs.fractal.interfaces.ItemGroupParent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

@Mod("fractal")
public class Fractal {
	public Fractal(IEventBus modBus) {
		TABS_REGISTER.register(modBus);
		modBus.addListener(this::onFMLCommonSetup);
		modBus.addListener(this::onItemSubGroupsModifyEntries);
		modBus.addListener(this::onItemSubGroupsModifyEntriesAll);
		modBus.addListener(this::onBuildCreativeModeTabContents);
	}

	@SubscribeEvent
	public void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
		event.accept(Items.AZALEA);
	}

	@SubscribeEvent
	public void onItemSubGroupsModifyEntries(ItemSubGroupEvents.ModifyEntriesEvent e) {
		if(Objects.equals(e.getId(), new ResourceLocation("fractal:equipment"))) {
			e.getEntries().accept(new ItemStack(Items.ACACIA_PLANKS));
		}
	}

	@SubscribeEvent
	public void onItemSubGroupsModifyEntriesAll(ItemSubGroupEvents.ModifyEntriesAllEvent event) {
		event.getEntries().accept(new ItemStack(Items.BEACON));
	}

	@SubscribeEvent
	public void onFMLCommonSetup(FMLCommonSetupEvent e) {
		e.enqueueWork(() -> {
			EQUIPMENT = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:equipment"), Component.translatable("itemGroup.mymod.equipment")).entries((displayContext, entries) -> entries.accept(Items.APPLE)).build();
			FUNCTIONAL = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:functional"), Component.translatable("itemGroup.mymod.functional")).entries((displayContext, entries) -> entries.accept(Items.BAKED_POTATO)).build();
			CUISINE = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:cuisine"), Component.translatable("itemGroup.mymod.cuisine")).entries((displayContext, entries) -> entries.accept(Items.CACTUS)).build();
			RESOURCES = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:resources"), Component.translatable("itemGroup.mymod.resources")).entries((displayContext, entries) -> entries.accept(Items.DANDELION)).build();
		});
	}

	public static final DeferredRegister<CreativeModeTab> TABS_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "fractal");


	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS_REGISTER.register("main_group", () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(Blocks.REDSTONE_BLOCK))
			.displayItems((displayContext, entries) -> {
				entries.accept(Items.APPLE);
				ItemGroupParent parent = (ItemGroupParent) Fractal.MAIN.get();
				for (ItemSubGroup subGroup : parent.fractal$getChildren()) {
					entries.acceptAll(subGroup.getSearchTabDisplayItems(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
				}
			})
			.title(Component.translatable("mymod.1"))
			.hideTitle()
			.build());
	public static CreativeModeTab EQUIPMENT;
	public static CreativeModeTab FUNCTIONAL;
	public static CreativeModeTab CUISINE;
	public static CreativeModeTab RESOURCES;

}