public class SistArq {
    private Usuario usuarioLogado;
    private Admin admin;
    private Usuario[] usuarios;
    private Professor[] professores;
    private int codProfessor = 1;
    private Prova[] provas;
    private int codProva = 1;
    private Questao[] questoes;
    private int codQuestao = 1;
    private QuestaoMultiplaEscolha[] questoesMultiplaEscolha;
    private Disciplina[] disciplinas;
    private int codDisciplina = 1;

    public SistArq(Admin admin) {
        setAdmin(admin);
        this.usuarios = new Usuario[0];
        this.professores = new Professor[0];
        this.provas = new Prova[0];
        this.questoes = new Questao[0];
        this.questoesMultiplaEscolha = new QuestaoMultiplaEscolha[0];
        this.disciplinas = new Disciplina[0];
    }

    public void setAdmin(Admin admin) {
        if (admin != null) 
        {
            this.admin = admin;   
        }
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    public Admin getAdmin() {
        return admin;
    }

    //gerenciamento de diciplinas
    public void cadastrarDisciplina(String nome) {
        Usuario autor = this.usuarioLogado; 
        
        if (autor != null) {
            Disciplina nova = new Disciplina(codDisciplina, nome, autor, null);
            adicionarDisciplina(nova);
            System.out.println("Disciplina " + nome + " cadastrada com sucesso!");
            codDisciplina++;
        } else {
            System.out.println("Erro: Nenhum usuário logado para realizar o cadastro.");
        }
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            Disciplina[] novo = new Disciplina[this.disciplinas.length + 1];
            for (int i = 0; i < disciplinas.length; i++) novo[i] = disciplinas[i];
            novo[disciplinas.length] = disciplina;
            this.disciplinas = novo;
        }
    }

    public void removerDisciplina(Disciplina disciplina) {

        if (this.usuarioLogado == null) 
        {
            System.out.println("Erro: Acesso negado.");
        }
        else
        {
            int pos = -1;
            int i = 0;

            while (i < disciplinas.length && pos == -1) 
            {
                if (disciplinas[i] == disciplina) pos = i;
                i++;
            }
            if (pos != -1) 
            {
                Disciplina[] novo = new Disciplina[disciplinas.length - 1];
                int j = 0;
                for (int k = 0; k < disciplinas.length; k++) {
                    if (k != pos) novo[j++] = disciplinas[k];
                }
                this.disciplinas = novo;
                System.out.println("Disciplina removida.");
            } else {
                System.out.println("Erro: Disciplina não encontrada no sistema.");
            }
        }
    }


    //gerenciamento de questoes
    public void cadastrarQuestao(String enunciado, Disciplina disciplina, String assunto, int nivel) {
        Usuario autor = this.usuarioLogado; 
        
        if (autor != null) {
            Questao nova = new Questao(codQuestao, enunciado, disciplina, assunto, nivel, autor, null);
            adicionarQuestao(nova);
            System.out.println("Questão cadastrada com sucesso!");
            codQuestao++;
        } else {
            System.out.println("Erro: Nenhum usuário logado para realizar o cadastro.");
        }
    }

    public void adicionarQuestao(Questao questao) {
        if (questao != null) {
            Questao[] novo = new Questao[this.questoes.length + 1];
            for (int i = 0; i < questoes.length; i++) novo[i] = questoes[i];
            novo[questoes.length] = questao;
            this.questoes = novo;
        }
    }

    public void removerQuestao(Questao questao) {
        if (this.usuarioLogado == null) {
            System.out.println("Erro: Acesso negado.");
        } else {
            int pos = -1;
            int i = 0;
            while (i < questoes.length && pos == -1) {
                if (questoes[i] == questao) pos = i;
                i++;
            }
            if (pos != -1) {
                Questao[] novo = new Questao[questoes.length - 1];
                int j = 0;
                for (int k = 0; k < questoes.length; k++) {
                    if (k != pos) novo[j++] = questoes[k];
                }
                this.questoes = novo;
                System.out.println("Questão removida.");
            } else {
                System.out.println("Erro: Questão não encontrada.");
            }
        }
    }

    //gerenciamento de provas
    public void cadastrarProva(Disciplina disciplina, String semestre) {
        Usuario autor = this.usuarioLogado; 
        
        if (autor != null) {
            Prova nova = new Prova(codProva, disciplina, semestre, autor, null);
            
            adicionarProva(nova);
            System.out.println("Prova de " + semestre + " cadastrada com sucesso!");
            codProva++;
        } else {
            System.out.println("Erro: Nenhum usuário logado para realizar o cadastro.");
        }
    }

    public void adicionarProva(Prova prova) {
        if (prova != null) {
            Prova[] novo = new Prova[this.provas.length + 1];
            for (int i = 0; i < provas.length; i++) novo[i] = provas[i];
            novo[provas.length] = prova;
            this.provas = novo;
        }
    }

