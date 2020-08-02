package com.androidcourse.olx.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.androidcourse.olx.R;
import com.androidcourse.olx.helper.ConfiguracaoFirebase;
import com.androidcourse.olx.helper.Permissoes;
import com.androidcourse.olx.model.Anuncio;
import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskedittext.MaskEditText;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;


public class CadastrarAnunioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText campoTitulo ,campoDescricao;
    private ImageView imagem1, imagem2, imagem3;
    private Spinner campoEstado, campoCategoria;
    private CurrencyEditText campoValor;
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private MaskEditText campoTelefone;
    private Anuncio anuncio;
    private AlertDialog dialog;
    private StorageReference storage;
    private  String [ ] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anunio);

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        Permissoes.validarPermissoes(permissoes, this, 1);


        inicializarComponentes();
        carregarDadosSpinner();
    }


    private void carregarDadosSpinner(){
         String [] estados = getResources().getStringArray(R.array.estados);
         ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,estados);
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
         campoEstado.setAdapter(adapter);

        String [] categorias = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapterCatergoria = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,categorias);
        adapterCatergoria.setDropDownViewResource(android.R.layout.simple_spinner_item);
        campoCategoria.setAdapter(adapterCatergoria);

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;

            case R.id.imageCadastro2:
                escolherImagem(2);
                break;

            case R.id.imageCadastro3:
                escolherImagem(3);
                break;

        }

    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();
            Log.d("Imageeeeewewe ",caminhoImagem);
            if(requestCode ==1){
                Log.d("Imageeeee ",caminhoImagem);
                imagem1.setImageURI(imagemSelecionada);
            }
            else if (requestCode ==2){
                imagem2.setImageURI(imagemSelecionada);
            }
            else if (requestCode ==3){
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }

    }

    private void inicializarComponentes () {

        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao= findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoCategoria =findViewById(R.id.spinnerCategoria);
        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);


        //Configura localidade para pt -> portugues BR

        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado: grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }

        }

    }

    private void alertaValidacaoPermissao (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permisoes Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public  void salvarAnuncio (View view){

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Salvando Anúncio").setCancelable(false).build();
        dialog.show();

        for(int i=0; i< listaFotosRecuperadas.size(); i++ ){
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem,tamanhoLista, i );
        }

    }


    private void salvarFotoStorage(String urlString , final int totalFotos, int contador){

        // Criar nó no storage
        StorageReference imagemAnuncio = storage.child("imagens")
        .child("anuncios").child(anuncio.getIdAnuncio()).child("imagem"+contador);

        //Fazer upload anúncio
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                String urlConvertida = firebaseUrl.toString();
                listaURLFotos.add( urlConvertida);

                if(totalFotos ==listaURLFotos.size()){
                    anuncio.setFotos(listaURLFotos);
                    anuncio.salvar();
                    dialog.dismiss();
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer o upload");
            }
        });

    }


    private Anuncio configurarAnuncio(){
        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String telefone = campoTelefone.getText().toString();
        String descricao = campoDescricao.getText().toString();

        anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTelefone(telefone);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setDescricao(descricao);

        return anuncio;

    }



    public void validarDadosAnuncio (View view) {

        anuncio = configurarAnuncio();
        String valor = String.valueOf(campoValor.getRawValue());

        if(listaFotosRecuperadas.size() != 0 ){
            if(! anuncio.getEstado().isEmpty()){
                if(!anuncio.getCategoria().isEmpty()){
                    if(!anuncio.getTitulo().isEmpty()){
                        if(!valor.isEmpty() && !valor.equals("0")){

                            if(!anuncio.getTelefone().isEmpty() ){
                                if(!anuncio.getDescricao().isEmpty() ){
                                    salvarAnuncio(view);
                                }
                                else{
                                    exibirMensagemErro("Preencha o campo descricao!");
                                }
                            }
                            else{
                                exibirMensagemErro("Preencha o campo telefone!, digite ao menos 10 números");
                            }

                        }
                        else{
                            exibirMensagemErro("Preencha o campo valor!");
                        }

                    }
                    else{
                        exibirMensagemErro("Preencha o campo título!");
                    }

                }
                else{
                    exibirMensagemErro("Preencha o campo categoria!");
                }

            }
            else{
                exibirMensagemErro("Preencha o campo estado!");
            }


        }else{
            exibirMensagemErro("Selecione ao menos uma foto!");
        }

    }

    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }







}
