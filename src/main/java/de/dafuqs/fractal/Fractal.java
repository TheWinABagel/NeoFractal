package de.dafuqs.fractal;

import de.dafuqs.fractal.api.ItemSubGroup;
import de.dafuqs.fractal.api.ModifyEntriesAllEvent;
import net.minecraft.core.Holder;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod("fractal")
public class Fractal {
    public Fractal(IEventBus modBus) {
        tabs.register(modBus);
        modBus.addListener(this::onModifyEntriesAll);
        modBus.addListener(this::onFMLCommonSetup);
    }

    @SubscribeEvent
    public void onModifyEntriesAll(ModifyEntriesAllEvent event) {

    }

    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            Test.test();
        });
    }

    public static final DeferredRegister<CreativeModeTab> tabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "fractal");


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = tabs.register("main_group", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(Blocks.REDSTONE_BLOCK))
            .displayItems((displayContext, entries) -> entries.accept(Items.APPLE))
            .title(Component.translatable("mymod.1"))
            .hideTitle()
            .build());

    private static class Test {
        public static final Holder<CreativeModeTab> EQUIPMENT = Holder.direct(new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:test1"), Component.translatable("itemGroup.mymod.equipment")).entries((displayContext, entries) -> entries.accept(Items.PODZOL)).build());
        public static final CreativeModeTab FUNCTIONAL = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:test2"), Component.translatable("itemGroup.mymod.functional")).entries((displayContext, entries) -> entries.accept(Items.BAKED_POTATO)).build();
        public static final CreativeModeTab CUISINE = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:test3"), Component.translatable("itemGroup.mymod.cuisine")).entries((displayContext, entries) -> entries.accept(Items.CACTUS)).build();
        public static final CreativeModeTab RESOURCES = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:test4"), Component.translatable("itemGroup.mymod.resources")).entries((displayContext, entries) -> entries.accept(Items.DANDELION)).build();

        public static void test() {

        }
    }
}