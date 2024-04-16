# About Fractal
This repo is a fork of [lib39/fractal](https://git.sleeping.town/unascribed-mods/Lib39) by unascribed, with added support for styled groups. I will take care of this repo while unascribed is busy.

Fractal introduces item **subgroups for the creative menu**.

### Why Fractal?
- Fractals Subgroups are very condensed, allowing you to add up to 12 subgroups for each of your tabs
- Creating a new ItemSubGroup only takes one line of code and no changes in the way you assign your items to groups. Just pass them the ItemSubGroup instead of your main item group

### Limitations
- More than 12 subgroups per item group, while fully functional, will look weird.
- The tiny font used for the labels does not support full unicode

## Examples

### Vanilla Style Subgroups

![Screenshots of the Creative Tabs](images/screenshot_vanilla_style.png)

```java

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

//Do not register subgroups to the deferred register
public static CreativeModeTab EQUIPMENT;
public static CreativeModeTab FUNCTIONAL;
public static CreativeModeTab CUISINE;
public static CreativeModeTab RESOURCES;

public ExapleModConstructor(IEventBus modBus) {
    TABS_REGISTER.register(modBus);
    modBus.addListener(this::onFMLCommonSetup);
}

@SubscribeEvent
public void onFMLCommonSetup(FMLCommonSetupEvent e) {
    //Statically setting this will result in an error
    e.enqueueWork(() -> {
        EQUIPMENT = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:equipment"), Component.translatable("itemGroup.mymod.equipment")).entries((displayContext, entries) -> entries.accept(Items.APPLE)).build();
        FUNCTIONAL = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:functional"), Component.translatable("itemGroup.mymod.functional")).entries((displayContext, entries) -> entries.accept(Items.BAKED_POTATO)).build();
        CUISINE = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:cuisine"), Component.translatable("itemGroup.mymod.cuisine")).entries((displayContext, entries) -> entries.accept(Items.CACTUS)).build();
        RESOURCES = new ItemSubGroup.Builder(MAIN.get(), new ResourceLocation("fractal:resources"), Component.translatable("itemGroup.mymod.resources")).entries((displayContext, entries) -> entries.accept(Items.DANDELION)).build();
    });
}
```

### Applying a custom style
You are also able to apply a style to your ItemSubGroups, by supplying a custom texture that will be getting used as background for your subgroup. You can even mix and match!
In this example, the first two ItemSubGroups use a custom style by supplying a texture file that is being shipped with your mod. The latter two tabs use the vanilla style.

Just ship a modified [Texture Template](images/tabs_template.png) in your mods resources folder, and you are good to go!

![Screenshots of the Creative Tabs](images/screenshot_custom_style.png)

```java
public static final Identifier ITEM_GROUP_BACKGROUND_TEXTURE_IDENTIFIER = new Identifier("mymod", "textures/item_group.png");

public static final ItemGroup EQUIPMENT = new ItemSubGroup.Builder(MAIN, Text.translatable("itemGroup.mymod.equipment")).backgroundTexture(ITEM_GROUP_BACKGROUND_TEXTURE_IDENTIFIER).entries((displayContext, entries) -> entries.add(I1)).build();
public static final ItemGroup FUNCTIONAL = new ItemSubGroup.Builder(MAIN, Text.translatable("itemGroup.mymod.functional")).backgroundTexture(ITEM_GROUP_BACKGROUND_TEXTURE_IDENTIFIER).entries((displayContext, entries) -> entries.add(I2)).build();
public static final ItemGroup CUISINE = new ItemSubGroup.Builder(MAIN, Text.translatable("itemGroup.mymod.cuisine")).entries((displayContext, entries) -> entries.add(I3)).build();
public static final ItemGroup RESOURCES = new ItemSubGroup.Builder(MAIN, Text.translatable("itemGroup.mymod.resources")).entries((displayContext, entries) -> entries.add(I4)).build();
```

### Adding items to existing ItemSubGroups

There exists an API for that! `ItemSubGroupEvents.modifyEntriesEvent(<Identifier of the Subtab you want to modify)` behaves exactly like its Fabric API counterpart, but targets ItemSubGroups instead of ItemGroups (it needs to be a separate event, since ItemSubGroups are not registered item groups, having no RegistryEntry that the Fabric API version targets)