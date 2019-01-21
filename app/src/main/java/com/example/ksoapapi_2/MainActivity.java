package com.example.ksoapapi_2;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.config.Configurcation;
import com.example.model.Contact;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {

    EditText txtNhapMa;
    TextView txtKQMa, txtTen,txtPhone;
    Button btnLayTT;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnLayTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyLayContact();
            }
        });
    }

    private void xuLyLayContact() {
        int ma = Integer.parseInt(txtNhapMa.getText().toString());
        ContactAstack astack = new ContactAstack();
        astack.execute(ma);
    }

    class ContactAstack extends AsyncTask<Integer,Void, Contact>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtKQMa.setText("");
            txtTen.setText("");
            txtPhone.setText("");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Contact contact) {
            super.onPostExecute(contact);
            txtKQMa.setText(contact.getMa()+"");
            txtTen.setText(contact.getTen());
            txtPhone.setText(contact.getPhone());
            dialog.dismiss();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Contact doInBackground(Integer... integers) {
            try {
                int ma = integers[0];
                SoapObject requet = new SoapObject(Configurcation.NAME_SPACE,Configurcation.METHOD);
                requet.addProperty(Configurcation.PARAM_MA,ma);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(requet);

                HttpTransportSE transportSE = new HttpTransportSE(Configurcation.SERVER_URL);
                transportSE.call(Configurcation.SOAP_ACTION,envelope);

                SoapObject data = (SoapObject) envelope.getResponse();
                Contact contact = new Contact();
                if (data.hasProperty("Ma")){
                    contact.setMa((Integer) data.getProperty("Ma"));
                }
                if (data.hasProperty("Ten")){
                    contact.setTen((String) data.getProperty("Ten"));
                }
                if (data.hasProperty("Phone")){
                    contact.setPhone((String) data.getProperty("Phone"));
                }
                return contact;

            }catch (Exception e){
                Log.e("Loi",e.toString());
            }
            return null;
        }
    }

    private void addControls() {
        txtNhapMa = findViewById(R.id.txtNhapMa);
        txtKQMa = findViewById(R.id.txtKQMa);
        txtPhone = findViewById(R.id.txtSDT);
        txtTen = findViewById(R.id.txtTen);
        btnLayTT = findViewById(R.id.btnLayTT);

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Thông báo");
        dialog.setMessage("Vui lòng chờ");
        dialog.setCanceledOnTouchOutside(false);
    }
}
