package io.restx.pojos;

import javax.validation.constraints.NotNull;

/**
 * @author fcamblor
 */
public class SimpleAggregatablePOJO {
    @NotNull
    private String label;

    public SimpleAggregatablePOJO(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
