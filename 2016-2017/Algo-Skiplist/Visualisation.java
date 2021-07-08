package skiplist;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

class Couple<A, B> {
	public A a;
	public B b;
}


/**
 * Afin de ne pas surcharger la classe Skiplist, nous créons un objet qui est l'extension de Skiplist
 * Cette classe nous fourniera toutes les méthodes permettant de visualiser la Skiplist.
 */
class PrintableSkiplist extends Skiplist {
	
	public PrintableSkiplist()			 {	super();	}
	public PrintableSkiplist(int h_max) {	super(h_max);	}


	/**
	 * Affiche la skiplist avec les valeurs pointées par chaque élément
	 */
	public void afficherElementsPointes() {
		if (debut == null) {
			System.out.println("Liste vide");
			return;
		}
		
		System.out.print("{Sentinelle} ");
		debut.afficherClesPointees();
		System.out.println();
		
		Element actuel;
		
		for(actuel = debut.etage[0] ; actuel != null ; actuel = actuel.tour.etage[0]) {
			actuel.afficher2();
			System.out.println();
		}
		
		System.out.println();
	}
	
	
	/**
	 * Affiche le nombre moyen d'éléments sautés par chaque étage.
	 */
	public void afficherStatsHauteurs() {
		int nombreDeToursDetage[] = nombreDeToursParEtage();
		
		
		int nbTotalDelem = this.nb_delements();
		
		if (nbTotalDelem == 0) {
			System.out.println("Liste vide");
			return;
		}
		
		
		DecimalFormat dformat = new DecimalFormat (" # % ");
		
		int nbDeToursDejaVues = 0;

		int etage;
		
		for (etage = hauteur_maximale-1 ; etage > 0 ; etage--) {
			if (nombreDeToursDetage[etage] == 0)
				continue;
			
			nbDeToursDejaVues += nombreDeToursDetage[etage];
			double pourcentagePresence = (double)nbDeToursDejaVues/nbTotalDelem;
			
			double elemSautesMoyens = (double)(-nbDeToursDejaVues+nbTotalDelem)/nbDeToursDejaVues;
			System.out.println("Etage " + etage + " : " + nombreDeToursDetage[etage] + "( Total : " + nbDeToursDejaVues + ", " + dformat.format(pourcentagePresence) + ") - " + elemSautesMoyens + " elements sautés en moyenne");
		}
		
		
		
	}
	
	/**
	 * Renvoie le nombre de tours pour chaque étage
	 * @return Un tableau tq t[i] est le nombre de tours ayant i étages.
	 */
	private int[] nombreDeToursParEtage() {
		int nombreDeToursDetage[] = new int[hauteur_maximale];
	
		for (Element element : this) {
			nombreDeToursDetage[element.tour.hauteur-1] ++;
		}
		
		return nombreDeToursDetage;
	}
	 
	 
	/**
	 * renvoie le nombre de pointeurs différents
	 * Dans cette fonction,, deux pointeurs sont considérés comme identiques si ils sont sur la même tour et qu'ils pointent sur la même tour
	 * Ainsi, une tour pointant sur une autre tour n'incrèmentera le nombre affiché que de un, qu'elle le fasse sur un seul étage ou sur un million
	 */
	public int pointeursDifferents() {
		int nbPointeursUtiles = debut.getNbDelementsPointes();
		 
		for (Element element : this) {
			nbPointeursUtiles += element.tour.getNbDelementsPointes();
		}
		 
		return nbPointeursUtiles;
	}
	 
