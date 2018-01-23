package org.needle.sample;

import org.needle.di.annotations.Inject;
import org.needle.di.annotations.Service;

@Service
public class AuthService implements Initializable {
	
	@Inject
	private NameService nameService;

	@Override
	public void init() {
		System.out.println("Hello from AuthService");
		nameService.init();
	}

}
