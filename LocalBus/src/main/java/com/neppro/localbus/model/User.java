package com.neppro.localbus.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="user")
public class User implements Serializable {
		
	private static final long serialVersionUID = 1L;

	
	@Id
	@Column(name = "user_name")
	private String username;
	
	@Column(name = "password")
	private String password;

	@ManyToOne
    @JoinColumn(name="role_name")
	private Role role;
	
	@Column(name="password_salt")
	private String passwordSalt;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}
		
}
