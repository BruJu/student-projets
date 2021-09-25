package Graphe;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JPanel;

class Graphe extends JPanel {
	/* MAIN */
	public static long chronometre = -1;
	
	/**
	 * Premier appel : lance le chronometre
	 * Deuxième appel : renvoie le temps en ms écoulé depuis le premier appel
	 */
	public static long chronometrer() {
		if (chronometre == -1) {
			chronometre = System.currentTimeMillis();
			return 0;
		} else {
			long temps = chronometre;
			chronometre = -1;
			return System.currentTimeMillis() - temps;
		}
	}
	
	public static void main(String[] args) {
		
		int choix = -1;
		Graphe.ACMPar methode = Graphe.ACMPar.Prim;
		Graphe graphe = null;
		Graphe.ACMPar methodes[] = {
				Graphe.ACMPar.Prim,
				Graphe.ACMPar.PrimTas,
				Graphe.ACMPar.Kruskal,
				Graphe.ACMPar.KruskalForet};
		
		System.out.println("Devoir 2 d'Algorithmique Avancée : Graphes");
		System.out.println("Menu : ");
		System.out.println("1 - Modifier la méthode pour créer des graphes");
		System.out.println("2 - Créer un graphe à partir du fichier graphe.txt");
		System.out.println("3 - Créer un graphe à partir d'un nuage de point généré aléatoirement");
		System.out.println("4 - Afficher le graphe");
		System.out.println("5 - Afficher l'ACM");
		System.out.println("6 - Calculer l'hypermétrique");
		System.out.println("7 - Chronometrer la construction de 1000 ACM");
		System.out.println("0 - Quitter");
		
		Scanner sc = new Scanner(System.in);
		
		do {
			System.out.print("Votre choix : ");
			choix = sc.nextInt();
			
			switch (choix) {
			case 1:
				System.out.println("1 = Prim, 2 = Prim avec un tas, 3 = Kruskal, 4 = Kruskal via Union-Find par foret");
				choix = sc.nextInt();
				if (choix >= 1 && choix <= 4)
					methode = methodes[choix-1];
				
				if (graphe != null)
					graphe.construireACM(methode);
				
				choix = 1;
				break;
			case 2:
				graphe = new Graphe("graphe");
				graphe.construireACM(methode);
				graphe.bleuterACM(true);
				System.out.println("Graphe chargé");
				break;
			case 3:
				System.out.print("Combien de points ? ");
				choix = sc.nextInt();
				if (choix > 0) {
					graphe = new Graphe();
					graphe.creerNuageDePoints(choix, 100, 100, 1400, 700, 50);
					graphe.construireACM(methode);
					graphe.bleuterACM(true);
					
				}
				
				choix = 3;
				break;
			case 4:
				graphe.creerFrame("Graphe");
				break;
			case 5:
				graphe.acmCorrespondant.creerFrame("Arbre Couvrant Minimal");
				break;
			case 6:
				graphe.getUltrametrique(graphe.sommets.get(0), graphe.sommets.get(graphe.getNbSommets()-1));
				graphe.acmCorrespondant.ultraMetriqueChemin = true;
				break;
			case 7:
				Graphe.chronometrerDesConstitutionsdACM(methode);
				break;
			}
		} while (choix != 0);
		
		sc.close();
		
		if (graphe != null)
			graphe.fermerFrame();
		
		if (graphe.acmCorrespondant != null)
			graphe.acmCorrespondant.fermerFrame();
	}

	private static void chronometrerDesConstitutionsdACM(Graphe.ACMPar methode) {
		Graphe graphes[] = new Graphe[1000];
		for (int i = 0 ; i < 1000 ; i++) {
			graphes[i] = new Graphe();
			graphes[i].creerNuageDePoints(200, 0, 0, 2000, 2000, 10);
		}
		
		Graphe.ACMPar methode1, methode2;
		
		if (methode == Graphe.ACMPar.Kruskal || methode == Graphe.ACMPar.KruskalForet) {
			methode1 = Graphe.ACMPar.Kruskal;
			methode2 = Graphe.ACMPar.KruskalForet;
		} else {
			methode1 = Graphe.ACMPar.Prim;
			methode2 = Graphe.ACMPar.PrimTas;
		}
		
		long temps1, temps2;
		
		Graphe.chronometrer();
		for (int i = 0 ; i < 1000 ; i++) {
			graphes[i].construireACM(methode1);
		}
		
		temps1 = Graphe.chronometrer();
		
		Graphe.chronometrer();

		for (int i = 0 ; i < 1000 ; i++) {
			graphes[i].construireACM(methode2);
		}
		
		temps2 = Graphe.chronometrer();
		
		System.out.println("Non optimisé : " + temps1 + " ms / Optimisé : " + temps2 + " ms");
	}
	
	
	/* CLASSE GRAPHE */
	

