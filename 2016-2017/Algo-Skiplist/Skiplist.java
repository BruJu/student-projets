package skiplist;

/*
 * Ce fichier contient l'implémentation de base des skiplist
 * 
 * 
 */

public class Skiplist implements Iterable<Element> {
	protected Tour debut;
	protected int hauteur_maximale;
	
	
	/**
	 * Probabilité d'ajouter un étage
	 */
	final private double proba = 0.5;
	
	
	public Skiplist(int h_max) {
		hauteur_maximale = h_max;
		debut = new Tour(hauteur_maximale, 1);
	}
	
	public Skiplist() {
		hauteur_maximale = 10;
		debut = new Tour(hauteur_maximale, 1);
	}
	
	
	/**
	 * Compte le nombre d'�l�ments dans la liste
	 * @return Le maximum entre tous les nombres n�gatifs et le nombre d'�l�ments dans la liste
	 * Cout : n
	 */
	public int nb_delements() {
		int compter = 0;
		
		Element actuel = debut.etage[0];
		while(actuel != null) {
			compter++;
			actuel = actuel.tour.etage[0];
		}
		
		return compter;
	}
	
	/**
	 * Affiche la skiplist
	 * Cout : n
	 */
	public void afficher() {
		if (debut == null) {
			System.out.println("Liste vide");
			return;
		}
		
		Element actuel;
		
		for(actuel = debut.etage[0] ; actuel != null ; actuel = actuel.tour.etage[0]) {
			actuel.afficher();
		}
		
		System.out.println();
	}
	
	
	/**
	 * Renvoie vrai si un élément de clé clé est présent
	 * @param cle
	 * Cout : n * h
	 */
	public boolean estPresent(int cle) {		
		Element actuel = debut.getNextByKey(cle);
		
		while (actuel != null && actuel.cle != cle) {
			actuel = actuel.tour.getNextByKey(cle);
		}
		
		return actuel != null;
	}
	
	
	/**
	 * Insèrer un élément de clé clé dans la skiplist
	 * Cout : n * h
	 * @param cle
	 * @return Faux si un élément ayant la même clé est déjà présent, vrai sinon
	 */
	public boolean inserer(int cle) {
		if (estPresent(cle))
			return false;
		
		Element a_ajouter = new Element(cle, hauteur_maximale, proba);		
		
		return inserer(a_ajouter);
	}

	/**
	 * Insère l'élément nouvelElem dans la skiplist.
	 * @param nouvelElem
	 * @return Toujours vrai
	 */
	protected boolean inserer(Element nouvelElem) {
		int hauteurSurPlusPetit;
		Tour currentTower = debut;
		
		while (currentTower != null) {
			hauteurSurPlusPetit = currentTower.pointerSurAuPlus(nouvelElem);
			currentTower = currentTower.getNextTower(hauteurSurPlusPetit);
		}
		
		return true;
	}





	/**
	 * Supprime l'élément de <code>clé/code> clé de la skiplist
	 * Cout : nh
	 * 
	 * @param cle Clé de l'élément à suprpimer
	 */
	public void supprimer(int cle) {
		Tour current = debut;
		int etageSurElementSuivant;
		
		while (current != null) {
			etageSurElementSuivant = current.nePlusPointerSur(cle);
			current = current.getNextTower(etageSurElementSuivant);
		}
		
		return;
	}



	
	/**
	 * Renvoie vrai si le chainage de la liste chainée est valide.
	 * Cout : n*h
	 * 
	 * @return Vrai si la liste des valide dans son chainage des étages supérieurs
	 */
	public boolean estValide() {
		if (debut.etage[0] == null)
			return true;
		
		int clesPointeesEnTeteDeListe[] = new int[hauteur_maximale];
		
		debut.inscrireClesPointeesParEtage(clesPointeesEnTeteDeListe);
		
		return debut.getNext(0).estValide(clesPointeesEnTeteDeListe);
	}
	
	
	/**
	 * Vide la skiplist
	 */
	public void vider() {
		debut.vider();
	}
	

	
	
	/**
	 * On définit un itérateur qui permet d'utiliser le sucre syntaxique ci-dessous pour parcourir par le
	 * bas (à la manière d'une LinkedList) la Skiplist
	 * for (Element elem : this)
	 */
	private class Iterator implements java.util.Iterator<Element> {

		Element actuel;
		
		public Iterator() {
			actuel = debut.getNext(0);
		}

		@Override
		public boolean hasNext() {
			return actuel != null;
		}

		@Override
		public Element next() {
			Element retourner = actuel;
			actuel = actuel.tour.getNext(0);
			return retourner;
		}

		@Override
		public void remove() {
			// Certains builds de projet Java demandent d'implémenter la suppression pour faire un Iterator 
			supprimer(actuel.cle);
		}
	}

	@Override
	public java.util.Iterator<Element> iterator() {
		return new Iterator();
	}
	
			
}


/*
 * ======================================================================================
 * ====================================== ELEMENT =======================================
 * ======================================================================================
 */

