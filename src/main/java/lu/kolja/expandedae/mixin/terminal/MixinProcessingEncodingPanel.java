package lu.kolja.expandedae.mixin.terminal;

import appeng.client.gui.WidgetContainer;
import appeng.client.gui.me.items.EncodingModePanel;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.me.items.ProcessingEncodingPanel;
import lu.kolja.expandedae.client.button.ModifyIconButton;
import lu.kolja.expandedae.helper.patternprovider.IPatternEncodingTerminalMenu;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static lu.kolja.expandedae.client.gui.widgets.ExpIcon.*;

@Mixin(value = ProcessingEncodingPanel.class, remap = false)
public abstract class MixinProcessingEncodingPanel extends EncodingModePanel {
    @Unique
    private ModifyIconButton eae$x2;
    @Unique
    private ModifyIconButton eae$x3;
    @Unique
    private ModifyIconButton eae$x8;
    @Unique
    private ModifyIconButton eae$div2;
    @Unique
    private ModifyIconButton eae$div3;
    @Unique
    private ModifyIconButton eae$div8;

    protected MixinProcessingEncodingPanel(PatternEncodingTermScreen<?> screen, WidgetContainer widgets) {
        super(screen, widgets);
    }

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void init(PatternEncodingTermScreen<?> screen, WidgetContainer widgets, CallbackInfo ci) {
        eae$x2 = new ModifyIconButton(b -> ((IPatternEncodingTerminalMenu) menu).eae$ModifyPattern(2),
                MULTIPLY_2,
                Component.translatable("gui.buttons.pattern.mult", 2),
                Component.translatable("gui.buttons.tooltips.pattern.mult", 2));
        eae$x3 = new ModifyIconButton(b -> ((IPatternEncodingTerminalMenu) menu).eae$ModifyPattern(3),
                MULTIPLY_3,
                Component.translatable("gui.buttons.pattern.mult", 3),
                Component.translatable("gui.buttons.tooltips.pattern.mult", 3));
        eae$x8 = new ModifyIconButton(b -> ((IPatternEncodingTerminalMenu) menu).eae$ModifyPattern(8),
                MULTIPLY_8,
                Component.translatable("gui.buttons.pattern.mult", 8),
                Component.translatable("gui.buttons.tooltips.pattern.mult", 8));
        eae$div2 = new ModifyIconButton(b -> ((IPatternEncodingTerminalMenu) menu).eae$ModifyPattern(-2),
                DIVISION_2,
                Component.translatable("gui.buttons.pattern.div", 2),
                Component.translatable("gui.buttons.tooltips.pattern.div", 2));
        eae$div3 = new ModifyIconButton(b -> ((IPatternEncodingTerminalMenu) menu).eae$ModifyPattern(-3),
                DIVISION_3,
                Component.translatable("gui.buttons.pattern.div", 3),
                Component.translatable("gui.buttons.tooltips.pattern.div", 3));
        eae$div8 = new ModifyIconButton(b -> ((IPatternEncodingTerminalMenu) menu).eae$ModifyPattern(-8),
                DIVISION_8,
                Component.translatable("gui.buttons.pattern.div", 8),
                Component.translatable("gui.buttons.tooltips.pattern.div", 8));

        widgets.add("mult2", eae$x2);
        widgets.add("mult3", eae$x3);
        widgets.add("mult8", eae$x8);
        widgets.add("div2", eae$div2);
        widgets.add("div3", eae$div3);
        widgets.add("div8", eae$div8);
    }

    @Inject(method = "setVisible", at = @At("TAIL"), remap = false)
    private void setVisibleHooks(boolean visible, CallbackInfo ci) {
        eae$x2.setVisibility(visible);
        eae$x3.setVisibility(visible);
        eae$x8.setVisibility(visible);
        eae$div2.setVisibility(visible);
        eae$div3.setVisibility(visible);
        eae$div8.setVisibility(visible);
    }
}
