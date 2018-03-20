package com.example.root.trabajofinal.Listeners;

import com.example.root.trabajofinal.Objetos.Comentario;

import java.util.ArrayList;

/**
 * Created by pedro on 19/02/18.
 */

public interface GetComentariosListener {
    void onResponseGetComentariosListener(ArrayList<Comentario> comentarios);
}
