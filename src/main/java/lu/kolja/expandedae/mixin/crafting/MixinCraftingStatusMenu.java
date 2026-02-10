package lu.kolja.expandedae.mixin.crafting;

import appeng.menu.ISubMenu;
import appeng.menu.me.crafting.CraftingCPUMenu;
import appeng.menu.me.crafting.CraftingStatusMenu;
import lu.kolja.expandedae.helper.misc.ICancellable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingStatusMenu.class, remap = false)
public abstract class MixinCraftingStatusMenu extends CraftingCPUMenu implements ISubMenu, ICancellable {

    @Unique
    private final static String ACTION_CANCEL_ALL = "cancelAll";

    private MixinCraftingStatusMenu(MenuType<?> menuType, int id, Inventory ip, Object te) {
        super(menuType, id, ip, te);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void expandedae$init(CallbackInfo ci) {
        this.registerClientAction(ACTION_CANCEL_ALL, this::expandedae$cancelAll);
    }

    /**
     * Cancel all currently running jobs
     */
    @Unique
    @Override
    public void expandedae$cancelAll() {
        if (isClientSide()) {
            sendClientAction(ACTION_CANCEL_ALL);
            return;
        }
        var grid = this.getActionHost().getActionableNode().getGrid();
        if (grid != null) {
            for (var cpu : grid.getCraftingService().getCpus()) {
                cpu.cancelJob();
            }
        }
    }
}
