package model.DAO;

import model.entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionDAO {

    public void insert(Question question) throws SQLException {
        String sql = "INSERT INTO question (question_type, statement, answer_key, topic, subject_id, difficulty, alternatives, expected_lines) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setStatementParams(st, question);
            st.executeUpdate();
            
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    question.setQuestionId(rs.getInt(1));
                }
            }
        }
    }

    public void update(Question question) throws SQLException {
        String sql = "UPDATE question SET question_type = ?, statement = ?, answer_key = ?, topic = ?, "
                   + "subject_id = ?, difficulty = ?, alternatives = ?, expected_lines = ? "
                   + "WHERE question_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            
            setStatementParams(st, question);
            st.setInt(9, question.getQuestionId()); 
            
            st.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM question WHERE question_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public List<Question> findAll() throws SQLException {
        String sql = "SELECT * FROM question";
        List<Question> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            while (rs.next()) {
                list.add(instantiateQuestion(rs));
            }
        }
        return list;
    }

    public Question findById(int id) throws SQLException {
        String sql = "SELECT * FROM question WHERE question_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setInt(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return instantiateQuestion(rs);
                }
            }
        }
        return null;
    }

    public List<Question> findBySubject(int subjectId) throws SQLException {
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
        }
        return list;
    }

    public List<Question> findByTopic(String topic) throws SQLException {
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
        }
        return list;
    }

    public List<Question> findByDifficulty(String difficulty) throws SQLException {
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
        }
        return list;
    }

    public List<Question> findRandomByCriteria(int subjectId, String difficulty, int limit) throws SQLException {
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
        }
        return list;
    }

    public List<Question> findByExam(int examId) throws SQLException {
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
        }
        return list;
    }
    public List<Question> findByTeacher(int teacherId) throws SQLException {
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

        //Lógica de Polimorfismo
        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            String alternativesStr = String.join(";;", mcq.getAlternatives());
            
            st.setString(7, alternativesStr);
            st.setNull(8, Types.INTEGER);
            
        } else if (question instanceof DiscursiveQuestion) {
            DiscursiveQuestion dq = (DiscursiveQuestion) question;
            
            st.setNull(7, Types.VARCHAR);
            st.setInt(8, dq.getExpectedLines());
        }
        if (question.getTeacher() != null) {
            st.setInt(9, question.getTeacher().getUser().getIdUser()); 
        } else {
            st.setNull(9, Types.INTEGER);
        }
    }

    private Question instantiateQuestion(ResultSet rs) throws SQLException {
        String type = rs.getString("question_type");
        
        //A FACTORY ASSUME A CRIAÇÃO
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

        //Preenchimento das colunas específicas usando polimorfismo
        if (question instanceof MultipleChoiceQuestion) {
            String alts = rs.getString("alternatives");
            if (alts != null && !alts.isEmpty()) {
                ((MultipleChoiceQuestion) question).setAlternatives(Arrays.asList(alts.split(";;")));
            }
        } else if (question instanceof DiscursiveQuestion) {
            ((DiscursiveQuestion) question).setExpectedLines(rs.getInt("expected_lines"));
        }
        
        return question;
    } 
}