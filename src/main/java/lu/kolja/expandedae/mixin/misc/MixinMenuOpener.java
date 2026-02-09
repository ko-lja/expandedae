package lu.kolja.expandedae.mixin.misc;

import appeng.menu.MenuOpener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MenuOpener.class, remap = false)
public class MixinMenuOpener {
    
    @Inject(
            method = "open",
            at = @At("HEAD")
    )
    private static void beforeOpen(MenuType<?> type, Player player, Object menuHostLocator, CallbackInfo ci) {
        lu.kolja.expandedae.Expandedae.LOGGER.info("[EAE DEBUG] MenuOpener.open() called for MenuType: {} Player: {}", 
                type, player.getName().getString());
    }
    
    @Inject(
            method = "open",
            at = @At("RETURN")
    )
    private static void afterOpen(MenuType<?> type, Player player, Object menuHostLocator, CallbackInfo ci) {
        lu.kolja.expandedae.Expandedae.LOGGER.info("[EAE DEBUG] MenuOpener.open() completed for MenuType: {}", type);
    }
    
    @Inject(
            method = "sendPacketToClient",
            at = @At("HEAD")
    )
    private static void beforeSendPacket(ServerPlayer player, Object sentData, CallbackInfo ci) {
        lu.kolja.expandedae.Expandedae.LOGGER.info("[EAE DEBUG] MenuOpener.sendPacketToClient() called with data: {}", 
                sentData != null ? sentData.getClass().getSimpleName() : "null");
    }
}
