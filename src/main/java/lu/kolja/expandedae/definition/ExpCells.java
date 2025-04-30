package lu.kolja.expandedae.definition;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.resources.ResourceLocation;

public class ExpCells {
    public static final ResourceLocation ARTIFICIAL_UNIVERSE_CELL = new ResourceLocation("ae2:block/drive/cells/creative_cell");
    public static final ResourceLocation[] MODELS;
    public static Collection<ResourceLocation> getModels() {
        return Arrays.asList(MODELS);
    }

    private ExpCells() {
    }
    public static void init() {
       // StorageCellModels.registerModel(ExpItems.ARTIFICIAL_UNIVERSE_CELL, ARTIFICIAL_UNIVERSE_CELL);
    }

    static {
        MODELS = new ResourceLocation[]{ARTIFICIAL_UNIVERSE_CELL};
    }
}
