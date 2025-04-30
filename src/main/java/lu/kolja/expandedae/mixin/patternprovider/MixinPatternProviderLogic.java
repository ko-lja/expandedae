package lu.kolja.expandedae.mixin.patternprovider;

import java.util.List;
import java.util.Objects;

import lu.kolja.expandedae.definition.ExpItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.stacks.KeyCounter;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.client.gui.me.common.FinishedJobToast;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.core.AEConfig;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.util.SearchInventoryEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@Mixin(value = PatternProviderLogic.class, remap = false, priority = 100)
public abstract class MixinPatternProviderLogic implements IUpgradeableObject {

    @Unique
    private IUpgradeInventory eae_$upgrades = UpgradeInventories.empty();

    @Final
    @Shadow
    private PatternProviderLogicHost host;

    @Final
    @Shadow
    private IManagedGridNode mainNode;

    @Unique
    private void eae_$onUpgradesChanged() {
        this.host.saveChanges();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.eae_$upgrades;
    }

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V",
            at = @At("TAIL")
    )
    private void eae_$initUpgrade(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        eae_$upgrades = UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1, this::eae_$onUpgradesChanged);
    }

    @Inject(
            method = "writeToNBT",
            at = @At("TAIL")
    )
    private void eae_$saveUpgrade(CompoundTag tag, CallbackInfo ci) {
        this.eae_$upgrades.writeToNBT(tag, "upgrades");
    }

    @Inject(
            method = "readFromNBT",
            at = @At("TAIL")
    )
    private void eae_$loadUpgrade(CompoundTag tag, CallbackInfo ci) {
        this.eae_$upgrades.readFromNBT(tag, "upgrades");
    }

    @Inject(
            method = "addDrops",
            at = @At("TAIL")
    )
    private void eae_$dropUpgrade(List<ItemStack> drops, CallbackInfo ci) {
        for (var is : this.eae_$upgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Inject(
            method = "clearContent",
            at = @At("TAIL")
    )
    private void eae_$clearUpgrade(CallbackInfo ci) {
        this.eae_$upgrades.clear();
    }

    @Inject(
            method = "pushPattern",
            at = @At("RETURN")
    )
    private void eae_$checkUpgrades(IPatternDetails patternDetails, KeyCounter[] inputHolder, CallbackInfoReturnable<Boolean> cir) {
        if (this.eae_$upgrades.isInstalled(ExpItems.AUTO_COMPLETE_CARD)) {
            List<ICraftingCPU> matchedCpus = eae_$getCraftingCpus().stream()
                    .filter(cpu -> cpu.getJobStatus() != null)
                    .filter(cpu -> eae_$getCraftingCpus().stream()
                            .map(ICraftingCPU::getJobStatus)
                            .filter(Objects::nonNull)
                            .anyMatch(
                                    job -> cpu.getJobStatus().progress() == job.progress()
                                            && cpu.getJobStatus().crafting().equals(job.crafting())
                            )
                    ).toList();

            matchedCpus.forEach(x -> {
                var what = x.getJobStatus().crafting().what();
                var whatId = what.getId();
                for (var outputs : patternDetails.getOutputs()) {
                    var outputWhatId = outputs.what().getId();
                    if (whatId == outputWhatId) {
                        x.cancelJob();
                        var minecraft = Minecraft.getInstance();
                        if (AEConfig.instance().isNotifyForFinishedCraftingJobs()
                            && !(minecraft.screen instanceof MEStorageScreen<?>)
                            && minecraft.player != null && expandedae$hasNotificationEnablingItem(minecraft.player)) {
                            minecraft.getToasts().addToast(new FinishedJobToast(what, 1)); //set to 1 since the card doesn't support more
                        }
                        return;
                    }
                }
            });
        }
    }
    @Unique
    private static boolean expandedae$hasNotificationEnablingItem(LocalPlayer player) {
        for (ItemStack stack : SearchInventoryEvent.getItems(player)) {
            if (!stack.isEmpty()
                    && stack.getItem() instanceof WirelessTerminalItem wirelessTerminal
                    // Should have some power
                    && wirelessTerminal.getAECurrentPower(stack) > 0
                    // Should be linked (we don't know if it's linked to the grid for which we get notifications)
                    && wirelessTerminal.getLinkedPosition(stack) != null) {
                return true;
            }
        }
        return false;
    }

    @Unique
    public List<ICraftingCPU> eae_$getCraftingCpus() {
        return mainNode.getGrid().getCraftingService().getCpus().stream().toList();
    }
}