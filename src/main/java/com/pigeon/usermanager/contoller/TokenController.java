package com.pigeon.usermanager.contoller;

import com.pigeon.usermanager.model.dto.TokenDto;
import com.pigeon.usermanager.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Token", description = "API для получения и обновления токенов аунтентификации")
@RestController
@RequestMapping("/v1/token")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final TokenService tokenService;

    @GetMapping
    @Operation(description = "Получение токена авторизации")
    public ResponseEntity<TokenDto> getAuthToken() {
        TokenDto tokenDto = tokenService.getTokens();
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping
    @Operation(description = "Получение обновленного JWT")
    public ResponseEntity<TokenDto> updateToken() {
        TokenDto tokenDto = tokenService.updateAuthToken();
        return ResponseEntity.ok(tokenDto);
    }
}
