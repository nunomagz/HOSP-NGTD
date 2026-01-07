package Modelo;

public class Sintoma {
    private String nome;
    private String nivelUrgencia;
    private String[] codigoEspecialidade;

    public Sintoma(String nome, String nivelUrgencia, String[] codigoEspecialidade) {
        this.nome = nome;
        this.nivelUrgencia = nivelUrgencia;
        this.codigoEspecialidade = codigoEspecialidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNivelUrgencia() {

        return nivelUrgencia;
    }

    public void setNivelUrgencia(String nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }

    public String[] getCodigoEspecialidade() {
        return codigoEspecialidade;
    }

    public void setCodigoEspecialidade(String[] codigoEspecialidade) {
        this.codigoEspecialidade = codigoEspecialidade;
    }
}
