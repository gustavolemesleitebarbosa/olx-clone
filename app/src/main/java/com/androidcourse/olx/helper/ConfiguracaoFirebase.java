package com.androidcourse.olx.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutenticacao;
    private static StorageReference referenciaStorage;



    public static String getIdUsuario(){
        FirebaseAuth autenticacao = getFirebaseAuth();
        return autenticacao.getCurrentUser().getUid();
    }


    public static DatabaseReference getFirebase(){
        if (referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
      return referenciaFirebase;
    }


    public static FirebaseAuth getFirebaseAuth(){
        if (referenciaAutenticacao==null){
            referenciaAutenticacao =FirebaseAuth.getInstance();
        }
        return referenciaAutenticacao;
    }


    public static StorageReference getFirebaseStorage(){

        if(referenciaStorage ==null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }

}
