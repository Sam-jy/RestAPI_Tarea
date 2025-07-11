package com.example.uthrestapi250;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.uthrestapi250.Config.Personas;
import com.example.uthrestapi250.Config.RestApiMethods;

import org.json.JSONObject;

import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

    private EditText nombres, apellidos, direccion, telefono, fecha;
    private ImageView imageView;
    private Button btnUpdate, btnDelete, btnBack;
    private Personas persona;
    private RequestQueue requestQueue;
    private Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();
        loadPersonData();
        setupListeners();
    }

    private void initViews() {
        nombres = findViewById(R.id.nombres);
        apellidos = findViewById(R.id.apellidos);
        direccion = findViewById(R.id.direccion);
        telefono = findViewById(R.id.telefono);
        fecha = findViewById(R.id.fecha);
        imageView = findViewById(R.id.imageView);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadPersonData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("persona")) {
            persona = (Personas) intent.getSerializableExtra("persona");
            if (persona != null) {
                nombres.setText(persona.getNombres());
                apellidos.setText(persona.getApellidos());
                direccion.setText(persona.getDireccion());
                telefono.setText(persona.getTelefono());
                fecha.setText(persona.getFechanac());

                if (persona.getFoto() != null && !persona.getFoto().isEmpty()) {
                    try {
                        byte[] imageBytes = Base64.decode(persona.getFoto(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        imageView.setImageResource(R.mipmap.ic_launcher);
                    }
                } else {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
            }
        }
    }

    private void setupListeners() {
        fecha.setOnClickListener(view -> {
            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    DetailActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        fecha.setText(fechaSeleccionada);
                    },
                    año, mes, dia
            );
            datePickerDialog.show();
        });

        btnUpdate.setOnClickListener(v -> updatePerson());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        btnBack.setOnClickListener(v -> finish());
    }

    private void updatePerson() {
        if (persona == null) return;

        persona.setNombres(nombres.getText().toString());
        persona.setApellidos(apellidos.getText().toString());
        persona.setDireccion(direccion.getText().toString());
        persona.setTelefono(telefono.getText().toString());
        persona.setFechanac(fecha.getText().toString());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", persona.getId());
            jsonObject.put("nombres", persona.getNombres());
            jsonObject.put("apellidos", persona.getApellidos());
            jsonObject.put("direccion", persona.getDireccion());
            jsonObject.put("telefono", persona.getTelefono());
            jsonObject.put("foto", persona.getFoto() != null ? persona.getFoto() : "");

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, RestApiMethods.EndpointUpdatePerson,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String mensaje = response.getString("message");
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error al actualizar", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

            requestQueue.add(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar)
                .setMessage(R.string.confirmar_eliminar)
                .setPositiveButton(R.string.si, (dialog, which) -> deletePerson())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void deletePerson() {
        if (persona == null) return;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", persona.getId());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, RestApiMethods.EndpointDeletePerson,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String mensaje = response.getString("message");
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error al eliminar", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

            requestQueue.add(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
} 