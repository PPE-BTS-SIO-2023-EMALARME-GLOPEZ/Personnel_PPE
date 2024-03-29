package jdbc;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import personnel.*;

public class JDBC implements Passerelle {
	Connection connection;

	public JDBC() {
		try {
			Class.forName(Credentials.getDriverClassName());
			connection = DriverManager.getConnection(Credentials.getUrl(), Credentials.getUser(),
					Credentials.getPassword());
		} catch (ClassNotFoundException e) {
			System.out.println("Pilote JDBC non installé.");
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	@Override
	public GestionPersonnel getGestionPersonnel() {
		GestionPersonnel gestionPersonnel = new GestionPersonnel();
		getOrCreateRoot();
		gestionPersonnel.setLigues(LigueDAO.make().init());
		return gestionPersonnel;
	}

	@Override
	public void sauvegarderGestionPersonnel(GestionPersonnel gestionPersonnel) throws SauvegardeImpossible {
		close();
	}

	public void close() throws SauvegardeImpossible {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			throw new SauvegardeImpossible(e);
		}
	}

	@Override
	public int insert(Ligue ligue) throws SauvegardeImpossible {
		try {
			PreparedStatement instruction;
			instruction = connection.prepareStatement("insert into ligue (nom_ligue) values(?)",
					Statement.RETURN_GENERATED_KEYS);
			instruction.setString(1, ligue.getNom());
			instruction.executeUpdate();
			ResultSet id = instruction.getGeneratedKeys();
			id.next();
			return id.getInt(1);
		} catch (SQLException exception) {
			exception.printStackTrace();
			throw new SauvegardeImpossible(exception);
		}
	}

	public void setRoot(Employe root) {
		try {
			PreparedStatement requete = connection.prepareStatement(
					"insert into employe (nom_employe, prenom_employe, mail, password) values(?,?,?,?)");

			requete.setString(1, root.getNom());
			requete.setString(2, root.getPrenom());
			requete.setString(3, root.getMail());
			requete.setString(4, root.getPassword());
			requete.executeUpdate();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}

	public Employe getOrCreateRoot() {
		Employe root = null;
		try {
			PreparedStatement requete = connection.prepareStatement("select * from employe where nom_employe = ?");
			requete.setString(1, "root");
			ResultSet resultat = requete.executeQuery();
			if (resultat.next()) {
				// Le root existe déjà en base de données, on récupére son ID pour le donner à
				// l'objet root
				GestionPersonnel.getGestionPersonnel().getRoot().setId(resultat.getInt("id_employe"));
			} else {
				// Le root n'existe pas encore en base de données, on le crée
				root = GestionPersonnel.getGestionPersonnel().getRoot();
				setRoot(root);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return root;
	}

	public Connection getConnection() {
		return this.connection;
	}
}
