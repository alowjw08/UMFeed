package com.example.umfeed.models.recipe;


public class NutritionFilter {
    private Range carbRange;
    private Range proteinRange;
    private Range fatRange;
    private final boolean matchAll;

    public NutritionFilter(Range carbRange, Range proteinRange, Range fatRange, boolean matchAll) {
        this.carbRange = carbRange;
        this.proteinRange = proteinRange;
        this.fatRange = fatRange;
        this.matchAll = matchAll;
    }

    public Range getCarbRange() { return carbRange; }
    public Range getProteinRange() { return proteinRange; }
    public Range getFatRange() { return fatRange; }

    public boolean isMatchAll() {
        return matchAll;
    }
}
