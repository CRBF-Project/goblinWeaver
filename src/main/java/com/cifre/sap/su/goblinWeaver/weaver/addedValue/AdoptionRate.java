package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.goblinWeaver.graphEntities.ValueObject;

import java.util.Iterator;

public class AdoptionRate extends AbstractAddedValue<Double> {

    public AdoptionRate(String nodeId) {
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.ADOPTION_RATE;
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
        this.value = fillAdoptionRate(nodeId);
    }

    private double fillAdoptionRate(String gav) {
        String[] parts = gav.split(":");
        if (parts.length != 3) {
            return 0.0;
        }
        String artifactGa = parts[0] + ":" + parts[1];
        String version = parts[2];

        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        InternGraph graph = gdb.executeQuery(
                gdb.getQueryDictionary().getReleaseAdoptionRate(artifactGa, version));

        Iterator<ValueObject> iterator = graph.getGraphValues().iterator();
        if (iterator.hasNext()) {
            try {
                return Double.parseDouble(iterator.next().getValue());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}