package personnel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import jdbc.EmployeDAO;
import jdbc.LigueDAO;

/**
 * Représente une ligue. Chaque ligue est reliée à une liste
 * d'employés dont un administrateur. Comme il n'est pas possible
 * de créer un employé sans l'affecter à une ligue, le root est
 * l'administrateur de la ligue jusqu'à ce qu'un administrateur
 * lui ait été affecté avec la fonction {@link #setAdministrateur}.
 */

public class Ligue implements Serializable, Comparable<Ligue> {
	private static final long serialVersionUID = 1L;
	private int id = -1; // Reste à -1 tant que l'objet n'est pas enregistré dans la BDD
	private String nom;
	private SortedSet<Employe> employes;
	private Employe administrateur;
	private GestionPersonnel gestionPersonnel;

	/**
	 * Crée une ligue.
	 * 
	 * @param nom le nom de la ligue.
	 */

	public Ligue(GestionPersonnel gestionPersonnel, String nom) throws SauvegardeImpossible {
		this(gestionPersonnel, -1, nom);
		this.id = gestionPersonnel.insert(this);
	}

	public Ligue(GestionPersonnel gestionPersonnel, int id, String nom) {
		this.nom = nom;
		employes = new TreeSet<>();
		this.gestionPersonnel = gestionPersonnel;
		administrateur = gestionPersonnel.getRoot();
		this.id = id;
	}

	public void setListeEmployes(TreeSet<Employe> employes) {
		this.employes = employes;
	}

	/**
	 * Retourne le nom de la ligue.
	 * 
	 * @return le nom de la ligue.
	 */

	public String getNom() {
		return nom;
	}

	/**
	 * Change le nom.
	 * 
	 * @param nom le nouveau nom de la ligue.
	 */

	public void setNom(String nom) {
		this.nom = nom;
		LigueDAO.make().update(this);
	}

	/**
	 * Retourne l'administrateur de la ligue.
	 * 
	 * @return l'administrateur de la ligue.
	 */

	public Employe getAdministrateur() {
		return administrateur;
	}

	/**
	 * Fait de administrateur l'administrateur de la ligue.
	 * Lève DroitsInsuffisants si l'administrateur n'est pas
	 * un employé de la ligue ou le root. Révoque les droits de l'ancien
	 * administrateur.
	 * 
	 * @param administrateur le nouvel administrateur de la ligue.
	 */

	public void setAdministrateur(Employe administrateur) {
		Employe root = gestionPersonnel.getRoot();
		if (administrateur != root && administrateur.getLigue() != this)
			throw new DroitsInsuffisants();
		this.administrateur = administrateur;
		LigueDAO.make().update(this);
	}

	/**
	 * Retourne les employés de la ligue.
	 * 
	 * @return les employés de la ligue dans l'ordre alphabétique.
	 */

	public SortedSet<Employe> getEmployes() {
		return Collections.unmodifiableSortedSet(this.employes);
	}

	public Employe getEmployeById(int id) {
		for (Employe employe : employes) {
			if (employe.getId() == id) {
				return employe;
			}
		}
		return null; // si aucun employé avec cet ID n'est trouvé
	}

	/**
	 * // * Ajoute un employé dans la ligue. Cette méthode
	 * est le seul moyen de créer un employé.
	 * 
	 * @param nom      le nom de l'employé.
	 * @param prenom   le prénom de l'employé.
	 * @param mail     l'adresse mail de l'employé.
	 * @param password le password de l'employé.
	 * @return l'employé créé.
	 */

	public Employe addEmploye(String nom, String prenom, String mail, String password) // Contrat en CDI
	{
		Employe employe = new Employe(this.gestionPersonnel, this, nom, prenom, mail, password);
		employes.add(employe);
		EmployeDAO.make().insert(employe);
		return employe;
	}

	// Contrat en CDI
	public Employe addEmploye(String nom, String prenom, String mail, String password, LocalDate dateFinContrat) {
		Employe employe = new Employe(this.gestionPersonnel, this, nom, prenom, mail, password, dateFinContrat);
		employes.add(employe);
		EmployeDAO.make().insert(employe);
		return employe;
	}

	public void removeEmploye(Employe employe) {
		employes.remove(employe);
		EmployeDAO.make().delete(employe);
	}

	/**
	 * Supprime la ligue, entraîne la suppression de tous les employés
	 * de la ligue.
	 */

	public void remove() {
		gestionPersonnel.remove(this);
		System.out.println(this);
	}

	/**
	 * Retourne l'ID de la ligue
	 * 
	 * @return l'ID de la ligue
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Fixe l'ID de la ligue, cet ID doit être celui retourné par JDBC
	 * après enregistrement de la ligue dans la base de donnée.
	 * Cette méthode ne doit être utilisée que par la classe LigueDAO
	 * 
	 * @param id l'identifiant de la ligue en base de donnée
	 */
	public void setId(int id) {
		this.id = id;
		LigueDAO.make().update(this);
	}

	@Override
	public int compareTo(Ligue autre) {
		return getNom().compareTo(autre.getNom());
	}

	@Override
	public String toString() {
		return nom;
	}
}
