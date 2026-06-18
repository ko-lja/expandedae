package lu.kolja.expandedae.enums;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;

public enum ADDONS {
        EXT("extendedae"),
        MEGA("megacells"),
        APPFLUX("appflux"),
        ADV("advanced_ae"),
        APPMEK("appmek"),
        ARSENG("arseng"),
        APPEX("appex");

        private final String mod;
        ADDONS(String mod) { this.mod = mod; }
        public boolean isLoaded() { return ModList.get().isLoaded(mod); }
        public Component getUnavailableTooltip() {
            return Component.literal("Mod not installed!").withStyle(ChatFormatting.DARK_RED);
        }
    }
