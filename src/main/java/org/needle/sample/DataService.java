package org.needle.sample;

import org.needle.di.Service;

@Service
public class DataService implements Initializable {

	@Override
	public void init() {
		System.out.println("Hello from DataService");
	}

}
