package com.rick.smartparkingplatform.entity;

import com.rick.smartparkingplatform.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    // Identificador do usuário.
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // E-mail do usuário.
    @Column(nullable = false, unique = true)
    private String email;

    // Senha do usuário.
    @Column(nullable = false)
    private String password;

    // Perfil de acesso do usuário.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Indica se o usuário está ativo.
    @Column(nullable = false)
    private boolean active = true;

    // Data de criação.
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Data da última atualização.
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Retorna as permissões do usuário.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    // Retorna o e-mail utilizado para autenticação.
    @Override
    public String getUsername() {
        return email;
    }

    // Retorna a senha utilizada para autenticação.
    @Override
    public String getPassword() {
        return password;
    }

    // Indica se o usuário está habilitado.
    @Override
    public boolean isEnabled() {
        return active;
    }

    // Indica se a conta não está expirada.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indica se a conta não está bloqueada.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indica se as credenciais não estão expiradas.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}