package com.agprogramming.seom_v2_bff.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agprogramming.seom_v2_bff.exceptions.ExpiredRefreshTokenException;
import com.agprogramming.seom_v2_bff.models.RefreshToken;
import com.agprogramming.seom_v2_bff.repository.RefreshTokenRepository;
import com.agprogramming.seom_v2_bff.repository.UserRepository;

@Service
public class RefreshTokenService {
	@Value("${seom_auth.app.jwtRefreshExpirationMs}")
	private Long refreshTokenDurationMs;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private UserRepository userRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createRefreshToken(Long userId) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(userRepository.findById(userId).get());
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new ExpiredRefreshTokenException();
		}
		return token;
	}

	@Transactional
	public int deleteByUserId(Long userId) {
		return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
	}
}
