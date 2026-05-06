package com.example.temas;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {
    private WorkersDatabaseHelper databaseHelper;
    private LinearLayout workersContainer;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new WorkersDatabaseHelper(this);
        buildScreen();
        loadWorkers();
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        root.setBackgroundColor(0xFFF7F9FC);

        TextView title = new TextView(this);
        title.setText("Trabajadores");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(0xFF263238);
        root.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        Button registerButton = new Button(this);
        registerButton.setText("Registrar trabajador");
        registerButton.setAllCaps(false);
        registerButton.setOnClickListener(view -> showWorkerForm());
        root.addView(registerButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        emptyMessage = new TextView(this);
        emptyMessage.setText("Todavía no hay trabajadores registrados.");
        emptyMessage.setTextSize(16);
        emptyMessage.setGravity(Gravity.CENTER);
        emptyMessage.setPadding(0, 40, 0, 40);
        root.addView(emptyMessage, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ScrollView scrollView = new ScrollView(this);
        workersContainer = new LinearLayout(this);
        workersContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(workersContainer);
        root.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        setContentView(root);
    }

    private void showWorkerForm() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(32, 16, 32, 0);

        EditText nombreInput = createTextInput("Nombre");
        EditText apellidosInput = createTextInput("Apellidos");
        EditText edadInput = createTextInput("Edad");
        edadInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText oficioInput = createTextInput("Oficio");
        EditText descripcionInput = createTextInput("Descripción de su oficio");
        descripcionInput.setMinLines(3);
        descripcionInput.setGravity(Gravity.TOP);
        EditText contactoInput = createTextInput("Número de contacto");
        contactoInput.setInputType(InputType.TYPE_CLASS_PHONE);

        form.addView(nombreInput);
        form.addView(apellidosInput);
        form.addView(edadInput);
        form.addView(oficioInput);
        form.addView(descripcionInput);
        form.addView(contactoInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Registrar trabajador")
                .setView(form)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> saveWorker(
                        dialog,
                        nombreInput,
                        apellidosInput,
                        edadInput,
                        oficioInput,
                        descripcionInput,
                        contactoInput
                )));
        dialog.show();
    }

    private EditText createTextInput(String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(false);
        input.setPadding(0, 12, 0, 12);
        return input;
    }

    private void saveWorker(AlertDialog dialog, EditText nombreInput, EditText apellidosInput,
            EditText edadInput, EditText oficioInput, EditText descripcionInput,
            EditText contactoInput) {
        String nombre = nombreInput.getText().toString().trim();
        String apellidos = apellidosInput.getText().toString().trim();
        String edadText = edadInput.getText().toString().trim();
        String oficio = oficioInput.getText().toString().trim();
        String descripcion = descripcionInput.getText().toString().trim();
        String contacto = contactoInput.getText().toString().trim();

        if (nombre.isEmpty() || apellidos.isEmpty() || edadText.isEmpty()
                || oficio.isEmpty() || descripcion.isEmpty() || contacto.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadText);
        } catch (NumberFormatException exception) {
            Toast.makeText(this, "La edad debe ser un número válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edad <= 0) {
            Toast.makeText(this, "La edad debe ser mayor que cero.", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = databaseHelper.insertWorker(
                nombre,
                apellidos,
                edad,
                oficio,
                descripcion,
                contacto
        );
        if (result == -1) {
            Toast.makeText(this, "No se pudo guardar el trabajador.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Trabajador guardado.", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        loadWorkers();
    }

    private void loadWorkers() {
        workersContainer.removeAllViews();
        List<Worker> workers = databaseHelper.getAllWorkers();
        emptyMessage.setVisibility(workers.isEmpty() ? View.VISIBLE : View.GONE);

        for (Worker worker : workers) {
            workersContainer.addView(createWorkerCard(worker));
        }
    }

    private View createWorkerCard(Worker worker) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 24, 24, 24);
        card.setBackgroundColor(0xFFFFFFFF);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 16, 0, 0);
        card.setLayoutParams(cardParams);

        TextView name = new TextView(this);
        name.setText(worker.nombre + " " + worker.apellidos);
        name.setTextSize(20);
        name.setTextColor(0xFF111827);
        card.addView(name);

        TextView details = new TextView(this);
        details.setText("Edad: " + worker.edad
                + "\nOficio: " + worker.oficio
                + "\nDescripción: " + worker.descripcionOficio
                + "\nContacto: " + worker.numeroContacto);
        details.setTextSize(15);
        details.setTextColor(0xFF374151);
        details.setPadding(0, 12, 0, 12);
        card.addView(details);

        Button deleteButton = new Button(this);
        deleteButton.setText("Eliminar trabajador");
        deleteButton.setAllCaps(false);
        deleteButton.setOnClickListener(view -> confirmDelete(worker));
        card.addView(deleteButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        return card;
    }

    private void confirmDelete(Worker worker) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar trabajador")
                .setMessage("¿Quieres eliminar a " + worker.nombre + " " + worker.apellidos + "?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialogInterface, which) -> deleteWorker(worker.id))
                .show();
    }

    private void deleteWorker(long workerId) {
        if (databaseHelper.deleteWorker(workerId)) {
            Toast.makeText(this, "Trabajador eliminado.", Toast.LENGTH_SHORT).show();
            loadWorkers();
        } else {
            Toast.makeText(this, "No se pudo eliminar el trabajador.", Toast.LENGTH_SHORT).show();
        }
    }
}
