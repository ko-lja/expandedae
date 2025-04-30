package lu.kolja.expandedae.mixin;

import lu.kolja.expandedae.definition.ExpItems;
import lu.kolja.expandedae.storage.ExpandedCellHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import appeng.init.internal.InitStorageCells;
import net.minecraft.resources.ResourceLocation;

@Mixin(value = InitStorageCells.class, remap = false)
public class MixinInitStorageCells {
    @Shadow @Final private static ResourceLocation MODEL_CELL_CREATIVE;

    @Inject(method = "init", at = @At("HEAD"))
    private static void init(CallbackInfo ci) {
        StorageCells.addCellHandler(ExpandedCellHandler.INSTANCE);
        StorageCellModels.registerModel(ExpItems.ARTIFICIAL_UNIVERSE_CELL, MODEL_CELL_CREATIVE);
    }
}
