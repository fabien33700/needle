package org.needle.sample;

import org.needle.di.Inject;
import org.needle.di.Service;

@Service
public class UserService implements Initializable {
	
	@Inject private DataService dataService;

	@Override
	public void init() {
		System.out.println("Hello from UserService");
		dataService.init();
	}

}
