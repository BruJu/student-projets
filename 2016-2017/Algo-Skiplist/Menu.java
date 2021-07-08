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
			System.out.print("� ");
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
			System.out.println("Skiplist cr�e");
			break;
		case 2: // Ins�rer
			nouvelElem = entree("Cl� ?");
			if (skiplist.inserer(nouvelElem)) {
				valeurs.add(nouvelElem);
				System.out.println("L'�l�ment de cl� " + nouvelElem + " a bien �t� inser�.");
			} else {
				System.out.println("Il y avait d�j� un �l�ment de cl� " + nouvelElem + " ins�r�e.");
			}
			break;
		case 3: // Supprimer
			nouvelElem = entree("Cl� � supprimer ?");
			skiplist.supprimer(nouvelElem);
			valeurs.remove(new Integer(nouvelElem));
			System.out.println("Il n'y a plus d'�l�ment de cl� " + nouvelElem);
			break;
		case 11: // G�n�rer al�atoirement
			skiplist = new PrintableSkiplist(hauteur_choisie);
			valeurs = skiplist.remplirAleatoirement(entree("Nombre de valeurs ?"), valeur_max);
			System.out.println("Skiplist g�n�r�e al�atoirement cr�e");
			break;
		case 12: // Retirer al�atoirement des �l�ments
			skiplist.supprimerDesElements(valeurs, entree("Nombre d'�l�ments � supprimer ?"));
			System.out.println("Les �l�ments ont �t� supprim�s");
			break;
		case 21: // Afficher
			if (skiplist == null)
				System.out.println("Pas de skiplist");
			else {
				methode = entree("Quelle m�thode ? (0 : Cl�s, Hauteur ; 1 : Avec cl�s point�es)");
				if (methode == 0) {
					skiplist.afficher();	
				} else {
					skiplist.afficherElementsPointes();
				}
			}
			break;
		case 22: // Chercher un �l�ment
			Menu.afficherArrayList(valeurs);
			if (skiplist.estPresent(entree("Valeur � rechercher ?"))) {
				System.out.println("La valeur est pr�sente");
			} else {
				System.out.println("La valeur n'est pas pr�sente");
			}
			
			break;
		case 23: // Afficher la liste des �l�ments pr�sents
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
		case 33: // Afficher le temps gagn� sur une linekd list
			skiplist.tempsGagneSurLinkedList(valeurs);
			break;
		case 51: // Statistiques : nb de tours
			longueur = entree("Combien de sections ?");
			nbElem = entree("Combien d'�l�ments ?");
			
			//t.getPourcentagesDeToursParSection(longueur, nbElem);
			t.afficher_stats_repartition_spatiale(longueur, nbElem);
			
			break;
		case 52: // Statistiques : temps gagn� par rapport � linkedlist
			premierNbElem = entree("Premier point ?");
			dernierNbElem = entree("Dernier point ?");
			pas = entree("Pas ?");
			
			StatsSurSkiplist.printPointsGraphique(premierNbElem, dernierNbElem, pas);
			break;
		case 34:
			Couple<Long, Long> temps = skiplist.testerTempsAccessEquilibre(valeurs);
			System.out.println("Temps d'acc�s avec sch�ma id�al : " + temps.b + "ms");
			System.out.println("Temps d'acc�s avec sch�ma g�n�r� al�atoirement : " + temps.a + "ms");
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
			System.out.println("La Skiplist a �t� remplac�e par une skiplist avec des tours id�alement plac�es");
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
		
		System.out.println("- Skiplist g�r�e par l'utilisateur");
		System.out.println("1 : Initialiser la skiplist");
		System.out.println("2 : Ins�rer un �l�ment");
		System.out.println("3 : Retirer un �l�ment");


		System.out.println("- Skiplist automatique");
		System.out.println("11 : Remplacer par une skiplist automatiquement");
		System.out.println("12 : Retirer plusieurs �l�ments al�atoirement");
		System.out.println("13 : Remplacer par une skiplist contenant les m�mes �l�ments mais organis�s id�alement");
		

		System.out.println("- Affichage");
		System.out.println("21 : Afficher la skiplist courante");
		System.out.println("22 : Tester la pr�sence d'un �l�ment");
		System.out.println("23 : Afficher la liste des valeurs pr�sentes (tableau)");
		System.out.println("24 : Afficher la structure");
		System.out.println("25 : Afficher la structure avec le nombre de sauts");

		System.out.println("31 : Afficher le nombre de pointeurs diff�rents");
		System.out.println("32 : Afficher le nombre d'�l�ments saut�s moyens par �tage");
		System.out.println("36 : Afficher le nombre de tours pour chaque �tages");
		

		System.out.println("- Analyse basique");
		System.out.println("33 : Afficher le temps gagn� sur une linked list pour tout chercher");
		System.out.println("34 : Afficher la diff�rence de temps entre sch�ma id�al et sch�ma actuel");
		System.out.println("35 : Afficher l'�cart type � chaque �tage");

		System.out.println("- Statistiques sur plusieurs skiplists");
		System.out.println("51 : Afficher le pourcentage de tours par section de liste");
		System.out.println("52 : G�n�rer coordonn�es pour comparer Skip et Linked list");		
		
		System.out.println("- Divers");
		System.out.println("99 : Tester la validit� du chainage des �l�ments");
	}
}