	/**
	 * Compte le nombre total de pointeurs sur les éléments
	 * @return Le nombre de pointeurs
	 */
	public int pointeursTotal() {
		int nbPointeurs = debut.hauteur;
		 
		for (Element element : this) {
			nbPointeurs += element.tour.hauteur;
		}
		 
		return nbPointeurs;
	}
	 
	 
	/**
	 * Affiche une représentation de la structure de la skiplist
	 * @param format Le format utilisé pour afficher la skiplist
	 */
	public void afficherSkiplistSansValeurs(Format format) {
		String[] representation = format.getString(this);
		 
		for (String string : representation)
			System.out.println(string);
	}

	
	/**
	 * Ajoute dans le tableau compte le nombre de tours de chaque hauteur.
	 * tours_par_case tours seront ajoutées au total dans chaque colonne.
	 * @param compte
	 * @param tours_par_case
	 */
	public void compterTours(long[][] compte, int tours_par_case) {
		int num_tour = 0;
		int x;
		Tour actuelle = debut.getVoisin();
		 
		x = 0;
		 
		while (actuelle != null) {
			compte[actuelle.hauteur][x]++;
			 
			num_tour++;
			
			if (num_tour == tours_par_case) {
				num_tour = 0;
				x++;
			}
			
			actuelle = actuelle.getVoisin();
		}
	}


	/**
	 * Chronomètre le temps requis pour chercher un élément et le renvoie en millisecondes … en considérant que la skiplist est une linkedlist.
	 * @param cle
	 * @return
	 */
	public void chercherElementLinkedList(int cle) {
		for (Element elem : this) {
			if (elem.cle == cle)
				break;
		}
	}
	
	/**
	 * Cherche les éléments dans valeurs et renvoie le temps en milliseconde supplémentaire utilisé par la méthode par linkedlist
	 * @param valeurs Liste des clés à chercher
	 * @return La différence de temps passé entre la manière linkedlist et la manière skiplist
	 */
	public void tempsGagneSurLinkedList(List<Integer> valeurs) {
		long tempsSkip = getTimeOnSkiplist(valeurs);
		long tempsLinked = getTimeOnLinkedList(valeurs);
		
		System.out.println("Skiplist : " + tempsSkip + " / LinkedList : " + tempsLinked + " / Gain : " + (tempsLinked - tempsSkip));
	}
	
	
	/**
	 * Cherche tous les élémennts dans la liste valeurs dans la Skiplist comme si c'était une Linkedlist
	 * @param valeurs Liste de valeurs à chercher
	 * @return Renvoie la différence entre l'heure de fin et de début de recherche
	 */
	public long getTimeOnLinkedList(List<Integer> valeurs) {
		long debut = System.currentTimeMillis();
		
		for (Integer cle : valeurs) {
			chercherElementLinkedList(cle);
		}
		
		long fin = System.currentTimeMillis();
		
		long tempsLinked = fin - debut;
		
		return tempsLinked;
	}
	

	/**
	 * Cherche tous les élémennts dans la liste valeurs dans la Skiplist
	 * @param valeurs Liste de valeurs à chercher
	 * @return Renvoie la différence entre l'heure de fin et de début de recherche
	 */
	public long getTimeOnSkiplist(List<Integer> valeurs) {
		long debut = System.currentTimeMillis();
		
		for (Integer cle : valeurs) {
			estPresent(cle);
		}
		
		long fin = System.currentTimeMillis();
		
		long tempsSkip = fin - debut;
		
		return tempsSkip;
	}
	
	
	/**
	 * Remplie aléatoirement la Skiplist avec nbDeValeurs valeurs comprises entre 0 et valMaximale
	 * @param nbDeValeurs
	 * @param valMaximale
	 * @return Une collection comprenant la liste des nombres insérés dans la skiplist dans l'ordre d'insertion
	 */
	public ArrayList<Integer> remplirAleatoirement(int nbDeValeurs, int valMaximale) {
		if (nbDeValeurs > valMaximale)
			return null;

		ArrayList<Integer> valeursInserees = new ArrayList<Integer>(nbDeValeurs);
		
		for (int i = 0 ; i < nbDeValeurs ; i++) {
			int nombre;
			do {
				nombre = (int) (Math.random() * (valMaximale));
			} while (estPresent(nombre));
			
			inserer(nombre);
			valeursInserees.add(nombre);
		}
		
		return valeursInserees;
	}
	
	
	/**
	 * Affiche le nombre de pointeurs différents sur la sortie standard
	 * Un pointeur est considéré comme étant le même qu'un autre si il existe un autre pointeur sur la même tour pointant sur la même tour
	 */
	public void afficherNbDePointeursDifferents() {
		int point = pointeursTotal();
		int pointDif = pointeursDifferents();
		double pourcent = ((double) pointDif) / point;

		DecimalFormat dformat = new DecimalFormat (" # % ");
		System.out.println("Pointeurs : " + pointDif + " différents / " + point + " totaux (" + dformat.format(pourcent) + ")");
	}
	
	
	/**
	 * Teste la validité des pointeurs sur 10000 skip list et affiche le résultat sur la sortie standard
	 * @return Vrai, sauf si le test du chainage a échoué sur les éléments testés.
	 */
	static boolean testerValiditeDesSkiplist() {
		int i;
		for (i = 0 ; i < 10000 && PrintableSkiplist.validiteChainage() ; i++);
		
		if (i == 10000) {
			System.out.println("Le chaînage est valide (dans le cadre du test effectué)");
			return true;
		} else {
			System.out.println("Le chaînage numéro " + i + "a échoué.");
			return false;
		}
	}
	
