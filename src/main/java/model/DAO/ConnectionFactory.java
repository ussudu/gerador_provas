package model.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String url = "jdbc:mysql://localhost:3306/gerador_provas";
    private static final String usuario = "root";
    private static final String senha = "12345678";

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(url, usuario, senha);

        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados", e);
        }
    }

    //testando conexão
    public static void main(String[] args) {
        try {
            Connection conn = ConnectionFactory.getConnection();
            
            if (conn != null) {
                System.out.println("Conexão com o banco de dados realizada perfeitamente!");
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Não foi possível conectar ao banco de dados.");
            System.out.println("Motivo: " + e.getMessage());
        }
    }
}