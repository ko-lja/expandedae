package lu.kolja.expandedae.mixin.highlight;

import appeng.api.stacks.AEKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.crafting.AbstractTableRenderer;
import appeng.client.gui.me.crafting.CraftingCPUScreen;
import appeng.client.gui.me.crafting.CraftingStatusTableRenderer;
import appeng.menu.me.crafting.CraftingCPUMenu;
import appeng.menu.me.crafting.CraftingStatusEntry;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import java.util.List;
import lu.kolja.expandedae.helper.misc.ICraftingCPUMenu;
import lu.kolja.expandedae.helper.misc.KeybindUtil;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CraftingStatusTableRenderer.class, remap = false)
public abstract class MixinCraftingStatusTableRenderer extends AbstractTableRenderer<CraftingStatusEntry> {
    public MixinCraftingStatusTableRenderer(AEBaseScreen<?> screen, int x, int y, int rows) {
        super(screen, x, y, rows);
    }

    @Inject(
            method = "getEntryStack(Lappeng/menu/me/crafting/CraftingStatusEntry;)Lappeng/api/stacks/AEKey;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getEntryStack(CallbackInfoReturnable<AEKey> cir) {
        var key = cir.getReturnValue();
        var writtenKey = ((ICraftingCPUMenu) (((CraftingCPUScreen<? extends CraftingCPUMenu>) this.screen).getMenu())).expandedae$writeToKey(key);
        cir.setReturnValue(writtenKey);
    }

    @Inject(
            method = "getEntryTooltip(Lappeng/menu/me/crafting/CraftingStatusEntry;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getEntryTooltip(CraftingStatusEntry entry, CallbackInfoReturnable<List<Component>> cir) {
        if (!KeybindUtil.isShiftDown()) return;
        var lines = cir.getReturnValue();
        lines.add(Component.translatable("gui.expandedae.shift_to_highlight"));
        cir.setReturnValue(lines);
    }
}