class Element {
	
	/**
	 * Clé
	 */
	int			cle;
	
	
	/**
	 * Pointeurs
	 */
	Tour		tour;
	
	
	
	/**
	 * Cr� un nouvel �l�ment de cl� <code>cl�</code> et de hauteur maximale <code>hauteur_maximale</code>
	 * @param cle Valeur de l'�l�ment
	 * @param proba 
	 */
	Element(int cle, int hauteur_maximale, double proba) {
		// Affectation de la clé
		this.cle = cle;
		
		// Génération de la hauteur de la tour
		tour = new Tour(hauteur_maximale, proba);
	}
	
	/**
	 * Affiche l'�l�ment sous le format {cl�,hauteur} dans la sortie standard
	 * Cout : 1
	 */
	void afficher() {
		System.out.print("{" + cle + " ," + tour.hauteur + "}");
		
	}
	
	/**
	 * Affiche l'�l�ment sous le format {cl�,hauteur} cl� des �l�ments point�s par chaque �tage
	 * Cout : h
	 */
	void afficher2() {
		System.out.print("{" + cle + "," + tour.hauteur +"} ");
		tour.afficherClesPointees();
	}


	

		
	/**
	 * Renvoie vrai si pour tout e € [0 ; hauteur de l'élément courant[, clesPointeesParLElementPrecedent[e] = cle de l'élément courant.
	 *                           et si en remplacant tous les clesPointeesParLElementPrecedent[e] par la clé de l'élément pointé par tour.etage[e]
	 *                           et en appelant estValide() sur l'élément juste après l'élément courant (ou si on est en tête de liste),
	 *                              on obtient également vrai.
	 * Cout : n * h
	 * @return Vrai si le chainage de la skiplist est valide
	 */
	boolean estValide(int clesPointees[]) {
		// Vérifier la validité pour l'élément courant
		int i;
		for (i = 0 ; i < tour.hauteur ; i++) {
			if (clesPointees[i] != cle) {
				return false;	
			}
		}
		
		// Passer au suivant
		if (tour.getNext(0) == null) {
			return true;
		} else {
			// Renseigner les étages pointés par la tour courante
			tour.inscrireClesPointeesParEtage(clesPointees);
			
			// Tester si la tour suivante est également valide
			return tour.getNext(0).estValide(clesPointees);
		}
	}
	
}

/*
 * ======================================================================================
 * ======================================== TOUR ========================================
 * ======================================================================================
 */

class Tour {
	/**
	 * Pointeurs vers le ou les éléments suivants
	 */
	Element		etage[];
	
	/**
	 * hauteur == tour.length
	 */
	int			hauteur;
	
	/**
	 * Construit une tour de hauteur_maximale hauteur_maximale
	 * @param hauteur_maximale
	 * @param proba 
	 */
	public Tour(int hauteur_maximale, double proba) {
		// Définition de la hauteur
		hauteur = 1;
		while (hauteur < hauteur_maximale && Math.random() < proba) {
			hauteur++;
		}
		
		// Construction de la tour
		etage = new Element[hauteur];
	}

	/**
	 * Affiche la clé des éléments pointés par la tour
	 */
	public void afficherClesPointees() {
		for (int i = 0 ; i < hauteur ; i++) {
			if (getNext(i) == null) {
				System.out.print("null ");
			} else {
				System.out.print(getNext(i).cle + " ");
			}
		}
	}

	// ----------- ANALYSE ----------- //

	/**
	 * Renvoie le nombre d'éléments différents pointés
	 * @return Le nombre d'éléments différents pointés
	 */
	int getNbDelementsPointes() {
		int elem = 0;
		
		for (int etage = 0 ; etage < hauteur && this.etage[etage] != null ; etage++) {
			if (pointeSurElementDifferentQuEnDessous(etage) )
				elem++;
		}
		
		return elem;
	}

	public boolean pointeSurElementDifferentQuEnDessous(int etage) {
		return etage == 0 || this.etage[etage] != this.etage[etage-1];
	}
	
	/**
	 * Renvoie un tableau renseignant la position des éléments pointés par rapport à la tour courante
	 * Ainsi, l'élément pointé par le rdc a toujours une position 1.
	 * @return Un tableau renseignant la distance entre l'élément courant et chaque élément point� en empruntant le chemin le plus long
	 */
	int[] getNbDeSautsParEtage() {
		int nbDeSautsTotal = 0;
		int[] nbDeSautsEffectues = new int[hauteur];
		
		int etageCourant = 0;
		
		Tour tourCourante = this;
		Element elementCourant;
		

		// Parcours vertical de la tour
		while (getNext(etageCourant) != null) {
			// +1 dans le parcours horizontal
			elementCourant = tourCourante.etage[0];
			tourCourante = elementCourant.tour;
			nbDeSautsTotal++;
			
			while (getNext(etageCourant) != null 
					&& elementCourant == getNext(etageCourant)) {
				// Si l'élément trouvé en horizontal est celui pointé en vertical, on a trouvé la longueur du chemin le plus long
				nbDeSautsEffectues[etageCourant] = nbDeSautsTotal;
				
				// +1 dans le parcours vertical
				etageCourant++;
			}
		}

		return nbDeSautsEffectues;
	}
	
