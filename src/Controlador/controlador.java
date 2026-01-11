package Controlador;

import Modelo.Especialidade;
import Modelo.Medico;
import Modelo.Sintoma;

public class controlador {

    private final EspecialidadeRepo especialidades = new EspecialidadeRepo();
    private  final MedicoRepo medicos = new MedicoRepo();
    private final SintomaRepo sintomas = new SintomaRepo();

    private DataIO io = new DataIO();

    public EspecialidadeRepo getEspecialidades() {
        return especialidades;
    }
    public MedicoRepo getMedicos() {
        return medicos;
    }
    public SintomaRepo getSintomas() {
        return sintomas;
    }

    public void carregar() throws Exception {
        io.carregarTudo(especialidades, medicos, sintomas);
    }

    public void guardar() throws Exception {
        io.guardarTudo(especialidades, medicos, sintomas);
    }
}