	/**
	 * Insère 42 et 26, insère 20 éléments dans une skiplist, supprime 42 et 26 
	 * @return Vrai si le chainage reste valide
	 */
	public static boolean validiteChainage() {
		Skiplist s = new Skiplist();

		// On insère 42 et 26
		s.inserer(42);
		s.inserer(26);
		
		for (int i = 0 ; i < 20 ; i++) {
			int nombre;
			do {
				nombre = (int) (Math.random() * (100));
			} while (s.estPresent(nombre));
			
			s.inserer(nombre);
			if (!s.estValide()) {
				return false;
			}
		}
		
		try {
			// On supprime 42 et 26
			s.supprimer(42);
			s.supprimer(26);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return s.estValide() && s.nb_delements() == 20;
	}
	
	/**
	 * Supprime aléatoirement nbElemSupprimes clés de la Skiplist parmis les clés de la liste cles
	 * @param cles Liste des clés supprimables (modifié par la méthode)
	 * @param nbElemSupprimes Nombre d'éléments à supprimer
	 */
	public void supprimerDesElements(List<Integer> cles, int nbElemSupprimes) {
		for (int i = 0 ; i < nbElemSupprimes || cles.size() == 0 ; i++) {
			// Seletionner
			int indiceSuppr = (int) (Math.random() * cles.size());
			int cleSupprimee = cles.get(indiceSuppr);
			
			// Affichage de l'action
			System.out.println("\nSupression de : " + cleSupprimee);
			
			// Suppression
			supprimer(cleSupprimee);
			cles.remove(new Integer(cleSupprimee));
			
			// Affichage du résultat
			afficher();
			Menu.afficherArrayList(cles);
		}
	}
	

	/**
	 * Renvoie une Skiplist avec les mêmes clés que la Skiplist courante mais avec des tours ayant le schéma idéal de répartition de tours
	 * @return Une skiplist optimisée pour la recherche de clés
	 */
	public PrintableSkiplist equilibrerTours() {
		PrintableSkiplist equilibree = new PrintableSkiplist();
		
		
		int nbElem = this.nb_delements(); 
		
		int[] schemaIdeal = new int[nbElem];
		
		remplirSchemaIdeal(schemaIdeal, 0, nbElem);
		
		int i = 0;
		for (Element elem : this) {
			equilibree.inserer(new Element(elem.cle, schemaIdeal[i], 1));
			i++;
		}
		
		return equilibree;
	}
	
	
	/**
	 * Renvoie un couple d'entiers contenant les temps d'accès cumulés aux éléments de clé valeurs.
	 * Le premier nombre correspond au temps d'accés en ms aux éléments avec la SKiplist actuelle
	 * Le second correspond au temps d'accés en ms aux éléments si on avait eu une skiplist possédant le schéma idéal.
	 * @param valeurs Liste des clés à chercher
	 * @return (a, b) a étant le temps pour trouver les clés avec la skiplist courante, b le temps si la skiplist avait eu le schéma de tours idéal
	 */
	public Couple<Long, Long> testerTempsAccessEquilibre(ArrayList<Integer> valeurs) {
		PrintableSkiplist pslEq = equilibrerTours();
		
		Couple<Long, Long> temps = new Couple<Long, Long>();
		
		temps.a = getTimeOnSkiplist(valeurs);
		temps.b = pslEq.getTimeOnSkiplist(valeurs);
		return temps;
	}
	
	
	/**
	 * Rempli le tableau schema en indiquant le numéro du dernier étage qui doit être défini pour optimiser les skiplist
	 * @param schema Un tableau d'int où sera inscrit le nombre d'étage idéal à la position i dans la liste
	 * @param debut L'indice de début
	 * @param fin L'indice de fin
	 * @return Le nombre d'étage que possède la tour au milieu de la liste
	 */
	private int remplirSchemaIdeal(int[] schema, int debut, int fin) {
		
		if (fin - debut +1 < 3) {
			return 0;
		}

		
		int milieu;
		milieu = (fin + debut +1) / 2;
		/*
		 * Le +1 permet d'avoir la configuration de gauche au lieu de la configuration de droite :
		 * |  |            |  |
		 * | ||            || |
		 * |||| au lieu de ||||
		 * Cela permet de repousser légèrement le moment où on aura un pointeur "inutile"
		 */
		
		int hauteurDuMilieu = 1 + Math.max(remplirSchemaIdeal(schema, debut, milieu), remplirSchemaIdeal(schema, milieu, fin));
		
		schema[milieu] = hauteurDuMilieu;
		
		return hauteurDuMilieu;
	}
	
	
	/**
	 * Renvoie l'écart type pour chaque étage de tour
	 * @return Un tableau de double avec l'écart type de chaque étage de tour
	 */
	public double[] getEcartType() {
		
		int[] nbDeTours = getNbToursParEtage();

		int nbDeToursTotales = this.nb_delements();
		
		for (int i = hauteur_maximale-2 ; i != 0 ; i--) {
			nbDeTours[i] += nbDeTours[i+1];
		}

		
		double[] nbDelemSautesMoyen = new double[hauteur_maximale];
		int hauteurMaxUtilisee = 0;
		int i;
		for (i = 1 ; i < hauteur_maximale ; i++) {
			if (nbDeTours[i] == 0)
				break;
			
			nbDelemSautesMoyen[i] = ((double) (nbDeToursTotales - nbDeTours[i]))/nbDeToursTotales;
		}
		hauteurMaxUtilisee = i;
		
		
		double[] sommeDesVariances = new double[hauteurMaxUtilisee];
		
		for( Element elem : this) {
			int nombreDeSauts[] = elem.tour.getNbDeSautsParEtage();
			
			for (int etage = 1 ; etage < elem.tour.hauteur ; etage++) {
				double moyenneMoinsValeur = nbDelemSautesMoyen[etage] - nombreDeSauts[etage];
				sommeDesVariances[etage] += (moyenneMoinsValeur * moyenneMoinsValeur);
			}
		}

		for (i = 1 ; i < hauteurMaxUtilisee ; i++) {
			sommeDesVariances[i] = Math.sqrt(sommeDesVariances[i] / nbDeTours[i]);
		}
		
		return sommeDesVariances;
	}
	
	/**
	 * Renvoie le nombre de tours pour chaque étage
	 * @return un tableua t tq t[x] est le nombre de tours ayant pour dernier étage x
	 */
	public int[] getNbToursParEtage() {
		int[] nbDeTours = new int[hauteur_maximale];
		this.forEach(elem -> nbDeTours[elem.tour.hauteur-1]++);
		return nbDeTours;
	}
}



class StatsSurSkiplist {