	void inscrireClesPointeesParEtage(int clesPointeesEnTeteDeListe[]) {
		for (int floor = 0 ; floor < hauteur ; floor++) {
			if (etage[floor] == null) // Fin de liste
				break;
			
			clesPointeesEnTeteDeListe[floor] = etage[floor].cle;
		}
	}


	// ----------- ELEMENT SUIVANT ----------- //
	

	/**
	 * Renvoie le numéro de l'étage pointant sur un élément inférieur ou égal à clé
	 * 
	 * Cout : h
	 * @param cle clé cherchée
	 * @return etage e dans tour tel que tour[e].cle <= cle et e est le plus grand possible ; 
	 *         -1 si tous les elements suivants sont > à clé 
	 */
	int etageSuivantInferieurOuEgalA(int cle) {
		int etageCourant = hauteur-1;
		
		while (etageCourant >= 0 && (etage[etageCourant] == null || etage[etageCourant].cle > cle)) {
			etageCourant--;
		}
		
		return etageCourant;
	}

	/**
	 * Renvoie l'élément suivant directement l'élément courant dans la liste tel que la cl� de l'�l�ment est <= � cl�
	 * @param cle Clé cherchée
	 * @return Un pointeur sur l'élément, ou null si tous les éléments suivant l'élément courant sont plus grand que clé
	 * Cout : h
	 */
	public Element getNextByKey(int cle) {
		return getNext(etageSuivantInferieurOuEgalA(cle));
	}

	/**
	 * Renvoie l'élément pointé par l'étage h ou null
	 * @param h Numéro de l'étage indiquant l'élément suivant.
	 * @return L'objet suivant si h € [0, hauteur-1], null sinon
	 * Cout : 1
	 */
	Element getNext(int h) {
		if (h == -1 || h >= hauteur) {
			return null;
		} else {
			return etage[h];
		}
	}


	/**
	 * Renvoie la tour de l'élément pointé par étage
	 * @param etage Numéro de l'étage
	 * @return null si pas d'élément pointé par étage, la tour sinon
	 */
	Tour getNextTower(int etage) {
		Element suivant = getNext(etage);		
		return (suivant == null) ? null : suivant.tour;
	}
	
	
	/**
	 * Renvoie la tour directement voisine de la tour actuelle
	 * @return null si fin de liste, une tour sinon
	 */
	Tour getVoisin() {
		return getNextTower(0);
	}
	


	// ----------- MODIFIER SKIPLIST ----------- //

	
	/**
	 * Redirige les pointeurs de la tour courante sur l'élément newElement si ces pointeurs
	 * pointaient sur un élément plus grand que newElement.
	 * Cout : h
	 * @param newElement L'élément dans la liste
	 * @return Plus haut étage de la tour courante qui pointe sur un élément de clé inférieure à newElement
	 */
	public int pointerSurAuPlus(Element newElement) {
		// Recherche de l'étage le plus haut en commun entre la tour courante et la tour à insérer
		int etageLePlusHautEnCommun = Math.min(hauteur, newElement.tour.hauteur)-1;
		
		// Recherche de l'étage le plus haut qui pointe sur un élément inférieur à la clé
		int etageSurElemPlusPetit = etageSuivantInferieurOuEgalA(newElement.cle);
		
		// Dans la tour courante, chaque étage qui pointe sur un élément plus grand que l'élément à insérer 
		// et qui ne dépasse pas la hauteur de la tour à insérer doivent pointer sur l'élément à insérer
		for (int i = etageLePlusHautEnCommun; i > etageSurElemPlusPetit; i--) {
			newElement.tour.etage[i] = etage[i];
			etage[i] = newElement;
		}
		
		return etageSurElemPlusPetit;
	}
	
	
	/**
	 * Redirige les pointeurs de l'élément courant vers l'élément de clé clé sur celui pointé par clé
	 * Cout : h
	 * @param cle Clé de l'élément à supprimer
	 * @return L'étage le plus haut pointant sur un élément de clé inférieur à clé
	 */
	public int nePlusPointerSur(int cle) {
		int etageCourant;
		etageCourant = etageSuivantInferieurOuEgalA(cle);
		
		// Faire pointer les étages pointant sur clé sur l'élément d'après
		while (etageCourant >= 0 && getNext(etageCourant).cle == cle) {
			// Supression du pointeur
			etage[etageCourant] = getNextTower(etageCourant).getNext(etageCourant) ;
			
			// Descente d'un étage
			etageCourant--;
		}
		return etageCourant;
	}

	/**
	 * Fait pointer tous les éléments de la liste sur null (fin de liste)
	 */
	public void vider() {
		for (int i = 0 ; i < hauteur ; i++)
			etage[i] = null;
	}

}





