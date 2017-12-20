package org.needle.sample;

import org.needle.di.InjectionException;
import org.needle.di.ServiceBuilder;

public class Example {

	public static void main(String[] args) {
		try {
			UserService service = ServiceBuilder
				.instance(UserService.class)
				.build();
			
			service.init();
		} catch (InjectionException e) {
			e.printStackTrace();
		}
		
		
	}

}