	/**
	 * Crée un nouvel objet pouvant faire des analyses sur des séries de skiplist
	 * @param hauteur_choisie La hauteur maximale des skiplist
	 * @param valeur_max Toutes les skiplist auront des clés compris entre 0 et valeurs_max
	 * @param nbSkiplist Le nombre de skiplist
	 */
	public StatsSurSkiplist(int hauteur_choisie, int valeur_max, int nbSkiplist) {
		super();
		this.hauteur_choisie = hauteur_choisie;
		this.valeur_max = valeur_max;
		this.nbSkiplist = nbSkiplist;
	}

	private int hauteur_choisie;
	private int valeur_max;
	private int nbSkiplist;

	
	/**
	 * Rempli un tableau à double entrée avec les pourcentages de tours à chaque étage
	 * @param longueur Le nombre de sections
	 * @param nbElem Le nombre d'éléments maximal dans chaque skiplist (le nombre effectif d'éléments considéré sera le plus grand multiple de longueur inférieur ou égal à nbElem)
	 * @return Un tableau à double entrée tel que avec x le numéro de la section et y la hauteur, t[x][y] = le pourcentage de tours faisant cette taille 
	 */
	public double[][] getPourcentagesDeToursParSection(int longueur, int nbElem) {
		int nbElemParSection = nbElem / longueur;
		nbElem = nbElemParSection * longueur;
		
		long[][] valeurs = getSomme(longueur, nbElem, nbElemParSection);
		
		// Faire les %
		double[][] pourcentages = new double[hauteur_choisie][longueur];
		
		for (int i = 0 ; i < hauteur_choisie ; i++) {
			for (int j = 0 ; j < longueur ; j++) {
				
				if (valeurs[i][longueur] != 0)
					pourcentages[i][j] = ((double)valeurs[i][j]) / valeurs[i][longueur];
				else
					pourcentages[i][j] = 0;
			}
		}
		
		return pourcentages;
	}

