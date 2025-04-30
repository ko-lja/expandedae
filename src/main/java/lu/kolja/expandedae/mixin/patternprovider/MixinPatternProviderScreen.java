package lu.kolja.expandedae.mixin.patternprovider;

import java.util.ArrayList;
import java.util.List;

import lu.kolja.expandedae.client.gui.widgets.ExpActionButton;
import lu.kolja.expandedae.client.gui.widgets.ExpActionItems;
import lu.kolja.expandedae.helper.IPatternProvider;
import lu.kolja.expandedae.helper.IUpgradableMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import appeng.api.upgrades.Upgrades;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Mixin(value = PatternProviderScreen.class, remap = false)
public abstract class MixinPatternProviderScreen<C extends PatternProviderMenu> extends AEBaseScreen<C> {
    @Unique
    private ExpActionButton modifyPatterns;

    private MixinPatternProviderScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void init(PatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        this.widgets.add("upgrades", new UpgradesPanel(
                menu.getSlots(SlotSemantics.UPGRADE),
                this::eae_$getCompatibleUpgrades));
        this.modifyPatterns = new ExpActionButton(ExpActionItems.MODIFY_PATTERNS, act -> ((IPatternProvider) menu).modifyPatterns(
                ((AEBaseScreen<?>) Minecraft.getInstance().screen).isHandlingRightClick()
        ));
        this.addToLeftToolbar(this.modifyPatterns);
        if (((IUpgradableMenu) menu).getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, ((IUpgradableMenu) menu).getToolbox().getName()));
        }
    }

    @Unique
    private List<Component> eae_$getCompatibleUpgrades() {
        var list = new ArrayList<Component>();
        list.add(GuiText.CompatibleUpgrades.text());
        list.addAll(Upgrades.getTooltipLinesForMachine(((IUpgradableMenu) menu).getUpgrades().getUpgradableItem()));
        return list;
    }
}