package com.example.project;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class ModelActivity extends AppCompatActivity {
    private EditText editTextAge;
    private Spinner spinnerFeelingSad, spinnerIrritable, spinnerSleepTrouble,
            spinnerConcentrationProblems, spinnerEatingIssues, spinnerAnxious,
            spinnerGuilt, spinnerBondingProblems, spinnerSuicidal;
    private Button buttonNavigateModel;

    private int consecutiveModeratelyDepressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);

        initializeViews();
        setupSpinners();
        setButtonClickListeners();
    }

    private void initializeViews() {
        editTextAge = findViewById(R.id.editTextAge);
        spinnerFeelingSad = findViewById(R.id.spinnerFeelingSad);
        spinnerIrritable = findViewById(R.id.spinnerIrritable);
        spinnerSleepTrouble = findViewById(R.id.spinnerSleepTrouble);
        spinnerConcentrationProblems = findViewById(R.id.spinnerConcentrationProblems);
        spinnerEatingIssues = findViewById(R.id.spinnerEatingIssues);
        spinnerAnxious = findViewById(R.id.spinnerAnxious);
        spinnerGuilt = findViewById(R.id.spinnerGuilt);
        spinnerBondingProblems = findViewById(R.id.spinnerBondingProblems);
        spinnerSuicidal = findViewById(R.id.spinnerSuicideAttempt);

        buttonNavigateModel = findViewById(R.id.button_navigate_model);
    }

    private void setupSpinners() {
        setupSpinner(spinnerFeelingSad, R.array.feeling_sad_options);
        setupSpinner(spinnerIrritable, R.array.irritable_options);
        setupSpinner(spinnerSleepTrouble, R.array.sleep_trouble_options);
        setupSpinner(spinnerConcentrationProblems, R.array.concentration_problems_options);
        setupSpinner(spinnerEatingIssues, R.array.eating_issues_options);
        setupSpinner(spinnerAnxious, R.array.anxious_options);
        setupSpinner(spinnerGuilt, R.array.guilt_options);
        setupSpinner(spinnerBondingProblems, R.array.bonding_problems_options);
        setupSpinner(spinnerSuicidal, R.array.suicide_attempt_options);
    }

    private void setButtonClickListeners() {
        buttonNavigateModel.setOnClickListener(v -> handleSpinnerSelections());
    }

    private void handleSpinnerSelections() {
        float[] inputValues = new float[10];
        inputValues[0] = getSelectedSpinnerValue(spinnerFeelingSad);
        inputValues[1] = getSelectedSpinnerValue(spinnerIrritable);
        inputValues[2] = getSelectedSpinnerValue(spinnerSleepTrouble);
        inputValues[3] = getSelectedSpinnerValue(spinnerConcentrationProblems);
        inputValues[4] = getSelectedSpinnerValue(spinnerEatingIssues);
        inputValues[5] = getSelectedSpinnerValue(spinnerAnxious);
        inputValues[6] = getSelectedSpinnerValue(spinnerGuilt);
        inputValues[7] = getSelectedSpinnerValue(spinnerBondingProblems);
        inputValues[8] = getSelectedSpinnerValue(spinnerSuicidal);

        String ageInput = editTextAge.getText().toString();
        if (!ageInput.isEmpty()) {
            inputValues[9] = Float.parseFloat(ageInput);
        } else {
            Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 10}, DataType.FLOAT32);
            inputFeature0.loadArray(inputValues);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] outputValues = outputFeature0.getFloatArray();

            // Display results
            displayResults(outputValues);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            Toast.makeText(this, "Model inference failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private float getSelectedSpinnerValue(Spinner spinner) {
        return (float) spinner.getSelectedItemPosition();
    }

    private void displayResults(float[] outputValues) {
        StringBuilder results = new StringBuilder("Model Output:\n\n");
        for (int i = 0; i < outputValues.length; i++) {
            results.append("Output ").append(i).append(": ").append(outputValues[i]).append("\n");
        }

        float outputValue = outputValues[0];
        String depressionStatus = getDepressionStatus(outputValue);
        results.append("\nDepression Status: ").append(depressionStatus);

        if (outputValue == 2) {
            consecutiveModeratelyDepressed++;
        } else if (outputValue == 1 && consecutiveModeratelyDepressed >= 2) {
            consecutiveModeratelyDepressed = 0; // Reset counter after showing toast
            Toast.makeText(this, "Depression status has improved to Not Depressed", Toast.LENGTH_LONG).show();
        } else {
            consecutiveModeratelyDepressed = 0; // Reset counter if output is not 2 or 1
        }

        Toast.makeText(this, depressionStatus, Toast.LENGTH_LONG).show();
    }

    private String getDepressionStatus(float outputValue) {
        switch ((int) outputValue) {
            case 0:
                return "Depressed";
            case 1:
                return "Not Depressed";
            case 2:
                return "Moderately Depressed";
            default:
                return "Unknown";
        }
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