	/**
	 * Rempli un tableau à double entrée avec pour chaque section le nombre de tours atteignant chaque étage
	 * @param longueur Nombre de sections
	 * @param nbElem longueur * nbElemParSection
	 * @param nbElemParSection Nombre d'éléments par section
	 * @return Un tableau t tel que avec x le numéro de la section et y une hauteur t[x][y] = le nombre de tours de la section de hauteur >= y
	 */
	public long[][] getSomme(int longueur, int nbElem, int nbElemParSection) {
		long[][] valeurs = new long[hauteur_choisie][longueur+1];
		
		// nbElem est un multiple de longueur
		
		PrintableSkiplist skiplist = new PrintableSkiplist();
		
		for (int i = 0 ; i < nbSkiplist ; i++) {
			skiplist.vider();
			skiplist.remplirAleatoirement(nbElem, valeur_max);
			skiplist.compterTours(valeurs, nbElemParSection);
		}
		
		skiplist.vider();
		
		// Faire la somme
		for (int i = 0 ; i < hauteur_choisie ; i++) {
			long somme = 0;
			int j;
			for (j = 0 ; j < longueur ; j++) {
				somme += valeurs[i][j];
			}
			
			valeurs[i][j] = somme;
		}
		return valeurs;
	}
	
	/**
	 * Affiche pour chaque étage et pour chaque section le pourcentage de tours ayant un pointeur
	 * @param longueur Nombre de section
	 * @param nbElem Nombre d'éléments maximal considéré
	 */
	public void afficher_stats_repartition_spatiale(int longueur, int nbElem) {
		double[][] triple = getPourcentagesDeToursParSection(longueur, nbElem);
		
		DecimalFormat dformat = new DecimalFormat ("(00.00%) ");
		
		for (int i = hauteur_choisie -1 ; i != 0 ; i--) {
			for (int j = 0 ; j < longueur ; j++) {
				System.out.print(dformat.format(triple[i][j]));
			}
			System.out.println();
		}	
	}
	
