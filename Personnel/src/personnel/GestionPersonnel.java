package personnel;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import jdbc.JDBC;
import jdbc.LigueDAO;

/**
 * Gestion du personnel. Un seul objet de cette classe existe.
 * Il n'est pas possible d'instancier directement cette classe,
 * la méthode {@link #getGestionPersonnel getGestionPersonnel}
 * le fait automatiquement et retourne toujours le même objet.
 * Dans le cas où {@link #sauvegarder()} a été appelé lors
 * d'une exécution précédente, c'est l'objet sauvegardé qui est
 * retourné.
 */

public class GestionPersonnel implements Serializable {

	private static final long serialVersionUID = -105283113987886425L;
	private static GestionPersonnel gestionPersonnel = null;
	private SortedSet<Ligue> ligues;
	private Employe root = new Employe(this, null, "root", "", "", "toor");
	public final static int SERIALIZATION = 1, JDBC = 2;
	public static int TYPE_PASSERELLE = JDBC;
	private static Passerelle passerelle = TYPE_PASSERELLE == JDBC ? new JDBC()
			: new serialisation.Serialization();

	/**
	 * Retourne l'unique instance de cette classe.
	 * Crée cet objet s'il n'existe déjà.
	 * 
	 * @return l'unique objet de type {@link GestionPersonnel}.
	 */

	public static GestionPersonnel getGestionPersonnel() {
		if (gestionPersonnel == null) {
			gestionPersonnel = passerelle.getGestionPersonnel();
			if (gestionPersonnel == null)
				gestionPersonnel = new GestionPersonnel();
		}
		return gestionPersonnel;
	}

	public GestionPersonnel() {
		if (gestionPersonnel != null) {
			throw new RuntimeException("Vous ne pouvez créer qu'une seuls instance de cet objet.");
		}
		ligues = new TreeSet<>();
		gestionPersonnel = this;
	}

	public void sauvegarder() throws SauvegardeImpossible {
		passerelle.sauvegarderGestionPersonnel(this);
	}

	/**
	 * Retourne la ligue dont administrateur est l'administrateur,
	 * null s'il n'est pas un administrateur.
	 * 
	 * @param administrateur l'administrateur de la ligue recherchée.
	 * @return la ligue dont administrateur est l'administrateur.
	 */

	public Ligue getLigue(Employe administrateur) {
		if (administrateur.estAdmin(administrateur.getLigue()))
			return administrateur.getLigue();
		else
			return null;
	}

	/**
	 * Retourne toutes les ligues enregistrées.
	 * 
	 * @return toutes les ligues enregistrées.
	 */

	public SortedSet<Ligue> getLigues() {
		return Collections.unmodifiableSortedSet(ligues);
	}

	public Ligue addLigue(String nom) throws SauvegardeImpossible {
		Ligue ligue = new Ligue(this, nom);
		ligues.add(ligue);
		LigueDAO.connect().insert(ligue);
		return ligue;
	}

	public Ligue addLigue(int id, String nom) {
		Ligue ligue = new Ligue(this, id, nom);
		ligues.add(ligue);
		LigueDAO.connect().insert(ligue);
		return ligue;
	}

	void remove(Ligue ligue) {
		ligues.remove(ligue);
		LigueDAO.connect().delete(ligue);
	}

	int insert(Ligue ligue) throws SauvegardeImpossible {
		return passerelle.insert(ligue);
	}

	/**
	 * Retourne le root (super-utilisateur).
	 * 
	 * @return le root.
	 */

	public Employe getRoot() {
		return root;
	}

	public void setRoot() {
		try {
			GestionPersonnel.passerelle.setRoot(root);
		} catch (SauvegardeImpossible exception) {
			System.out.println("Erreur lors de la sauvegarde du root : " + exception.getMessage());
		}
	}
}
