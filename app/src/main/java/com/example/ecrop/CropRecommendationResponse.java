package com.example.ecrop;

import java.util.List;

public class CropRecommendationResponse {
    private List<Double> predicted_values;

    public List<Double> getPredictedValues() {
        return predicted_values;
    }

    public void setPredictedValues(List<Double> predicted_values) {
        this.predicted_values = predicted_values;
    }
}