    public void removerProva(Prova prova) {
        if (this.usuarioLogado == null) {
            System.out.println("Erro: Acesso negado.");
        } else {
            int pos = -1;
            int i = 0;
            while (i < provas.length && pos == -1) {
                if (provas[i] == prova) pos = i;
                i++;
            }
            if (pos != -1) {
                Prova[] novo = new Prova[provas.length - 1];
                int j = 0;
                for (int k = 0; k < provas.length; k++) {
                    if (k != pos) novo[j++] = provas[k];
                }
                this.provas = novo;
                System.out.println("Prova removida.");
            } else {
                System.out.println("Erro: Prova não encontrada no sistema.");
            }
        }
    }
    
    
    //gerencimaneto de professores
    public void cadastrarProfessor(String nome, String email, String senha) {
        //só pode add professores se o usuário logado for admin
        if (this.usuarioLogado != null && this.usuarioLogado.getEmail().equals(this.admin.getEmail())) {
            Professor novo = new Professor(codProfessor, nome, email, senha);
            adicionarProfessor(novo);
            
            System.out.println("Professor " + nome + " cadastrado com sucesso pelo Admin!");
            codProfessor++;
        } else {
            System.out.println("Erro: Apenas o Administrador tem permissão para cadastrar professores.");
        }
    }

    public void adicionarProfessor(Professor professor) {
        if (professor != null) {
            Professor[] novo = new Professor[this.professores.length + 1];
            for (int i = 0; i < professores.length; i++) {
                novo[i] = professores[i];
            }
            novo[professores.length] = professor;
            this.professores = novo;
        }
    }

    public void removerProfessor(Professor professor) {
        if (this.usuarioLogado != null && this.usuarioLogado.getEmail().equals(this.admin.getEmail())) {
            int pos = -1;
            int i = 0;
            while (i < professores.length && pos == -1) 
            {
                if (professores[i] == professor) pos = i;
                i++;
            }
            if (pos != -1) {
                Professor[] novo = new Professor[professores.length - 1];
                int j = 0;
                for (int k = 0; k < professores.length; k++) {
                    if (k != pos) {
                        novo[j++] = professores[k];
                    }
                }
                this.professores = novo;
                System.out.println("Professor removido com sucesso.");
            } else {
                System.out.println("Erro: Professor não encontrado.");
            }
            
        } else {
            System.out.println("Erro: Acesso negado. Apenas o Admin pode remover professores.");
        }
    }

    //login
    public boolean realizarLogin(String email, String senha) {
        int i = 0;
        while (i < usuarios.length) {
            if (usuarios[i].validarAcesso(email, senha)) {
                this.usuarioLogado = usuarios[i];
                return true;
            }
            i++;
        }
        return false;
    }

    //buscar questoes
    public Questao[] buscarQuestoes(Disciplina disciplina) {
        Questao[] resultado = new Questao[0];
        for (int i = 0; i < questoes.length; i++) {
            if (questoes[i].getDisciplina() == disciplina) {
                Questao[] temp = new Questao[resultado.length + 1];
                for (int j = 0; j < resultado.length; j++) temp[j] = resultado[j];
                temp[resultado.length] = questoes[i];
                resultado = temp;
            }
        }
        return resultado;
    }

    public Questao[] buscarQuestoes(String assunto, int nivelDificuldade) {
        Questao[] resultado = new Questao[0];
        for (int i = 0; i < questoes.length; i++) {
            if (questoes[i].getAssunto().equals(assunto) && questoes[i].getNivelDificuldade() == nivelDificuldade) {
                Questao[] temp = new Questao[resultado.length + 1];
                for (int j = 0; j < resultado.length; j++) temp[j] = resultado[j];
                temp[resultado.length] = questoes[i];
                resultado = temp;
            }
        }
        return resultado;
    }

    public Questao[] buscarQuestoes(int nivelDificuldade) {
        Questao[] resultado = new Questao[0];
        for (int i = 0; i < questoes.length; i++) {
            if (questoes[i].getNivelDificuldade() == nivelDificuldade) {
                Questao[] temp = new Questao[resultado.length + 1];
                for (int j = 0; j < resultado.length; j++) temp[j] = resultado[j];
                temp[resultado.length] = questoes[i];
                resultado = temp;
            }
        }
        return resultado;
    }

    //buscar provas
    public Prova[] buscarProvas(Disciplina disciplina) {
        Prova[] resultado = new Prova[0];
        for (int i = 0; i < provas.length; i++) {
            if (provas[i].getDisciplina() == disciplina) {
                Prova[] temp = new Prova[resultado.length + 1];
                for (int j = 0; j < resultado.length; j++) temp[j] = resultado[j];
                temp[resultado.length] = provas[i];
                resultado = temp;
            }
        }
        return resultado;
    }

    public Prova[] buscarProvas(String semestre) {
        Prova[] resultado = new Prova[0];
        for (int i = 0; i < provas.length; i++) {
            if (provas[i].getSemestre().equals(semestre)) {
                Prova[] temp = new Prova[resultado.length + 1];
                for (int j = 0; j < resultado.length; j++) temp[j] = resultado[j];
                temp[resultado.length] = provas[i];
                resultado = temp;
            }
        }
        return resultado;
    }

    //gerar relatório de provas
    public Prova[] gerarRelatório() {
        Prova[] copia = new Prova[provas.length];
        for (int i = 0; i < provas.length; i++) {
            copia[i] = provas[i];
        }
        return copia;
    }

}