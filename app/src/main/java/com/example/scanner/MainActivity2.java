package com.example.scanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class MainActivity2 extends AppCompatActivity implements Validator.ValidationListener{

    private final String message = "Todos los campos son obligatorios";
    @NotEmpty(message = message)
    private EditText etAlias, etCardName;
    @Length(min = 16,message = "Debe contener almenos 16 dígitos")
    private EditText etCardNumber;
    @Length(min = 3,message = "Debe contener 3 dígitos")
    private EditText etSecureCode;
    private String expiration = "Seleccione";
    private Boolean expirationSelected = false;
    private Button btnExpiration,btnScanCard;

    private static final int SCAN_RESULT = 100;
    private static final String TAG = "MAIN";

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
        btnScanCard = findViewById(R.id.btnScanCard);



        btnScanCard.setOnClickListener(button ->{

            Intent intent = new Intent(getApplicationContext(), CardIOActivity.class)
                    .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY,true)
                    .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV,false)
                    .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE,false);
            startActivityForResult(intent,SCAN_RESULT);

                });

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SCAN_RESULT){

             if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)){
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                 etCardNumber.setText(scanResult.cardNumber);
                Log.d(TAG,"Número de tarjeta: " +scanResult.getRedactedCardNumber() +
                        "\n"+scanResult.getCardType());

                if(scanResult.isExpiryValid()){
                    String mes = String.valueOf(scanResult.expiryMonth);
                    String anio = String.valueOf(scanResult.expiryYear);
                    //.setText(mes +"/" +anio);
                    btnExpiration.setText(mes + "/" + anio);
                }
            }
        }
    }

    @Override
    public void onValidationSucceeded() {

        mensaje();
        SharedPreferences sh = getSharedPreferences("DATOS", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        edit.putString("ALIAS",etAlias.getText().toString());
        edit.putString("TITULAR",etCardName.getText().toString());
        edit.putString("TARJETA",etCardNumber.getText().toString());
        edit.putString("FECHA",btnExpiration.getText().toString());
        edit.commit();

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

    public void mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Su información fue guardada exitosamente.")
                .setTitle("Información guardada");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}