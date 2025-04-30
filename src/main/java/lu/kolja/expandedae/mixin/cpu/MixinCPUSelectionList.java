package lu.kolja.expandedae.mixin.cpu;

import lu.kolja.expandedae.helper.NumberUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import appeng.client.gui.widgets.CPUSelectionList;
import appeng.menu.me.crafting.CraftingStatusMenu;

@Mixin(value = CPUSelectionList.class, remap = false)
public class MixinCPUSelectionList {

    /**
     * @author Kolja
     * @reason Truncates CPU Crafting Storages with Formatting
     */
    @Overwrite(remap = false)
    private String formatStorage(CraftingStatusMenu.CraftingCpuListEntry cpu) {
        return NumberUtil.numberFormat(cpu.storage()).getString();
    }
}