	private static final long serialVersionUID = -4263939870024057027L;
	List<Sommet> sommets;
	List<Arete>  aretes;
	
	Graphe acmCorrespondant;
	boolean acmEnBleu = false;
	
	List<Arete> cheminUltra;
	Arete cheminPlusGrandPoids;
	boolean ultraMetriqueChemin = false;
	
	JFrame frame;
	
	public Graphe() {
		sommets = new ArrayList<Sommet>();
		aretes  = new ArrayList<Arete>();
		setPreferredSize(new Dimension(1300, 700));
	}
	
	/** Construit le graphe à partir d'un fichier */
	public Graphe(String fichier) {
		this();
		
		String ligne;
		
		BufferedReader ficTexte;
		try {
			ficTexte = new BufferedReader(new FileReader(new File(fichier)));
			
			ligne = ficTexte.readLine();
			System.out.println(ligne);
			
			while (ligne != null) {
				charger_element(ligne);
				
				ligne = ficTexte.readLine();
			}
			
			ficTexte.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/* ----- AFICHAGE ---- */
	
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		
		if (acmEnBleu) {
			graphics.setColor(Color.blue);
			
			for (Sommet sommet : sommets)
				sommet.paint(graphics);
						
			for (Arete arete : aretes) {
				if (acmCorrespondant.aretes.contains(arete)) {
					graphics.setColor(Color.blue);
				} else {
					graphics.setColor(Color.black);
				}
				
				arete.paint(graphics);
			}
		} else if (ultraMetriqueChemin) {
			graphics.setColor(Color.black);
			
			for (Sommet sommet : sommets)
				sommet.paint(graphics);
						
			for (Arete arete : aretes) {
				if (arete == cheminPlusGrandPoids) {
					graphics.setColor(Color.red);
				} else if (cheminUltra.contains(arete)) {
					graphics.setColor(Color.blue);
				} else {
					graphics.setColor(Color.black);
				}
				
				arete.paint(graphics);
			}
		} else {
			graphics.setColor(Color.BLACK);
			for (Sommet sommet : sommets)
				sommet.paint(graphics);
			
			for (Arete arete : aretes)
				arete.paint(graphics);
		}
	}
	
	/**
	 * Crée une fenêtre affichant le graphe
	 */
	public void creerFrame(String nomFrame) {
		fermerFrame();
		
		frame = new JFrame(nomFrame);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Ferme la fenètre du graphe si elle existe
	 */
	public void fermerFrame() {
		if (frame != null) {
			frame.dispose();
			frame = null;
		}
	}
	
	
	/** Modifie acmEnBleu */
	public void bleuterACM(boolean state) {
		acmEnBleu = state;
	}
	
	/* Manipulation des sommets et des aretes */
	
	/**
	 * Ajoute le sommet sommet au graphe
	 */
	public void ajouterSommet(Sommet sommet) {
		sommets.add(sommet);
	}
	
	/**
	 * Ajoute l'arete <code>arete</code> au graphe
	 */
	public void ajouterArete(Arete arete) {
		aretes.add(arete);
	}

	/**
	 * Crée un graphe ayant les mêmes sommets que le graphe actuel mais sans les arêtes
	 */
	public Graphe getGrapheSansLesAretes() {
		Graphe graphe = new Graphe();
		for (Sommet sommet : sommets) {
			graphe.ajouterSommet(sommet);
		}
		return graphe;
	}
	
	
	/** Renvoie le nombre de sommets */
	private int getNbSommets() {
		return sommets.size();
	}

	/** Renvoie le nombre d'arêtes */
	private int getNbAretes() {
		return aretes.size();
	}

	/** Renvoie vrai si l'arête arete produit un cycle dans le graphe */
	boolean produitUnCycle(Arete arete) {
		return explorer(arete.sommetA, arete.sommetB, null, null);
	}
	
	/* ----- ACM ------ */
	public enum ACMPar {
		Prim,
		PrimTas,
		Kruskal,
		KruskalForet
	};
	
	public Graphe construireACM(ACMPar methode) {
		switch (methode) {
		case Prim:
			acmAvecPrim(sommets.get(0));
			break;
		case PrimTas:
			acmAvecPrimParTas(sommets.get(0));
			break;
		case Kruskal:
			acmAvecKruskal();
			break;
		case KruskalForet:
			acmAvecKruskalUnionFind();
			break;
		}
		
		return acmCorrespondant;
	}
	
	/* ----- PRIM ------ */
	
	/** Crée un ACM en utilisant l'algorithme de Prim en partant du sommet debut */
	public Graphe acmAvecPrim(Sommet debut) {
		// CrÃ©ation d'un nouveau graphe pour l'ACM
		Graphe acm = new Graphe();
		
		// Ajout du sommet initial
		acm.ajouterSommet(debut);
		
		// Liste des sommets non traitÃ©s
		List<Sommet> sommetsPasEncoreDansLACM = new ArrayList<Sommet>();
		sommetsPasEncoreDansLACM.addAll(sommets);
		sommetsPasEncoreDansLACM.remove(debut);
		
		
		while (acm.getNbSommets() != getNbSommets()) {
			// On dÃ©termine quelle est l'arÃªte traversante de plus petit poids
			Arete areteTraversanteDePlusPetitPoids = getAreteTraversanteDePlusPetitPoids(acm.sommets);
			acm.ajouterArete(areteTraversanteDePlusPetitPoids);
			
			// On ajoute le nouveau sommet traitÃ©
			Sommet nouveauSommetDansACM = acm.getSommetAbsent(areteTraversanteDePlusPetitPoids);
			acm.ajouterSommet(nouveauSommetDansACM);
			sommetsPasEncoreDansLACM.remove(nouveauSommetDansACM);
		}
		
		acmCorrespondant = acm;
		return acm;
	}
	

	/** Renvoie le sommet de arete qui n'est pas dans la liste des sommets du graphe */
	private Sommet getSommetAbsent(Arete arete) {
		for (Sommet sommetCourant : sommets) {
			if (sommetCourant == arete.sommetA) {
				return arete.sommetB;
			}

			if (sommetCourant == arete.sommetB) {
				return arete.sommetA;
			}
		}
		
		return null;
	}

	/** Renvoie l'arête de plus petit poids du graphe actuel dont seul un des deux sommets est dans sommetsA */
	private Arete getAreteTraversanteDePlusPetitPoids(List<Sommet> sommetsA) {
		Arete areteTraversanteMinimale = null;
		
		for (Arete arete : aretes) {
			if ( areteTraversanteMinimale == null
					|| areteTraversanteMinimale.poids > arete.poids) {
				if (arete.nombreDeSommetsAppartenantA(sommetsA) == 1)
					areteTraversanteMinimale = arete;
			}
		}
		
		return areteTraversanteMinimale;
	}

	/** Crée un ACM en utilisant l'algorithme de Prim en partant du sommet debut optimisé via les tas */
	public Graphe acmAvecPrimParTas(Sommet debut) {
		// CrÃ©ation d'un nouveau graphe pour l'ACM
		Graphe acm = new Graphe();
		Tas tas = new Tas(getNbAretes());
		
		// Ajout du sommet initial
		acm.ajouterSommet(debut);
		tas.entasserSauf(aretes, debut, acm.sommets);
		
		// Liste des sommets non traitÃ©s
		List<Sommet> sommetsPasEncoreDansLACM = new ArrayList<Sommet>();
		sommetsPasEncoreDansLACM.addAll(sommets);
		sommetsPasEncoreDansLACM.remove(debut);
		
		
		while (acm.getNbSommets() != getNbSommets()) {
			Arete areteCandidate = tas.extraire();
			
			if (areteCandidate.nombreDeSommetsAppartenantA(sommetsPasEncoreDansLACM) != 1) {
				// pas traversante
				continue;
			}
			
			// On ajoute le nouveau sommet traitÃ©
			Sommet nouveauSommetDansACM = acm.getSommetAbsent(areteCandidate);
			acm.ajouterSommet(nouveauSommetDansACM);
			sommetsPasEncoreDansLACM.remove(nouveauSommetDansACM);
			tas.entasserSauf(aretes, nouveauSommetDansACM, acm.sommets);
			
			acm.aretes.add(areteCandidate);
		}
		
		acmCorrespondant = acm;
		return acm;
	}


	/* ----- KRUSKAL ----- */
	
	/** Crée l'ACM en utilisant l'algorithme de Kruskal (sans Union-Find) */
	public Graphe acmAvecKruskal() {
		Graphe acm = new Graphe();

		// Nous trions la liste des arÃªtes
		List<Arete> aretesTriees = new ArrayList<Arete>();
		aretesTriees.addAll(aretes);
		Collections.sort(aretesTriees); // Trie en utilisant une variante du TimSort
		
		acm.sommets.addAll(sommets);
		
		int index = 0;
		while ( acm.getNbAretes()+1 != getNbSommets()) {
			Arete arete = aretesTriees.get(index);
			
			if (!acm.produitUnCycle(arete)) {
				acm.ajouterArete(arete);
			}
			
			index ++;
		}
		
		acmCorrespondant = acm;
		return acm;
	}
	

	/** Construit l'ACM en utilisant l'algorithme de Kruskal avec l'optimisation de l'Union Find via les forets */
	public Graphe acmAvecKruskalUnionFind() {
		Graphe acm = new Graphe();

		List<Arete> aretesTriees = new ArrayList<Arete>();
		aretesTriees.addAll(aretes);
		Collections.sort(aretesTriees); // Trie en utilisant une variante du TimSort
		
		acm.sommets.addAll(sommets);
		
		int arbres[] = new int[this.getNbSommets()];
		int hauteur[] = new int[this.getNbSommets()];
		
		for (int i = 0 ; i < getNbSommets() ; i++) {
			arbres[i] = i;
			hauteur[i] = 1;
		}
		
		
		int racine_gauche, racine_droite;
		int index_gauche, index_droite;
		
		int index = 0;
		while ( acm.getNbAretes()+1 != getNbSommets()) {
			Arete arete = aretesTriees.get(index);
			
			index_gauche = sommets.indexOf(arete.sommetA);
			index_droite = sommets.indexOf(arete.sommetB);
			
			racine_gauche = index_gauche;
			racine_droite = index_droite;
			
			while (racine_gauche != arbres[racine_gauche])
				racine_gauche = arbres[racine_gauche];

			while (racine_droite != arbres[racine_droite])
				racine_droite = arbres[racine_droite];
			
			if (racine_gauche != racine_droite) {
				
				if (hauteur[racine_droite] > hauteur[racine_gauche]) {
					arbres[racine_gauche] = racine_droite;
				} else {
					if (hauteur[racine_droite] == hauteur[racine_gauche])
						hauteur[racine_gauche]++;
					
					arbres[racine_droite] = racine_gauche;
				}
				
				acm.aretes.add(arete);
			}

			
			index ++;
		}
		
		acmCorrespondant = acm;
		return acm;
	}
	
	
	/**
	 * Affiche la liste des sommets du graphe et les arêtes en utilisant les index dans les arraylist
	 */
	void afficher_graphe() {
		System.out.println("Grpahe crÃ©Ã© : ");
		for (Sommet s : sommets) {
			System.out.println(sommets.indexOf(s));
		}
		
		for (Arete a : aretes) {
			System.out.println("(" + 
					sommets.indexOf(a.sommetA) + "," +
					sommets.indexOf(a.sommetB) + "," +
					a.poids + ")");
		}
	}
	
	/**
	 * Lit une chaîne de caractère
	 * Crée un sommet si il n'y a qu'une chaine
	 * Crée une arête si il y a deux chaines et un entier
	 */
	void charger_element(String ligne) {
		
		String nom1;	Sommet sommet1;
		String nom2;	Sommet sommet2;
		int poids;
		
		// Lecture des éléments
		StringTokenizer analyse = new StringTokenizer(ligne, "(),", false);
		
		if (!analyse.hasMoreElements()) {
			System.out.println("Erreur à la ligne1 " + ligne);
			return;
		}
			
		nom1 = analyse.nextToken();
		
		if (!analyse.hasMoreElements()) {
			// Sommet
			ajouterSommet(new Sommet(nom1));

		} else {
			// Arete : lecture d'un autre sommet et d'une arête
			nom2 = analyse.nextToken();
			
			if (!analyse.hasMoreElements()) {
				System.out.println("Erreur à la ligne2 " + ligne);
				return;
			}
			
			poids = Integer.parseInt(analyse.nextToken());
			
			// Recupération des sommets
			sommet1 = Sommet.searchSommet(sommets, nom1);
			sommet2 = Sommet.searchSommet(sommets, nom2);
			
			if (sommet1 == null || sommet2 == null) {
				System.out.println("Erreur à la ligne3 " + ligne);
				return;
			}
			
			// Ajout de l'arête
			ajouterArete(new Arete(sommet1, sommet2, poids));
		}
	}
	
	
	/**
	 * Crée un nuage de points dan sle graphe
	 * @param nombre_de_points Nombre de points
	 * @param (x, y) Coordonnées minimale du sommet
	 * @param (dist_x, dist_y) Vecteur donnant l'écart maximal du point en haut à gauche des sommets
	 * @param distance_min distance minimale entre les sommets
	 */
	public void creerNuageDePoints(int nombre_de_points, int x, int y, int dist_x, int dist_y, double distance_min) {
		int nb_de_points_crees = 0;
		
		Sommet nouveauSommet;
		
		while (nb_de_points_crees != nombre_de_points) {
			nouveauSommet = new Sommet(x, y, dist_x, dist_y);
			
			if (nouveauSommet.integrerSommet(sommets, aretes, distance_min)) {
				nb_de_points_crees++;
			}
		}
	}
	
	
	/**
	 * Renvoie l'ultramétrique entre origine et destination.
	 * Le graphe doit être un ACM ou un appel à une méthode de construction d'ACM doit avoir été utilisée
	 * @param origine Point d'origine
	 * @param destination Destination
	 * @return Le poids de l'arète la plus grande entre origine et destination
	 */
	public int getUltrametrique(Sommet origine, Sommet destination) {
		if (origine == destination)
			return 0;
		
		if (acmCorrespondant != null) {
			return acmCorrespondant.getUltrametrique(origine, destination);
		}
		
		List<Arete> chemin = getChemin(origine, destination);
		
		int ultraMetrique = 0;
		
		for (Arete arete : chemin) {
			if (ultraMetrique < arete.poids) {
				ultraMetrique = arete.poids;
				cheminPlusGrandPoids = arete;
			}
		}
		
		System.out.println("Points choisis : " + origine + " et " + destination);
		System.out.println("Ultramétrique : " + ultraMetrique);
		
		return ultraMetrique;
	}

	/**
	 * Trouve le chemin pour aller de origine à destination dans le graphe et le renvoie
	 * Le sauvegarde également dans l'attribut chemin.
	 * 
	 * Il ne doit pas y avoir de cycle
	 * @param origine
	 * @param destination
	 * @return Le chemin pour aller de origine à destination 
	 */
	private List<Arete> getChemin(Sommet origine, Sommet destination) {
		List<Arete> chemin = new ArrayList<Arete>(getNbAretes());
		
		explorer(origine, destination, null, chemin);
		
		cheminUltra = chemin;
		return chemin;
	}

	/**
	 * Renvoie vrai si il existe un chemin dans le graphe qui part de origine et qui mène à destination.
	 * Boucle à l'infini si il y a un cycle dans le graphe.
	 * 
	 * Fonction recursive dont le premier appel est explorer(sommetA, sommetB, null, chemin ou null) 
	 * 
	 * @param origine Point d'origine
	 * @param destination Point de destination
	 * @param pere Ignore l'arête (origine, pere)
	 * @param chemin Si non null, ajoute les arêtes permettant d'aller de origine à destination
	 * @return Vrai si il existe un chemin pour aller de origine à destination, faux sinon
	 */
	private boolean explorer(Sommet origine, Sommet destination, Sommet pere, List<Arete> chemin) {
		for (Arete arete : aretes) {
			// On exclue l'arête si le père est concerné ou si l'origine n'y est pas
			if (pere != null && arete.concerneSommet(pere))
				continue;
			
			if (!arete.concerneSommet(origine)) {
				continue;
			}
			
			// Arête finale
			if (arete.concerneSommet(destination) // Arête finale
				|| explorer(arete.getAutreSommet(origine), destination, origine, chemin)
					// Il existe un chemin partant de l'autre sommet de l'arête et menant à destination
					) {
				
				if (chemin != null) chemin.add(arete);
				
				return true;
			}
			
		}
		
		// Aucune arête de origine ne permet d'atérir sur destination
		return false;
	}

}

