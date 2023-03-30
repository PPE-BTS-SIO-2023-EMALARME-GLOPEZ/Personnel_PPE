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

    public static LigueDAO connect() {
        JDBC jdbc = new JDBC();
        return new LigueDAO(jdbc.getConnection());
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

            return ligues;

        } catch (SQLException exception) {
            System.out.println("Erreur lors de l'initialisation des ligues : " + exception.getMessage());
            return new TreeSet<>();
        }
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
