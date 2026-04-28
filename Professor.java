public class Professor {
    private int codigo;
    private String nome;
    private String email;
    private String senha;

    //construtor
    public Professor(int codigo, String nome, String email, String senha) {
        setCodigo(codigo);
        setNome(nome);
        setEmail(email);
        setSenha(senha);
    }

    public int getCodigo() {
        return codigo;
    }
    public String getEmail() {
        return email;
    }
    public String getNome() {
        return nome;
    }

    public void setCodigo(int codigo) {
        if(codigo >= 0)
        {
            this.codigo = codigo;
        }
    }
    public void setEmail(String email) {
        if (email != null && email != this.email) 
        {
            this.email = email;
        }
    }
    public void setNome(String nome) {
        if (nome != null && nome != this.nome) 
        {
            this.nome = nome;
        }
    }
    public void setSenha(String senha) {
        if (senha != null && senha!= this.senha && senha.length() >= 8) 
        {
            this.senha = senha;
        }
    }

    public boolean validarAcesso(String email, String senha) {
        return this.email.equals(email) && this.senha.equals(senha); //retorna se o usuario tem o email e a senha digitada
    }
    
}
