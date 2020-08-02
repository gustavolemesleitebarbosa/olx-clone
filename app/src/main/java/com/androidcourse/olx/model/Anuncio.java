package com.androidcourse.olx.model;

import com.androidcourse.olx.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Anuncio implements Serializable {

    private String idAnuncio;
    private String estado;
    private  String categoria;
    private String titulo;
    private String valor;
    private String telefone;
    private  String descricao;


    public Anuncio()  {
        DatabaseReference anunncioRef = ConfiguracaoFirebase.getFirebase().child("meus_anuncios");
        setIdAnuncio(anunncioRef.push().getKey());
    }

    public void salvar(){

        String idUsuario = ConfiguracaoFirebase.getIdUsuario();

        DatabaseReference anunncioRef = ConfiguracaoFirebase.getFirebase().child("meus_anuncios");
        anunncioRef.child(idUsuario)
                  .child(getIdAnuncio())
                .setValue(this);

        salvarAnuncioPublico();
    }

    public void remover(){

        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anunncioRef = ConfiguracaoFirebase.getFirebase().child("meus_anuncios").child(idUsuario).child(getIdAnuncio());
        anunncioRef.removeValue();
        removerAnuncioPublico();
    }


    public void removerAnuncioPublico(){

        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anunncioRef = ConfiguracaoFirebase.getFirebase().child("anuncios").child(getEstado()).child(getCategoria()).child(getIdAnuncio());
        anunncioRef.removeValue();
}


    public void salvarAnuncioPublico(){

        DatabaseReference anunncioRef = ConfiguracaoFirebase.getFirebase().child("anuncios");
        anunncioRef.child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio())
                .setValue(this);
    }


    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    private List<String> fotos;




}
