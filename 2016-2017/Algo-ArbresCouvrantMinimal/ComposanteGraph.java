package Graphe;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


/* Ce fichier implémente les arêtes et les sommets */

/**
 * Implémentation d'une arête
 */
class Arete implements Comparable<Arete> {
	Sommet	sommetA;
	Sommet	sommetB;
	int		poids;
	
	
	/** Construit une arête de poids poids et dont les sommets sont sommetA et sommetB */
	public Arete(Sommet sommetA, Sommet sommetB, int poids) {
		super();
		this.sommetA = sommetA;
		this.sommetB = sommetB;
		this.poids = poids;
	}
	
	/** Renvoie le nombre de sommet de l'arête qui sont dans la liste liste */
	int nombreDeSommetsAppartenantA(List<Sommet> liste) {
		int resultat = 0;
		
		for (Sommet sommet : liste) {
			if (concerneSommet(sommet))
				resultat++;
		}
		
		return resultat;
	}
	
	/** Renvoie vrai si sommet est un des deux sommets de l'arête */
	boolean concerneSommet(Sommet sommet) {
		return sommetA == sommet || sommetB == sommet;
	}
	
	/** Si sommet est un des sommets de l'arête, renvoie l'autre sommet de l'arête */
	Sommet getAutreSommet(Sommet sommet) {
		if (sommet == sommetA)
			return sommetB;
		else
			return sommetA;
	}
	
	/** Renvoie la différence du poids de l'arête courante avec l'arête arg0 */
	@Override
	public int compareTo(Arete arg0) {
		return poids - arg0.poids;
	}
	
	/** Dessine l'arête sur Graphics */
	void paint(Graphics graphics) {
		graphics.drawLine(sommetA.x, sommetA.y, sommetB.x, sommetB.y);
		
		graphics.drawString(
				Integer.toString(poids),
				(sommetA.x+sommetB.x)/2,
				(sommetA.y + sommetB.y)/2
				);
	}
	
	/** Renvoie "sommetA sommetB poids" */
	public String toString() {
		return sommetA + " " + sommetB + " " + poids;
	}
	
	
}

/**
 * Implémentation d'un sommet
 */
class Sommet {
	String nom;
	int x;
	int y;
	final int radius = 5;

	/** Constructeur basique d'un sommet */
	public Sommet() {
		nom = "NA";
		setCoordonees(0, 0, 1300, 700);
	}
	
	/** Construit un sommet placé aléatoirement sur une fenêtre de 1600x900 pixels de nom i-1 */
	public Sommet(String nom) {
		this.nom = nom;
		setCoordonees(0, 0, 1300, 700);
	}
	
	/** Construit un sommet situé aléatoirement entre (xmin, ymin) et (xmin+xdistancemax,ymin+ydistancemax)
	 * dont le nom est les coordonnées du sommet sur le plan */
	public Sommet(int xmin, int ymin, int x_distance_max, int y_distance_max) {
		setCoordonees(xmin, ymin, x_distance_max, y_distance_max);
		nom = "("+x+", "+y+")";
	}
	
	/**
	 * Positionne aléatoirement le sommet dans le plan
	 */
	private void setCoordonees(int xmin, int ymin, int x_distance_max, int y_distance_max) {
		x = xmin + (int) (Math.random() * x_distance_max);
		y = ymin + (int) (Math.random() * y_distance_max);
	}
	
	
	/** Dessine le sommet dans graphics en noir */
	void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillOval(x-radius, y-radius, radius*2, radius*2);
		
		graphics.drawString(this.nom, x - radius, y - radius - 5);
	}
	
	/**
	 * Si le sommet courant a une distance avec tous les autres sommets supérieure à distanceMin,
	 * ajoute le sommet.
	 * 
	 * @param sommets La liste des sommets. Modifié si le sommet est ajouté
	 * @param aretes La liste des arêtes du graphe. Modifié si le sommet est ajouté
	 * @return Vrai si le sommet a été intégré, faux sinon
	 */
	public boolean integrerSommet(List<Sommet> sommets, List<Arete> aretes, double distance_min) {
		List<Arete> aretesDuSommet = new ArrayList<Arete>(sommets.size());
		
		double distance;
		
		for (Sommet sommet : sommets) {
			// Vérification que le sommet est compatible
			distance = distanceAvec(sommet);
			
			if (distance < distance_min)
				return false;
			
			// Ajout de l'arète
			aretesDuSommet.add(new Arete(sommet, this, (int) distance));
		}
		
		// Le sommet est valide : on ajoute ses arêtes et le sommet à notre graphe.
		sommets.add(this);
		aretes.addAll(aretesDuSommet);
		
		return true;
	}

	/**
	 * Renvoie la distance entre le sommet actuel et le sommet sommet dans le plan.
	 */
	private double distanceAvec(Sommet sommet) {
		double distX, distY;
		distX = this.x - sommet.x;
		distY = this.y - sommet.y;
		return Math.sqrt(distX * distX + distY * distY);
	}
	
	/** Renvoie le nom du sommet */
	public String toString() {
		return this.nom;
	}
	
	/**
	 * Renvoie le sommet ayant pour nom nom si il existe dans la liste sommets
	 * Renvoie null sinon
	 */
	public static Sommet searchSommet(List<Sommet> sommets, String nom) {
		for (Sommet sommet : sommets) {
			if (sommet.toString().equals(nom))
				return sommet;
		}
		
		return null;
	}
}

