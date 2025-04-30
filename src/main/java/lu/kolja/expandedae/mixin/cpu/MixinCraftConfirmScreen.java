package lu.kolja.expandedae.mixin.cpu;

import lu.kolja.expandedae.helper.NumberUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.crafting.CraftConfirmScreen;
import appeng.client.gui.me.crafting.CraftConfirmTableRenderer;
import appeng.client.gui.me.crafting.CraftErrorScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.localization.GuiText;
import appeng.menu.me.crafting.CraftConfirmMenu;
import appeng.menu.me.crafting.CraftingPlanSummary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Mixin(value = CraftConfirmScreen.class, remap = false)
public abstract class MixinCraftConfirmScreen extends AEBaseScreen<CraftConfirmMenu> {
    @Shadow @Final private Scrollbar scrollbar;
    @Shadow @Final private Button selectCPU;

    @Shadow protected abstract Component getNextCpuButtonLabel();

    @Shadow @Final private Button start;

    @Shadow @Final private CraftConfirmTableRenderer table;

    protected MixinCraftConfirmScreen(CraftConfirmMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    /**
     * @author Kolja
     * @reason Format bytes
     */
    @Overwrite
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        var errorResult = menu.submitError.result();
        if (errorResult != null && errorResult.errorCode() != null) {
            switchToScreen(new CraftErrorScreen((CraftConfirmScreen) Minecraft.getInstance().screen, errorResult.errorCode(), errorResult.errorDetail()));
            return;
        }

        this.selectCPU.setMessage(getNextCpuButtonLabel());

        CraftingPlanSummary plan = menu.getPlan();
        boolean planIsStartable = plan != null && !plan.isSimulation();
        this.start.active = !this.menu.hasNoCPU() && planIsStartable;
        this.selectCPU.active = planIsStartable;

        // Show additional status about the selected CPU and plan when the planning is done
        Component planDetails = GuiText.CalculatingWait.text();
        Component cpuDetails = Component.empty();
        if (plan != null) {

            //String byteUsed = NumberFormat.getInstance().format(plan.getUsedBytes());
            String byteUsed = NumberUtil.formatLong(plan.getUsedBytes());
            planDetails = GuiText.BytesUsed.text(byteUsed);

            if (plan.isSimulation()) {
                cpuDetails = GuiText.PartialPlan.text();
            } else if (this.menu.getCpuAvailableBytes() > 0) {
                cpuDetails = GuiText.ConfirmCraftCpuStatus.text(
                        this.menu.getCpuAvailableBytes(),
                        this.menu.getCpuCoProcessors());
            } else {
                cpuDetails = GuiText.ConfirmCraftNoCpu.text();
            }
        }

        setTextContent(TEXT_ID_DIALOG_TITLE, GuiText.CraftingPlan.text(planDetails));
        setTextContent("cpu_status", cpuDetails);

        final int size = plan != null ? plan.getEntries().size() : 0;
        scrollbar.setRange(0, this.table.getScrollableRows(size), 1);
    }
}
