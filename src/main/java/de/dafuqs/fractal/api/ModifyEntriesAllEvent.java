package de.dafuqs.fractal.api;

import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class ModifyEntriesAllEvent extends Event implements IModBusEvent {

    private final ItemSubGroup group;
    private final CreativeModeTab.Output entries;

    public ModifyEntriesAllEvent(ItemSubGroup group, CreativeModeTab.Output entries) {
        this.group = group;
        this.entries = entries;
    }
}
