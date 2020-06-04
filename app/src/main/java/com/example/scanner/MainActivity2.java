package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.Calendar;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements Validator.ValidationListener{

    private final String message = "Todos los campos son obligatorios";
    @NotEmpty(message = message)
    private EditText etAlias, etCardName;
    @Length(min = 16)
    private EditText etCardNumber;
    @Length(min = 3)
    private EditText etSecureCode;

    private String expiration = "Seleccione";
    private Boolean expirationSelected = false;
    private Button btnExpiration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Validator validator = new Validator(this);
        validator.setValidationListener(this);

        etAlias = findViewById(R.id.etAlias);
        etCardName = findViewById(R.id.etCardName);
        etCardNumber = findViewById(R.id.etCardNumber);
        etSecureCode = findViewById(R.id.etSecureCode);

        btnExpiration = findViewById(R.id.btnCardExp);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
            expiration = month + "/" + year;
            expirationSelected = true;
            btnExpiration.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            btnExpiration.setText(expiration);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        btnExpiration.setOnClickListener(button ->
                datePicker.show()
        );
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(button ->{
            validator.validate();
        });
    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else if (view instanceof Spinner) {
                ((TextView) ((Spinner) view).getSelectedView()).setError(message);
            }
        }
        if (!expirationSelected){
            btnExpiration.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_alert,0);
        }
    }
}