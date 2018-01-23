package org.needle.sample;

import org.needle.di.annotations.Resolve;
import org.needle.di.annotations.Service;

@Service
public class NameService implements Initializable {
	
	private String prenom;
	
	public String getPrenom() {
		return prenom;
	}

	@Resolve
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}



	@Override
	public void init() {
		System.out.println("Hello, " + prenom);
	}
	
}