	/**
	 * Affiche le nombre d'éléments ayant un pointeur à chaque étage
	 * @param longueur Nombre de sections
	 * @param nbElem Nombre maximal d'éléments considérés
	 */
	public void afficherNombreDelementsParEtage(int longueur, int nbElem) {
		int nbElemParSection = nbElem / longueur;
		nbElem = nbElemParSection * longueur;
		
		long[][] valeurs = getSomme(longueur, nbElem, nbElemParSection);
		
		for (int i = hauteur_choisie -1 ; i != 0 ; i--) {

			System.out.print(valeurs[i][longueur]);
			
			for (int j = 0 ; j < longueur ; j++) {
				System.out.print(";"+valeurs[i][j]);
			}
			System.out.println();
		}
	}

	
	/**
	 * Renvoie un couple de tableaux contenant pour chaque nombre d'éléments dans la skiplist le temps total pour accéder à tous les éléments
	 * à la manière d'une skiplist et à la manière d'une linked list
	 * @param nombreElements Le nombre d'éléments
	 * @return Un couple de somme de temps d'accés à chaque élément, le premier étant avec une skiplist, le second avec une linkedlist
	 */
	public static Couple<long[], long[]> getRapportDeTemps(int[] nombreElements) {
		long[] tempsSkiplist = new long[nombreElements.length];
		long[] tempsLinkedlist = new long[nombreElements.length];
		List<Integer> values;

		PrintableSkiplist s = new PrintableSkiplist();
		
		for (int index = 0 ; index < nombreElements.length ; index++) {
			s.vider();
			
			values = s.remplirAleatoirement(nombreElements[index], 10 * nombreElements[index]);
			
			tempsSkiplist[index] = s.getTimeOnSkiplist(values);
			tempsLinkedlist[index] = s.getTimeOnLinkedList(values);
		}
		
		Couple<long[], long[]> result = new Couple<long[], long[]>();
		result.a = tempsSkiplist;
		result.b = tempsLinkedlist;
		
		return result;
	}
	
	/**
	 * Afficher sur la sortie standard la liste des nb d'éléments tester, le temps écoulé pour tout chercher pour une skiplist,
	 * le temps écoulé pour tout chercher dans une linked list et le rapport
	 * @param premierNbElem Premier nombre de d'éléments à tester
	 * @param dernierNbElem Dernier nombre d'éléments à tester
	 * @param pas Pas pour le parcours (doit être un multiple de la différence du premierNbElem et dernierNbElem)
	 */
	public static void printPointsGraphique(int premierNbElem, int dernierNbElem, int pas) {
		int nbNbElem;
		if (premierNbElem == dernierNbElem) {
			nbNbElem = 1;
		} else if (pas == 0) {
			return;
		} else {
			nbNbElem = (dernierNbElem - premierNbElem)/pas + 1;
		}
		
		
		int[] x = new int[nbNbElem];
		long[] ySkip = null;
		long[] yLinked = null;
		
		for (int index = 0 ; index < nbNbElem ; index++)
			x[index] = premierNbElem + index * pas;
		
		Couple<long[], long[]> c = getRapportDeTemps(x);
		
		ySkip = c.a;
		yLinked = c.b;
		

		System.out.print("Nombre d'éléments");
		for (int index = 0 ; index < nbNbElem ; index++)
			System.out.print(";"+x[index]);
		
		System.out.println();

		System.out.print("Temps Skiplist");
		for (int index = 0 ; index < nbNbElem ; index++)
			System.out.print(";"+ySkip[index]);
		
		System.out.println();
		
		System.out.print("Temps Linked");
		for (int index = 0 ; index < nbNbElem ; index++)
			System.out.print(";"+yLinked[index]);
		
		System.out.println();
		
		System.out.print("Rapport Temps d'accés LinkedList/SkipList");
		for (int index = 0 ; index < nbNbElem ; index++) {
			if (ySkip[index] == 0) {
				System.out.print(";");
				continue;
			}
			System.out.print(";"+((double)yLinked[index]/ySkip[index]));
		}
		
		System.out.println();
	}
	
	
}

/*
 * ============ TOUS LES FORMATS D'AFFICHAGES =============
 */

/*
 * INTERFACES ET CLASSES ABSTRAITES
 */

interface Format {
	String[] getString(Skiplist liste);

	String getNom();
}

abstract class FormatSansDebordement implements Format {
	
	public abstract char[] getSymbols(Skiplist list, Tour tower);
	
	public abstract String getNom();

