package jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.SortedSet;
import java.util.TreeSet;

import personnel.*;

public class LigueDAO {

    private Connection connection;

    public LigueDAO(Connection connection) {
        this.connection = connection;
    }

    /*
     * public TreeSet<Ligue> init() {
     * 
     * SortedSet<Ligue> ligues = new TreeSet<>();
     * String requete = "SELECT * FROM ligue";
     * PreparedStatement instruction = null;
     * ResultSet resultSet = null;
     * 
     * try {
     * instruction = connection.prepareStatement(requete);
     * resultSet = instruction.executeQuery();
     * 
     * while(resultSet.next())
     * {
     * int id = resultSet.getInt("id_ligue");
     * String nom = resultSet.getString("nom_ligue");
     * Employe administrateur = resultSet.getInt("administrateur");
     * Ligue ligue = new Ligue();
     * }
     * 
     * }
     * }
     */

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
