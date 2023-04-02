package jdbc;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import personnel.*;

public class LigueDAO {

    private Connection connection;

    public LigueDAO() {
        JDBC jdbc = (JDBC) GestionPersonnel.getPasserelle();
        this.connection = jdbc.getConnection();
    }

    public static LigueDAO connect() {
        return new LigueDAO();
    }

    public TreeSet<Ligue> init() {

        String requete = "SELECT * FROM ligue";

        try (PreparedStatement instruction = connection.prepareStatement(requete);
                ResultSet resultSet = instruction.executeQuery()) {

            TreeSet<Ligue> ligues = new TreeSet<>();

            while (resultSet.next()) {
                // Création de la ligue
                GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
                int id = resultSet.getInt("id_ligue");
                String nom = resultSet.getString("nom_ligue");

                Ligue ligue = new Ligue(gestionPersonnel, id, nom);

                // Ajout des employes
                TreeSet<Employe> employes = EmployeDAO.connect().getEmployesByLigue(ligue);

                ligue.setListeEmployes(employes);

                // Ajout de l'administrateur
                int idAdministrateur = resultSet.getInt("administrateur");
                Employe admin = null;
                if (idAdministrateur == 0 || idAdministrateur == 3) {
                    admin = gestionPersonnel.getRoot();
                    ligue.setAdministrateur(admin);
                } else {
                    admin = ligue.getEmployeById(idAdministrateur);
                    ligue.setAdministrateur(admin);
                }

                // Ajout de la ligue à la liste des ligues
                ligues.add(ligue);
            }

            return ligues;

        } catch (SQLException exception) {
            System.out.println("Erreur lors de l'initialisation des ligues : " + exception.getMessage());
            return new TreeSet<>();
        }
    }

    public LigueDAO insert(Ligue ligue) {
        String requete = "INSERT INTO ligue (nom_ligue, administrateur) VALUES (?, ?)";
        PreparedStatement instruction = null;
        ResultSet generatedKeys = null;
        try {
            instruction = connection.prepareStatement(requete, instruction.RETURN_GENERATED_KEYS);
            instruction.setString(1, ligue.getNom());
            instruction.setInt(2, ligue.getAdministrateur().getId());
            instruction.executeUpdate();

            generatedKeys = instruction.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                ligue.setId(id);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion de la ligue : " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (instruction != null) {
                    instruction.close();
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
            }
        }
        return this;
    }

    public LigueDAO update(Ligue ligue) {
        String requete = "UPDATE ligue SET nom_ligue = ?, administrateur = ? WHERE id_ligue = ?";
        try {
            PreparedStatement instruction = connection.prepareStatement(requete);
            instruction.setString(1, ligue.getNom());
            instruction.setInt(2, ligue.getAdministrateur().getId());
            instruction.setInt(3, ligue.getId());
            instruction.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la ligue : " + e.getMessage());
        }
        return this;
    }

    public LigueDAO delete(Ligue ligue) {
        String requete = "DELETE FROM ligue WHERE id_ligue = ?";
        PreparedStatement instruction = null;
        try {
            instruction = connection.prepareStatement(requete);
            instruction.setInt(1, ligue.getId());
            instruction.executeUpdate();
        } catch (SQLException exception) {
            System.err.println("Erreur lors de la suppression de la ligue : " + exception.getMessage());
        } finally {
            if (instruction != null) {
                try {
                    instruction.close();
                } catch (SQLException exception) {
                    System.err.println("Erreur lors de la fermeture de l'instruction : " + exception.getMessage());
                }
            }
        }
        return this;
    }

}
