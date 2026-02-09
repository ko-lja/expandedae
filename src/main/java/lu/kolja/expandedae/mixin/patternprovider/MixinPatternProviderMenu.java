package lu.kolja.expandedae.mixin.patternprovider;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.PatternProviderMenu;
import lu.kolja.expandedae.definition.ExpSettings;
import lu.kolja.expandedae.enums.BlockingMode;
import lu.kolja.expandedae.helper.base.IUpgradableMenu;
import lu.kolja.expandedae.helper.misc.PatternHelper;
import lu.kolja.expandedae.helper.patternprovider.IPatternProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PatternProviderMenu.class, remap = false)
public abstract class MixinPatternProviderMenu extends AEBaseMenu implements IUpgradableMenu, IPatternProvider {
    @Final
    @Shadow(remap = false)
    protected PatternProviderLogic logic;

    @Unique
    private ToolboxMenu eae_$toolbox;

    @Unique
    @GuiSync(8)
    private BlockingMode eae$blockingMode = BlockingMode.DEFAULT;

    public MixinPatternProviderMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V",
            at = @At("TAIL"),
            remap = false
    )
    private void initToolbox(MenuType<?> menuType, int id, Inventory playerInventory, PatternProviderLogicHost host, CallbackInfo ci) {
        this.eae_$toolbox = new ToolboxMenu(this);
        this.setupUpgrades(((IUpgradeableObject) host).getUpgrades());
        this.registerClientAction("modifyPatterns", Integer.class, this::expandedae$modifyPatterns);
    }

    @Unique
    @Override
    public void expandedae$modifyPatterns(Integer mult) {
        if (this.isClientSide()) this.sendClientAction("modifyPatterns", mult);
        else {
            for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
                slot.set(PatternHelper.modifyPatterns(slot.getItem(), mult, this.getPlayer().level()));
            }
        }
    }

    @Override
    public ToolboxMenu expandedae$getToolbox() {
        return this.eae_$toolbox;
    }

    @Override
    public IUpgradeInventory expandedae$getUpgrades() {
        return ((IUpgradeableObject) this.logic).getUpgrades();
    }

    @Override
    public boolean expandedae$hasUpgrade(ItemLike upgradeCard) {
        return expandedae$getUpgrades().isInstalled(upgradeCard);
    }

    @Inject(
            method = "broadcastChanges",
            at = @At("HEAD"),
            remap = true
    )
    public void tickToolbox(CallbackInfo ci) {
        this.eae_$toolbox.tick();
        if (this.isServerSide()) {
            var blockingMode = logic.getConfigManager().getSetting(ExpSettings.BLOCKING_MODE);
            if (blockingMode != null) {
                eae$blockingMode = blockingMode;
            }
        }
    }

    @Override
    public BlockingMode expandedae$getBlockingMode() {
        return eae$blockingMode;
    }
}