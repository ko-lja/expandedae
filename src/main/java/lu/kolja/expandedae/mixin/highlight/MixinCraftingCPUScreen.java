package lu.kolja.expandedae.mixin.highlight;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.crafting.CraftingCPUScreen;
import appeng.client.gui.me.crafting.CraftingStatusTableRenderer;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.me.crafting.CraftingCPUMenu;
import lu.kolja.expandedae.client.render.ExpHighlightHandler;
import lu.kolja.expandedae.helper.misc.KeybindUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CraftingCPUScreen.class, remap = false)
public class MixinCraftingCPUScreen<T extends CraftingCPUMenu> extends AEBaseScreen<T> {

    @Shadow @Final private CraftingStatusTableRenderer table;

    public MixinCraftingCPUScreen(T menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        var hoveredStack = this.table.getHoveredStack();
        if (hoveredStack == null || !KeybindUtil.isShiftDown()) return false;
        var data = hoveredStack.stack().what().toTag();
        var pos = BlockPos.of(data.getLong("pos"));
        ExpHighlightHandler.highlight(
                pos,
                getPlayer().level().dimension(),
                System.currentTimeMillis() + 100000
        );
        return super.mouseClicked(xCoord, yCoord, btn);
    }
}
