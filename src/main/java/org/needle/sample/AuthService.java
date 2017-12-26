package org.needle.sample;

import org.needle.di.annotations.Service;

@Service
public class AuthService implements Initializable {

	@Override
	public void init() {
		System.out.println("Hello from AuthService");
	}

}
