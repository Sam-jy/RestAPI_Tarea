package com.example.uthrestapi250;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.uthrestapi250.PersonAdapter;
import com.example.uthrestapi250.Config.Personas;
import com.example.uthrestapi250.Config.RestApiMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Personas> personList;
    PersonAdapter adapter;
    RequestQueue requestQueue;
    FloatingActionButton fabAdd;
    private static final int REQUEST_DETAIL = 100;
    private static final int REQUEST_CREATE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listview);
        fabAdd = findViewById(R.id.fabAdd);
        personList = new ArrayList<>();

        adapter = new PersonAdapter(this, personList);
        listView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        loadPersons();
        
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateActivity.class);
            startActivityForResult(intent, REQUEST_CREATE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            loadPersons();
        }
    }

    private void loadPersons() {
        personList.clear();
        
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, RestApiMethods.EndpointGetPersons,
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);

                            Personas person = new Personas();
                            person.setId(object.getString("id"));
                            person.setNombres(object.getString("nombres"));
                            person.setApellidos(object.getString("apellidos"));
                            person.setDireccion(object.getString("direccion"));
                            person.setTelefono(object.optString("telefono", null));
                            person.setFechanac(object.optString("fechanac", ""));
                            person.setFoto(object.optString("foto", ""));

                            personList.add(person);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        requestQueue.add(request);
    }
}

