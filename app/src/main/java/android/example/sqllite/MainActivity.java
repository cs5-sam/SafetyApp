package android.example.sqllite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText nameInput, numberInput, messageInput,idInput;
    private Button addContactBtn, showDataBtn,detailBtn,deleteBtn,instructionsBtn;

    private DatabaseHelper myDb;

    private String name,number,id;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);
        nameInput = findViewById(R.id.nameInput);
        numberInput = findViewById(R.id.numberInput);
        idInput = findViewById(R.id.id_input);
        addContactBtn = findViewById(R.id.add_data_btn);
        showDataBtn = findViewById(R.id.show_data_btn);
        detailBtn = findViewById(R.id.detail_data_btn);
        instructionsBtn = findViewById(R.id.instructions_btn);
        loadingBar = new ProgressDialog(this);

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Adding contact");
                loadingBar.setMessage("Please wait, adding contact");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                AddData();
            }
        });

        showDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,DetailsActivity.class);
                startActivity(i);
            }
        });

        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAll();
            }
        });

        instructionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,InstructionsActivity.class);
                startActivity(i);
            }
        });
    }

    //ADDING DATA
    private void AddData(){

        name = nameInput.getText().toString();
        number = numberInput.getText().toString();
        id = idInput.getText().toString();

        // CHECKING FOR NULL
        if(name.equals("")){
            loadingBar.dismiss();
            Toast.makeText(MainActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
            nameInput.setFocusableInTouchMode(true);
            nameInput.requestFocus();
        }
        else if(number.equals("")){
            loadingBar.dismiss();
            Toast.makeText(MainActivity.this, "Enter number", Toast.LENGTH_SHORT).show();
            numberInput.setFocusableInTouchMode(true);
            numberInput.requestFocus();
        }

        else{

            // INSERTING DATA IN DATABASE
            boolean isInserted = myDb.insertData(name,number);
            if(isInserted == true){
                loadingBar.dismiss();
                Toast.makeText(MainActivity.this, "Contact Added!", Toast.LENGTH_SHORT).show();
                idInput.setText("");
                nameInput.setText("");
                numberInput.setText("");
            }
            else{
                loadingBar.dismiss();
                Toast.makeText(MainActivity.this, "Data not inserted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // VIEWING DATA
    public void viewAll(){
        Cursor res = myDb.getAllData();
        if(res.getCount() == 0){
            //  show message
            showData("Error","No data found");
            return;
        }
        else{
            StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                buffer.append("id : " + res.getString(0) + "\n");
                buffer.append("name : " + res.getString(1) + "\n");
                buffer.append("number : " + res.getString(2) + "\n\n");
            }
            showData("Data",buffer.toString());
        }
    }

    //CREATING ALERT DIALOG BOX
    public void showData(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    // UPDATING DATA
    public void updateData(){
        
        boolean isUpdated = myDb.updateData(id,name,number);
        
        if (isUpdated == true){
            Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Data not updated", Toast.LENGTH_SHORT).show();
        }
    }

    // DELETING DATA
    public void deleteData(){

        boolean deletedRows = myDb.deleteData(number);
        if(deletedRows){
            Toast.makeText(this, "Contact Deleted", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Contact not deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
