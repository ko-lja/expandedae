package lu.kolja.expandedae.mixin.misc;

import appeng.client.gui.style.StyleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = StyleManager.class, remap = false)
public class MixinStyleManager {
    @ModifyVariable(
            method = "loadStyleDoc",
            at = @At(value = "LOAD", ordinal = 0),
            argsOnly = true
    )
    private static String loadStyleDocHooks(String path) {
        // Only modify Terminal-related paths
        if (path.contains("wireless_pattern_encoding_terminal.json")) {
            return "/screens/wtlib/modify_wireless_pattern_encoding_terminal.json";
        } else if (path.contains("pattern_encoding_terminal.json")) {
            return "/screens/terminals/modify_pattern_encoding_terminal.json";
        }
        return path;
    }
}
