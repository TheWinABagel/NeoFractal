package de.dafuqs.fractal.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;


public final class ItemSubGroupEvents {
	private ItemSubGroupEvents() {
	}

	/**
	 * Returns the modify entries event for a specific item group. This uses the group ID and
	 * is suitable for modifying a modded item group that might not exist.
	 *
	 * @param identifier the {@link ResourceLocation} of the item group to modify
	 * @return the event
	 */
	public static ModifyEntriesEvent modifyEntriesEvent(ResourceLocation identifier, CreativeModeTab.Output entries) {

		return new ModifyEntriesEvent(identifier, entries);
	}

	public static class ModifyEntriesEvent extends Event implements IModBusEvent {
		private final ResourceLocation id;
		private final CreativeModeTab.Output entries;

		public ModifyEntriesEvent(ResourceLocation id, CreativeModeTab.Output entries) {
			this.id = id;
			this.entries = entries;
		}

		public ResourceLocation getId() {
			return this.id;
		}

		public CreativeModeTab.Output getEntries() {
			return entries;
		}


	}

	/**
	 * This event allows the entries of any item group to be modified.
	 * <p/>
	 * Use {@link #modifyEntriesEvent(ResourceLocation, CreativeModeTab.Output)} to get the event for a specific item group.
	 * <p/>
	 * This event is invoked after those two more specific events.
	 */
	public static class ModifyEntriesAllEvent extends Event implements IModBusEvent {
		private final ItemSubGroup group;
		private final CreativeModeTab.Output entries;

		public ModifyEntriesAllEvent(ItemSubGroup group, CreativeModeTab.Output entries) {
			this.group = group;
			this.entries = entries;
		}

		public ItemSubGroup getGroup() {
			return group;
		}

		public CreativeModeTab.Output getEntries() {
			return entries;
		}
	}
}