package com.example.scanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {

    private Button btnLector,btnGuardar;
    @NotEmpty(message = "Requiere un alias")
    private EditText edtAlias;
    @NotEmpty(message = "Requiere el nombre del titular")
    private EditText edtTitular;
    @NotEmpty(message = "Requiere el número de tarjeta")
    private EditText edtTarjeta;
    @NotEmpty(message = "Requiere la fecha de vencimiento")
    private EditText edtFecha;
    //@Pattern(regex = "[0-9]")
    @NotEmpty(message = "Requiere el código de seguridad")
    private EditText edtCode;

    private static final int SCAN_RESULT = 100;
    private static final String TAG = "MAIN";
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        validator = new Validator(this);
        validator.setValidationListener(this);
        initViews();

        btnLector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CardIOActivity.class)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY,true)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV,false)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE,false);
                startActivityForResult(intent,SCAN_RESULT);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SCAN_RESULT){
            Log.d(TAG,"");
            if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)){
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                //txtNumero.setText(scanResult.getRedactedCardNumber());
                edtTarjeta.setText(scanResult.getRedactedCardNumber());
                Log.d(TAG,"Número de tarjeta: " +
                        "\n "+scanResult.cardNumber +
                        "\n"+scanResult.getCardType());

                if(scanResult.isExpiryValid()){
                    String mes = String.valueOf(scanResult.expiryMonth);
                    String anio = String.valueOf(scanResult.expiryYear);
                    //txtFecha.setText(mes + "/" +anio);
                    edtFecha.setText(mes +"/" +anio);
                }
            }
        }
    }

    @Override
    public void onValidationSucceeded() {

        mensaje();
        Toast.makeText(MainActivity.this, "Guardando información", Toast.LENGTH_SHORT).show();
        SharedPreferences sh = getSharedPreferences("DATOS", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh.edit();
        edit.putString("ALIAS",edtAlias.getText().toString());
        edit.putString("TITULAR",edtTitular.getText().toString());
        edit.putString("TARJETA",edtTarjeta.getText().toString());
        edit.putString("FECHA",edtFecha.getText().toString());
        edit.commit();
        //String codigo = edtCode.getText().toString();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }

    }

    public void initViews(){

        btnLector   = (Button)findViewById(R.id.btnLector);
        btnGuardar  = (Button)findViewById(R.id.btnGuardar);
        edtAlias    = (EditText)findViewById(R.id.edtAlias);
        edtTitular  = (EditText)findViewById(R.id.edtTitular);
        edtTarjeta  = (EditText)findViewById(R.id.edtNumero);
        edtFecha    = (EditText)findViewById(R.id.edtFecha);
        edtCode     = (EditText)findViewById(R.id.edtCode);
    }

    public void mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Su información fue guardada exitosamente.")
                .setTitle("Información guardada");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}