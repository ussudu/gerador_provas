package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.entities.Question;

public class QuestionDAO {

    public void inserir(Question question) {
        String sql = "insert into question (tipo, enunciado, gabarito, assunto, nivel_dificuldade) values (?, ?, ?, ?, ?)";

        try {
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(sql);

            stmt.setString(1, question.getTipo());
            stmt.setString(2, question.getEnunciado());
            stmt.setString(3, question.getGabarito());
            stmt.setString(4, question.getAssunto());
            stmt.setInt(5, question.getNivelDificuldade());

            stmt.executeUpdate();

            stmt.close();
            conexao.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Question> listar() {
        String sql = "select * from question";

        ArrayList<Question> questions = new ArrayList<>();

        try {
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Question question = new Question();

                question.setCodigo(rs.getInt("codigo"));
                question.setTipo(rs.getString("tipo"));
                question.setEnunciado(rs.getString("enunciado"));
                question.setGabarito(rs.getString("gabarito"));
                question.setAssunto(rs.getString("assunto"));
                question.setNivelDificuldade(rs.getInt("nivel_dificuldade"));

                questions.add(question);
            }

            rs.close();
            stmt.close();
            conexao.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    public void atualizar(Question question) {
        String sql = "update question set tipo = ?, enunciado = ?, gabarito = ?, assunto = ?, nivel_dificuldade = ? where codigo = ?";

        try {
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(sql);

            stmt.setString(1, question.getTipo());
            stmt.setString(2, question.getEnunciado());
            stmt.setString(3, question.getGabarito());
            stmt.setString(4, question.getAssunto());
            stmt.setInt(5, question.getNivelDificuldade());
            stmt.setInt(6, question.getCodigo());

            stmt.executeUpdate();

            stmt.close();
            conexao.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int codigo) {
        String sql = "delete from question where codigo = ?";

        try {
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(sql);

            stmt.setInt(1, codigo);

            stmt.executeUpdate();

            stmt.close();
            conexao.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}