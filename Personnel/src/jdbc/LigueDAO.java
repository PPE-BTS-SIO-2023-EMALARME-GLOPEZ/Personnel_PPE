package jdbc;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import personnel.*;

public class LigueDAO {

    private Connection connection;

    public LigueDAO(Connection connection) {
        this.connection = connection;
    }

    public TreeSet<Ligue> init() {

        TreeSet<Ligue> ligues = new TreeSet<>();
        String requete = "SELECT * FROM ligue";
        PreparedStatement instruction = null;
        ResultSet resultSet = null;

        try {
            instruction = connection.prepareStatement(requete);
            resultSet = instruction.executeQuery();

            while (resultSet.next()) {
                // Création de la ligue
                GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
                int id = resultSet.getInt("id_ligue");
                String nom = resultSet.getString("nom_ligue");
                Ligue ligue = new Ligue(gestionPersonnel, id, nom);

                // Ajout des employes
                EmployeDAO employeDAO = new EmployeDAO(connection);
                TreeSet<Employe> employes = employeDAO.getEmployesByLigue(ligue);
                ligue.initEmployes(employes);

                // Ajout de l'administrateur
                int idAdministrateur = resultSet.getInt("administrateur");
                Employe admin = ligue.getEmployeById(idAdministrateur);
                ligue.setAdministrateur(admin);

                // Ajout de la ligue à la liste des ligues
                ligues.add(ligue);
            }

        } catch (SQLException exception) {
            System.out.println("Erreur lors de l'initialisation des ligues : " + exception.getMessage());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException exception) {
                    System.out.println("Erreur lors de la fermeture du ResultSet : " + exception.getMessage());
                }
            }
            if (instruction != null) {
                try {
                    instruction.close();
                } catch (SQLException exception) {
                    System.out.println("Erreur lors de la fermeture de l'instruction : " + exception.getMessage());
                }
            }
        }

        return ligues;
    }

    public void insert(Ligue ligue) throws SQLException {
        String requete = "INSERT INTO ligue (nom_ligue, administrateur) VALUES (?, ?)";
        PreparedStatement instruction = connection.prepareStatement(requete);
        instruction.setString(1, ligue.getNom());
        instruction.setInt(2, ligue.getAdministrateur().getId());
        instruction.executeUpdate();

        ResultSet generatedKeys = instruction.getGeneratedKeys();
        if (generatedKeys.next()) {
            int id = generatedKeys.getInt(1);
            ligue.setId(id);
        }
    }

    public void update(Ligue ligue) throws SQLException {
        String requete = "UPDATE ligue SET nom = ?, administrateur_id = ? WHERE id = ?";
        PreparedStatement instruction = connection.prepareStatement(requete);
        instruction.setString(1, ligue.getNom());
        instruction.setInt(2, ligue.getAdministrateur().getId());
        instruction.setInt(3, ligue.getId());
        instruction.executeUpdate();
    }

    public void delete(Ligue ligue) throws SQLException {
        String requete = "DELETE FROM ligue WHERE id = ?";
        PreparedStatement instruction = connection.prepareStatement(requete);
        instruction.setInt(1, ligue.getId());
        instruction.executeUpdate();
    }

}
