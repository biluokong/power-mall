package com.biluo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements UserDetails {
	// 商城后台系统用户的属性
	private Long userId;
	private String username;
	private String password;
	private Integer status;
	private Long shopId;

	// 商城小程序系统用户的属性
	private String openid;

	private String loginType;
	private Set<String> perms = new HashSet<>();

	public Set<String> getPerms() {
		HashSet<String> permissions = new HashSet<>();
		this.perms.forEach(perm -> {
			if (perm.contains(",")) {
				String[] realPerms = perm.split(",");
				permissions.addAll(Arrays.asList(realPerms));
			} else {
				permissions.add(perm);
			}
		});
		return permissions;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return loginType + userId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return status == 1;
	}

	@Override
	public boolean isAccountNonLocked() {
		return status == 1;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return status == 1;
	}

	@Override
	public boolean isEnabled() {
		return status == 1;
	}
}
