package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.util.TreeSet;

import personnel.*;

public class EmployeDAO {

    private Connection connection;

    public EmployeDAO(Connection connection) {
        this.connection = connection;
    }

    public static EmployeDAO connect() {
        JDBC jdbc = new JDBC();
        return new EmployeDAO(jdbc.getConnection());
    }

    private static Date toSqlDate(LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }

    public void insert(Employe employe) {

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
            if (instruction != null) {
                try {
                    instruction.close();
                } catch (SQLException exception) {
                    System.out.println("Erreur lors de l'insertion de l'employé : " + exception.getMessage());
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException exception) {
                    System.out.println("Erreur lors de l'insertion de l'employé : " + exception.getMessage());
                }
            }
        }
    }

    public void update(Employe employe) {

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
            if (instruction != null) {
                try {
                    instruction.close();
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la modification de l'employé : " + e.getMessage());
                }
            }
        }
    }

    public void delete(Employe employe) {
        String requete = "DELETE FROM employe WHERE id_employe = ?";
        PreparedStatement instruction = null;
        try {
            instruction = connection.prepareStatement(requete);
            instruction.setInt(1, employe.getId());
            instruction.executeUpdate();
        } catch (SQLException exception) {
            System.out.println("Erreur lors de la suppression de l'employé : " + exception.getMessage());
        } finally {
            if (instruction != null) {
                try {
                    instruction.close();
                } catch (Exception exception) {
                    System.out.println("Erreur lors de la suppression de l'employé : " + exception.getMessage());
                }
            }
        }
    }

    public TreeSet<Employe> getEmployesByLigue(Ligue ligue) {
        String requete = "SELECT * FROM employe WHERE id_ligue = ?";
        TreeSet<Employe> employes = new TreeSet<>();
        GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
        PreparedStatement instruction = null;
        ResultSet resultSet = null;

        try {
            instruction = connection.prepareStatement(requete);
            instruction.setInt(1, ligue.getId());
            resultSet = instruction.executeQuery();

            while (resultSet.next()) {
                // On récupére les données de l'employe
                String nom = resultSet.getString("nom_employe");
                String prenom = resultSet.getString("prenom_employe");
                String password = resultSet.getString("password");
                String mail = resultSet.getString("mail");

                LocalDate date_arrivee = resultSet.getDate("date_arrivee").toLocalDate();
                LocalDate date_depart = resultSet.getDate("date_depart") != null
                        ? resultSet.getDate("date_depart").toLocalDate()
                        : null;

                // On crée l'employe
                Employe employe = new Employe(gestionPersonnel, ligue, nom, prenom, mail, password, date_arrivee,
                        date_depart);

                // On ajoute l'employe a la liste des employes
                employes.add(employe);
            }
        } catch (SQLException exception) {
            System.out.println("Erreur lors de la récupération des employés : " + exception.getMessage());
        } finally {
            if (instruction != null) {
                try {
                    instruction.close();
                } catch (Exception exception) {
                    System.out.println("Erreur lors de la récupération des employés : " + exception.getMessage());
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception exception) {
                    System.out.println("Erreur lors de la récupération des employés : " + exception.getMessage());
                }
            }
        }

        return employes;
    }

}
