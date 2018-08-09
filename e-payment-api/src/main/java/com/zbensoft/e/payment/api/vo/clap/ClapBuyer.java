package com.zbensoft.e.payment.api.vo.clap;

import org.apache.commons.lang3.StringUtils;

public class ClapBuyer {

	public ClapBuyer() {

	}

	public ClapBuyer(String record) {
		if (record != null && record.length() > 0) {
			String[] newbuyer = record.split(",",20);
			if (newbuyer != null && newbuyer.length == 14) {
				if (StringUtils.isNoneEmpty(newbuyer[0])) {
					vid = newbuyer[0];
				}
				if (StringUtils.isNoneEmpty(newbuyer[1])) {
					patrimonyCardSerial = newbuyer[1];
				}
				if (StringUtils.isNoneEmpty(newbuyer[2])) {
					patrimonyCardCode = newbuyer[2];
				}
				if (StringUtils.isNoneEmpty(newbuyer[3])) {
					status = newbuyer[3];
				}
				if (StringUtils.isNoneEmpty(newbuyer[4])) {
					name1 = newbuyer[4];
				}
				if (StringUtils.isNoneEmpty(newbuyer[5])) {
					name2 = newbuyer[5];
				}
				if (StringUtils.isNoneEmpty(newbuyer[6])) {
					lastName1 = newbuyer[6];
				}
				if (StringUtils.isNoneEmpty(newbuyer[7])) {
					lastName2 = newbuyer[7];
				}
				if (StringUtils.isNoneEmpty(newbuyer[8])) {
					sex = newbuyer[8];
				}
				if (StringUtils.isNoneEmpty(newbuyer[9])) {
					dateBirth = newbuyer[9];
				}
				if (StringUtils.isNoneEmpty(newbuyer[10])) {
					familyCode = newbuyer[10];
				}
				if (StringUtils.isNoneEmpty(newbuyer[11])) {
					CommunityCode = newbuyer[11];
				}
				if (StringUtils.isNoneEmpty(newbuyer[12])) {
					clapCode = newbuyer[12];
				}
				if (StringUtils.isNoneEmpty(newbuyer[13])) {
					clapName = newbuyer[13];
				}
			}
		}
	}

	private String vid;
	private String patrimonyCardSerial;
	private String patrimonyCardCode;
	private String status;
	private String name1;
	private String name2;
	private String lastName1;
	private String lastName2;
	private String sex;
	private String dateBirth;
	private String familyCode;
	private String CommunityCode;
	private String clapCode;
	private String clapName;

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getPatrimonyCardSerial() {
		return patrimonyCardSerial;
	}

	public void setPatrimonyCardSerial(String patrimonyCardSerial) {
		this.patrimonyCardSerial = patrimonyCardSerial;
	}

	public String getPatrimonyCardCode() {
		return patrimonyCardCode;
	}

	public void setPatrimonyCardCode(String patrimonyCardCode) {
		this.patrimonyCardCode = patrimonyCardCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getLastName2() {
		return lastName2;
	}

	public void setLastName2(String lastName2) {
		this.lastName2 = lastName2;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getDateBirth() {
		return dateBirth;
	}

	public void setDateBirth(String dateBirth) {
		this.dateBirth = dateBirth;
	}

	public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	public String getCommunityCode() {
		return CommunityCode;
	}

	public void setCommunityCode(String communityCode) {
		CommunityCode = communityCode;
	}

	public String getClapCode() {
		return clapCode;
	}

	public void setClapCode(String clapCode) {
		this.clapCode = clapCode;
	}

	public String getClapName() {
		return clapName;
	}

	public void setClapName(String clapName) {
		this.clapName = clapName;
	}

	
	public static void main(String[] args) {
		String test="V00988055,0016173660,0014647372,1,Domingo,,Urbina,,m,1928/05/02,,,";
		String[] tsets=test.split(",", 14);
		System.out.println(tsets.toString()+" length:"+tsets.length);
	}
}
