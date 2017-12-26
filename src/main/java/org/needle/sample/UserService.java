package org.needle.sample;

import org.needle.di.annotations.Inject;
import org.needle.di.annotations.Service;

@Service
public class UserService implements Initializable {
	
	private DataService dataService;

	@Override
	public void init() {
		System.out.println("Hello from UserService");
		dataService.init();
	}
	
	@Inject
	public UserService(DataService dataService, Object hello) {
		this.dataService = dataService;
	}

}
