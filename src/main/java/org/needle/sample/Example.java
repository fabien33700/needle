package org.needle.sample;

import org.needle.di.ServiceBuilder;
import org.needle.di.errors.InjectionException;

public class Example {

	public static void main(String[] args) {
		try {
			UserService service = ServiceBuilder
				.instance(UserService.class)
				.configure()
					.put("prenom", "Fabien")
					.put("age", 15)
					.done()
				.build();
			
			service.init();
		} catch (InjectionException e) {
			e.printStackTrace();
		}
		
		
	}

}
