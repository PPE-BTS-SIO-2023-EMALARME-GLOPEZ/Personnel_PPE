package jdbc;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import personnel.*;

public class LigueDAO {

    private Connection connection;

    /**
     * Constructeur de la classe LigueDAO. Initialise la connexion à la base de
     * données.
     */
    public LigueDAO() {
        JDBC jdbc = (JDBC) GestionPersonnel.getPasserelle();
        this.connection = jdbc.getConnection();
    }

    /**
     * Factory method qui retourne une instance de LigueDAO.
     *
     * @return une instance de LigueDAO.
     */
    public static LigueDAO make() {
        return new LigueDAO();
    }

    /**
     * Récupère toutes les ligues de la base de données, avec leurs employés et
     * administrateurs, et les ajoute à un TreeSet.
     * 
     * @return Un TreeSet de toutes les ligues de la base de données.
     *         Si une erreur SQL survient, renvoie un TreeSet vide.
     */
    public TreeSet<Ligue> init() {

        String requete = "SELECT * FROM ligue";

        try (PreparedStatement instruction = connection.prepareStatement(requete);
                ResultSet resultSet = instruction.executeQuery()) {

            TreeSet<Ligue> ligues = new TreeSet<>();

            while (resultSet.next()) {
                // Création de la ligue
                Ligue ligue = createLigue(resultSet);

                // Ajout des employes
                TreeSet<Employe> employes = EmployeDAO.make().getEmployesByLigue(ligue);
                ligue.setListeEmployes(employes);

                // Ajout de l'administrateur
                Employe admin = getLigueAdministrateur(resultSet, ligue);
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

    /**
     * Crée une instance de la classe Ligue à partir des informations contenues dans
     * un objet ResultSet.
     *
     * @param resultSet l'objet ResultSet contenant les informations de la ligue à
     *                  créer
     * @return une instance de la classe Ligue créée à partir des informations du
     *         ResultSet
     * @throws SQLException si une erreur survient lors de l'accès aux données dans
     *                      le ResultSet
     */

    private Ligue createLigue(ResultSet resultSet) throws SQLException {
        GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
        int id = resultSet.getInt("id_ligue");
        String nom = resultSet.getString("nom_ligue");

        Ligue ligue = new Ligue(gestionPersonnel, id, nom);
        return ligue;
    }

    /**
     * Récupère l'employé qui est l'administrateur de la ligue spécifiée dans le
     * ResultSet et définit cet employé comme administrateur de la ligue.
     * Si l'administrateur de la ligue est l'employé racine, renvoie l'employé
     * racine.
     * 
     * @param resultSet le ResultSet contenant les informations de la ligue, y
     *                  compris l'identifiant de l'administrateur.
     * @param ligue     la ligue pour laquelle on souhaite récupérer
     *                  l'administrateur.
     * @return l'employé qui est l'administrateur de la ligue, ou l'employé racine
     *         si l'administrateur est l'employé racine.
     * 
     */

    private Employe getLigueAdministrateur(ResultSet resultSet, Ligue ligue) {
        GestionPersonnel gestionPersonnel = GestionPersonnel.getGestionPersonnel();
        int idAdministrateur = 0;
        try {
            idAdministrateur = resultSet.getInt("administrateur");
        } catch (SQLException exception) {
            System.err.println(
                    "Erreur lors de la récupération de l'administrateur de la ligue : " + exception.getMessage());
        }
        Employe admin = null;
        if (idAdministrateur == 0 || idAdministrateur == 3) {
            admin = gestionPersonnel.getRoot();
            ligue.setAdministrateur(admin);
        } else {
            admin = ligue.getEmployeById(idAdministrateur);
            ligue.setAdministrateur(admin);
        }
        return admin;
    }

    /**
     * Insère une ligue dans la base de données.
     * 
     * @param ligue la ligue à insérer
     * @return la LigueDAO courante pour permettre les appels en chaine
     */
    public LigueDAO insert(Ligue ligue) {
        String requete = "INSERT INTO ligue (nom_ligue, administrateur) VALUES (?, ?)";
        PreparedStatement instruction = null;
        ResultSet generatedKeys = null;
        try {
            instruction = connection.prepareStatement(requete, instruction.RETURN_GENERATED_KEYS);
            // On prépare la requête SQL en y insérant les valeurs de ligue à ajouter
            instruction.setString(1, ligue.getNom());
            instruction.setInt(2, ligue.getAdministrateur().getId());
            instruction.executeUpdate();

            generatedKeys = instruction.getGeneratedKeys();
            if (generatedKeys.next()) {
                // Si la clé primaire générée automatiquement par la BDD est disponible
                // on l'attribue à l'objet ligue comme identifiant
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

    /**
     * Met à jour la ligue dans la base de donnée
     * 
     * @param ligue la ligue à mettre à jour
     * @return une instance de l'objet LigueDAO pour permettre les appels en chaine
     */
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

    /**
     * Supprime une ligue de la base de données
     * 
     * @param ligue la ligue à supprimer
     * @return l'instance de l'objet LigueDAO pour permettre les appels en chaine
     */
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
