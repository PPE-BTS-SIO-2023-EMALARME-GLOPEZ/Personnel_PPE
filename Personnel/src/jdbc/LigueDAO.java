package jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import personnel.*;

public class LigueDAO {

    private Connection connection;

    public LigueDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(Ligue ligue) throws SQLException {
        String requete = "INSERT INTO ligue (nom_ligue, administrateur) VALUES (?, ?)";
        PreparedStatement instruction = connection.prepareStatement(requete);
        instruction.setString(1, ligue.getNom());
        // instruction.setString(2, ligue.getAdministrateur().getId());
        // instruction.executeUpdate();
    }

}
