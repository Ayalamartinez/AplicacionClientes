package com.ayalamart.appcliente;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ayalamart.helper.AppController;
import com.ayalamart.helper.GestionSesionesUsuario;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Act_Signup extends Activity {
	private EditText contrasena; 
	private EditText conf_contrasena; 
	private EditText nombre;
	private EditText apellido; 
	private EditText cedula;
	private EditText correo;
	private EditText telefono;
	GestionSesionesUsuario sesion; 
	private ProgressDialog pDialog;
	String urlCrearCliente = "http://10.10.0.99:8080/Restaurante/rest/createCliente"; 

	private static String TAG = Act_Signup.class.getSimpleName();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_act_signup);

		sesion = new GestionSesionesUsuario(getApplicationContext()); 

		nombre = (EditText)findViewById(R.id.nombre_signup); 
		apellido = (EditText)findViewById(R.id.apellido_signup);
		cedula = (EditText)findViewById(R.id.cedula_signup); 
		correo = (EditText)findViewById(R.id.email_signup); 
		telefono = (EditText)findViewById(R.id.telefono_signup); 
		contrasena = (EditText)findViewById(R.id.contrasena_signup); 
		conf_contrasena = (EditText)findViewById(R.id.et_confirm_contrasena);

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Por favor espere...");
		pDialog.setCancelable(false);

		//declarando el mensaje de error, y colocandole el mismo color de fondo para esconderlo 
		final TextView mensaje_error = (TextView)findViewById(R.id.alerta_contr_no_match); 
		mensaje_error.setBackgroundColor(Color.parseColor("#96B497"));

		Button button_signup = (Button) findViewById(R.id.but_signup);
		button_signup.setOnClickListener(new OnClickListener() {		

			@Override
			public void onClick(View v) {	



				final String name = "nombre_ejm"; 
				final String email = "correo_ejm"; 
				final String nombre_str = nombre.getText().toString(); 
				final String apellido_str = apellido.getText().toString();
				final String cedula_str = cedula.getText().toString(); 
				final String correo_str = correo.getText().toString(); 
				final String telefono_str = telefono.getText().toString();
				final String contrasena_str = contrasena.getText().toString(); 
				final String conf_contrasena_str = conf_contrasena.getText().toString();



				Log.d(TAG,bin2hex(getHash(contrasena_str)));
				
				String contrasena_cript = bin2hex(getHash(contrasena_str)); 
				
				if (validarCorreo(correo_str)) {
					if (validarDatos(nombre_str, cedula_str, correo_str, telefono_str)) {
						if (validarPassword(contrasena_str)) {
							//si el password es valido, entra al siguiente ciclo 

							if (contrasena_str.equals(conf_contrasena_str)) {

								String match = " ";
								mensaje_error.setBackgroundColor(Color.parseColor("#96B497"));
								mensaje_error.setText(match);
								Long idcliente = new Long(0); 
								JSONObject json_ob = new JSONObject(); 
								try {
									json_ob.put("apeCliente", apellido_str);
									json_ob.put("cedCliente", cedula_str);
									json_ob.put("emailCliente", correo_str); 
									json_ob.put("estatus", "1"); 
									json_ob.put("idCliente", idcliente); 
									json_ob.put("nomCliente", nombre_str); 
									json_ob.put("passCliente", contrasena_cript); 
									json_ob.put("telCliente", telefono_str); 
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Log.d(TAG, "ERROR DE JSON"); 
								} 
								
							
								JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, 
										urlCrearCliente, json_ob, null , new Response.ErrorListener() {
									@Override
									public void onErrorResponse(VolleyError error) {
										VolleyLog.d(TAG, "Error: " + error.getMessage());

										Log.d(TAG, "Error: " + error.getMessage()); 
										/*Toast.makeText(getApplicationContext(),
														error.getMessage(), Toast.LENGTH_SHORT).show();
										 */
										// hide the progress dialog
										hidepDialog();
									}
								});	
								sesion.crearSesionUSuario(name, email, nombre_str, apellido_str, cedula_str, correo_str, telefono_str, contrasena_cript);
								Log.d(TAG, "LOGRO REGISTRARSE"); 
								hidepDialog();

								Intent intent_ppal = new Intent(getApplicationContext(), Act_Principal.class); 
								intent_ppal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
								intent_ppal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
								startActivity(intent_ppal);
								finish(); 
								// Adding request to request queue
								AppController.getInstance().addToRequestQueue(jsonObjReq);	
							}
							else{

								String str_nomatch = "La contrase�a no coincide";
								mensaje_error.setBackgroundColor(Color.parseColor("#CC5D4C"));
								mensaje_error.setText(str_nomatch);
								hidepDialog();
							}

						}

						else{
							// si el password no es valido, entonces mostrara el error de password invalido. 
							contrasena.setError("Contrase�a invalida. Debe como m�nimo 5 caracteres o ser no nula");
							hidepDialog();
						}
					}
				}
				else {
					correo.setError("Correo Inv�lido");
					hidepDialog();
				}





			}
		});
	}

	private boolean validarCorreo (String correo_str){
		String PATRON_CORREO = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; 

		Pattern patron = Pattern.compile(PATRON_CORREO); 
		Matcher correlacionador = patron.matcher(correo_str); 
		return correlacionador.matches();	
	}

	private boolean validarPassword (String contrasena_str){

		if (contrasena_str != null && contrasena_str.length() > 5 ){
			return true; 
		}
		return false; 

	}

	private boolean validarDatos (String nombre_str, String cedula_str, String correo_str, String telefono_str ){

		if (nombre_str != null && cedula_str != null && telefono_str != null && correo_str != null ){
			return true; 
		}
		return false; 
	}
	
private byte[] getHash(String password) {
    MessageDigest digest=null;
 try {
     digest = MessageDigest.getInstance("SHA-256");
 } catch (NoSuchAlgorithmException e1) {
     // TODO Auto-generated catch block
     e1.printStackTrace();
 }
    digest.reset();
    return digest.digest(password.getBytes());
}
private static String bin2hex(byte[] data) {
    return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
}

	private void showpDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hidepDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

}
