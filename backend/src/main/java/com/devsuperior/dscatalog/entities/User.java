package com.devsuperior.dscatalog.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_user")
public class User implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER) // Anotação para forçar que sempre que um usuário for buscado no banco, vira os atributos de todas entidades associadas a ele
    @JoinTable( // Anotação que cria uma tabel e faz a associação entre duas entidades
            name = "tb_user_role",
            joinColumns = @JoinColumn(name = "user_id"),  // joinColumns Estabelece a chave estrangeira relacionada a classe presente
            inverseJoinColumns = @JoinColumn(name = "role_id")) // inverseJoinColumns Estabelece a outra chave estrangeira com base no objeto da coleção, nesse caso é Category
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(Long id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Métodos implementados a partir da interface UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Estamos convertendo todas as Roles para SimpleGrantedAuthority
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // como não utilizaremos esse método, vamos retornar true
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // como não utilizaremos esse método, vamos retornar true
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // como não utilizaremos esse método, vamos retornar true
        return true;
    }

    @Override
    public boolean isEnabled() {
        // como não utilizaremos esse método, vamos retornar true
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }


}
