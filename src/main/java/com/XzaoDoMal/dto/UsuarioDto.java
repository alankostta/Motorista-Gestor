package com.XzaoDoMal.dto;

import java.util.Set;
import com.XzaoDoMal.modelo.Role;

public record UsuarioDto(long id, String login, String senha, Set<Role> roles) {
}
