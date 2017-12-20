package org.needle.di;

public class InjectionException extends Exception {

	private static final long serialVersionUID = 4701495830786103003L;
	
	public final static String INSTANCIATION_FAILED = "Impossible d'instancier le service %s. Avez-vous oublier le constructeur vide ?";
	public final static String CYCLIC_DEPENDENCIES  = "La classe %s apparaît déjà dans le graphe de dépendances, il y a donc une référence circulaire entre les services.";
	public final static String INJECTION_FAILED     = "Impossible d'injecter une instance dans le champ %s.";
	public final static String NESTED_EXCEPTION     = "Impossible de créer la dépendance à injecter dans le champ %s. Exception imbriquée : %s";
	public final static String UNAVAILABLE_PARAM    = "Le paramètre %s n'a pas pu être résolu avec la configuration de l'injecteur. Le paramètre a-t-il été déclaré ?";

	public InjectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InjectionException(String message) {
		super(message);
	}
	
	

}
