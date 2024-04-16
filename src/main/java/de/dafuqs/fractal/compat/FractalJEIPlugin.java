package de.dafuqs.fractal.compat;

import de.dafuqs.fractal.interfaces.ItemGroupParent;
import de.dafuqs.fractal.interfaces.SubTabLocation;
import de.dafuqs.fractal.mixin.client.CreativeInventoryScreenAccessor;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

@JeiPlugin
public class FractalJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("fractal:fractal");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(CreativeModeInventoryScreen.class, new IGuiContainerHandler<CreativeModeInventoryScreen>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(CreativeModeInventoryScreen screen) {
                CreativeModeTab selected = CreativeInventoryScreenAccessor.fractal$getSelectedTab();
                if (selected instanceof ItemGroupParent parent && screen instanceof SubTabLocation stl && parent.fractal$getChildren() != null && !parent.fractal$getChildren().isEmpty()) {
                    return List.of(
                            new Rect2i(stl.fractal$getX(), stl.fractal$getY(), 72, stl.fractal$getH()),
                            new Rect2i(stl.fractal$getX2(), stl.fractal$getY(), 72, stl.fractal$getH2())
                    );
                }
                return List.of();
            }
        });
    }
}
