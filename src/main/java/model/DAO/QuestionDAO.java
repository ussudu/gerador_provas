package model.DAO;

import model.entities.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public void insert(Question question) {
        String sql = "INSERT INTO question (question_type, statement, answer_key, topic, subject_id, difficulty, alt_a, alt_b, alt_c, alt_d, expected_lines, teacher_id) "
           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setStatementParams(st, question);
            st.executeUpdate();
            
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    question.setQuestionId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir a questão no banco de dados: " + e.getMessage(), e);
        }
    }

    public void update(Question question) {
        String sql = "UPDATE question SET question_type = ?, statement = ?, answer_key = ?, topic = ?, "
           + "subject_id = ?, difficulty = ?, alt_a = ?, alt_b = ?, alt_c = ?, alt_d = ?, expected_lines = ?, teacher_id = ? "
           + "WHERE question_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            
            setStatementParams(st, question);
            st.setInt(13, question.getQuestionId()); // CORREÇÃO DE ÍNDICE: O ID agora é o 13º parâmetro
            
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar a questão no banco de dados: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM question WHERE question_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir a questão. Verifique se ela não está vinculada a uma prova. Detalhes: " + e.getMessage(), e);
        }
    }

    public List<Question> findAll() {
        String sql = "SELECT * FROM question";
        List<Question> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            while (rs.next()) {
                list.add(instantiateQuestion(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as questões: " + e.getMessage(), e);
        }
        return list;
    }

    public Question findById(int id) {
        String sql = "SELECT * FROM question WHERE question_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setInt(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return instantiateQuestion(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar a questão pelo ID: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Question> findBySubject(int subjectId) {
        String sql = "SELECT * FROM question WHERE subject_id = ?";
        List<Question> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setInt(1, subjectId);
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar questões por disciplina: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Question> findByTopic(String topic) {
        String sql = "SELECT * FROM question WHERE topic = ?";
        List<Question> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setString(1, topic);
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar questões por tópico: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Question> findByDifficulty(String difficulty) {
        String sql = "SELECT * FROM question WHERE difficulty = ?";
        List<Question> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setString(1, difficulty);
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar questões por dificuldade: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Question> findRandomByCriteria(int subjectId, String difficulty, int limit) {
        String sql = "SELECT * FROM question WHERE subject_id = ? AND difficulty = ? ORDER BY RAND() LIMIT ?";
        List<Question> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setInt(1, subjectId);
            st.setString(2, difficulty);
            st.setInt(3, limit);
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar questões aleatórias para a prova: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Question> findByExam(int examId) {
        String sql = "SELECT q.* FROM question q "
                   + "INNER JOIN exam_question eq ON q.question_id = eq.question_id "
                   + "WHERE eq.exam_id = ?";
                   
        List<Question> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setInt(1, examId);
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar as questões vinculadas à prova: " + e.getMessage(), e);
        }
        return list;
    }
    
    public List<Question> findByTeacher(int teacherId) {
        String sql = "SELECT * FROM question WHERE teacher_id = ?";
        List<Question> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setInt(1, teacherId);
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar as questões deste professor: " + e.getMessage(), e);
        }
        return list;
    }

    private void setStatementParams(PreparedStatement st, Question question) throws SQLException {
        String typeFlag = (question instanceof MultipleChoiceQuestion) ? "MULTIPLE_CHOICE" : "DISCURSIVE";
        
        st.setString(1, typeFlag);
        st.setString(2, question.getStatement());
        st.setString(3, question.getAnswerKey());
        st.setString(4, question.getTopic());
        st.setInt(5, question.getSubject().getIdSubject());
        st.setString(6, question.getDifficulty().name());

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            
            st.setString(7, mcq.getAlternativeA());
            st.setString(8, mcq.getAlternativeB());
            st.setString(9, mcq.getAlternativeC());
            st.setString(10, mcq.getAlternativeD());
            
            st.setNull(11, Types.INTEGER);
            
        } else if (question instanceof DiscursiveQuestion) {
            DiscursiveQuestion dq = (DiscursiveQuestion) question;
            
            st.setNull(7, Types.VARCHAR); 
            st.setNull(8, Types.VARCHAR); 
            st.setNull(9, Types.VARCHAR); 
            st.setNull(10, Types.VARCHAR); 
            
            st.setInt(11, dq.getExpectedLines());
        }

        if (question.getTeacher() != null && question.getTeacher().getUser() != null) {
            st.setInt(12, question.getTeacher().getUser().getIdUser()); 
        } else {
            st.setNull(12, Types.INTEGER);
        }
    }

    private Question instantiateQuestion(ResultSet rs) throws SQLException {
        String type = rs.getString("question_type");
        
        Question question = QuestionFactory.createQuestion(type);

        question.setQuestionId(rs.getInt("question_id"));
        question.setStatement(rs.getString("statement"));
        question.setAnswerKey(rs.getString("answer_key"));
        question.setTopic(rs.getString("topic"));
        question.setDifficulty(Difficulty.valueOf(rs.getString("difficulty"))); 

        Subject subject = new Subject();
        subject.setIdSubject(rs.getInt("subject_id"));
        question.setSubject(subject);

        Teacher teacher = new Teacher();
        User user = new User();
        user.setIdUser(rs.getInt("teacher_id"));
        teacher.setUser(user);
        question.setTeacher(teacher);

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            
            mcq.setAlternativeA(rs.getString("alt_a"));
            mcq.setAlternativeB(rs.getString("alt_b"));
            mcq.setAlternativeC(rs.getString("alt_c"));
            mcq.setAlternativeD(rs.getString("alt_d"));
            
        } else if (question instanceof DiscursiveQuestion) {
            ((DiscursiveQuestion) question).setExpectedLines(rs.getInt("expected_lines"));
        }
        
        return question;
    } 
}