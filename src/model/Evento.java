package model;

import java.time.LocalDateTime;

public class Evento {
    private Long id;
    private String nome;
    private String endereco;
    private EnumCategoria categoria;
    private LocalDateTime horario;
    private String descricao;

    public Evento(Long id, String nome, String endereco, EnumCategoria categoria, LocalDateTime horario, String descricao) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public EnumCategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(EnumCategoria categoria) {
        this.categoria = categoria;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatusHorario() {
        LocalDateTime agora = LocalDateTime.now();
        String status = "";
        if (horario.isBefore(agora)) {
            status = "Ja ocorreu!";
        } else if (horario.isEqual(agora)) {
            status = "Esta ocorrendo!";
        } else {
            status = "Ira ocorrer";
        }
            return status;
    }

}