	/**
	 * 
	 * @param element
	 * @param stringBuilders
	 */
	void appendElement(Skiplist liste, Tour tour, StringBuilder[] stringBuilders) {
		char symboles[] = getSymbols(liste, tour);

		for (int i = 0 ; i < liste.hauteur_maximale ; i++) {
			stringBuilders[i].append(symboles[i]);
		}
	}
	
	@Override
	public String[] getString(Skiplist liste) {
		 
		 StringBuilder[] stringBuilders = new StringBuilder[liste.hauteur_maximale];
		 
		 // Initialiser stringBuilders
		 for (int i = 0 ; i < liste.hauteur_maximale ; i++) {
			 stringBuilders[i] = new StringBuilder(liste.nb_delements());
		 }
		 
		 // TODO : mettre la sentinelle
		 appendElement(liste, liste.debut, stringBuilders);

		 for (int i = 0 ; i < liste.hauteur_maximale ; i++) {
			 stringBuilders[i].append(' ');
		 }
		 
		 
		 // Remplir les string builder
		 for (Element elem : liste) {
			 appendElement(liste, elem.tour, stringBuilders);
		 }
		 
		 // Convertir les stringbuilder en string (et inverser l'ordre)
		 String[] result = new String[liste.hauteur_maximale];
		 
		 for (int i = liste.hauteur_maximale - 1 ; i >= 0 ; i--) {
			result[liste.hauteur_maximale -1 - i] = stringBuilders[i].toString();
		 }
		 
		 return result;
	}

}

abstract class FormatReliant implements Format {


	protected abstract char debFleche();
	protected abstract char midFleche();
	protected abstract char finFleche();
	protected abstract char versNull();
	
	@Override
	public String[] getString(Skiplist liste) {
		char[][] caracteres = new char[liste.nb_delements()][liste.hauteur_maximale];
		
		mettreDesEspaces(caracteres);
		
		premierPassage(liste, caracteres);
		
		relierFleches(liste, caracteres);
		
		char[][] bonsCaracteres = translation(caracteres);
		
		String[] strings = new String[liste.hauteur_maximale];
		
		for (int i = 0 ; i < liste.hauteur_maximale ; i++) {
			strings[liste.hauteur_maximale -1 - i] = String.valueOf(bonsCaracteres[i]);
		}
		
		return strings;
	}

	/**
	 * Renvoie la translation de la matrice A (ou peu importe le bon nom)
	 * @param matriceA
	 * @return
	 */
	private char[][] translation(char[][] matriceA) {
		char[][] matriceB = new char[matriceA[0].length][matriceA.length];
		
		for (int i = 0 ; i < matriceA.length ; i++) {
			for (int j = 0 ; j < matriceA[0].length ; j++)
				matriceB[j][i] = matriceA[i][j];		
		}
		
		return matriceB;
	}
	private void relierFleches(Skiplist liste, char[][] caracteres) {
		for (int etage = 1 ; etage < liste.hauteur_maximale ; etage++) {
			tirerFleche(caracteres, etage);
		}
		
	}
	
	
	private void tirerFleche(char[][] caracteres, int etage) {
		int numElement = 0;
		
		int lastElement = caracteres.length;
		
		// Recherche du premier element ayant un pointeur à cet étage
		while (numElement < lastElement && caracteres[numElement][etage] == ' ') {
			numElement++;
		}
		
		
		// Commencer le grand remplacement
		
		boolean first = true;
		char current;
		
		while (numElement < lastElement) {
			
			current = caracteres[numElement][etage];
			
			if (current != ' ') {
				// Remplacer le précédent caractère par une fin de flèche
				if (first)
					first = false;
				else {
					caracteres[numElement-1][etage] = finFleche();
				}
				
				
				// Remplacer l'actuel
				if (current == 'x') {
					caracteres[numElement][etage] = debFleche();
				} else {
					caracteres[numElement][etage] = versNull();
					return;
				}
				
				
			} else {
				if (!first)
					caracteres[numElement][etage] = midFleche();
			}
			
			
			
			numElement++;
		}
		
		
		
	}
	private void premierPassage(Skiplist liste, char[][] caracteres) {
		Element actuel = liste.debut.etage[0];
		int position = 0;
		
		while (actuel != null) {
			for (int etage = 1 ; etage < actuel.tour.hauteur ; etage++) {
				caracteres[position][etage] = actuel.tour.etage[etage] == null ? 'X' : 'x';	
			}
			
			actuel = actuel.tour.etage[0];
			position++;
		}
	}
	
	
	private void mettreDesEspaces(char[][] caracteres) {
		for (int i = 0 ; i < caracteres.length ; i ++) {
			caracteres[i][0] = finFleche();
			
			for (int j = 1 ; j < caracteres[0].length-1 ; j++)
				caracteres[i][j] = ' ';
			
			caracteres[i][caracteres[0].length - 1] = versNull();
		}
		
	}
	
	
	@Override
	public abstract String getNom();

	
	
}



/*
 * IMPLEMENTATIONS DE FORMATS
 */

/**
 * Affiche sous forme de batonet dans la sortie standard la skip list
 * 
 * Edxemple d'affichage :
 * )
 * | || )
 * |||||||)
 * cout : n
 */
class FormatBaton extends FormatSansDebordement {

