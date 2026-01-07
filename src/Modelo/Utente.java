package Modelo;

public class Utente {
    private int numero;
    public String nome;
    public int idade;
    private String sintoma;
    private String NivelUrgencia;

    public Utente(int numero, String nome, int idade, String sintoma, String nivelUrgencia) {
        this.numero = numero;
        this.nome = nome;
        this.idade = idade;
        this.sintoma = sintoma;
        NivelUrgencia = nivelUrgencia;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getSintoma() {
        return sintoma;
    }

    public void setSintoma(String sintoma) {
        this.sintoma = sintoma;
    }

    public String getNivelUrgencia() {
        return NivelUrgencia;
    }

    public void setNivelUrgencia(String nivelUrgencia) {
        NivelUrgencia = nivelUrgencia;
    }

    @Override
    public String toString(){
        return  " Número:" + numero + " | Nome: " + nome + " | Idade:  " + idade +  " | Sintoma " + sintoma + " | Nível da Urgência " + NivelUrgencia;
    }
}
