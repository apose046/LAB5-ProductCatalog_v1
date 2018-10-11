package mgarzon.createbest.productcatalog;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextPrice;
    Button buttonAddProduct;
    ListView listViewProducts;

    DatabaseReference databaseProducts;

    List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        listViewProducts = (ListView) findViewById(R.id.listViewProducts);
        buttonAddProduct = (Button) findViewById(R.id.addButton);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");
        products = new ArrayList<>();

        //adding an onclicklistener to button
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        listViewProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = products.get(i);
                showUpdateDeleteDialog(product.getId(), product.getProductName());
                return true;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

    // Attaching value event listener

    databaseProducts.addValueEventListener(new ValueEventListener(){
        @Override
                public void onDataChange(DataSnapshot dataSnapshot){

            // Clearing the previous artist list
            products.clear();

            // Iterating through all the nodes
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                // Getting Product
                Product product = postSnapshot.getValue(Product.class);

                // Adding product to the list
                products.add(product);
            }

            // Creating adapter
            ProductList productsAdapter = new ProductList(MainActivity.this, products);

            // Attaching adapter to the listview
            listViewProducts.setAdapter(productsAdapter);
        }
        @Override
                public void onCancelled(DatabaseError databaseError){

        }
    });
    }

    private void showUpdateDeleteDialog(final String productId, String productName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final EditText editTextPrice  = (EditText) dialogView.findViewById(R.id.editTextPrice);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateProduct);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteProduct);

        dialogBuilder.setTitle(productName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                double price = Double.parseDouble(String.valueOf(editTextPrice.getText().toString()));
                if (!TextUtils.isEmpty(name)) {
                    updateProduct(productId, name, price);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct(productId);
                b.dismiss();
            }
        });
    }

    private void updateProduct(String id, String name, double price) {

        // Getting the specified product reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(id);

        // Updating product
        Product product = new Product(id, name, price);
        dR.setValue(product);

        Toast.makeText(getApplicationContext(), "Product Updated", Toast.LENGTH_LONG).show();

        // Toast.makeText(getApplicationContext(), "NOT IMPLEMENTED YET", Toast.LENGTH_LONG).show();
    }

    private void deleteProduct(String id) {

        // Get the specified product reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(id);

        // Removing Product
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_LONG).show();

        // Toast.makeText(getApplicationContext(), "NOT IMPLEMENTED YET", Toast.LENGTH_LONG).show();
    }

    private void addProduct() {

        // Getting the values to save
        String name = editTextName.getText().toString().trim();
        double price = Double.parseDouble(String.valueOf(editTextPrice.getText().toString()));

        // Checking if the value is provided
        if (!TextUtils.isEmpty(name)){

            // Getting a unique id using push().getKey() method
            // It will create a unique id and we will use it as the Primary Key for our Product
            String id = databaseProducts.push().getKey();

            // Creating a Product and Object
            Product product = new Product(id, name, price);

            // Saving the Product
            databaseProducts.child(id).setValue(product);

            // Setting editText to blank again
            editTextName.setText("");
            editTextPrice.setText("");

            // Displaying a success toast
            Toast.makeText(this, "Product added", Toast.LENGTH_LONG).show();


            // Displaying a success toast
            Toast.makeText(this, "Product added", Toast.LENGTH_LONG).show();
        } else {

            // If the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }

        // Toast.makeText(this, "NOT IMPLEMENTED YET", Toast.LENGTH_LONG).show();
    }
}