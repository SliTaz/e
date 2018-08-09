package com.zbensoft.e.payment.api.quartz.job.clap.firstImport.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//cedula,nombre1,nombre2,apellido1,apellido2,fecha_nacimiento,genero,serial_ciudadano,serial_carnet,clap_nombre,clap_codigo,comunidad_nombre,comunidad_codigo,family_id
//ID,Name1,Name2,Lastname1,Lastname2,bitrhDay,gender,
public class Record {
	
	public Record(String line){
		if(line!=null&&!"".equals(line)){
			
			Pattern pCells = Pattern.compile("(\"[^\"]*(\"{2})*[^\"]*\")*[^,]*,");  
            Matcher mCells = pCells.matcher(line);  
            List<String> cells = new ArrayList<String>();//每行记录一个list  
            //读取每个单元格  
            String str="";
            while (mCells.find()) {  
                str = mCells.group();  
                str = str.replaceAll("(?sm)\"?([^\"]*(\"{2})*[^\"]*)\"?.*,", "$1");  
                str = str.replaceAll("(?sm)(\"(\"))", "$2");
                cells.add(str);  
            }  
            String[] tmp=line.split(",");
            cells.add(tmp[tmp.length-1]);
            
			if(cells.size()==14){
				idNumber=cells.get(0).trim();
				name1=cells.get(1);
				name2=cells.get(2);
				lastname1=cells.get(3);
				lastname2=cells.get(4);
				sex=cells.get(6).trim();
				birthDay=cells.get(5);
				familyCode=cells.get(13).trim();
				communityCode=cells.get(12).trim();
				clapCard=cells.get(7).trim();
				clapCardSeri=cells.get(8).trim();
				clapStore=cells.get(10).trim();
			}
		}
		
	}
	
	
	private String idNumber;//0
	private String name1;//1
	private String name2;//2
	private String lastname1;//3
	private String lastname2;//4
	private String sex;//6
	private String birthDay;//5
	private String familyCode;//13
	private String communityCode;//12
	private String clapCard;//7
	private String clapCardSeri;//8
	private String clapStore;//11
	
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
	public String getLastname1() {
		return lastname1;
	}
	public void setLastname1(String lastname1) {
		this.lastname1 = lastname1;
	}
	public String getLastname2() {
		return lastname2;
	}
	public void setLastname2(String lastname2) {
		this.lastname2 = lastname2;
	}
	
	public String getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}
	public String getFamilyCode() {
		return familyCode;
	}
	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}
	
	public String getClapCard() {
		return clapCard;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getCommunityCode() {
		return communityCode;
	}
	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}
	public void setClapCard(String clapCard) {
		this.clapCard = clapCard;
	}
	public String getClapStore() {
		return clapStore;
	}
	public void setClapStore(String clapStore) {
		this.clapStore = clapStore;
	}
	public String getClapCardSeri() {
		return clapCardSeri;
	}
	public void setClapCardSeri(String clapCardSeri) {
		this.clapCardSeri = clapCardSeri;
	}
	
	
	
	

}
