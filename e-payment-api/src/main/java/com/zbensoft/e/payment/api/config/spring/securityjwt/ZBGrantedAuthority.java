package com.zbensoft.e.payment.api.config.spring.securityjwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * 权限实体
 * @author xieqiang
 *
 */
public class ZBGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private String authority;
//	private final String url;

	public ZBGrantedAuthority() {
		
	}
	public ZBGrantedAuthority(String authority) {
		//Assert.hasText(role, "A granted authority textual representation is required");
		this.authority = authority;
	}

	public String getAuthority() {
		return authority;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof ZBGrantedAuthority) {
			return authority.equals(((ZBGrantedAuthority) obj).authority);
		}

		return false;
	}

	public int hashCode() {
		return this.authority.hashCode();
	}

	public String toString() {
		return this.authority;
	}

}
