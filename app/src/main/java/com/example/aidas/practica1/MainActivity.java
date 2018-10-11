package com.example.aidas.practica1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.InterfaceAddress;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MITAG";
    private WebView webView;
    private String user;
    private String pass;
    private Object error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = "";
        pass = "";

        webView = findViewById(R.id.wvPrincipal);
        webView.getSettings().setJavaScriptEnabled(true);
        final InterfazWebView iaw = new InterfazWebView();
        webView.addJavascriptInterface(iaw, "puente");
        webView.loadUrl("http://www.juntadeandalucia.es/averroes/centros-tic/18700098/moodle2/login/index.php");

        webView.setWebViewClient(new WebViewClient(){

            //contador == 0: pide y almacena los datos en las SP
            // contador > 0: inicia sesión automáticamente con los datos de las SP
            int contador = 0;

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                Log.v(TAG, "Ha cargado la página");

                getShPref();

                Log.v(TAG, "Desues del primer GET - Usuario: " + user + " Pass: " + pass);

                //Si tenemos datos distintos de "" en las SP aumentamos el contador
                if (user != "" && pass != "") {

                    contador ++;

                    Log.v(TAG, "Hay SP disponibles - Usuario: " + user + " Pass: " + pass);

                }

                if(contador == 0){

                    Log.v(TAG, "contador == 0");

                    //Si es la primera vez que carga la página:

                    /*Establecemos un listener en el boton para que al hacer login captemos los datos
                    introducidos.
                    Ademas intentamos coger el elemento loginerrormessag, que solo aparece cuando los
                    datos introducidos son erroneos
                    */
                    String javaScript = "" +
                            "   var boton = document.getElementById('loginbtn');" +
                            "   boton.addEventListener('click', function() {" +
                            "    var nombre = document.getElementById('username').value;" +
                            "    var clave  = document.getElementById('password').value;" +
                            "    puente.sendData(nombre, clave);" +
                            "});";

                    webView.loadUrl("javascript: " + javaScript);

                    //Obtenemos los datos que ha almacenado la interfaz.
                    user = iaw.getUsuario();
                    pass = iaw.getPass();

                    //Si los valores son distintos de cadena nula los guardamos en las SP,
                    //y aumentamos el contador
                    if(user != null && pass !=null){

                        Log.v(TAG, "contador == 0, user y pass != \"\" ");

                        setShPref();

                        Log.v(TAG, "Usuario guardado en SP: " + user);
                        Log.v(TAG, "Pass guardada en SP: " + pass);

                        contador++;

                    }

                    //En caso de ser cadena nula dejamos contador = 0 y no guardamos los datos.


                }else{

                    Log.v(TAG, "contador > 0");

                    getShPref();

                    // Introducimos esos valores de las SP en los campos del formulario
                    // Añadimos un event listener al boton para captar el elemento loginerrormessag
                    // el cual aparece cuando no se ha iniciado sesion correctamente
                    // Iniciamos sesion automáticamente con ellos haciendo click() en el boton

                    String javaScript = "" +
                            "    document.getElementById('username').value = \""+ user + "\";" +
                            "    document.getElementById('password').value = \""+ pass + "\";" +
                            "    boton.addEventListener('click', function() {" +
                            "    var error  = document.getElementById('loginerrormessag');" +
                            "    puente.sendData(error);" + "});" +
                            "    document.getElementById('loginbtn').click();";

                    webView.loadUrl("javascript: " + javaScript);

                    // error == null: se ha iniciado sesión correctamente, los datos son buenos
                    // error != null: el objeto existe, ha fallado el inicio

                    if(error != null){

                        contador = 0;

                    }

                }

            }
        });
    }

    //Metodo para guardar las preferencias compartidas
    public void setShPref() {

        SharedPreferences pref = getSharedPreferences(getString(R.string.archivoSP), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user", user);
        editor.putString("pass", pass);

        editor.apply();

        Log.v(TAG, "SET");
    }

    //Metodo para obtener los datos almacenados en las p.c.

    public void getShPref(){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        user = pref.getString("user", "Default");
        pass = pref.getString("pass","Default");

        Log.v(TAG, "GET");

    }
}
