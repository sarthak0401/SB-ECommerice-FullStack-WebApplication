package org.ecommerce.project.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    public String username;
    public String jwtToken;
    public List<String> roles;
}
