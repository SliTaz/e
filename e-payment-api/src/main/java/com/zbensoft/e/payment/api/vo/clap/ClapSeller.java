package com.zbensoft.e.payment.api.vo.clap;

import org.apache.commons.lang3.StringUtils;

public class ClapSeller {
	
	public ClapSeller(){
		
	}
	
	public ClapSeller(String record){
		if (record != null && record.length() > 0) {
			String[] newSeller = record.split(",",20);
			if (newSeller != null && newSeller.length == 8) {
				if (StringUtils.isNoneEmpty(newSeller[0])) {
					clapCode=newSeller[0];
				}
				if (StringUtils.isNoneEmpty(newSeller[1])) {
					roleType=newSeller[1];
				}
				if (StringUtils.isNoneEmpty(newSeller[2])) {
					vid=newSeller[2];
				}
				if (StringUtils.isNoneEmpty(newSeller[3])) {
					name1=newSeller[3];
				}
				if (StringUtils.isNoneEmpty(newSeller[4])) {
					name2=newSeller[4];
				}
				if (StringUtils.isNoneEmpty(newSeller[5])) {
					lastName1=newSeller[5];
				}
				if (StringUtils.isNoneEmpty(newSeller[6])) {
					lastname2=newSeller[6];
				}
				if (StringUtils.isNoneEmpty(newSeller[7])) {
					status=newSeller[7];
				}
				
				
			}
		}
	}
	
	
	private String clapCode;
	private String roleType;
	private String vid;
	private String name1;
	private String name2;
	private String lastName1;
	private String lastname2;
	private String status;
	
	
	public String getClapCode() {
		return clapCode;
	}

	public void setClapCode(String clapCode) {
		this.clapCode = clapCode;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getLastName1() {
		return lastName1;
	}

	public void setLastName1(String lastName1) {
		this.lastName1 = lastName1;
	}

	public String getLastname2() {
		return lastname2;
	}

	public void setLastname2(String lastname2) {
		this.lastname2 = lastname2;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
