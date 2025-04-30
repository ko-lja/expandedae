package lu.kolja.expandedae.storage;

import java.util.*;

import appeng.api.config.IncludeExclude;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.AEConfig;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import appeng.items.storage.StorageCellTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class ExpandedCellHandler implements ICellHandler {
    public static final ExpandedCellHandler INSTANCE = new ExpandedCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return ExpandedCellInventory.isCell(is);
    }

    @Override
    public ExpandedCellInventory getCellInventory(ItemStack is, ISaveProvider container) {
        return ExpandedCellInventory.createInventory(is, container);
    }

    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);
        if (handler == null) {
            return;
        }

        lines.add(Tooltips.bytesUsed(handler.getUsedBytes(), handler.getTotalBytes()));
        lines.add(Tooltips.typesUsed(handler.getStoredItemTypes(), handler.getTotalItemTypes()));

        if (handler.isPreformatted()) {
            var list = (handler.getPartitionListMode() == IncludeExclude.WHITELIST ? GuiText.Included
                    : GuiText.Excluded)
                    .text();

            if (handler.isFuzzy()) {
                lines.add(GuiText.Partitioned.withSuffix(" - ").append(list).append(" ").append(GuiText.Fuzzy.text()));
            } else {
                lines.add(
                        GuiText.Partitioned.withSuffix(" - ").append(list).append(" ").append(GuiText.Precise.text()));
            }
        }
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack is) {
        var handler = getCellInventory(is, null);
        if (handler == null) {
            return Optional.empty();
        }

        var upgradeStacks = new ArrayList<ItemStack>();
        if (AEConfig.instance().isTooltipShowCellUpgrades()) {
            for (var upgrade : handler.getUpgradesInventory()) {
                upgradeStacks.add(upgrade);
            }
        }

        // Find items with the highest stored amount
        boolean hasMoreContent;
        List<GenericStack> content;
        if (AEConfig.instance().isTooltipShowCellContent()) {
            content = new ArrayList<>();

            var maxCountShown = AEConfig.instance().getTooltipMaxCellContentShown();

            var availableStacks = handler.getAvailableStacks();
            for (var entry : availableStacks) {
                content.add(new GenericStack(entry.getKey(), entry.getLongValue()));
            }

            // Fill up with stacks from the filter if it's not inverted
            if (content.size() < maxCountShown && handler.getPartitionListMode() == IncludeExclude.WHITELIST) {
                var config = handler.getConfigInventory();
                for (int i = 0; i < config.size(); i++) {
                    var what = config.getKey(i);
                    if (what != null) {
                        // Don't add it twice
                        if (availableStacks.get(what) <= 0) {
                            content.add(new GenericStack(what, 0));
                        }
                    }
                    if (content.size() > maxCountShown) {
                        break; // Don't need to add filters beyond 6 (to determine if it has more than 5 below)
                    }
                }
            }

            // Sort by amount descending
            content.sort(Comparator.comparingLong(GenericStack::amount).reversed());

            hasMoreContent = content.size() > maxCountShown;
            if (content.size() > maxCountShown) {
                content.subList(maxCountShown, content.size()).clear();
            }
        } else {
            hasMoreContent = false;
            content = Collections.emptyList();
        }

        return Optional.of(new StorageCellTooltipComponent(
                upgradeStacks,
                content,
                hasMoreContent,
                true));
    }
}
