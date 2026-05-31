package src.model.DAO;

import src.model.entities.MultipleChoiceQuestion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MultipleChoiceQuestionDAO {

    public void inserir(MultipleChoiceQuestion question) {
        String sql = "insert into multiple_choice_question " +
                "(tipo, enunciado, gabarito, assunto, nivel_dificuldade) " +
                "values (?, ?, ?, ?, ?)";

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

    public ArrayList<MultipleChoiceQuestion> listar() {
        String sql = "select * from multiple_choice_question";

        ArrayList<MultipleChoiceQuestion> questions = new ArrayList<>();

        try {
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MultipleChoiceQuestion question = new MultipleChoiceQuestion();

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

    public void atualizar(MultipleChoiceQuestion question) {
        String sql = "update multiple_choice_question " +
                "set tipo = ?, enunciado = ?, gabarito = ?, assunto = ?, nivel_dificuldade = ? " +
                "where codigo = ?";

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
        String sql = "delete from multiple_choice_question where codigo = ?";

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