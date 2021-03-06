package com.ayalamart.helper;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.toolbox.JsonArrayRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GestionPedidoUsuario {
	SharedPreferences pref_ap_ped;
	Editor editor_ap_ped; 
	Context context_ap_ped;
	int PRIVATE_MODE = 0;
	
	
	private static final String TAG = GestionPedidoUsuario.class.getSimpleName(); 
	private static final String EXISTE_PEDIDO = "ExisteElPedido"; 
	private static final String PREFER_NAME = "AndroidPedidos"; 
	public static final String Pedido = "pedido"; 
	public static final String Subtotal = "subtotal"; 
	// public static final JSONObject pedido_obj ; 
	
	
	public GestionPedidoUsuario(Context context_p){
		this.context_ap_ped = context_p; 
		pref_ap_ped = context_p.getSharedPreferences(PREFER_NAME, PRIVATE_MODE); 
		editor_ap_ped= pref_ap_ped.edit(); 
		
	}
public void crearPedidoUsuario(String pedido, String subtotal){
	editor_ap_ped.putBoolean(EXISTE_PEDIDO, true); 
	editor_ap_ped.putString(Pedido, pedido); 
	editor_ap_ped.putString(Subtotal, subtotal); 
	editor_ap_ped.commit(); 
	
}

public void borrarPedido(){
	editor_ap_ped.clear(); 
	editor_ap_ped.commit();
	
	//falta un intent que va hacia el principal, o hacia el menu. Revisar 
}
public HashMap<String, String> getDetallesPedido(){
	HashMap<String, String> pedido = new HashMap<String, String>(); 
	pedido.put(Pedido, pref_ap_ped.getString(Pedido, null));
	pedido.put(Subtotal, pref_ap_ped.getString(Subtotal, null)); 
	return pedido; 
	
}

public boolean existePedido(){
	return pref_ap_ped.getBoolean(EXISTE_PEDIDO, false); 
}





}