/**
 * Implémentation de la structure de Tas sur des arêtes afin d'en contrôler
 * exactement l'implémentation.
 */
class Tas {
	/** Liste des arêtes dans le tas */
	Arete aretesEntassees[];
	int capacite;
	int position;
	
	/** Crée un tas pouvant de base accueillir capacité éléments */
	public Tas(int capacite) {
		aretesEntassees = new Arete[capacite];
		this.capacite = capacite;
		this.position = 0;
	}
	
	/**
	 * Double la capacité du tas
	 */
	public void doublerCapacite() {
		Arete nouveauTas[] = new Arete[capacite*2];
		for (int i = 0 ; i < capacite ; i++) {
			nouveauTas[i] = aretesEntassees[i];
		}
		
		aretesEntassees = nouveauTas;
		
		capacite = capacite *2;
	}
	
	/** Insère l'ârete arete dans le tas */
	public void inserer(Arete arete) {
		if (position == capacite)
			doublerCapacite();
		
		aretesEntassees[position] = arete;
		arranger(position);
		position++;
	}
	
	/** Réorganise le tableau de manière à ce que le tas soit de nouveau un tas
	 *  aretesEntassees[i] doit être le seul élément qui ne vérifie pas la structure de tas */
	private void arranger(int i) {
		int pere = (i-1) / 2;
		
		while (i > 0 && aretesEntassees[i].compareTo(aretesEntassees[pere]) < 0) {
			echanger(i, pere);
			i = pere;
			pere = (i-1)/2;
		}
	}
	
	/** Echange les arêtes d'indice a et b dans le tas */
	private void echanger(int a, int b) {
		Arete temp;
		temp = aretesEntassees[a];
		aretesEntassees[a] = aretesEntassees[b];
		aretesEntassees[b] = temp;
	}

	/** Extrait l'arête de poids minimal du tas */
	public Arete extraire() {
		position--;
		echanger(0, position);
		
		int i = 0;
		int fils_gauche = 1;
		int fils_droit = 2;
		int fils_min;

		while (fils_gauche < position) {
			if (fils_droit < position && aretesEntassees[fils_droit].compareTo(aretesEntassees[fils_gauche]) < 0 ) {
				fils_min = fils_droit;
			} else {
				fils_min = fils_gauche;
			}
			
			if (aretesEntassees[i].compareTo(aretesEntassees[fils_min]) <= 0)
				break;
			
			echanger(i, fils_min);
			
			
			i = fils_min;
			fils_gauche = i*2+1;
			fils_droit = fils_gauche +1;
		}
		
		return aretesEntassees[position];
	}
	

	
	/**
	 * Entasse toutes les arêtes de <code>aretes</code> dans le tas dont <code>actuel</code>
	 * est un des sommets et dont l'autre sommet n'est pas dans <code>sommetsACM</code>
	 * Requiert : actuel doit être inclus dans sommetsACM
	 * @param aretes Liste des arêtes existantes
	 * @param actuel Le sommet dont on cherche à entasser les arêtes, inclus dans sommetsACM
	 * @param sommetsACM Liste des sommets tel que pour chauqe sommet, si elle existe, l'arête (sommet,actuel) ne sera pas entassée
	 */
	public void entasserSauf(List<Arete> aretes, Sommet actuel, List<Sommet> sommetsACM) {
		for (Arete a : aretes) {
			if (a.concerneSommet(actuel) && a.nombreDeSommetsAppartenantA(sommetsACM) == 1)
				inserer(a);
		}
	}
	
	
	/* ----- AFFICHAGE ----- */

	/** Affiche la liste des poids contenus dans le tas dans l'ordre du tableau */
	public void afficherTas() {
		System.out.print("Tas : ");
		for (int i = 0 ; i < position ; i++)
			System.out.print( aretesEntassees[i].poids + " " );
		
		System.out.println();
	}
	
}
