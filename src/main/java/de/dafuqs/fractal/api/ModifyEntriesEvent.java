package de.dafuqs.fractal.api;

import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.Consumer;

public class ModifyEntriesEvent extends Event implements IModBusEvent {
    private final CreativeModeTab.Output entries;

    public ModifyEntriesEvent(CreativeModeTab.Output entries) {
        this.entries = entries;
    }

    public void accept(Consumer<CreativeModeTab.Output> entries) {
        entries.accept(this.entries);
    }
}
