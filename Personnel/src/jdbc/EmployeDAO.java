package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;

import personnel.*;

public class EmployeDAO {

    private Connection connection;

    public EmployeDAO(Connection connection) {
        this.connection = connection;
    }

    private static Date toSqlDate(LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }

    public void insert(Employe employe) throws SQLException {

        String requete = "INSERT INTO employe (nom_employe, prenom_employe, password, mail, date_arrivee, date_depart, id_ligue) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement instruction = null;
        ResultSet resultSet = null;

        try {
            instruction = connection.prepareStatement(requete, instruction.RETURN_GENERATED_KEYS);

            instruction.setString(1, employe.getNom());
            instruction.setString(2, employe.getPrenom());
            instruction.setString(3, employe.getPassword());
            instruction.setString(4, employe.getMail());

            instruction.setDate(5, EmployeDAO.toSqlDate(employe.getDateArrivee()));
            instruction.setDate(6, EmployeDAO.toSqlDate(employe.getDateDepart()));

            instruction.setInt(7, employe.getLigue().getId());

            instruction.executeUpdate();

            resultSet = instruction.getGeneratedKeys();

            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                employe.setId(id);
            }

        } catch (SQLException exception) {
            System.out.println("Erreur lors de l'insertion de l'employé : " + exception.getMessage());
        } finally {
            instruction.close();
            resultSet.close();
        }
    }

    public void update(Employe employe) throws SQLException {

        PreparedStatement instruction = null;
        String requete = "UPDATE employe SET nom_employe = ?, prenom_employe = ?, password = ?, mail = ?, date_depart = ?, date_arrivee = ?, id_ligue = ? WHERE id_employe = ? ";

        try {
            instruction = connection.prepareStatement(requete);

            instruction.setString(1, employe.getNom());
            instruction.setString(2, employe.getPrenom());
            instruction.setString(3, employe.getPassword());
            instruction.setString(4, employe.getMail());

            instruction.setDate(5, EmployeDAO.toSqlDate(employe.getDateArrivee()));
            instruction.setDate(6, EmployeDAO.toSqlDate(employe.getDateDepart()));

            instruction.setInt(7, employe.getLigue().getId());

            instruction.executeUpdate();
        } catch (SQLException exception) {
            System.out.println("Erreur lors de la modification de l'employé : " + exception.getMessage());
        } finally {
            instruction.close();
        }
    }
}
