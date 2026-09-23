package br.com.jhonnyazevedo.timegrid_backend.auth.service;

import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginRequest;
import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
