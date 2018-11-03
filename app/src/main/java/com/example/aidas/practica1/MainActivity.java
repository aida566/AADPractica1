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
    private Boolean spCorrectos;

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

        final String urlMoodle = "http://www.juntadeandalucia.es/averroes/centros-tic/18700098/moodle2/login/index.php";

        webView.loadUrl(urlMoodle);

        webView.setWebViewClient(new WebViewClient(){

            int contador = 0;

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                Log.v(TAG, "Ha cargado la página");


                /*Si tenemos datos en las SP y son correctos aumentamos el contador para iniciar sesión automaticamete.
                * contador == 0: no hay SP, o hay SP incorrectas
                * contador >=1: hay SP correctas*/

                getShPref();

                if (user != "" && pass != "") {

                    if(getShPrefError().equalsIgnoreCase("true")){

                        contador++;
                        Log.v(TAG, "Hay datos correctos. Contador: " + contador);

                    }else{

                        spCorrectos = false;
                        Log.v(TAG, "Hay datos INcorrectos. Contador: " + contador);
                    }
                }

                if(contador == 0){
                    /*Establecemos un listener en el boton para que al hacer login captemos los datos
                    introducidos mediante la interfaz.
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

                    /*Si los valores son distintos de null los guardamos en las SP,
                    *y aumentamos el contador */
                    if(user != null && pass != null){

                        /*Si no se ha inicializado y por tanto es la primera vez que se ejectura:
                         * guardamos en SP independientemente de si son datos correctos o no */

                        if(spCorrectos == null){

                            setShPref();
                            Log.v(TAG, "spCorrectos es null: " + user + " - " + pass);

                        }else{

                            Log.v(TAG, "spCorrectos NO ES null: " + user + " - " + pass);

                            /*Por el contrario si no es la primera vez guardaremos en SP solo si los datos
                            son correctos. */

                            if(url.equalsIgnoreCase(urlMoodle)){

                                setShPrefError("false"); //Los datos guardados son incorrectos.
                                spCorrectos = false;

                                contador = 0;

                            }else{
                                setShPref();
                                Log.v(TAG, "Usuario y pass guardadas en SP: " + user + " - " + pass);

                                setShPrefError("true"); //Los datos guardados son correctos.
                                spCorrectos = true;
                                contador++;
                            }

                        }

                    }

                }else{

                    getShPref();

                    /* Introducimos esos valores de las SP en los campos del formulario
                    * Añadimos un event listener al boton para captar el elemento loginerrormessag
                    * el cual aparece cuando no se ha iniciado sesion correctamente */

                    String javaScript = "" +
                            "    document.getElementById('username').value = \""+ user + "\";" +
                            "    document.getElementById('password').value = \""+ pass + "\";" +
                            "    document.getElementById('login').submit();" +
                            "    var e = document.getElementById('loginerrormessage');" +
                            "    puente.setErrors(e);";

                    webView.loadUrl("javascript: " + javaScript);

                    Log.v(TAG, "Valor de errors: " + iaw.getError());

                    // error == null: se ha iniciado sesión correctamente, los datos son buenos
                    // error != null: el objeto existe, ha fallado el inicio

                    if(iaw.getError() != null){

                        Log.v(TAG, "Ha fallado el inicio");

                        contador = 0;

                    }else{

                        Log.v(TAG, "NO hay error");
                    }

                    Log.v(TAG, "web view: " + webView.getUrl());
                    Log.v(TAG, "URL: " + urlMoodle);
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

        editor.commit();

        Log.v(TAG, "SET");
    }

    public void setShPrefError(String e){
        SharedPreferences pref = getSharedPreferences(getString(R.string.archivoSP), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("error", e);

        editor.commit();
    }
    public String getShPrefError(){
        SharedPreferences pref = getSharedPreferences(getString(R.string.archivoSP), Context.MODE_PRIVATE);
        String error = pref.getString("error", "");

        return error;
    }

    //Metodo para obtener los datos almacenados en las p.c.

    public void getShPref(){

        SharedPreferences pref = getSharedPreferences(getString(R.string.archivoSP), Context.MODE_PRIVATE);
        user = pref.getString("user", "");
        pass = pref.getString("pass","");

        Log.v(TAG, "usuario en getShPrf: " + user + " pass: " + pass);

        Log.v(TAG, "GET");

    }
}
