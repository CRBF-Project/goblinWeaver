package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.ValueObject;

import java.util.Iterator;

public class AdoptionLifeSpan extends AbstractAddedValue<Double> {

    private static final long MS_PER_DAY = 86_400_000L;

    public AdoptionLifeSpan(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.ADOPTION_LIFESPAN;
    }

    @Override
    public Double stringToValue(String jsonString) {
        return Double.valueOf(jsonString);
    }

    @Override
    public String valueToString(Double value) {
        return String.valueOf(value);
    }

    @Override
    public void computeValue() {
        this.value = fillAdoptionLifespan(nodeId);
    }

    private double fillAdoptionLifespan(String gav) {
        String[] parts = gav.split(":");
        if (parts.length != 3)
            return 0.0;

        String artifactGa = parts[0] + ":" + parts[1];
        String version = parts[2];

        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        InternGraph graph = gdb.executeQuery(
                gdb.getQueryDictionary().getReleaseAdoptionLifespan(artifactGa, version));

        Iterator<ValueObject> iterator = graph.getGraphValues().iterator();
        if (iterator.hasNext()) {
            String val = iterator.next().getValue();
            if (val == null || val.equalsIgnoreCase("null") || val.isBlank())
                return 0.0;
            try {
                return Double.parseDouble(val) / MS_PER_DAY;
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}