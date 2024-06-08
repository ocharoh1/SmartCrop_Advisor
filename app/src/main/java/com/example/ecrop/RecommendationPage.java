package com.example.ecrop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class RecommendationPage extends AppCompatActivity {

    private Interpreter tflite;
    private EditText etN, etP, etK, etTemperature, etHumidity, etPh, etRainfall;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendationpage);

        // Initialize views
        etN = findViewById(R.id.etN);
        etP = findViewById(R.id.etP);
        etK = findViewById(R.id.etK);
        etTemperature = findViewById(R.id.etTemperature);
        etHumidity = findViewById(R.id.etHumidity);
        etPh = findViewById(R.id.etPh);
        etRainfall = findViewById(R.id.etRainfall);
        Button btnPredict = findViewById(R.id.btnPredict);
        tvResult = findViewById(R.id.tvResult);

        // Load the model
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set button click listener
        btnPredict.setOnClickListener(v -> {
            float[] inputValues = {
                    Float.parseFloat(etN.getText().toString()),
                    Float.parseFloat(etP.getText().toString()),
                    Float.parseFloat(etK.getText().toString()),
                    Float.parseFloat(etTemperature.getText().toString()),
                    Float.parseFloat(etHumidity.getText().toString()),
                    Float.parseFloat(etPh.getText().toString()),
                    Float.parseFloat(etRainfall.getText().toString())
            };

            float[] outputValues = new float[22]; // Assuming 22 crop classes

            tflite.run(inputValues, outputValues);

            int predictedIndex = getMaxIndex(outputValues);
            String[] crops = {"rice", "maize", "chickpea", "kidneybeans", "pigeonpeas",
                    "mothbeans", "mungbean", "blackgram", "lentil", "pomegranate",
                    "banana", "mango", "grapes", "watermelon", "muskmelon", "apple",
                    "orange", "papaya", "coconut", "cotton", "jute", "coffee"};
            String predictedCrop = crops[predictedIndex];

            tvResult.setText("The recommended crop is: " + predictedCrop);
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(getAssets().openFd("crop.tflite").getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = getAssets().openFd("crop.tflite").getStartOffset();
        long declaredLength = getAssets().openFd("crop.tflite").getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private int getMaxIndex(float[] outputValues) {
        int maxIndex = 0;
        for (int i = 1; i < outputValues.length; i++) {
            if (outputValues[i] > outputValues[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
