package com.example.ecrop;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class EcropModel extends AppCompatActivity {

    private Interpreter tflite;
    private EditText inputN, inputP, inputK, inputTemperature, inputHumidity, inputPH, inputRainfall;
    private TextView outputRecommendation;
    private String[] crops = {"rice", "maize", "chickpea", "kidneybeans", "pigeonpeas",
            "mothbeans", "mungbean", "blackgram", "lentil", "pomegranate",
            "banana", "mango", "grapes", "watermelon", "muskmelon", "apple",
            "orange", "papaya", "coconut", "cotton", "jute", "coffee"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecrop_model);

        inputN = findViewById(R.id.input_n);
        inputP = findViewById(R.id.input_p);
        inputK = findViewById(R.id.input_k);
        inputTemperature = findViewById(R.id.input_temperature);
        inputHumidity = findViewById(R.id.input_humidity);
        inputPH = findViewById(R.id.input_ph);
        inputRainfall = findViewById(R.id.input_rainfall);
        Button buttonGetRecommendation = findViewById(R.id.button_get_recommendation);
        outputRecommendation = findViewById(R.id.output_recommendation);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception e) {
            e.printStackTrace();
            outputRecommendation.setText("Failed to load model.");
        }

        buttonGetRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendCrop();
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetManager assetManager = getAssets();
        try (AssetFileDescriptor fileDescriptor = assetManager.openFd("model.tflite")) {
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private void recommendCrop() {
        if (tflite == null) {
            outputRecommendation.setText("Model is not loaded.");
            return;
        }

        try {
            float N = Float.parseFloat(inputN.getText().toString());
            float P = Float.parseFloat(inputP.getText().toString());
            float K = Float.parseFloat(inputK.getText().toString());
            float temperature = Float.parseFloat(inputTemperature.getText().toString());
            float humidity = Float.parseFloat(inputHumidity.getText().toString());
            float ph = Float.parseFloat(inputPH.getText().toString());
            float rainfall = Float.parseFloat(inputRainfall.getText().toString());
                        if (N < 0 || N > 100 || P < 1 || P > 150 || K < 1 || K > 210 ||
                    temperature < 1 || temperature > 45 || humidity < 1 || humidity > 100 ||
                    ph < 1 || ph > 7 || rainfall < 1 || rainfall > 250) {
                outputRecommendation.setText("Input values out of range. Please check the values.");
                return;
            }

            float[][] input = {{N, P, K, temperature, humidity, ph, rainfall}};
            float[][] output = new float[1][22];

            tflite.run(input, output);

            int predictedIndex = argMax(output[0]);
            String predictedCrop = crops[predictedIndex];

            outputRecommendation.setText("Recommended Crop: " + predictedCrop);
        } catch (NumberFormatException e) {
            outputRecommendation.setText("Invalid input values.");
        }
    }

    private int argMax(float[] array) {
        int maxIndex = -1;
        float maxValue = Float.MIN_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
