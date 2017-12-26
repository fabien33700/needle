package org.needle.sample;

import org.needle.di.annotations.Inject;
import org.needle.di.annotations.Service;

@Service
public class DataService implements Initializable {
	
	private AuthService authService;
	
	@Inject 
	public DataService(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public void init() {
		System.out.println("Hello from DataService");
		authService.init();
	}

}
