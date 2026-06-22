package model.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.entities.Exam;

public class ExamDAO {

    public void inserir(Exam exam) {
        String sql = "insert into exam (data_de_criacao, semestre) values (?, ?)";

        try {
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(sql);

            if (exam.getDataDeCriacao() != null) {
                stmt.setDate(1, Date.valueOf(exam.getDataDeCriacao()));
            } else {
                stmt.setDate(1, null);
            }

            stmt.setString(2, exam.getSemestre());

            stmt.executeUpdate();

            stmt.close();
            conexao.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Exam> listar() {
        String sql = "select * from exam";

        ArrayList<Exam> exams = new ArrayList<>();

        try {
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Exam exam = new Exam();

                exam.setCodigo(rs.getInt("codigo"));

                Date data = rs.getDate("data_de_criacao");
                if (data != null) {
                    exam.setDataDeCriacao(data.toLocalDate());
                }

                exam.setSemestre(rs.getString("semestre"));

                exams.add(exam);
            }

            rs.close();
            stmt.close();
            conexao.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exams;
    }

    public void atualizar(Exam exam) {
        String sql = "update exam set data_de_criacao = ?, semestre = ? where codigo = ?";

        try {
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement stmt = conexao.prepareStatement(sql);

            if (exam.getDataDeCriacao() != null) {
                stmt.setDate(1, Date.valueOf(exam.getDataDeCriacao()));
            } else {
                stmt.setDate(1, null);
            }

            stmt.setString(2, exam.getSemestre());
            stmt.setInt(3, exam.getCodigo());

            stmt.executeUpdate();

            stmt.close();
            conexao.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int codigo) {
        String sql = "delete from exam where codigo = ?";

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