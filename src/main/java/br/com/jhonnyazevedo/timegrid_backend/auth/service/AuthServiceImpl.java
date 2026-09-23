package br.com.jhonnyazevedo.timegrid_backend.auth.service;

import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginRequest;
import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginResponse;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Email ou senha invalidos."));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new BusinessException("Usuario inativo.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Email ou senha invalidos.");
        }

        return new LoginResponse(jwtService.generateToken(user));
    }
}