	public char[] getSymbols(Skiplist list, Tour tour) {
		char chaines[] = new char[list.hauteur_maximale];
		
		for (int i = 0 ; i < list.hauteur_maximale ; i++) {
			if (i >= tour.hauteur) {
				chaines[i] = ' ';
			} else if (tour.etage[i] == null) {
				chaines[i] = '\\';
			} else {
				chaines[i] = '|';
			}
		}
		return chaines;
	}

	public String getNom() {
		return "Baton";
	}
}


/**
  * Affiche sous forme de batonet dans la sortie standard la skip list
  * 
  * Edxemple d'affichage :
  * 0
  * 2 11113   0
  * 111111111110
  *  Cout : n²
  */
class FormatChiffres extends FormatSansDebordement {
	
	private int hauteur_maximale;
	
	public char[] getSymbols(Skiplist list, Tour tour) {
		hauteur_maximale = list.hauteur_maximale;
		int nombreDeSauts[] = tour.getNbDeSautsParEtage();
		char nombreDeSautsChars[] = enChar(nombreDeSauts);
		
		return nombreDeSautsChars;
	}



	private char[] enChar(int[] nombreDeSauts) {
		char[] affichage = new char[hauteur_maximale];
		
		int ligne = 0;
		
		while (ligne < nombreDeSauts.length) {
			
			if (nombreDeSauts[ligne]>15) {
				affichage[ligne] = '+';
			} else if (nombreDeSauts[ligne]>9) {
				affichage[ligne] = (char) ('A' + nombreDeSauts[ligne] - 10);
			} else {
				affichage[ligne] = (char) ('0' + nombreDeSauts[ligne]);
			}
			
			
			ligne++;
		}

		while (ligne < affichage.length) {
			affichage[ligne] = ' ';
			ligne++;
		}
		
		return affichage;
	}




	@Override
	public String getNom() {
		return "Nombres";
	}
	
	
}


class FormatFleches extends FormatSansDebordement {

	public char[] getSymbols(Skiplist list, Tour tour) {
		char chaines[] = new char[list.hauteur_maximale];
		
		for (int i = 0 ; i < list.hauteur_maximale ; i++) {
			if (i >= tour.hauteur) {
				chaines[i] = ' ';
			} else if (tour.etage[i] == null) {
				chaines[i] = 'X';
			} else if (i > 0 && tour.etage[i] == tour.etage[i-1]) {
				chaines[i] = '.';
			} else {
				chaines[i] = '>';
			}
		}
		return chaines;
	}

	public String getNom() {
		return "Fleches";
	}
}


class FormatTrueFleches extends FormatReliant {
	@Override
	protected char debFleche() {
		return '-';
	}

	@Override
	protected char midFleche() {
		return '-';
	}

	@Override
	protected char finFleche() {
		return '>';
	}

	@Override
	protected char versNull() {
		return 'X';
	}

	@Override
	public String getNom() {
		return "Format True flèches";
	}

}

