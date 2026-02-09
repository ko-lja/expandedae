package lu.kolja.expandedae.mixin.cpu;

import appeng.api.networking.IGrid;
import appeng.api.stacks.AEKey;
import appeng.me.service.CraftingService;
import appeng.menu.AEBaseMenu;
import appeng.menu.me.crafting.CraftingCPUMenu;
import lu.kolja.expandedae.helper.cpu.IHighlightMenu;
import lu.kolja.expandedae.helper.patternprovider.IHighlightable;
import lu.kolja.expandedae.highlight.BlockHighlightHandler;
import lu.kolja.expandedae.network.ExpNetworkHandler;
import lu.kolja.expandedae.network.implementations.HighlightDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = CraftingCPUMenu.class, remap = false)
public abstract class MixinCraftingCPUMenu extends AEBaseMenu implements IHighlightMenu {
    @Shadow @Final private IGrid grid;

    public MixinCraftingCPUMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void init(CallbackInfo ci) {
        //registerClientAction("highlightStack", AEKey.class, this::eae$highlight);
    }

    @Unique
    public void eae$highlight(AEKey what) {
        if (isServerSide() && grid != null) {
            CraftingService service = (CraftingService) grid.getCraftingService();
            var patterns = service.getCraftingFor(what);
            Set<BlockEntity> bePositions = new HashSet<>();
            for (var pattern : patterns) {
                var provider = service.getProviders(pattern);
                for (var providerPos : provider) {
                    var be = providerPos instanceof IHighlightable highlightable ? highlightable.eae$getBlockPos() : null;
                    if (be != null) bePositions.add(be);
                }
            }
            if (!bePositions.isEmpty()) {
                for (var be : bePositions) {
                    var packet = new HighlightDataPacket(
                            be.getBlockPos(),
                            be.getLevel().dimension(),
                            BlockHighlightHandler.getTime(be.getBlockPos(), this.getPlayer().getOnPos())
                    );
                    ExpNetworkHandler.HANDLER.sendToClient(packet, (ServerPlayer) this.getPlayer());
                }
            }
        }
    }
}
