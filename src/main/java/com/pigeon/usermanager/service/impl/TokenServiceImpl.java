package com.pigeon.usermanager.service.impl;

import com.pigeon.usermanager.exception.TokenServiceException;
import com.pigeon.usermanager.exception.UserServiceException;
import com.pigeon.usermanager.exception.enums.TokenErrorCode;
import com.pigeon.usermanager.exception.enums.UserErrorCode;
import com.pigeon.usermanager.model.dto.TokenDto;
import com.pigeon.usermanager.model.entity.UserEntity;
import com.pigeon.usermanager.repository.UserRepository;
import com.pigeon.usermanager.security.JwtProvider;
import com.pigeon.usermanager.service.TokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final String TOKEN_KEY = "token";

    private final JwtProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public TokenDto getTokens() {
        Object tokens = this.getSession().getAttribute(TOKEN_KEY);

        if (tokens == null) throw this.generateException(TokenErrorCode.TOKEN_NOT_FOUND);
        else return (TokenDto) tokens;
    }

    @Override
    public TokenDto updateAuthToken() {
        TokenDto tokens = this.getTokens();
        UserEntity user = this.getUserFromRefresh(tokens.getRefresh());
        tokens.setAuthorization(tokenProvider.generateAccessToken(user));

        this.getSession().setAttribute(TOKEN_KEY, tokens);
        return tokens;
    }

    @Override
    public TokenDto createAuthToken(UserEntity user) {
        TokenDto tokens = TokenDto.builder()
                .authorization(tokenProvider.generateAccessToken(user))
                .refresh(tokenProvider.generateRefreshToken(user))
                .build();
        this.getSession().setAttribute(TOKEN_KEY, tokens);
        return tokens;
    }

    @Override
    public UserEntity removeToken() {
        TokenDto token = this.getTokens();
        UserEntity user = this.getUserFromRefresh(token.getRefresh());
        this.getSession().removeAttribute(TOKEN_KEY);
        return user;
    }

    private UserEntity getUserFromRefresh(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw this.generateException(TokenErrorCode.INVALID_REFRESH_TOKEN);
        }
        Claims claims = tokenProvider.getRefreshClaims(refreshToken);
        String login = claims.getSubject();
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserServiceException(UserErrorCode.USER_NOT_FOUND, new Exception()));
    }

    private HttpSession getSession() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        return ((ServletRequestAttributes) attributes).getRequest().getSession();
    }

    private TokenServiceException generateException(TokenErrorCode errorCode) {
        return new TokenServiceException(errorCode, new Exception());
    }
}
