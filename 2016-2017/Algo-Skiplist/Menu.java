package skiplist;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;




public class Menu {
	/*
	 * CONSTANTES POUR TESTER LES LISTES
	 */
	static final int valeur_max = 5000000;
	static final int hauteur_choisie = 20;
	
	static StatsSurSkiplist t = new StatsSurSkiplist(hauteur_choisie, valeur_max, 100);
	static Scanner scanner;
	static PrintableSkiplist skiplist;
	static ArrayList<Integer> valeurs;
	
	/*
	 * FONCTIONS PRATIQUES
	 */
	
	static void afficherArrayList(List<Integer> cles) {
		for (Integer val : cles)
			System.out.print(val + " ");
		System.out.println();
	}
	
	
	
	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		System.out.println("SKIPLIST : BRUYAT Julian, ESTAILLARD Julien");
		
		int option = -1;
		
		scanner = new Scanner(System.in);
		skiplist = null;
		valeurs = new ArrayList<Integer>();
		
		
		while (option != 0) {
			realiserAction(option);
			System.out.print("• ");
			option = getChoix();
		}
	}



	


	private static void realiserAction(int option) {
		
		int methode;
		int longueur;
		int nbElem;
		int premierNbElem;
		int dernierNbElem;
		int pas;
		int nouvelElem;
		
		switch (option) {
		case 0: // Quitter
			System.out.println("Fin du programme.");
			break;
		case 1: // Initialiser / Vider
			skiplist = new PrintableSkiplist();
			valeurs.clear();
			System.out.println("Skiplist crée");
			break;
		case 2: // Insérer
			nouvelElem = entree("Clé ?");
			if (skiplist.inserer(nouvelElem)) {
				valeurs.add(nouvelElem);
				System.out.println("L'élément de clé " + nouvelElem + " a bien été inseré.");
			} else {
				System.out.println("Il y avait déjà un élément de clé " + nouvelElem + " insérée.");
			}
			break;
		case 3: // Supprimer
			nouvelElem = entree("Clé à supprimer ?");
			skiplist.supprimer(nouvelElem);
			valeurs.remove(new Integer(nouvelElem));
			System.out.println("Il n'y a plus d'élément de clé " + nouvelElem);
			break;
		case 11: // Générer aléatoirement
			skiplist = new PrintableSkiplist(hauteur_choisie);
			valeurs = skiplist.remplirAleatoirement(entree("Nombre de valeurs ?"), valeur_max);
			System.out.println("Skiplist générée aléatoirement crée");
			break;
		case 12: // Retirer aléatoirement des éléments
			skiplist.supprimerDesElements(valeurs, entree("Nombre d'éléments à supprimer ?"));
			System.out.println("Les éléments ont été supprimés");
			break;
		case 21: // Afficher
			if (skiplist == null)
				System.out.println("Pas de skiplist");
			else {
				methode = entree("Quelle méthode ? (0 : Clés, Hauteur ; 1 : Avec clés pointées)");
				if (methode == 0) {
					skiplist.afficher();	
				} else {
					skiplist.afficherElementsPointes();
				}
			}
			break;
		case 22: // Chercher un élément
			Menu.afficherArrayList(valeurs);
			if (skiplist.estPresent(entree("Valeur à rechercher ?"))) {
				System.out.println("La valeur est présente");
			} else {
				System.out.println("La valeur n'est pas présente");
			}
			
			break;
		case 23: // Afficher la liste des éléments présents
			Menu.afficherArrayList(valeurs);
			break;
		case 24: // Aficher la structure en batons
			skiplist.afficherSkiplistSansValeurs(new FormatBaton());
			break;
		case 25: // Aficher la structure avec nb de sauts
			skiplist.afficherSkiplistSansValeurs(new FormatChiffres());
			break;
		case 31: // Afficher nb de pointeurs utiles
			skiplist.afficherNbDePointeursDifferents();
			break;
		case 32: // Afficher la moyenne du nb de sauts
			skiplist.afficherStatsHauteurs();
			break;
		case 33: // Afficher le temps gagné sur une linekd list
			skiplist.tempsGagneSurLinkedList(valeurs);
			break;
		case 51: // Statistiques : nb de tours
			longueur = entree("Combien de sections ?");
			nbElem = entree("Combien d'éléments ?");
			
			//t.getPourcentagesDeToursParSection(longueur, nbElem);
			t.afficher_stats_repartition_spatiale(longueur, nbElem);
			
			break;
		case 52: // Statistiques : temps gagné par rapport à linkedlist
			premierNbElem = entree("Premier point ?");
			dernierNbElem = entree("Dernier point ?");
			pas = entree("Pas ?");
			
			StatsSurSkiplist.printPointsGraphique(premierNbElem, dernierNbElem, pas);
			break;
		case 34:
			Couple<Long, Long> temps = skiplist.testerTempsAccessEquilibre(valeurs);
			System.out.println("Temps d'accés avec schéma idéal : " + temps.b + "ms");
			System.out.println("Temps d'accés avec schéma généré aléatoirement : " + temps.a + "ms");
			break;
		case 35 :
			double[] ecartsTypes = skiplist.getEcartType();
			for (int i = 0 ; i < ecartsTypes.length ; i++) {
				System.out.println("Etage " + i + " : " + ecartsTypes[i]);
			}
			break;
			
		case 36:
			int[] nbDeToursTotales = skiplist.getNbToursParEtage();
			
			for (int i = 0 ; i < skiplist.hauteur_maximale ; i++)
				System.out.println("Etage " + i + " : " + nbDeToursTotales[i] + " tours");
			break;
		case 13:
			skiplist = skiplist.equilibrerTours();
			System.out.println("La Skiplist a été remplacée par une skiplist avec des tours idéalement placées");
			break;
		case 99:
			PrintableSkiplist.testerValiditeDesSkiplist();
			break;
		default:
			afficher_menu();
		}
		
	}

	private static int entree(String chaine) {
		System.out.print(chaine + " ");
		return getChoix();
	}


	private static int getChoix() {
		int valeur = scanner.nextInt();
		scanner.reset();
		return valeur;
	}



	private static void afficher_menu() {
		System.out.println("-- Menu");
		System.out.println("0 : Quitter l'application");
		
		System.out.println("- Skiplist gérée par l'utilisateur");
		System.out.println("1 : Initialiser la skiplist");
		System.out.println("2 : Insérer un élément");
		System.out.println("3 : Retirer un élément");


		System.out.println("- Skiplist automatique");
		System.out.println("11 : Remplacer par une skiplist automatiquement");
		System.out.println("12 : Retirer plusieurs éléments aléatoirement");
		System.out.println("13 : Remplacer par une skiplist contenant les mêmes éléments mais organisés idéalement");
		

		System.out.println("- Affichage");
		System.out.println("21 : Afficher la skiplist courante");
		System.out.println("22 : Tester la présence d'un élément");
		System.out.println("23 : Afficher la liste des valeurs présentes (tableau)");
		System.out.println("24 : Afficher la structure");
		System.out.println("25 : Afficher la structure avec le nombre de sauts");

		System.out.println("31 : Afficher le nombre de pointeurs différents");
		System.out.println("32 : Afficher le nombre d'éléments sautés moyens par étage");
		System.out.println("36 : Afficher le nombre de tours pour chaque étages");
		

		System.out.println("- Analyse basique");
		System.out.println("33 : Afficher le temps gagné sur une linked list pour tout chercher");
		System.out.println("34 : Afficher la différence de temps entre schéma idéal et schéma actuel");
		System.out.println("35 : Afficher l'écart type à chaque étage");

		System.out.println("- Statistiques sur plusieurs skiplists");
		System.out.println("51 : Afficher le pourcentage de tours par section de liste");
		System.out.println("52 : Générer coordonnées pour comparer Skip et Linked list");		
		
		System.out.println("- Divers");
		System.out.println("99 : Tester la validité du chainage des éléments");
	}
}
