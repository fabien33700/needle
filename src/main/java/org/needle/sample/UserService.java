package org.needle.sample;

import org.needle.di.Inject;
import org.needle.di.Service;

@Service
public class UserService implements Initializable {
	
	private DataService dataService;

	@Override
	public void init() {
		System.out.println("Hello from UserService");
		dataService.init();
	}
	
	@Inject
	public UserService(DataService dataService) {
		this.dataService = dataService;
		
	}

}
