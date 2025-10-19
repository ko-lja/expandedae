package lu.kolja.expandedae.mixin.highlight;

import appeng.api.networking.IGrid;
import appeng.api.stacks.AEKey;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.menu.me.crafting.CraftingCPUMenu;
import lu.kolja.expandedae.helper.misc.ICraftingCPUMenu;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = CraftingCPUMenu.class, remap = false)
public abstract class MixinCraftingCPUMenu implements ICraftingCPUMenu {
    @Shadow abstract IGrid getGrid();

    @Shadow @Final private IGrid grid;

    @Unique
    @Nullable
    @Override
    public AEKey expandedae$writeToKey(AEKey key) {
        for (var machine : this.grid.getActiveMachines(PatternProviderBlockEntity.class)) {
            for (var inputs : machine.getLogic().getAvailablePatterns()) {
                if (inputs.getOutputs()[0].what() != key) continue;
                var tag = key.toTag();
                tag.putLong("pos", machine.getBlockPos().asLong());
                return AEKey.fromTagGeneric(tag);
            }
        }
        return key;
    }
}
