package com.zbensoft.e.payment.api.DJEncryption.hsm.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class DJHsmDef {
	
	public static class FIN_DEVICEINFO extends Structure {
		public byte[] sDevIP = new byte[20];           // 加密机IP         
		public byte[] sDevStat = new byte[4];          // 加密机状态0-初始1-运行 2-故障3-删除
		public byte[] sDMKCV = new byte[20];           // 密钥校验值(KCV) 
		public byte[] sMainVerNo = new byte[20];       // 主机服务版本信息 
		public byte[] sDevVerNo = new byte[20];        // 管理服务版本信息
		public byte[] sAppVerNo = new byte[20];        // 密码模块版本信息
		public byte[] sDevSerNo = new byte[20];        // 设备序列号		
	    
	    public static class ByReference extends FIN_DEVICEINFO 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends FIN_DEVICEINFO 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sDevIP", "sDevStat", "sDMKCV", 
            		"sMainVerNo", "sDevVerNo", "sAppVerNo", "sDevSerNo"});
        }

		public byte[] getsDevIP() {
			return DJHsmUtils.getHexByte(sDevIP);
		}

		public byte[] getsDevStat() {
			return DJHsmUtils.getHexByte(sDevStat);
		}

		public byte[] getsDMKCV() {
			return DJHsmUtils.getHexByte(sDMKCV);
		}
		
		public byte[] getsMainVerNo() {
			return DJHsmUtils.getHexByte(sMainVerNo);
		}
		
		public byte[] getsDevVerNo() {
			return DJHsmUtils.getHexByte(sDevVerNo);
		}

		public byte[] getsAppVerNo() {
			return DJHsmUtils.getHexByte(sAppVerNo);
		}

		public byte[] getsDevSerNo() {
			return DJHsmUtils.getHexByte(sDevSerNo);
		}
	}
	
	public static class FIN_DEVICESTAT extends Structure {
		public byte[] sDevIP = new byte[20];           // ���ܻ�IP         
		public byte[] sDevStat = new byte[4];          // ���ܻ�״̬0-��ʼ1-���� 2-����3-ɾ��		
	    
	    public static class ByReference extends FIN_DEVICESTAT 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends FIN_DEVICESTAT 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sDevIP", "sDevStat"});
        }

		public byte[] getsDevIP() {
			return DJHsmUtils.getHexByte(sDevIP);
		}

		public byte[] getsDevStat() {
			return DJHsmUtils.getHexByte(sDevStat);
		}
	}
	
	public static class SFF_FIN_DEVICEINFO extends Structure {
		public int devNum = 0;            					// �豸��Ŀ
		public FIN_DEVICEINFO[] devInfo;     					// �����豸��Ϣ		
		
	    public SFF_FIN_DEVICEINFO(){
	    	FIN_DEVICEINFO.ByValue tdata = new FIN_DEVICEINFO.ByValue();
	    	devInfo = (FIN_DEVICEINFO.ByValue[])tdata.toArray(32);
	    }
	    
	    public static class ByReference extends SFF_FIN_DEVICEINFO 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends SFF_FIN_DEVICEINFO 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"devNum", "devInfo"});
        }

		public int getDevNum() {
			return devNum;
		}

		public FIN_DEVICEINFO[] getDevInfo() {
			int len = getDevNum();
			
			FIN_DEVICEINFO[] newArr = null;
			
			if (len > 0 && len <= devInfo.length) {
				newArr = new FIN_DEVICEINFO[len];
				System.arraycopy(devInfo, 0, newArr, 0, len);
			} else if (len > devInfo.length){
				newArr = devInfo;
			} else {
				newArr = null;
			}
			return newArr;
		}
	}
	
	public static class SFF_FIN_DEVICESTAT extends Structure {
		public int devNum = 0;                               // �豸��Ŀ
		public FIN_DEVICESTAT[] devStat;                         // �����豸״̬
		
	    public SFF_FIN_DEVICESTAT(){
	    	FIN_DEVICESTAT.ByValue tdata = new FIN_DEVICESTAT.ByValue();
	    	devStat = (FIN_DEVICESTAT.ByValue[])tdata.toArray(32);
	    }
	    
	    public static class ByReference extends SFF_FIN_DEVICESTAT 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends SFF_FIN_DEVICESTAT 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"devNum", "devStat"});
        }

		public int getDevNum() {
			return devNum;
		}

		public FIN_DEVICESTAT[] getDevStat() {
			int len = getDevNum();
			
			FIN_DEVICESTAT[] newArr = null;
			
			if (len > 0 && len <= devStat.length) {
				newArr = new FIN_DEVICESTAT[len];
				System.arraycopy(devStat, 0, newArr, 0, len);
			} else if (len > devStat.length){
				newArr = devStat;
			} else {
				newArr = null;
			}
			
			return newArr;
		}
	}
	
	// -------------------------------------- ���� IC �� -------------------------------------- 
	//��Կ����
	public static class RQ_MSG_KR extends Structure {
		public byte[] sKeyType = new byte[4];         // ��Կ����
		public byte[] sLmkKeyFlag = new byte[2];      // ��Կ��ʶ(LMK)      
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ    
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����   
		public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����       
		public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ  
	    
	    public static class ByReference extends RQ_MSG_KR 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_KR 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", "sLmkKeyFlag", 
            		"sKeyStoreFlag", "sKeyStoreIndex", "sKeyTagLen", 
            		"sKeyTag"});
        }
	    
	    public void setsKeyType(byte[] keyType){
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}
		
		public void setsLmkKeyFlag(byte[] lmkKeyFlag){
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}
		
		public void setsKeyStoreFlag(byte[] keyStoreFlag){
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}
		
		public void setsKeyStoreIndex(byte[] keyStoreIndex){
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}
		
		public void setsKeyTagLen(byte[] keyTagLen){
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}
		
		public void setsKeyTag(byte[] keyTag){
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}
	}
	
	public static class RP_MSG_KS extends Structure {
		public byte[] sKeyLmk = new byte[50];         // ��Կ����         
		public byte[] sKeyCV = new byte[17];          // ��ԿУ��ֵ          
	    
	    public static class ByReference extends RP_MSG_KS 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_KS 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyLmk", "sKeyCV"});
        }
	    
	    public byte[] getsKeyLmk() {
	    	int len = DJHsmUtils.getSKeyLmkLength(sKeyLmk[0]);
	    	return DJHsmUtils.getData(sKeyLmk, len);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	}
	
	public static class RQ_MSG_KD extends Structure {
		public byte[] sSourceKeyType = new byte[4];          // Դ��Կ����
		public byte[] sSourceKey = new byte[50];             // Դ��Կ
		public byte[] sKeyType = new byte[4];                // ����Կ����
		public byte[] sKeyFlag = new byte[2];                // ����Կ��ʶ(LMK) 
		public byte[] sScatterAlgorithm = new byte[2];       // ��ɢ�㷨ģʽ
		public byte[] sScatterLvl = new byte[3];             // ��ɢ����           
		public byte[] sFactor = new byte[8*32+1];            // ��ɢ����            
		public byte[] sKeyStoreFlag = new byte[2];           // ��Կ�洢��ʶ           
		public byte[] sKeyStoreIndex = new byte[5];          // ��Կ����          
		public byte[] sKeyTagLen = new byte[3];              // ��Կ��ǩ����              
		public byte[] sKeyTag = new byte[17];                // ��Կ��ǩ  
	    
	    public static class ByReference extends RQ_MSG_KD 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_KD 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSourceKeyType", "sSourceKey", 
            		"sKeyType", "sKeyFlag", "sScatterAlgorithm", "sScatterLvl", 
            		"sFactor", "sKeyStoreFlag", "sKeyStoreIndex", "sKeyTagLen", 
            		"sKeyTag"});
        }
	    
	    public void setsSourceKeyType(byte[] sourceKeyType){
			System.arraycopy(sourceKeyType, 0, sSourceKeyType, 0, sourceKeyType.length);
		}
		
		public void setsSourceKey(byte[] sourceKey){
			System.arraycopy(sourceKey, 0, sSourceKey, 0, sourceKey.length);
		}
		
		public void setsKeyType(byte[] KeyType){
			System.arraycopy(KeyType, 0, sKeyType, 0, KeyType.length);
		}
		
		public void setsKeyFlag(byte[] keyFlag){
			System.arraycopy(keyFlag, 0, sKeyFlag, 0, keyFlag.length);
		}
		
		public void setsScatterAlgorithm(byte[] scatterAlgorithm){
			System.arraycopy(scatterAlgorithm, 0, sScatterAlgorithm, 0, scatterAlgorithm.length);
		}
		
		public void setsScatterLvl(byte[] scatterLvl){
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}
		
		public void setsFactor(byte[] factor){
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}
		
		public void setsKeyStoreFlag(byte[] keyStoreFlag){
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}
		
		public void setsKeyStoreIndex(byte[] keyStoreIndex){
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}
		
		public void setsKeyTagLen(byte[] keyTagLen){
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}
		
		public void setsKeyTag(byte[] keyTag){
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}
	}
	
	public static class RP_MSG_KE extends Structure {
		public byte[] sKeyLmk = new byte[50];         // ��Կ����              
		public byte[] sKeyCV = new byte[17];          // ��ԿУ��ֵ               
	    
	    public static class ByReference extends RP_MSG_KE 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_KE 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyLmk", "sKeyCV"});
        }

		public byte[] getsKeyLmk() {
			return DJHsmUtils.getHexByte(sKeyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_KH extends Structure {
		public byte[] sEncFlag = new byte[3];                // �����㷨ģʽ
		public byte[] sMacAlgMode = new byte[3];             // MAC�㷨ģʽ 
		public byte[] sMacValType = new byte[3];             // MACȡֵ��ʽ
		public byte[] sSrcKeyType = new byte[4];             // ������Կ����
		public byte[] sSrcKey = new byte[50];                // ������Կ
		public byte[] sSrcScatterLvl = new byte[3];          // ������Կ��ɢ����
		public byte[] sSrcFactor = new byte[8*32+1];         // ������Կ��ɢ����
		public byte[] sDesKeyType = new byte[4];             // ������Կ����
		public byte[] sDesKey = new byte[50];                // ������Կ
		public byte[] sDesScatterLvl = new byte[3];          // ������Կ��ɢ����
		public byte[] sDesFactor = new byte[8*32+1];         // ������Կ��ɢ����
		public byte[] sMacKeyType = new byte[4];             // MAC��Կ����
		public byte[] sMacKey = new byte[50];                // MAC��Կ
		public byte[] sMacScatterLvl = new byte[3];          // MAC��Կ��ɢ����
		public byte[] sMacFactor = new byte[8*32+1];         // MAC��Կ��ɢ����
		public byte[] sKeyHeadLen = new byte[3];             // ��Կͷ����
		public byte[] sKeyHead = new byte[65];               // ��Կͷ
		public byte[] sCmdHeadLen = new byte[3];             // ����ͷ����
		public byte[] sCmdHead = new byte[65];               // ����ͷ
		public byte[] sRandomLen = new byte[3];              // ���������
		public byte[] sRandom = new byte[65];                // �����
		public byte[] sIV = new byte[33];	                 // IV	                 
	    
	    public static class ByReference extends RQ_MSG_KH 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_KH 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sEncFlag", "sMacAlgMode", 
            		"sMacValType", "sSrcKeyType", "sSrcKey", "sSrcScatterLvl", 
            		"sSrcFactor", "sDesKeyType", "sDesKey", "sDesScatterLvl", 
            		"sDesFactor", "sMacKeyType", "sMacKey", "sMacScatterLvl", 
            		"sMacFactor", "sKeyHeadLen", "sKeyHead", "sCmdHeadLen", 
            		"sCmdHead", "sRandomLen", "sRandom", "sIV"});
        }
	    
		public void setsEncFlag(byte[] encFlag){
			System.arraycopy(encFlag, 0, sEncFlag, 0, encFlag.length);
		}
		
		public void setsMacAlgMode(byte[] macAlgMode){
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}
		
		public void setsMacValType(byte[] macValType){
			System.arraycopy(macValType, 0, sMacValType, 0, macValType.length);
		}
		
		public void setsSrcKeyType(byte[] srcKeyType){
			System.arraycopy(srcKeyType, 0, sSrcKeyType, 0, srcKeyType.length);
		}
		
		public void setsSrcKey(byte[] srcKey){
			System.arraycopy(srcKey, 0, sSrcKey, 0, srcKey.length);
		}
		
		public void setsSrcScatterLvl(byte[] srcScatterLvl){
			System.arraycopy(srcScatterLvl, 0, sSrcScatterLvl, 0, srcScatterLvl.length);
		}
		
		public void setsSrcFactor(byte[] srcFactor){
			System.arraycopy(srcFactor, 0, sSrcFactor, 0, srcFactor.length);
		}
		
		public void setsDesKeyType(byte[] desKeyType){
			System.arraycopy(desKeyType, 0, sDesKeyType, 0, desKeyType.length);
		}
		
		public void setsDesKey(byte[] desKey){
			System.arraycopy(desKey, 0, sDesKey, 0, desKey.length);
		}
		
		public void setsDesScatterLvl(byte[] desScatterLvl){
			System.arraycopy(desScatterLvl, 0, sDesScatterLvl, 0, desScatterLvl.length);
		}
		
		public void setsDesFactor(byte[] desFactor){
			System.arraycopy(desFactor, 0, sDesFactor, 0, desFactor.length);
		}
		
		public void setsMacKeyType(byte[] macKeyType){
			System.arraycopy(macKeyType, 0, sMacKeyType, 0, macKeyType.length);
		}
		
		public void setsMacKey(byte[] macKey){
			System.arraycopy(macKey, 0, sMacKey, 0, macKey.length);
		}
		
		public void setsMacScatterLvl(byte[] macScatterLvl){
			System.arraycopy(macScatterLvl, 0, sMacScatterLvl, 0, macScatterLvl.length);
		}
		
		public void setsMacFactor(byte[] macFactor){
			System.arraycopy(macFactor, 0, sMacFactor, 0, macFactor.length);
		}
		
		public void setsKeyHeadLen(byte[] keyHeadLen){
			System.arraycopy(keyHeadLen, 0, sKeyHeadLen, 0, keyHeadLen.length);
		}
		
		public void setsKeyHead(byte[] keyHead){
			System.arraycopy(keyHead, 0, sKeyHead, 0, keyHead.length);
		}
		
		public void setsCmdHeadLen(byte[] cmdHeadLen){
			System.arraycopy(cmdHeadLen, 0, sCmdHeadLen, 0, cmdHeadLen.length);
		}
		
		public void setsCmdHead(byte[] cmdHead){
			System.arraycopy(cmdHead, 0, sCmdHead, 0, cmdHead.length);
		}
		
		public void setsRandomLen(byte[] randomLen){
			System.arraycopy(randomLen, 0, sRandomLen, 0, randomLen.length);
		}
		
		public void setsRandom(byte[] random){
			System.arraycopy(random, 0, sRandom, 0, random.length);
		}
		
		public void setsIV(byte[] iv){
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}
	}
	
	public static class RP_MSG_KI extends Structure {
		public byte[] sKeyLen = new byte[5];       // ���ĳ���
		public byte[] sKey = new byte[129];        // ��Կ���ݿ�����
		public byte[] sMac = new byte[33];         // ����MAC
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ     	    
	    
	    public static class ByReference extends RP_MSG_KI 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_KI 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyLen", "sKey", 
            		"sMac", "sKeyCV"});
        }

		public byte[] getsKeyLen() {
			return DJHsmUtils.getHexByte(sKeyLen);
		}

		public byte[] getsKey() {
			return DJHsmUtils.getHexByte(sKey);
		}

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}

	public static class RQ_MSG_KI extends Structure {
		public byte[] sInType = new byte[2];             // ��������
		public byte[] sEncFlag = new byte[2+1];          // �����㷨ģʽ
		public byte[] sMacAlgMode = new byte[3];         // MAC�㷨ģʽ
		public byte[] sMacValType = new byte[3];         // MACȡֵ��ʽ
		public byte[] sSrcKeyType = new byte[4];         // ������Կ����
		public byte[] sSrcKey = new byte[50];            // ������Կ
		public byte[] sSrcScatterLvl = new byte[3];      // ������Կ��ɢ����
		public byte[] sSrcFactor = new byte[8*32+1];     // ������Կ��ɢ����
		public byte[] sDesKeyType = new byte[4];         // ������Կ����
		public byte[] sDesKeyFlag = new byte[2];         // ������Կ��ʶ(LMK)
		public byte[] sKeyStoreFlag = new byte[2];       // ������Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];      // ������Կ����
		public byte[] sKeyTagLen = new byte[3];          // ������Կ��ǩ����
	    public byte[] sKeyTag = new byte[17];            // ������Կ��ǩ
	    public byte[] sMacKeyType = new byte[4];         // MAC��Կ����
	    public byte[] sMacKey = new byte[50];            // MAC��Կ
	    public byte[] sMacScatterLvl = new byte[3];      // MAC��Կ��ɢ����
	    public byte[] sMacFactor = new byte[8*32+1];     // MAC��Կ��ɢ����
	    public byte[] sKeyHeadLen = new byte[3];         // ��Կͷ����
	    public byte[] sKeyHead = new byte[65];           // ��Կͷ
	    public byte[] sCmdHeadLen = new byte[3];         // ����ͷ����
	    public byte[] sCmdHead = new byte[65];           // ����ͷ
	    public byte[] sRandomLen = new byte[3];          // ���������
	    public byte[] sRandom = new byte[65];            // �����
	    public byte[] sIV = new byte[33];	             // IV              
	    public byte[] sKeyLen = new byte[5];             // ���ĳ���
	    public byte[] sKey = new byte[129];              // ��Կ���ݿ�����
	    public byte[] sMac = new byte[33];	             // ����MAC
	    
	    public static class ByReference extends RQ_MSG_KI 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_KI 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sInType", "sEncFlag", 
            		"sMacAlgMode", "sMacValType", "sSrcKeyType", "sSrcKey",
            		"sSrcScatterLvl", "sSrcFactor", "sDesKeyType", "sDesKeyFlag", 
            		"sKeyStoreFlag", "sKeyStoreIndex", "sKeyTagLen", "sKeyTag", 
            		"sMacKeyType", "sMacKey", "sMacScatterLvl", "sMacFactor", 
            		"sKeyHeadLen", "sKeyHead", "sCmdHeadLen", "sCmdHead", "sRandomLen", 
            		"sRandom", "sIV", "sKeyLen", "sKey", "sMac"});
        }

		public void setsInType(byte[] inType) {
			System.arraycopy(inType, 0, sInType, 0, inType.length);
		}

		public void setsEncFlag(byte[] encFlag) {
			System.arraycopy(encFlag, 0, sEncFlag, 0, encFlag.length);
		}

		public void setsMacAlgMode(byte[] macAlgMode) {
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}

		public void setsMacValType(byte[] macValType) {
			System.arraycopy(macValType, 0, sMacValType, 0, macValType.length);
		}

		public void setsSrcKeyType(byte[] srcKeyType) {
			System.arraycopy(srcKeyType, 0, sSrcKeyType, 0, srcKeyType.length);
		}

		public void setsSrcKey(byte[] srcKey) {
			System.arraycopy(srcKey, 0, sSrcKey, 0, srcKey.length);
		}

		public void setsSrcScatterLvl(byte[] srcScatterLvl) {
			System.arraycopy(srcScatterLvl, 0, sSrcScatterLvl, 0, srcScatterLvl.length);
		}

		public void setsSrcFactor(byte[] srcFactor) {
			System.arraycopy(srcFactor, 0, sSrcFactor, 0,srcFactor.length);
		}

		public void setsDesKeyType(byte[] desKeyType) {
			System.arraycopy(desKeyType, 0, sDesKeyType, 0, desKeyType.length);
		}

		public void setsDesKeyFlag(byte[] desKeyFlag) {
			System.arraycopy(desKeyFlag, 0, sDesKeyFlag, 0, desKeyFlag.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}

		public void setsMacKeyType(byte[] macKeyType) {
			System.arraycopy(macKeyType, 0, sMacKeyType, 0, macKeyType.length);
		}

		public void setsMacKey(byte[] macKey) {
			System.arraycopy(macKey, 0, sMacKey, 0, macKey.length);
		}

		public void setsMacScatterLvl(byte[] macScatterLvl) {
			System.arraycopy(macScatterLvl, 0, sMacScatterLvl, 0, macScatterLvl.length);
		}

		public void setsMacFactor(byte[] macFactor) {
			System.arraycopy(macFactor, 0, sMacFactor, 0, macFactor.length);
		}

		public void setsKeyHeadLen(byte[] keyHeadLen) {
			System.arraycopy(keyHeadLen, 0, sKeyHeadLen, 0, keyHeadLen.length);
		}

		public void setsKeyHead(byte[] keyHead) {
			System.arraycopy(keyHead, 0, sKeyHead, 0, keyHead.length);
		}

		public void setsCmdHeadLen(byte[] cmdHeadLen) {
			System.arraycopy(cmdHeadLen, 0, sCmdHeadLen, 0, cmdHeadLen.length);
		}

		public void setsCmdHead(byte[] cmdHead) {
			System.arraycopy(cmdHead, 0, sCmdHead, 0, cmdHead.length);
		}

		public void setsRandomLen(byte[] randomLen) {
			System.arraycopy(randomLen, 0, sRandomLen, 0, randomLen.length);
		}

		public void setsRandom(byte[] random) {
			System.arraycopy(random, 0, sRandom, 0, random.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sKeyLen, 0, keyLen.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsMac(byte[] mac) {
			System.arraycopy(mac, 0, sMac, 0, mac.length);
		}
	}
	
	public static class RP_MSG_KJ extends Structure {
		public byte[] sLMKProKey = new byte[50];       // ����Կ����(LMK)
		public byte[] sKeyCV = new byte[17];           // ��ԿУ��ֵ           
	    
	    public static class ByReference extends RP_MSG_KJ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_KJ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sLMKProKey", "sKeyCV"});
        }

		public byte[] getsLMKProKey() {
			return DJHsmUtils.getHexByte(sLMKProKey);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_SH extends Structure {
		public byte[] sEncFlag = new byte[3];          // �����㷨ģʽ
		public byte[] sSrcKeyType = new byte[4];       // ������Կ����
		public byte[] sSrcKey = new byte[50];          // ������Կ
		public byte[] sSrcScatterLvl = new byte[3];    // ������Կ��ɢ����
		public byte[] sSrcFactor = new byte[8*32+1];   // ������Կ��ɢ����
		public byte[] sSessionMode = new byte[3];      // �Ự��Կģʽ
		public byte[] sSessionFactor = new byte[33];   // �Ự��Կ����
	    public byte[] sDesKeyType = new byte[4];       // ������Կ����
	    public byte[] sDesKey = new byte[50];          // ������Կ
	    public byte[] sDesScatterLvl = new byte[3];    // ������Կ��ɢ���� 
	    public byte[] sDesFactor = new byte[8*32+1];   // ������Կ��ɢ����
	    public byte[] sKeyHeadLen = new byte[3];       // ��Կͷ����
	    public byte[] sKeyHead = new byte[65];         // ��Կͷ
	    public byte[] sExtFlag = new byte[2];          // ��չ��ʶ
	    public byte[] sPadFlag = new byte[3];          // PAD��ʶ
	    public byte[] sIV = new byte[33];	           // IV            
	    
	    public static class ByReference extends RQ_MSG_SH 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_SH 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sEncFlag", "sSrcKeyType", 
            		"sSrcKey", "sSrcScatterLvl", "sSrcFactor", "sSessionMode", 
            		"sSessionFactor", "sDesKeyType", "sDesKey", "sDesScatterLvl",
            		"sDesFactor", "sKeyHeadLen", "sKeyHead", "sExtFlag", 
            		"sPadFlag", "sIV"});
        }

		public void setsEncFlag(byte[] encFlag) {
			System.arraycopy(encFlag, 0, sEncFlag, 0, encFlag.length);
		}

		public void setsSrcKeyType(byte[] srcKeyType) {
			System.arraycopy(srcKeyType, 0, sSrcKeyType, 0, srcKeyType.length);
		}

		public void setsSrcKey(byte[] srcKey) {
			System.arraycopy(srcKey, 0, sSrcKey, 0, srcKey.length);
		}

		public void setsSrcScatterLvl(byte[] srcScatterLvl) {
			System.arraycopy(srcScatterLvl, 0, sSrcScatterLvl, 0, srcScatterLvl.length);
		}

		public void setsSrcFactor(byte[] srcFactor) {
			System.arraycopy(srcFactor, 0, sSrcFactor, 0, srcFactor.length);
		}

		public void setsSessionMode(byte[] sessionMode) {
			System.arraycopy(sessionMode, 0, sSessionMode, 0, sessionMode.length);
		}

		public void setsSessionFactor(byte[] sessionFactor) {
			System.arraycopy(sessionFactor, 0, sSessionFactor, 0, sessionFactor.length);
		}

		public void setsDesKeyType(byte[] desKeyType) {
			System.arraycopy(desKeyType, 0, sDesKeyType, 0, desKeyType.length);
		}

		public void setsDesKey(byte[] desKey) {
			System.arraycopy(desKey, 0, sDesKey, 0, desKey.length);
		}

		public void setsDesScatterLvl(byte[] desScatterLvl) {
			System.arraycopy(desScatterLvl, 0, sDesScatterLvl, 0, desScatterLvl.length);
		}

		public void setsDesFactor(byte[] desFactor) {
			System.arraycopy(desFactor, 0, sDesFactor, 0, desFactor.length);
		}

		public void setsKeyHeadLen(byte[] keyHeadLen) {
			System.arraycopy(keyHeadLen, 0, sKeyHeadLen, 0, keyHeadLen.length);
		}

		public void setsKeyHead(byte[] keyHead) {
			System.arraycopy(keyHead, 0, sKeyHead, 0, keyHead.length);
		}

		public void setsExtFlag(byte[] extFlag) {
			System.arraycopy(extFlag, 0, sExtFlag, 0, extFlag.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsIV(byte[] siv) {
			System.arraycopy(siv, 0, sIV, 0, siv.length);
		}
	}
	
	public static class RP_MSG_SI extends Structure {       
		public byte[] sKeyLen = new byte[5];       // ���ĳ���
		public byte[] sKey = new byte[129];        // ��Կ���ݿ�����
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ
	    
	    public static class ByReference extends RP_MSG_SI 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_SI 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyLen", "sKey", 
            		"sKeyCV"});
        }

		public byte[] getsKeyLen() {
			return DJHsmUtils.getHexByte(sKeyLen);
		}

		public byte[] getsKey() {
			return DJHsmUtils.getHexByte(sKey);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_SI extends Structure {
		public byte[] sEncFlag = new byte[3];         // �����㷨ģʽ
		public byte[] sSrcKeyType = new byte[4];      // ������Կ����
		public byte[] sSrcKey = new byte[50];         // ������Կ
		public byte[] sSrcScatterLvl = new byte[3];   // ������Կ��ɢ����
		public byte[] sSrcFactor = new byte[8*32+1];  // ������Կ��ɢ����
		public byte[] sSessionMode = new byte[3];     // �Ự��Կģʽ
		public byte[] sSessionFactor = new byte[33];  // �Ự��Կ����
		public byte[] sDesKeyType = new byte[4];      // ������Կ����
		public byte[] sDesKeyFlag = new byte[2];      // ������Կ��ʶ(LMK)
		public byte[] sDesKeyLen = new byte[5];       // ������Կ���ĳ���
		public byte[] sDesKey = new byte[257];        // ������Կ����
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
		public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ                
		public byte[] sKeyHeadLen = new byte[3];      // ��Կͷ����
		public byte[] sKeyHead = new byte[65];        // ��Կͷ
		public byte[] sExtFlag = new byte[2];         // ��չ��ʶ
		public byte[] sPadFlag = new byte[3];         // PAD��ʶ
		public byte[] sIV = new byte[33];	          // IV
		public byte[] sKeyCV = new byte[17];          // ��ԿУ��ֵ	    
	    
	    public static class ByReference extends RQ_MSG_SI 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_SI 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sEncFlag", "sSrcKeyType", 
            		"sSrcKey", "sSrcScatterLvl", "sSrcFactor", "sSessionMode", 
            		"sSessionFactor", "sDesKeyType", "sDesKeyFlag", "sDesKeyLen", 
            		"sDesKey", "sKeyStoreFlag", "sKeyStoreIndex", "sKeyTagLen", 
            		"sKeyTag", "sKeyHeadLen", "sKeyHead", "sExtFlag", "sPadFlag", 
            		"sIV", "sKeyCV"});
        }

		public void setsEncFlag(byte[] encFlag) {
			System.arraycopy(encFlag, 0, sEncFlag, 0, encFlag.length);
		}

		public void setsSrcKeyType(byte[] srcKeyType) {
			System.arraycopy(srcKeyType, 0, sSrcKeyType, 0, srcKeyType.length);
		}

		public void setsSrcKey(byte[] srcKey) {
			System.arraycopy(srcKey, 0, sSrcKey, 0, srcKey.length);
		}

		public void setsSrcScatterLvl(byte[] srcScatterLvl) {
			System.arraycopy(srcScatterLvl, 0, sSrcScatterLvl, 0, srcScatterLvl.length);
		}

		public void setsSrcFactor(byte[] srcFactor) {
			System.arraycopy(srcFactor, 0, sSrcFactor, 0, srcFactor.length);
		}

		public void setsSessionMode(byte[] sessionMode) {
			System.arraycopy(sessionMode, 0, sSessionMode, 0, sessionMode.length);
		}

		public void setsSessionFactor(byte[] sessionFactor) {
			System.arraycopy(sessionFactor, 0, sSessionFactor, 0, sessionFactor.length);
		}

		public void setsDesKeyType(byte[] desKeyType) {
			System.arraycopy(desKeyType, 0, sDesKeyType, 0, desKeyType.length);
		}

		public void setsDesKeyFlag(byte[] desKeyFlag) {
			System.arraycopy(desKeyFlag, 0, sDesKeyFlag, 0, desKeyFlag.length);
		}

		public void setsDesKeyLen(byte[] desKeyLen) {
			System.arraycopy(desKeyLen, 0, sDesKeyLen, 0, desKeyLen.length);
		}

		public void setsDesKey(byte[] desKey) {
			System.arraycopy(desKey, 0, sDesKey, 0, desKey.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}

		public void setsKeyHeadLen(byte[] keyHeadLen) {
			System.arraycopy(keyHeadLen, 0, sKeyHeadLen, 0, keyHeadLen.length);
		}

		public void setsKeyHead(byte[] keyHead) {
			System.arraycopy(keyHead, 0, sKeyHead, 0, keyHead.length);
		}

		public void setsExtFlag(byte[] extFlag) {
			System.arraycopy(extFlag, 0, sExtFlag, 0, extFlag.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsKeyCV(byte[] keyCV) {
			System.arraycopy(keyCV, 0, sKeyCV, 0, keyCV.length);
		}
	}
	
	public static class RP_MSG_SJ extends Structure {
		public byte[] sLMKProKey = new byte[50];     // ����Կ����(LMK) 
		public byte[] sKeyCV = new byte[17];         // ��ԿУ��ֵ	 
	    
	    public static class ByReference extends RP_MSG_SJ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_SJ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sLMKProKey", "sKeyCV"});
        }

		public byte[] getsLMKProKey() {
			return DJHsmUtils.getHexByte(sLMKProKey);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_KG extends Structure {
		public byte[] sKeyIndex = new byte[5];   // ��Կ������	 
	    
	    public static class ByReference extends RQ_MSG_KG 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_KG 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyIndex"});
        }

		public void setsKeyIndex(byte[] keyIndex) {
			System.arraycopy(keyIndex, 0, sKeyIndex, 0, keyIndex.length);
		}
	}
	
	public static class RP_MSG_KH extends Structure {
		public byte[] sKeyType = new byte[4];      // ��Կ����
		public byte[] sKeyFlag = new byte[2];      // ��Կ�㷨��ʶ
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ
		public byte[] sKeyTagLen = new byte[3];    // ��Կ��ǩ����
		public byte[] sKeyTag = new byte[17];      // ��Կ��ǩ                
		public byte[] sTimeLen = new byte[3];      // ʱ�䳤��
		public byte[] sTime = new byte[32];	       // ʱ��
	    
	    public static class ByReference extends RP_MSG_KH 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_KH 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", "sKeyFlag", 
            		"sKeyCV", "sKeyTagLen", "sKeyTag", 
            		"sTimeLen", "sTime"});
        }

		public byte[] getsKeyType() {
			return DJHsmUtils.getHexByte(sKeyType);
		}

		public byte[] getsKeyFlag() {
			return DJHsmUtils.getHexByte(sKeyFlag);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}

		public byte[] getsKeyTagLen() {
			return DJHsmUtils.getHexByte(sKeyTagLen);
		}

		public byte[] getsKeyTag() {
			String lenStr = new String(getsKeyTagLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sKeyTag, len);
		}

		public byte[] getsTimeLen() {
			return DJHsmUtils.getHexByte(sTimeLen);
		}

		public byte[] getsTime() {
			String lenStr = new String(getsTimeLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sTime, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_KF extends Structure {
		public byte[] sKeyType = new byte[3];    // ��Կ����
		public byte[] sKeyIndex = new byte[8];   // ��Կ������

	    public static class ByReference extends RQ_MSG_KF 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_KF 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", "sKeyIndex"});
        }

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKeyIndex(byte[] keyIndex) {
			System.arraycopy(keyIndex, 0, sKeyIndex, 0, keyIndex.length);
		}
	}
	
	public static class RQ_MSG_G1 extends Structure {
		public byte[] sAlgMode = new byte[3];      // �����㷨ģʽ
		public byte[] sProKey = new byte[50];      // ������Կ������KMC��
		public byte[] sExportKey = new byte[50];   // ������Կ(������KMC) 
		public byte[] sKeyData = new byte[13];     // ��Ƭ���˻���Կ����keydata
		public byte[] sCardCounter = new byte[5];  // ��Ƭ������

	    public static class ByReference extends RQ_MSG_G1 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_G1 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sProKey", 
            		"sExportKey", "sKeyData", "sCardCounter"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsProKey(byte[] proKey) {
			System.arraycopy(proKey, 0, sProKey, 0, proKey.length);
		}

		public void setsExportKey(byte[] exportKey) {
			System.arraycopy(exportKey, 0, sExportKey, 0, exportKey.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsCardCounter(byte[] cardCounter) {
			System.arraycopy(cardCounter, 0, sCardCounter, 0, cardCounter.length);
		}
	}
	
	public static class RP_MSG_G2 extends Structure {
		public byte[] sKenc = new byte[33];        // Issue Kenc����
		public byte[] sKencCK = new byte[17];      // Issue KencУ��ֵ
		public byte[] sKmac = new byte[33];        // Issue Kmac����
		public byte[] sKmacCK = new byte[17];      // Issue KmacУ��ֵ
		public byte[] sKdek = new byte[33];        // Issue Kdek����
		public byte[] sKdekCK = new byte[17];      // Issue KdekУ��ֵ

	    public static class ByReference extends RP_MSG_G2 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_G2
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKenc", "sKencCK", 
            		"sKmac", "sKmacCK", "sKdek", "sKdekCK"});
        }

		public byte[] getsKenc() {
			return DJHsmUtils.getHexByte(sKenc);
		}

		public byte[] getsKencCK() {
			return DJHsmUtils.getHexByte(sKencCK);
		}

		public byte[] getsKmac() {
			return DJHsmUtils.getHexByte(sKmac);
		}

		public byte[] getsKmacCK() {
			return DJHsmUtils.getHexByte(sKmacCK);
		}

		public byte[] getsKdek() {
			return DJHsmUtils.getHexByte(sKdek);
		}

		public byte[] getsKdekCK() {
			return DJHsmUtils.getHexByte(sKdekCK);
		}
	    
	    
	}

	public static class KeyData extends Structure {
		public byte[] sKeyType = new byte[4];         // ��������Կ����
		public byte[] sKey = new byte[50];            // ��������Կ
		public byte[] sKeyHead = new byte[65];        // ��Կͷ
	    
	    public static class ByReference extends KeyData 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends KeyData 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType","sKey",
            		"sKeyHead"});
        }

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsKeyHead(byte[] keyHead) {
			System.arraycopy(keyHead, 0, sKeyHead, 0, keyHead.length);
		}
	}
	
	public static class RQ_MSG_G2 extends Structure {
		public byte[] sAlgMode = new byte[3];         // �����㷨ģʽ
		public byte[] sProKey = new byte[50];         // ������Կ��������KMC��
		public byte[] sKeyData = new byte[13];        // ��Կ��������
		public byte[] sCardCounter = new byte[5];     // �����м�������SCP02��
		public byte[] sKeyHeadLen = new byte[3];      // ÿ����Կͷ����
		public byte[] sExpScatterLvl = new byte[3];   // ��������Կ�ķ�ɢ����
		public byte[] sExpFactor = new byte[257];     // ��������Կ��ɢ����
		public byte[] sExpKeyNum = new byte[3];       // Ҫ������Կ����NUM
		public KeyData.ByValue[] keyData;             // ��������Կ��Ϣ
	    public byte[] sSeparator = new byte[2];       // �ָ���
	    
	    public RQ_MSG_G2(){
	    	KeyData.ByValue tdata = new KeyData.ByValue();
	    	keyData = (KeyData.ByValue[])tdata.toArray(8);
	    }
	    
	    public static class ByReference extends RQ_MSG_G2 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_G2 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sProKey", 
            		"sKeyData", "sCardCounter", "sKeyHeadLen", 
            		"sExpScatterLvl", "sExpFactor", "sExpKeyNum", 
            		"keyData", "sSeparator"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsProKey(byte[] proKey) {
			System.arraycopy(proKey, 0, sProKey, 0, proKey.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsCardCounter(byte[] cardCounter) {
			System.arraycopy(cardCounter, 0, sCardCounter, 0, cardCounter.length);
		}

		public void setsKeyHeadLen(byte[] keyHeadLen) {
			System.arraycopy(keyHeadLen, 0, sKeyHeadLen, 0, keyHeadLen.length);
		}

		public void setsExpScatterLvl(byte[] expScatterLvl) {
			System.arraycopy(expScatterLvl, 0, sExpScatterLvl, 0, expScatterLvl.length);
		}

		public void setsExpFactor(byte[] expFactor) {
			System.arraycopy(expFactor, 0, sExpFactor, 0, expFactor.length);
		}

		public void setsExpKeyNum(byte[] expKeyNum) {
			System.arraycopy(expKeyNum, 0, sExpKeyNum, 0, expKeyNum.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}
	}
	
	public static class G3_KEYCV extends Structure {
		public byte[] sKeyCV = new byte[17];
	    
	    public static class ByReference extends G3_KEYCV 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends G3_KEYCV 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyCV"});
        }
	    
	    public byte[] getsKeyCV() {
	    	return DJHsmUtils.getHexByte(sKeyCV);
	    }
	}
	
	public static class RP_MSG_G3 extends Structure {
		public byte[] sKeyLen = new byte[5];       // ���ĳ���
		public byte[] sKey = new byte[640];        // ��Կ������
		public G3_KEYCV.ByValue[] sKeyCV;          // ����ԿУ��ֵ
		
		public RP_MSG_G3(){
			G3_KEYCV.ByValue tdata = new G3_KEYCV.ByValue();
			sKeyCV = (G3_KEYCV.ByValue[])tdata.toArray(8);
		}
	    
	    public static class ByReference extends RP_MSG_G3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_G3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyLen", "sKey", 
            		"sKeyCV"});
        }

		public byte[] getsKeyLen() {
			return DJHsmUtils.getHexByte(sKeyLen);
		}

		public byte[] getsKey() {
			String lenStr = new String(getsKeyLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 16) ? lenStr : "0", 16) * 2;
			return DJHsmUtils.getData(sKey, len);
		}

		public byte[][] getsKeyCV() {
			List<byte[]> res = new ArrayList<byte[]>();
			for (int i = 0; i < sKeyCV.length; i++) {
				G3_KEYCV.ByValue g3 = sKeyCV[i];
				byte[] b = g3.getsKeyCV();
				if (((byte[]) g3.sKeyCV)[0] == 0x00) {
					if (i == 0) {
						return null;
					}
					break;
				} else {
					res.add(b);
				}
			}
			byte[][] newb = new byte[res.size()][16];
			res.toArray(newb);
			return newb;
		}
	}
	
	public static class RQ_MSG_G3 extends Structure {
		public byte[] sAlgMode = new byte[3];      // �����㷨ģʽ
		public byte[] sKMC = new byte[50];         // KMCԴ��Կ
		public byte[] sKeyData = new byte[13];     // ��Կ��������
		public byte[] sCardCounter = new byte[5];  // �����м�������SCP02��
		public byte[] sDataLen = new byte[5];      // �������ݳ���
		public byte[] sData = new byte[2048];      // ��������
		public byte[] sPad = new byte[3];          // PAD��ʶ
	    
	    public static class ByReference extends RQ_MSG_G3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_G3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sKMC", 
            		"sKeyData", "sCardCounter", "sDataLen", "sData", 
            		"sPad"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKMC(byte[] kmc) {
			System.arraycopy(kmc, 0, sKMC, 0, kmc.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsCardCounter(byte[] cardCounter) {
			System.arraycopy(cardCounter, 0, sCardCounter, 0, cardCounter.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsPad(byte[] pad) {
			System.arraycopy(pad, 0, sPad, 0, pad.length);
		}
	}
	
	public static class RP_MSG_G4 extends Structure {
		public byte[] sCipherTextLen = new byte[5];// ���ĳ���
		public byte[] sCipherText = new byte[2048];// ��������	   
	    
	    public static class ByReference extends RP_MSG_G4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_G4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCipherTextLen", "sCipherText"});
        }

		public byte[] getsCipherTextLen() {
			return DJHsmUtils.getHexByte(sCipherTextLen);
		}

		public byte[] getsCipherText() {
			String lenStr = new String(getsCipherTextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 16) ? lenStr : "0", 16) * 2;
			return DJHsmUtils.getData(sCipherText, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_G4 extends Structure {
		public byte[] sAlgMode = new byte[3];      // �����㷨ģʽ
		public byte[] sKMC = new byte[50];         // KMCԴ��Կ
		public byte[] sKeyData = new byte[13];     // ��Կ��������
		public byte[] sCardCounter = new byte[5];  // �����м�������SCP02��
		public byte[] sDataLen = new byte[5];      // �������ݳ���
		public byte[] sData = new byte[2048];      // ��������
		public byte[] sPad = new byte[3];          // PAD��ʶ
	    
	    public static class ByReference extends RQ_MSG_G4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_G4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sKMC", 
            		"sKeyData", "sCardCounter", "sDataLen", "sData", 
            		"sPad"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKMC(byte[] kmc) {
			System.arraycopy(kmc, 0, sKMC, 0, kmc.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsCardCounter(byte[] cardCounter) {
			System.arraycopy(cardCounter, 0, sCardCounter, 0, cardCounter.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsPad(byte[] pad) {
			System.arraycopy(pad, 0, sPad, 0, pad.length);
		}
	}
	
	public static class RP_MSG_G5 extends Structure {
	    public byte[] sCipherTextLen = new byte[5];// ���ĳ���
	    public byte[] sCipherText = new byte[2048];// ��������
	    
	    public static class ByReference extends RP_MSG_G5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_G5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCipherTextLen", "sCipherText"});
        }

		public byte[] getsCipherTextLen() {
			return DJHsmUtils.getHexByte(sCipherTextLen);
		}

		public byte[] getsCipherText() {
			String lenStr = new String(getsCipherTextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 16) ? lenStr : "0", 16) * 2;
			return DJHsmUtils.getData(sCipherText, len);
		}
	    
	    
	}

	public static class RQ_MSG_G5 extends Structure {
		public byte[] sMacAlgMode = new byte[3];   // MAC�㷨ģʽ
		public byte[] sKMC = new byte[50];         // KMCԴ��Կ
		public byte[] sKeyData = new byte[13];     // ��Կ��������
		public byte[] sCardCounter = new byte[5];  // �����м�����
		public byte[] sDataLen = new byte[5];      // �������ݳ���
		public byte[] sData = new byte[2048];      // ��������
		public byte[] sPad = new byte[3];          // PAD��ʶ
		public byte[] sICVMode = new byte[2];      // ICVʹ��ģʽ
		public byte[] sICV = new byte[17];         // ICV
	    
	    public static class ByReference extends RQ_MSG_G5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_G5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMacAlgMode", "sKMC", 
            		"sKeyData", "sCardCounter", "sDataLen", "sData", 
            		"sPad", "sICVMode", "sICV"});
        }

		public void setsMacAlgMode(byte[] macAlgMode) {
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}

		public void setsKMC(byte[] kmc) {
			System.arraycopy(kmc, 0, sKMC, 0, kmc.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsCardCounter(byte[] cardCounter) {
			System.arraycopy(cardCounter, 0, sCardCounter, 0, cardCounter.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsPad(byte[] pad) {
			System.arraycopy(pad, 0, sPad, 0, pad.length);
		}

		public void setsICVMode(byte[] mode) {
			System.arraycopy(mode, 0, sICVMode, 0, mode.length);
		}

		public void setsICV(byte[] icv) {
			System.arraycopy(icv, 0, sICV, 0, icv.length);
		}
	}
	
	public static class RP_MSG_G6 extends Structure {
		public byte[] sMac = new byte[17];         // MACֵ
	    
	    public static class ByReference extends RP_MSG_G6 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_G6 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}
	
	public static class RQ_MSG_G6 extends Structure {
		public byte[] sMacAlgMode = new byte[3];   // MAC�㷨ģʽ
		public byte[] sKMC = new byte[50];         // KMCԴ��Կ
		public byte[] sKeyData = new byte[13];     // ��Կ��������
		public byte[] sCardCounter = new byte[5];  // �����м�������SCP02��
		public byte[] sDataLen = new byte[5];      // �������ݳ���
		public byte[] sData = new byte[2048];      // ��������
		public byte[] sPad = new byte[3];          // PAD��ʶ
		public byte[] sICVMode = new byte[2];      // ICVʹ��ģʽ
		public byte[] sICV = new byte[17];         // ICV
		public byte[] sMac = new byte[17];         // ����֤��MAC
	    
	    public static class ByReference extends RQ_MSG_G6 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_G6 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMacAlgMode", "sKMC", 
            		"sKeyData", "sCardCounter", "sDataLen", 
            		"sData", "sPad", "sICVMode", "sICV", 
            		"sMac"});
        }

		public void setsMacAlgMode(byte[] macAlgMode) {
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}

		public void setsKMC(byte[] kmc) {
			System.arraycopy(kmc, 0, sKMC, 0, kmc.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsCardCounter(byte[] cardCounter) {
			System.arraycopy(cardCounter, 0, sCardCounter, 0, cardCounter.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsPad(byte[] pad) {
			System.arraycopy(pad, 0, sPad, 0, pad.length);
		}

		public void setsICVMode(byte[] mode) {
			System.arraycopy(mode, 0, sICVMode, 0, mode.length);
		}

		public void setsICV(byte[] icv) {
			System.arraycopy(icv, 0, sICV, 0, icv.length);
		}

		public void setsMac(byte[] mac) {
			System.arraycopy(mac, 0, sMac, 0, mac.length);
		}
	}
	
	public static class RP_MSG_G7 extends Structure {
		public byte[] sMac = new byte[17];         // R-MACֵ
	    
	    public static class ByReference extends RP_MSG_G7 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_G7 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}

	public static class RQ_MSG_G7 extends Structure {
		public byte[] sKMC = new byte[50];           // KMCԴ��Կ
		public byte[] sKeyData = new byte[13];       // ��Կ��������
		public byte[] sHostChallenge = new byte[17]; // Host Challenge
		public byte[] sCardChallenge = new byte[17]; // Card Challenge
		public byte[] sCardAuth = new byte[17];      // ��Ƭ��֤����
	    
	    public static class ByReference extends RQ_MSG_G7 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_G7 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKMC", "sKeyData", 
            		"sHostChallenge", "sCardChallenge", "sCardAuth"});
        }

		public void setsKMC(byte[] kmc) {
			System.arraycopy(kmc, 0, sKMC, 0, kmc.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsHostChallenge(byte[] hostChallenge) {
			System.arraycopy(hostChallenge, 0, sHostChallenge, 0, hostChallenge.length);
		}

		public void setsCardChallenge(byte[] cardChallenge) {
			System.arraycopy(cardChallenge, 0, sCardChallenge, 0, cardChallenge.length);
		}

		public void setsCardAuth(byte[] cardAuth) {
			System.arraycopy(cardAuth, 0, sCardAuth, 0, cardAuth.length);
		}
	}
	
	public static class RP_MSG_G8 extends Structure {
		public byte[] sHostAuth = new byte[17];    // ������֤����
	    
	    public static class ByReference extends RP_MSG_G8 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_G8 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHostAuth"});
        }

		public byte[] getsHostAuth() {
			return DJHsmUtils.getHexByte(sHostAuth);
		}
	    
	    
	}
	
	public static class RQ_MSG_G8 extends Structure {
	    public byte[] sAlgMode = new byte[3];         // �����㷨ģʽ
	    public byte[] sKeyType = new byte[4];         // ������Կ����;
	    public byte[] sKey = new byte[50];            // ������Կ
	    public byte[] sScatterLvl = new byte[2+1];    // ��ɢ����
	    public byte[] sFactor = new byte[8*32+1];     // ��ɢ���� 
	    public byte[] sSessionMode = new byte[2+1];   // �Ự��Կ����ģʽ
	    public byte[] sSessionFactor = new byte[32+1];// �Ự��Կ���� 
	    public byte[] sExpKey = new byte[50];         // ������Կ(KMC)
	    public byte[] sKeyData = new byte[13];        // ��Ƭ���˻���Կ����keydata
	    public byte[] sCardCounter = new byte[5];	  // ��Ƭ������

	    public static class ByReference extends RQ_MSG_G8 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_G8 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sKeyType", 
            		"sKey", "sScatterLvl", "sFactor", "sSessionMode", 
            		"sSessionFactor", "sExpKey", "sKeyData", 
            		"sCardCounter"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsSessionMode(byte[] sessionMode) {
			System.arraycopy(sessionMode, 0, sSessionMode, 0, sessionMode.length);
		}

		public void setsSessionFactor(byte[] sessionFactor) {
			System.arraycopy(sessionFactor, 0, sSessionFactor, 0, sessionFactor.length);
		}

		public void setsExpKey(byte[] expKey) {
			System.arraycopy(expKey, 0, sExpKey, 0, expKey.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsCardCounter(byte[] cardCounter) {
			System.arraycopy(cardCounter, 0, sCardCounter, 0, cardCounter.length);
		}
	}
	
	public static class RP_MSG_G9 extends Structure {
		public byte[] sSenc = new byte[33];        // Senc����
		public byte[] sSencCV = new byte[17];      // SencУ��ֵ
		public byte[] sScmac = new byte[33];       // Scmac����
		public byte[] sScmacCV = new byte[17];     // ScmacУ��ֵ
	    public byte[] sSdek = new byte[33];        // Sdek����
	    public byte[] sSdekCV = new byte[17];      // SdekУ��ֵ
	    
	    public static class ByReference extends RP_MSG_G9 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_G9 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSenc", "sSencCV", 
            		"sScmac", "sScmacCV", "sSdek", "sSdekCV"});
        }

		public byte[] getsSenc() {
			return DJHsmUtils.getHexByte(sSenc);
		}

		public byte[] getsSencCV() {
			return DJHsmUtils.getHexByte(sSencCV);
		}

		public byte[] getsScmac() {
			return DJHsmUtils.getHexByte(sScmac);
		}

		public byte[] getsScmacCV() {
			return DJHsmUtils.getHexByte(sScmacCV);
		}

		public byte[] getsSdek() {
			return DJHsmUtils.getHexByte(sSdek);
		}

		public byte[] getsSdekCV() {
			return DJHsmUtils.getHexByte(sSdekCV);
		}
	    
	    
	}
		
	public static class RQ_MSG_GF extends Structure {
		public byte[] sAlgMode = new byte[3];      // �����㷨ģʽ
		public byte[] sKMC = new byte[50];         // KMCԴ��Կ
	    public byte[] sKeyData = new byte[13];     // ��Կ��������
	    public byte[] sCardCounter = new byte[5];  // �����м�����
	    public byte[] sRSAIndexFlag = new byte[2]; // RSA��Կ������ʶ
	    public byte[] sRSAIndex = new byte[5];     // RSA��Կ������
	    public byte[] sPriKeyLen = new byte[5];    // ˽Կ����
	    public byte[] sPriKey = new byte[2048];	   // ˽Կ����  

	    public static class ByReference extends RQ_MSG_GF 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_GF 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sKMC", 
            		"sKeyData", "sCardCounter", "sRSAIndexFlag", 
            		"sRSAIndex", "sPriKeyLen", "sPriKey"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKMC(byte[] kmc) {
			System.arraycopy(kmc, 0, sKMC, 0, kmc.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsCardCounter(byte[] cardCounter) {
			System.arraycopy(cardCounter, 0, sCardCounter, 0, cardCounter.length);
		}

		public void setsRSAIndexFlag(byte[] indexFlag) {
			System.arraycopy(indexFlag, 0, sRSAIndexFlag, 0, indexFlag.length);
		}

		public void setsRSAIndex(byte[] index) {
			System.arraycopy(index, 0, sRSAIndex, 0, index.length);
		}

		public void setsPriKeyLen(byte[] priKeyLen) {
			System.arraycopy(priKeyLen, 0, sPriKeyLen, 0, priKeyLen.length);
		}

		public void setsPriKey(byte[] priKey) {
			System.arraycopy(priKey, 0, sPriKey, 0, priKey.length);
		}
	}
	
	public static class RP_MSG_GG extends Structure {
		public byte[] sPubKey = new byte[1024];    // ��Կ
		public byte[] sDLen = new byte[5];         // ˽Կָ��d����
		public byte[] sD = new byte[1024];         // ˽Կָ��d
		public byte[] sPLen = new byte[5];         // ˽Կ����P����
		public byte[] sP = new byte[512];          // ˽Կ����P
		public byte[] sQLen = new byte[5];         // ˽Կ����Q����
		public byte[] sQ = new byte[512];          // ˽Կ����Q
		public byte[] sDPLen = new byte[5];        // ˽Կ����dP����
		public byte[] sDP = new byte[512];         // ˽Կ����dP
		public byte[] sDQLen = new byte[5];        // ˽Կ����dQ����
		public byte[] sDQ = new byte[512];         // ˽Կ����dQ
		public byte[] sQInvLen = new byte[5];      // ˽Կ����qInv����
		public byte[] sQInv = new byte[512];       // ˽Կ����qInv
	    
	    public static class ByReference extends RP_MSG_GG 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_GG 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPubKey", "sDLen",
            		"sD", "sPLen", "sP", "sQLen", "sQ", "sDPLen", 
            		"sDP", "sDQLen", "sDQ", "sQInvLen", "sQInv"});
        }

		public byte[] getsPubKey() {
			return DJHsmUtils.getsPubRSAKey(sPubKey);
		}

		public byte[] getsDLen() {
			return DJHsmUtils.getHexByte(sDLen);
		}

		public byte[] getsD() {
			String lenStr = new String(getsDLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sD, len);
		}

		public byte[] getsPLen() {
			return DJHsmUtils.getHexByte(sPLen);
		}

		public byte[] getsP() {
			String lenStr = new String(getsPLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sP, len);
		}

		public byte[] getsQLen() {
			return DJHsmUtils.getHexByte(sQLen);
		}

		public byte[] getsQ() {
			String lenStr = new String(getsQLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sQ, len);
		}

		public byte[] getsDPLen() {
			return DJHsmUtils.getHexByte(sDPLen);
		}

		public byte[] getsDP() {
			String lenStr = new String(getsDPLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sDP, len);
		}

		public byte[] getsDQLen() {
			return DJHsmUtils.getHexByte(sDQLen);
		}

		public byte[] getsDQ() {
			String lenStr = new String(getsDQLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sDQ, len);
		}

		public byte[] getsQInvLen() {
			return DJHsmUtils.getHexByte(sQInvLen);
		}

		public byte[] getsQInv() {
			String lenStr = new String(getsQInvLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sQInv, len);
		}
	    
	    
	}
		
	public static class RQ_MSG_G0 extends Structure {
		public byte[] sAlgMode = new byte[3];      // �����㷨ģʽ
		public byte[] sKMC = new byte[50];         // ������Կ��KMC��
		public byte[] sKeyData = new byte[13];     // ��Կ��������
		public byte[] sCardCounter = new byte[5];  // �����м�����
		public byte[] sCurveFlag = new byte[3];    // ���߱�ʶ
		public byte[] sSM2Index = new byte[5];     // SM2��Կ������
		public byte[] sPubKey = new byte[128];     // SM2��Կ
		public byte[] sPriKeyLen = new byte[5];    // SM2˽Կ���ĳ���
		public byte[] sPriKey = new byte[128];     // SM2˽Կ���� 

	    public static class ByReference extends RQ_MSG_G0 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_G0 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sKMC", 
            		"sKeyData", "sCardCounter", "sCurveFlag", 
            		"sSM2Index", "sPubKey", "sPriKeyLen", 
            		"sPriKey"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKMC(byte[] kmc) {
			System.arraycopy(kmc, 0, sKMC, 0, kmc.length);
		}

		public void setsKeyData(byte[] keyData) {
			System.arraycopy(keyData, 0, sKeyData, 0, keyData.length);
		}

		public void setsCardCounter(byte[] cardCounter) {
			System.arraycopy(cardCounter, 0, sCardCounter, 0, cardCounter.length);
		}

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsSM2Index(byte[] index) {
			System.arraycopy(index, 0, sSM2Index, 0, index.length);
		}

		public void setsPubKey(byte[] pubKey) {
			System.arraycopy(pubKey, 0, sPubKey, 0, pubKey.length);
		}

		public void setsPriKeyLen(byte[] priKeyLen) {
			System.arraycopy(priKeyLen, 0, sPriKeyLen, 0, priKeyLen.length);
		}

		public void setsPriKey(byte[] priKey) {
			System.arraycopy(priKey, 0, sPriKey, 0, priKey.length);
		}
	}
	
	public static class RP_MSG_G1 extends Structure {
		public byte[] sPubKey = new byte[128];     // ��Կ
	    public byte[] sDLen = new byte[5];         // ˽Կd���ĳ���
	    public byte[] sD = new byte[128];          // ˽Կd����
	    
	    public static class ByReference extends RP_MSG_G1 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_G1 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPubKey", "sDLen", 
            		"sD"});
        }

		public byte[] getsPubKey() {
			return DJHsmUtils.getsPubSM2Key(sPubKey);
		}

		public byte[] getsDLen() {
			return DJHsmUtils.getHexByte(sDLen);
		}

		public byte[] getsD() {
			String lenStr = new String(getsDLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sD, len);
		}
	    
	    
	}
	
	//PBOC/EMV�淶���׹���
	public static class RQ_MSG_K6 extends Structure {
		public byte[] sModeFlag = new byte[2];     // ģʽ��־
		public byte[] sMdkSrcKey = new byte[50];   // MDKԴ��Կ
		public byte[] sPan = new byte[17];         // PAN��PAN���к�
		public byte[] sAtc = new byte[5];          // ATC
		public byte[] sTranDataLen = new byte[3];  // �������ݳ���
		public byte[] sTranData = new byte[1024];  // ��������
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sArqc = new byte[17];        // ARQC/TC/AAC
		public byte[] sArc = new byte[5];          // ARC 

	    public static class ByReference extends RQ_MSG_K6 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_K6 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sModeFlag", "sMdkSrcKey", 
            		"sPan", "sAtc", "sTranDataLen", "sTranData", 
            		"sSeparator", "sArqc", "sArc"});
        }

		public void setsModeFlag(byte[] modeFlag) {
			System.arraycopy(modeFlag, 0, sModeFlag, 0, modeFlag.length);
		}

		public void setsMdkSrcKey(byte[] mdkSrcKey) {
			System.arraycopy(mdkSrcKey, 0, sMdkSrcKey, 0, mdkSrcKey.length);
		}

		public void setsPan(byte[] pan) {
			System.arraycopy(pan, 0, sPan, 0, pan.length);
		}

		public void setsAtc(byte[] atc) {
			System.arraycopy(atc, 0, sAtc, 0, atc.length);
		}

		public void setsTranDataLen(byte[] tranDataLen) {
			System.arraycopy(tranDataLen, 0, sTranDataLen, 0, tranDataLen.length);
		}

		public void setsTranData(byte[] tranData) {
			System.arraycopy(tranData, 0, sTranData, 0, tranData.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsArqc(byte[] arqc) {
			System.arraycopy(arqc, 0, sArqc, 0, arqc.length);
		}

		public void setsArc(byte[] arc) {
			System.arraycopy(arc, 0, sArc, 0, arc.length);
		}
	}
	
	public static class RP_MSG_K7 extends Structure {
		public byte[] sArpc = new byte[33];        // ARPC
		public byte[] sArqc = new byte[17];        // ARQC�������
	    
	    public static class ByReference extends RP_MSG_K7 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_K7 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sArpc", "sArqc"});
        }

		public byte[] getsArpc() {
			return DJHsmUtils.getHexByte(sArpc);
		}

		public byte[] getsArqc() {
			return DJHsmUtils.getHexByte(sArqc);
		}
	    
	    
	}

	public static class RQ_MSG_K2 extends Structure {
		public byte[] sMdkSrcKey = new byte[50];   // MDKԴ��Կ
		public byte[] sPan = new byte[17];         // PAN��PAN���к�
	    public byte[] sAtc = new byte[5];          // ATC
	    public byte[] sDataLen = new byte[4];      // ���ݳ���
	    public byte[] sData = new byte[2048];      // ����

	    public static class ByReference extends RQ_MSG_K2 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_K2 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMdkSrcKey", "sPan", 
            		"sAtc", "sDataLen", "sData"});
        }

		public void setsMdkSrcKey(byte[] mdkSrcKey) {
			System.arraycopy(mdkSrcKey, 0, sMdkSrcKey, 0, mdkSrcKey.length);
		}

		public void setsPan(byte[] pan) {
			System.arraycopy(pan, 0, sPan, 0, pan.length);
		}

		public void setsAtc(byte[] atc) {
			System.arraycopy(atc, 0, sAtc, 0, atc.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}
	
	public static class RP_MSG_K3 extends Structure {
		public byte[] sCipherText = new byte[2048];// ���ܺ������
	    
	    public static class ByReference extends RP_MSG_K3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_K3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCipherText"});
        }

		public byte[] getsCipherText() {
			return DJHsmUtils.getHexByte(sCipherText);
		}
	    
	    
	}

	public static class RQ_MSG_K4 extends Structure {
		public byte[] sMdkSrcKey = new byte[50];   // MDKԴ��Կ
		public byte[] sPan = new byte[17];         // PAN��PAN���к�
		public byte[] sAtc = new byte[5];          // ATC
		public byte[] sDataLen = new byte[4];      // ���ݳ���
		public byte[] sData = new byte[2048];      // ����

	    public static class ByReference extends RQ_MSG_K4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_K4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMdkSrcKey", "sPan", 
            		"sAtc", "sDataLen", "sData"});
        }

		public void setsMdkSrcKey(byte[] mdkSrcKey) {
			System.arraycopy(mdkSrcKey, 0, sMdkSrcKey, 0, mdkSrcKey.length);
		}

		public void setsPan(byte[] pan) {
			System.arraycopy(pan, 0, sPan, 0, pan.length);
		}

		public void setsAtc(byte[] atc) {
			System.arraycopy(atc, 0, sAtc, 0, atc.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}
	
	public static class RP_MSG_K5 extends Structure {
		public byte[] sMac = new byte[32+1];       // MAC
	    
	    public static class ByReference extends RP_MSG_K5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_K5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}
	
	//���ݼӽ���
	public static class RQ_MSG_S3 extends Structure {
		public byte[] sAlgMode = new byte[3];          // �����㷨ģʽ
		public byte[] sKeyType = new byte[4];          // ��Կ����;
		public byte[] sKey = new byte[50];             // ��Կ
		public byte[] sScatterLvl = new byte[3];       // ��Կ��ɢ����
		public byte[] sFactor = new byte[8*32+1];      // ��Կ��ɢ����
		public byte[] sSessionKeyMode = new byte[3];   // �Ự��Կģʽ
		public byte[] sSessionKeyFactor = new byte[33];// �Ự��Կ����
		public byte[] sPadFlag = new byte[3];          // PAD��ʶ
		public byte[] sDataLen = new byte[5];          // �������ݳ���
		public byte[] sData = new byte[4097];          // ��������
		public byte[] sIV = new byte[33];              // IV
	    
	    public static class ByReference extends RQ_MSG_S3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_S3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sKeyType", 
            		"sKey", "sScatterLvl", "sFactor", "sSessionKeyMode", 
            		"sSessionKeyFactor", "sPadFlag", "sDataLen", 
            		"sData", "sIV"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsSessionKeyMode(byte[] sessionKeyMode) {
			System.arraycopy(sessionKeyMode, 0, sSessionKeyMode, 0, sessionKeyMode.length);
		}

		public void setsSessionKeyFactor(byte[] sessionKeyFactor) {
			System.arraycopy(sessionKeyFactor, 0, sSessionKeyFactor, 0, sessionKeyFactor.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsIV(byte[] siv) {
			System.arraycopy(siv, 0, sIV, 0, siv.length);
		}
	}

	public static class RP_MSG_S4 extends Structure {
		public byte[] sCipherTextLen = new byte[5];// ���ĳ���
		public byte[] sCipherText = new byte[4097];// ����
	    
	    public static class ByReference extends RP_MSG_S4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_S4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCipherTextLen", "sCipherText"});
        }

		public byte[] getsCipherTextLen() {
			return DJHsmUtils.getHexByte(sCipherTextLen);
		}

		public byte[] getsCipherText() {
			String lenStr = new String(getsCipherTextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 16) ? lenStr : "0", 16);
			return DJHsmUtils.getData(sCipherText, len);
		}
	    
	    
	}
		
	public static class RQ_MSG_S4 extends Structure {
		public byte[] sAlgMode = new byte[3];           // �����㷨ģʽ
		public byte[] sKeyType = new byte[4];           // ��Կ����;
		public byte[] sKey = new byte[50];              // ��Կ
		public byte[] sScatterLvl = new byte[3];        // ��Կ��ɢ����
		public byte[] sFactor = new byte[8*32+1];       // ��ɢ����
		public byte[] sSessionKeyMode = new byte[3];    // �Ự��Կģʽ
		public byte[] sSessionKeyFactor = new byte[33]; // �Ự��Կ����
		public byte[] sPadFlag = new byte[3];           // PAD��ʶ
		public byte[] sCipherTextLen = new byte[5];     // �������ݳ���
		public byte[] sCipherText = new byte[4097];     // ��������
		public byte[] sIV = new byte[33];               // IV
	    
	    public static class ByReference extends RQ_MSG_S4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_S4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sKeyType", 
            		"sKey", "sScatterLvl", "sFactor", "sSessionKeyMode", 
            		"sSessionKeyFactor", "sPadFlag", "sCipherTextLen", 
            		"sCipherText", "sIV"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsSessionKeyMode(byte[] sessionKeyMode) {
			System.arraycopy(sessionKeyMode, 0, sSessionKeyMode, 0, sessionKeyMode.length);
		}

		public void setsSessionKeyFactor(byte[] sessionKeyFactor) {
			System.arraycopy(sessionKeyFactor, 0, sSessionKeyFactor, 0, sessionKeyFactor.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsCipherTextLen(byte[] cipherTextLen) {
			System.arraycopy(cipherTextLen, 0, sCipherTextLen, 0, cipherTextLen.length);
		}

		public void setsCipherText(byte[] cipherText) {
			System.arraycopy(cipherText, 0, sCipherText, 0, cipherText.length);
		}

		public void setsIV(byte[] siv) {
			System.arraycopy(siv, 0, sIV, 0, siv.length);
		}
	}

	public static class RP_MSG_S5 extends Structure {
		public byte[] sDataLen = new byte[5];      // ������ݳ���
		public byte[] sData = new byte[4097];      // �������
	    
	    public static class ByReference extends RP_MSG_S5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_S5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sDataLen", "sData"});
        }

		public byte[] getsDataLen() {
			return DJHsmUtils.getHexByte(sDataLen);
		}

		public byte[] getsData() {
			String lenStr = new String(getsDataLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 16) ? lenStr : "0", 16);
			return DJHsmUtils.getData(sData, len);
		}
	    
	    
	}	
	
	public static class RQ_MSG_S5 extends Structure {
		public byte[] sSrcAlgMode = new byte[3];          // Դ��Կ�����㷨ģʽ
		public byte[] sSrcKeyType = new byte[4];          // Դ��Կ����
		public byte[] sSrcKey = new byte[50];             // Դ��Կ
		public byte[] sSrcScatterLvl = new byte[3];       // Դ��Կ��ɢ����
		public byte[] sSrcFactor = new byte[8*32+1];      // Դ��Կ��ɢ����
		public byte[] sSrcSessionKeyMode = new byte[3];   // Դ��Կ�Ự��Կģʽ
		public byte[] sSrcSessionKeyFactor = new byte[33];// Դ��Կ�Ự��Կ����
		public byte[] sSrcPadFlag = new byte[3];          // Դ��Կ����ʱ������PAD��ʶ
		public byte[] sSrcIV = new byte[33];              // Դ��Կ����ʱ��IV
		public byte[] sDesAlgMode = new byte[3];          // Ŀ����Կ�����㷨ģʽ
		public byte[] sDesKeyType = new byte[4];          // Ŀ����Կ����
		public byte[] sDesKey = new byte[50];             // Ŀ����Կ
		public byte[] sDesScatterLvl = new byte[3];       // Ŀ����Կ��ɢ����
		public byte[] sDesFactor = new byte[8*32+1];      // Ŀ����Կ��ɢ����
		public byte[] sDesSessionKeyMode = new byte[3];   // Ŀ����Կ�Ự��Կģʽ
		public byte[] sDesSessionKeyFactor = new byte[33];// Ŀ����Կ�Ự��Կ����
		public byte[] sDesPadFlag = new byte[3];          // Ŀ����Կ����ʱ������PAD��ʶ
		public byte[] sDesIV = new byte[33];              // Ŀ����Կ����ʱ��IV
		public byte[] sCipherTextLen = new byte[5];       // ���ĳ���
		public byte[] sCipherText = new byte[4097];       // Դ��Կ���ܵ���������
	    
	    public static class ByReference extends RQ_MSG_S5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_S5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSrcAlgMode", "sSrcKeyType", 
            		"sSrcKey", "sSrcScatterLvl", "sSrcFactor", "sSrcSessionKeyMode", 
            		"sSrcSessionKeyFactor", "sSrcPadFlag", "sSrcIV", 
            		"sDesAlgMode", "sDesKeyType", "sDesKey", "sDesScatterLvl", 
            		"sDesFactor", "sDesSessionKeyMode", "sDesSessionKeyFactor", 
            		"sDesPadFlag", "sDesIV", "sCipherTextLen", "sCipherText"});
        }

		public void setsSrcAlgMode(byte[] srcAlgMode) {
			System.arraycopy(srcAlgMode, 0, sSrcAlgMode, 0, srcAlgMode.length);
		}

		public void setsSrcKeyType(byte[] srcKeyType) {
			System.arraycopy(srcKeyType, 0, sSrcKeyType, 0, srcKeyType.length);
		}

		public void setsSrcKey(byte[] srcKey) {
			System.arraycopy(srcKey, 0, sSrcKey, 0, srcKey.length);
		}

		public void setsSrcScatterLvl(byte[] srcScatterLvl) {
			System.arraycopy(srcScatterLvl, 0, sSrcScatterLvl, 0, srcScatterLvl.length);
		}

		public void setsSrcFactor(byte[] srcFactor) {
			System.arraycopy(srcFactor, 0, sSrcFactor, 0, srcFactor.length);
		}

		public void setsSrcSessionKeyMode(byte[] srcSessionKeyMode) {
			System.arraycopy(srcSessionKeyMode, 0, sSrcSessionKeyMode, 0, srcSessionKeyMode.length);
		}

		public void setsSrcSessionKeyFactor(byte[] srcSessionKeyFactor) {
			System.arraycopy(srcSessionKeyFactor, 0, sSrcSessionKeyFactor, 0, srcSessionKeyFactor.length);
		}

		public void setsSrcPadFlag(byte[] srcPadFlag) {
			System.arraycopy(srcPadFlag, 0, sSrcPadFlag, 0, srcPadFlag.length);
		}

		public void setsSrcIV(byte[] srcIV) {
			System.arraycopy(srcIV, 0, sSrcIV, 0, srcIV.length);
		}

		public void setsDesAlgMode(byte[] desAlgMode) {
			System.arraycopy(desAlgMode, 0, sDesAlgMode, 0, desAlgMode.length);
		}

		public void setsDesKeyType(byte[] desKeyType) {
			System.arraycopy(desKeyType, 0, sDesKeyType, 0, desKeyType.length);
		}

		public void setsDesKey(byte[] desKey) {
			System.arraycopy(desKey, 0, sDesKey, 0, desKey.length);
		}

		public void setsDesScatterLvl(byte[] desScatterLvl) {
			System.arraycopy(desScatterLvl, 0, sDesScatterLvl, 0, desScatterLvl.length);
		}

		public void setsDesFactor(byte[] desFactor) {
			System.arraycopy(desFactor, 0, sDesFactor, 0, desFactor.length);
		}

		public void setsDesSessionKeyMode(byte[] desSessionKeyMode) {
			System.arraycopy(desSessionKeyMode, 0, sDesSessionKeyMode, 0, desSessionKeyMode.length);
		}

		public void setsDesSessionKeyFactor(byte[] desSessionKeyFactor) {
			System.arraycopy(desSessionKeyFactor, 0, sDesSessionKeyFactor, 0, desSessionKeyFactor.length);
		}

		public void setsDesPadFlag(byte[] desPadFlag) {
			System.arraycopy(desPadFlag, 0, sDesPadFlag, 0, desPadFlag.length);
		}

		public void setsDesIV(byte[] desIV) {
			System.arraycopy(desIV, 0, sDesIV, 0, desIV.length);
		}

		public void setsCipherTextLen(byte[] cipherTextLen) {
			System.arraycopy(cipherTextLen, 0, sCipherTextLen, 0, cipherTextLen.length);
		}

		public void setsCipherText(byte[] cipherText) {
			System.arraycopy(cipherText, 0, sCipherText, 0, cipherText.length);
		}
	}

	public static class RP_MSG_S6 extends Structure {
	    public byte[] sCipherTextLen = new byte[5];   // ���ĳ���
	    public byte[] sCipherText = new byte[4097];   // Ŀ����Կ���ܵ���������
	    
	    public static class ByReference extends RP_MSG_S6 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_S6 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCipherTextLen", "sCipherText"});
        }

		public byte[] getsCipherTextLen() {
			return DJHsmUtils.getHexByte(sCipherTextLen);
		}

		public byte[] getsCipherText() {
			String lenStr = new String(getsCipherTextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 16) ? lenStr : "0", 16);
			return DJHsmUtils.getData(sCipherText, len);
		}
	    
	    
	}
	
	//����MAC����
	public static class RQ_MSG_D0 extends Structure {
		public byte[] sMacAlgMode = new byte[3];        // MAC�㷨ģʽ
		public byte[] sMacValType = new byte[3];        // MACȡֵ��ʽ
		public byte[] sKeyType = new byte[4];           // ��Կ����;
	    public byte[] sKey = new byte[50];              // Դ��Կ
	    public byte[] sScatterLvl = new byte[3];        // ��ɢ����
	    public byte[] sFactor = new byte[8*16+1];       // ��ɢ����
	    public byte[] sSessionKeyMode = new byte[3];    // �Ự��Կ����ģʽ
	    public byte[] sSessionKeyFactor = new byte[33]; // �Ự��Կ����
	    public byte[] sPadFlag = new byte[3];           // PAD��ʶ
	    public byte[] sDataLen = new byte[5];           // ���ݳ���
	    public byte[] sData = new byte[4097];           // ����
	    public byte[] sIV = new byte[33];               // IV
	    
	    public static class ByReference extends RQ_MSG_D0 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_D0 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMacAlgMode", 
            		"sMacValType", "sKeyType", "sKey", "sScatterLvl", 
            		"sFactor", "sSessionKeyMode", "sSessionKeyFactor",
            		"sPadFlag", "sDataLen", "sData",
            		"sIV"});
        }

		public void setsMacAlgMode(byte[] macAlgMode) {
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}

		public void setsMacValType(byte[] macValType) {
			System.arraycopy(macValType, 0, sMacValType, 0, macValType.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsSessionKeyMode(byte[] sessionKeyMode) {
			System.arraycopy(sessionKeyMode, 0, sSessionKeyMode, 0, sessionKeyMode.length);
		}

		public void setsSessionKeyFactor(byte[] sessionKeyFactor) {
			System.arraycopy(sessionKeyFactor, 0, sSessionKeyFactor, 0, sessionKeyFactor.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsIV(byte[] siv) {
			System.arraycopy(siv, 0, sIV, 0, siv.length);
		}
	}

	public static class RP_MSG_D1 extends Structure {
		public byte[] sMac = new byte[33];         // ����MAC
	    
	    public static class ByReference extends RP_MSG_D1 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_D1 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}
	
	public static class RQ_MSG_D1 extends Structure {
		public byte[] sMacAlgMode = new byte[3];        // MAC�㷨ģʽ
		public byte[] sMacValType = new byte[3];        // MACȡֵ��ʽ
		public byte[] sKeyType = new byte[4];           // ��Կ����;
		public byte[] sKey = new byte[50];              // Դ��Կ
		public byte[] sScatterLvl = new byte[3];        // ��ɢ����
		public byte[] sFactor = new byte[8*32+1];       // ��ɢ����
	    public byte[] sSessionKeyMode = new byte[3];    // �Ự��Կ����ģʽ
	    public byte[] sSessionKeyFactor = new byte[33]; // �Ự��Կ����
	    public byte[] sPadFlag = new byte[3];           // PAD��ʶ
	    public byte[] sDataLen = new byte[5];           // ���ݳ���
	    public byte[] sData = new byte[4097];           // ����
	    public byte[] sIV = new byte[33];               // IV
	    public byte[] sMac = new byte[33];              // ����֤��MAC/TAC
	    
	    public static class ByReference extends RQ_MSG_D1 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_D1 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMacAlgMode", 
            		"sMacValType", "sKeyType", "sKey", "sScatterLvl", 
            		"sFactor", "sSessionKeyMode", "sSessionKeyFactor",
            		"sPadFlag", "sDataLen", "sData",
            		"sIV", "sMac"});
        }

		public void setsMacAlgMode(byte[] macAlgMode) {
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}

		public void setsMacValType(byte[] macValType) {
			System.arraycopy(macValType, 0, sMacValType, 0, macValType.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsSessionKeyMode(byte[] sessionKeyMode) {
			System.arraycopy(sessionKeyMode, 0, sSessionKeyMode, 0, sessionKeyMode.length);
		}

		public void setsSessionKeyFactor(byte[] sessionKeyFactor) {
			System.arraycopy(sessionKeyFactor, 0, sSessionKeyFactor, 0, sessionKeyFactor.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsIV(byte[] siv) {
			System.arraycopy(siv, 0, sIV, 0, siv.length);
		}

		public void setsMac(byte[] mac) {
			System.arraycopy(mac, 0, sMac, 0, mac.length);
		}
	}

	public static class RP_MSG_D2 extends Structure {
		public byte[] sMac = new byte[33];         // MAC�������
	    
	    public static class ByReference extends RP_MSG_D2 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_D2 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}
	
	public static class RQ_MSG_S0 extends Structure {
		public byte[] sMacAlgMode = new byte[3];         // MAC�㷨ģʽ
		public byte[] sMacValType = new byte[3];         // MACȡֵ��ʽ
		public byte[] sKeyType = new byte[4];            // ��Կ����;
		public byte[] sKey = new byte[50];               // Դ��Կ
		public byte[] sScatterLvl = new byte[3];         // ��ɢ����
		public byte[] sFactor = new byte[8*32+1];        // ��ɢ����
		public byte[] sSessionKeyMode = new byte[3];     // �Ự��Կ����ģʽ
		public byte[] sSessionKeyFactor = new byte[33];  // �Ự��Կ����
		public byte[] sPadFlag = new byte[3];            // PAD��ʶ
		public byte[] sDataLen = new byte[5];            // ���ݳ���
		public byte[] sData = new byte[4097];            // ����
		public byte[] sIV = new byte[33];                // IV
	    
	    public static class ByReference extends RQ_MSG_S0 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_S0 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMacAlgMode", 
            		"sMacValType", "sKeyType", "sKey", "sScatterLvl", 
            		"sFactor", "sSessionKeyMode", "sSessionKeyFactor",
            		"sPadFlag", "sDataLen", "sData",
            		"sIV"});
        }

		public void setsMacAlgMode(byte[] macAlgMode) {
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}

		public void setsMacValType(byte[] macValType) {
			System.arraycopy(macValType, 0, sMacValType, 0, macValType.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsSessionKeyMode(byte[] sessionKeyMode) {
			System.arraycopy(sessionKeyMode, 0, sSessionKeyMode, 0, sessionKeyMode.length);
		}

		public void setsSessionKeyFactor(byte[] sessionKeyFactor) {
			System.arraycopy(sessionKeyFactor, 0, sSessionKeyFactor, 0, sessionKeyFactor.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsIV(byte[] siv) {
			System.arraycopy(siv, 0, sIV, 0, siv.length);
		}
	}

	public static class RP_MSG_S1 extends Structure {
		public byte[] sMacData = new byte[33];     // ����MAC
		public byte[] sMac = new byte[33];         // MAC����
	    
	    public static class ByReference extends RP_MSG_S1 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_S1 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMacData", "sMac"});
        }

		public byte[] getsMacData() {
			return DJHsmUtils.getHexByte(sMacData);
		}

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}
	
	public static class RQ_MSG_LR extends Structure {
		public byte[] sHashAlgFlag = new byte[3];  // HASH�㷨��ʶ
		public byte[] sKeyLen = new byte[5];       // ��Կ����
		public byte[] sKey = new byte[513];        // ��Կ����
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sDataLen = new byte[5];      // ���ݳ���
		public byte[] sData = new byte[2048];      // ��������
	    
	    public static class ByReference extends RQ_MSG_LR 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_LR 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashAlgFlag", 
            		"sKeyLen", "sKey", "sSeparator", "sDataLen", 
            		"sData"});
        }

		public void setsHashAlgFlag(byte[] hashAlgFlag) {
			System.arraycopy(hashAlgFlag, 0, sHashAlgFlag, 0, hashAlgFlag.length);
		}

		public void setsKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sKeyLen, 0, keyLen.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}

	public static class RP_MSG_LS extends Structure {
		public byte[] sHmacLen = new byte[5];      // HMAC����
		public byte[] sHmac = new byte[2048];      // HMACֵ
	    
	    public static class ByReference extends RP_MSG_LS 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_LS 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHmacLen", "sHmac"});
        }

		public byte[] getsHmacLen() {
			return DJHsmUtils.getHexByte(sHmacLen);
		}

		public byte[] getsHmac() {
			String lenStr = new String(getsHmacLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sHmac, len);
		}
	    
	    
	}
	
	//����ժҪ
	public static class RQ_MSG_3C extends Structure {
		public byte[] sHashAlgFlag = new byte[3];  // HASH�㷨��ʶ
		public byte[] sDataLen = new byte[5];      // ���ݿ鳤��
		public byte[] sData = new byte[8193];      // ���ݿ�
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sUserIDLen = new byte[5];    // �û�ID����
		public byte[] sUserID = new byte[65];      // �û�ID
		public byte[] sPKeyLen = new byte[5];      // ��Կ����
		public byte[] sPKey = new byte[257];       // SM2�㷨��Կ
	    
	    public static class ByReference extends RQ_MSG_3C 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_3C 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashAlgFlag", 
            		"sDataLen", "sData", "sSeparator", "sUserIDLen", 
            		"sUserID", "sPKeyLen", "sPKey"});
        }

		public void setsHashAlgFlag(byte[] hashAlgFlag) {
			System.arraycopy(hashAlgFlag, 0, sHashAlgFlag, 0, hashAlgFlag.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsUserIDLen(byte[] userIDLen) {
			System.arraycopy(userIDLen, 0, sUserIDLen, 0, userIDLen.length);
		}

		public void setsUserID(byte[] userID) {
			System.arraycopy(userID, 0, sUserID, 0, userID.length);
		}

		public void setsPKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sPKeyLen, 0, keyLen.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}
	}

	public static class RP_MSG_3D extends Structure {
		public byte[] sHashLen = new byte[3];      // HASH�������
		public byte[] sHash = new byte[256];       // HASH
	    
	    public static class ByReference extends RP_MSG_3D 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_3D 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashLen", "sHash"});
        }

		public byte[] getsHashLen() {
			return DJHsmUtils.getHexByte(sHashLen);
		}

		public byte[] getsHash() {
			String lenStr = new String(getsHashLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sHash, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_H1 extends Structure {
		public byte[] sHashAlgFlag = new byte[3];  // HASH�㷨��ʶ
		public byte[] sUserIDLen = new byte[5];    // �û�ID����
		public byte[] sUserID = new byte[65];      // �û�ID
		public byte[] sPKeyLen = new byte[5];      // ��Կ����
		public byte[] sPKey = new byte[257];       // SM2�㷨��Կ
	    
	    public static class ByReference extends RQ_MSG_H1 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_H1 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashAlgFlag", 
            		"sUserIDLen", "sUserID", "sPKeyLen", "sPKey"});
        }

		public void setsHashAlgFlag(byte[] hashAlgFlag) {
			System.arraycopy(hashAlgFlag, 0, sHashAlgFlag, 0, hashAlgFlag.length);
		}

		public void setsUserIDLen(byte[] userIDLen) {
			System.arraycopy(userIDLen, 0, sUserIDLen, 0, userIDLen.length);
		}

		public void setsUserID(byte[] userID) {
			System.arraycopy(userID, 0, sUserID, 0, userID.length);
		}

		public void setsPKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sPKeyLen, 0, keyLen.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}
	}

	public static class RP_MSG_H2 extends Structure {
		public byte[] sHashContextLen = new byte[5];  // HASH CONTEXT����
		public byte[] sHashContext = new byte[513];   // HASH CONTEXT
	    
	    public static class ByReference extends RP_MSG_H2 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_H2 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashContextLen", "sHashContext"});
        }

		public byte[] getsHashContextLen() {
			return DJHsmUtils.getHexByte(sHashContextLen);
		}

		public byte[] getsHashContext() {
			String lenStr = new String(getsHashContextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sHashContext, len);
		}
	    
	    
	}

	public static class RQ_MSG_H2 extends Structure {
		public byte[] sHashContextLen = new byte[5];   // HASH CONTEXT����
		public byte[] sHashContext = new byte[513];    // HASH CONTEXT
		public byte[] sDataLen = new byte[5];          // ���ݿ鳤��
		public byte[] sData = new byte[8193];          // ���ݿ�
	    
	    public static class ByReference extends RQ_MSG_H2 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_H2 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashContextLen", 
            		"sHashContext", "sDataLen", "sData"});
        }

		public void setsHashContextLen(byte[] hashContextLen) {
			System.arraycopy(hashContextLen, 0, sHashContextLen, 0, hashContextLen.length);
		}

		public void setsHashContext(byte[] hashContext) {
			System.arraycopy(hashContext, 0, sHashContext, 0, hashContext.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}

	public static class RP_MSG_H3 extends Structure {
		public byte[] sHashContextLen = new byte[5];   // HASH CONTEXT����
		public byte[] sHashContext = new byte[513];    // HASH CONTEXT
	    
	    public static class ByReference extends RP_MSG_H3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_H3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashContextLen", "sHashContext"});
        }

		public byte[] getsHashContextLen() {
			return DJHsmUtils.getHexByte(sHashContextLen);
		}

		public byte[] getsHashContext() {
			String lenStr = new String(getsHashContextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sHashContext, len);
		}
	    
	    
	}

	public static class RQ_MSG_H3 extends Structure {
		public byte[] sHashContextLen = new byte[5];    // HASH CONTEXT����
		public byte[] sHashContext = new byte[513];     // HASH CONTEXT
	    
	    public static class ByReference extends RQ_MSG_H3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_H3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashContextLen", 
            		"sHashContext"});
        }

		public void setsHashContextLen(byte[] hashContextLen) {
			System.arraycopy(hashContextLen, 0, sHashContextLen, 0, hashContextLen.length);
		}

		public void setsHashContext(byte[] hashContext) {
			System.arraycopy(hashContext, 0, sHashContext, 0, hashContext.length);
		}
	}

	public static class RP_MSG_H4 extends Structure {
		public byte[] sHashLen = new byte[3];      // HASH����
		public byte[] sHash = new byte[256];       // HASH���
	    
	    public static class ByReference extends RP_MSG_H4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_H4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashLen", "sHash"});
        }

		public byte[] getsHashLen() {
			return DJHsmUtils.getHexByte(sHashLen);
		}

		public byte[] getsHash() {
			String lenStr = new String(getsHashLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sHash, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_CR extends Structure {
		public byte[] sRandomLen = new byte[5];      // ���������
	    
	    public static class ByReference extends RQ_MSG_CR 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_CR 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sRandomLen"});
        }

		public void setsRandomLen(byte[] randomLen) {
			System.arraycopy(randomLen, 0, sRandomLen, 0, randomLen.length);
		}
	}
	
	public static class RP_MSG_CS extends Structure {
		public byte[] sRandomLen = new byte[5];      // ���������
		public byte[] sRandomData = new byte[2049];      // �����
		
	    
	    public static class ByReference extends RP_MSG_CS 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_CS 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sRandomLen", "sRandomData"});
        }
	    
		public byte[] getsRandomLen() {
			return DJHsmUtils.getHexByte(sRandomLen);
		}

		public byte[] getsRandomData() {
			String lenStr = new String(getsRandomLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sRandomData, len);
		}
		
	}
	
	///////////////////////////////////////////////////////////////
	//�׿�racal��������ָ��
	//������Կ����
	public static class RQ_MSG_A0 extends Structure {
		public byte[] sGenMode = new byte[2];         // ����ģʽ
		public byte[] sKeyType = new byte[4];         // ��Կ����;
		public byte[] sLmkKeyFlag = new byte[2];      // ��Կ��ʶ(LMK)
		public byte[] sZmkKey = new byte[50];         // ZMK����
		public byte[] sZmkKeyFlag = new byte[2];      // ��Կ��ʶ(ZMK)
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
		public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ                
	    
	    public static class ByReference extends RQ_MSG_A0 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_A0 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sGenMode", 
            		"sKeyType", "sLmkKeyFlag", "sZmkKey",
            		"sZmkKeyFlag", "sKeyStoreFlag", "sKeyStoreIndex",
            		"sKeyTagLen", "sKeyTag"});
        }

		public void setsGenMode(byte[] genMode) {
			System.arraycopy(genMode, 0, sGenMode, 0, genMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsZmkKey(byte[] zmkKey) {
			System.arraycopy(zmkKey, 0, sZmkKey, 0, zmkKey.length);
		}

		public void setsZmkKeyFlag(byte[] zmkKeyFlag) {
			System.arraycopy(zmkKeyFlag, 0, sZmkKeyFlag, 0, zmkKeyFlag.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}
	}

	public static class RP_MSG_A1 extends Structure {
		public byte[] sKeyLmk = new byte[50];      // ��Կ����(LMK)         
		public byte[] sKeyZmk = new byte[50];      // ��Կ����(ZMK)
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_A1 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_A1 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyLmk", "sKeyZmk", 
            		"sKeyCV"});
        }

		public byte[] getsKeyLmk() {
			return DJHsmUtils.getHexByte(sKeyLmk);
		}

		public byte[] getsKeyZmk() {
			return DJHsmUtils.getHexByte(sKeyZmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_A4 extends Structure {
		public byte[] sElementNum = new byte[2];      // ��Կ�ɷ�
		public byte[] sKeyType = new byte[4];         // ��Կ����;
		public byte[] sLmkKeyFlag = new byte[2];      // ��Կ��ʶ(LMK)
		public byte[] sElement1 = new byte[50];       // ��Կ�ɷ�1
		public byte[] sElement2 = new byte[50];       // ��Կ�ɷ�2
		public byte[] sElement3 = new byte[50];       // ��Կ�ɷ�3
		public byte[] sElement4 = new byte[50];       // ��Կ�ɷ�4
		public byte[] sElement5 = new byte[50];       // ��Կ�ɷ�5
		public byte[] sElement6 = new byte[50];       // ��Կ�ɷ�6
		public byte[] sElement7 = new byte[50];       // ��Կ�ɷ�7
		public byte[] sElement8 = new byte[50];       // ��Կ�ɷ�8
	    
	    public static class ByReference extends RQ_MSG_A4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_A4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sElementNum", 
            		"sKeyType", "sLmkKeyFlag", "sElement1",
            		"sElement2", "sElement3", "sElement4",
            		"sElement5", "sElement6", "sElement7",
            		"sElement8"});
        }

		public void setsElementNum(byte[] elementNum) {
			System.arraycopy(elementNum, 0, sElementNum, 0, elementNum.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsElement1(byte[] element1) {
			System.arraycopy(element1, 0, sElement1, 0, element1.length);
		}

		public void setsElement2(byte[] element2) {
			System.arraycopy(element2, 0, sElement2, 0, element2.length);
		}

		public void setsElement3(byte[] element3) {
			System.arraycopy(element3, 0, sElement3, 0, element3.length);
		}

		public void setsElement4(byte[] element4) {
			System.arraycopy(element4, 0, sElement4, 0, element4.length);
		}

		public void setsElement5(byte[] element5) {
			System.arraycopy(element5, 0, sElement5, 0, element5.length);
		}

		public void setsElement6(byte[] element6) {
			System.arraycopy(element6, 0, sElement6, 0, element6.length);
		}

		public void setsElement7(byte[] element7) {
			System.arraycopy(element7, 0, sElement7, 0, element7.length);
		}

		public void setsElement8(byte[] element8) {
			System.arraycopy(element8, 0, sElement8, 0, element8.length);
		}
	}

	public static class RP_MSG_A5 extends Structure {
	    public byte[] sKeyLmk = new byte[50];      // ��Կ����         
	    public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_A5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_A5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyLmk", "sKeyCV"});
        }

		public byte[] getsKeyLmk() {
			return DJHsmUtils.getHexByte(sKeyLmk);
		}

		public byte[] getsKeyCV() {
			//�ĵ�����8λ
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}

	public static class RQ_MSG_A6 extends Structure {
		public byte[] sKeyType = new byte[4];         // ��Կ����
		public byte[] sZmkKey = new byte[50];         // ZMK����
		public byte[] sKeybyZmk = new byte[50];       // ZMK�¼��ܵ���Կ
		public byte[] sLmkKeyFlag = new byte[2];      // ��Կ��ʶ(LMK)
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
		public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ                
	    
	    public static class ByReference extends RQ_MSG_A6 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_A6 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", 
            		"sZmkKey", "sKeybyZmk", "sLmkKeyFlag",
            		"sKeyStoreFlag", "sKeyStoreIndex", "sKeyTagLen",
            		"sKeyTag"});
        }

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsZmkKey(byte[] zmkKey) {
			System.arraycopy(zmkKey, 0, sZmkKey, 0, zmkKey.length);
		}

		public void setsKeybyZmk(byte[] keybyZmk) {
			System.arraycopy(keybyZmk, 0, sKeybyZmk, 0, keybyZmk.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}
	}

	public static class RP_MSG_A7 extends Structure {
		public byte[] sKeyLmk = new byte[50];      // ��Կ����         
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_A7 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_A7 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyLmk", "sKeyCV"});
        }

		public byte[] getsKeyLmk() {
			return DJHsmUtils.getHexByte(sKeyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}

	public static class RQ_MSG_A8 extends Structure {
		public byte[] sKeyType = new byte[4];      // ��Կ����
		public byte[] sZmkKey = new byte[50];      // ZMK����
		public byte[] sKeybyLmk = new byte[50];    // LMK�¼��ܵ���Կ
		public byte[] sZmkKeyFlag = new byte[2];   // ��Կ��ʶ(ZMK)
	    
	    public static class ByReference extends RQ_MSG_A8 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_A8 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", 
            		"sZmkKey", "sKeybyLmk", "sZmkKeyFlag"});
        }

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsZmkKey(byte[] zmkKey) {
			System.arraycopy(zmkKey, 0, sZmkKey, 0, zmkKey.length);
		}

		public void setsKeybyLmk(byte[] keybyLmk) {
			System.arraycopy(keybyLmk, 0, sKeybyLmk, 0, keybyLmk.length);
		}

		public void setsZmkKeyFlag(byte[] zmkKeyFlag) {
			System.arraycopy(zmkKeyFlag, 0, sZmkKeyFlag, 0, zmkKeyFlag.length);
		}
	}

	public static class RP_MSG_A9 extends Structure {
		public byte[] sKeyZmk = new byte[50];      // ��Կ����(ZMK)
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_A9 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_A9 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyZmk", "sKeyCV"});
        }

		public byte[] getsKeyZmk() {
			return DJHsmUtils.getHexByte(sKeyZmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_IA extends Structure {
		public byte[] sZmkKey = new byte[50];      // ZMK����
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sZmkKeyFlag = new byte[2];   // ��Կ��ʶ(ZMK)
		public byte[] sLmkKeyFlag = new byte[2];   // ��Կ��ʶ(LMK)
		public byte[] sKCVType = new byte[2];      // ��ԿУ��ֵ����(KCV) 
	    
	    public static class ByReference extends RQ_MSG_IA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_IA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sZmkKey", "sSeparator",
            		"sZmkKeyFlag", "sLmkKeyFlag", "sKCVType"});
        }

		public void setsZmkKey(byte[] zmkKey) {
			System.arraycopy(zmkKey, 0, sZmkKey, 0, zmkKey.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsZmkKeyFlag(byte[] zmkKeyFlag) {
			System.arraycopy(zmkKeyFlag, 0, sZmkKeyFlag, 0, zmkKeyFlag.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsKCVType(byte[] type) {
			System.arraycopy(type, 0, sKCVType, 0, type.length);
		}
	}

	public static class RP_MSG_IB extends Structure {
		public byte[] sKeybyZmk = new byte[50];    // ZPK����(ZMK)
		public byte[] sKeybyLmk = new byte[50];    // ZPK����(LMK)
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_IB 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_IB 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyZmk", "sKeybyLmk", 
            		"sKeyCV"});
        }

		public byte[] getsKeybyZmk() {
			return DJHsmUtils.getHexByte(sKeybyZmk);
		}

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}

	public static class RQ_MSG_FA extends Structure {
		public byte[] sZmkKey = new byte[50];      // ZMK���ģ�LMK��
		public byte[] sZpkKey = new byte[50];      // ZPK���ģ�ZMK��
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sZmkKeyFlag = new byte[2];   // ��Կ��ʶ(ZMK)
		public byte[] sLmkKeyFlag = new byte[2];   // ��Կ��ʶ(LMK)
		public byte[] sKCVType = new byte[2];      // ��ԿУ��ֵ(KCV)
	    
	    public static class ByReference extends RQ_MSG_FA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_FA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sZmkKey", "sZpkKey", 
            		"sSeparator", "sZmkKeyFlag", "sLmkKeyFlag", 
            		"sKCVType"});
        }

		public void setsZmkKey(byte[] zmkKey) {
			System.arraycopy(zmkKey, 0, sZmkKey, 0, zmkKey.length);
		}

		public void setsZpkKey(byte[] zpkKey) {
			System.arraycopy(zpkKey, 0, sZpkKey, 0, zpkKey.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsZmkKeyFlag(byte[] zmkKeyFlag) {
			System.arraycopy(zmkKeyFlag, 0, sZmkKeyFlag, 0, zmkKeyFlag.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsKCVType(byte[] type) {
			System.arraycopy(type, 0, sKCVType, 0, type.length);
		}
	}

	public static class RP_MSG_FB extends Structure {
		public byte[] sKeybyLmk = new byte[50];    // ZPK���ģ�LMK��
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_FB 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_FB 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyLmk", "sKeyCV"});
        }

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_GC extends Structure {
		public byte[] sZmkKey = new byte[50];      // ZMK���ģ�LMK��
		public byte[] sZpkKey = new byte[50];      // ZPK���ģ�LMK��
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sZmkKeyFlag = new byte[2];   // ��Կ��ʶ(ZMK)
		public byte[] sLmkKeyFlag = new byte[2];   // ��Կ��ʶ(LMK)
		public byte[] sKCVType = new byte[2];      // ��ԿУ��ֵ(KCV)
	    
	    public static class ByReference extends RQ_MSG_GC 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_GC 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sZmkKey", 
            		"sZpkKey", "sSeparator", "sZmkKeyFlag",
            		"sLmkKeyFlag", "sKCVType"});
        }

		public void setsZmkKey(byte[] zmkKey) {
			System.arraycopy(zmkKey, 0, sZmkKey, 0, zmkKey.length);
		}

		public void setsZpkKey(byte[] zpkKey) {
			System.arraycopy(zpkKey, 0, sZpkKey, 0, zpkKey.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsZmkKeyFlag(byte[] zmkKeyFlag) {
			System.arraycopy(zmkKeyFlag, 0, sZmkKeyFlag, 0, zmkKeyFlag.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsKCVType(byte[] type) {
			System.arraycopy(type, 0, sKCVType, 0, type.length);
		}
	}

	public static class RP_MSG_GD extends Structure {
		public byte[] sKeybyZmk = new byte[50];    // ZPK���ģ�ZMK��
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_GD 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_GD 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyZmk", "sKeyCV"});
        }

		public byte[] getsKeybyZmk() {
			return DJHsmUtils.getHexByte(sKeybyZmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_FI extends Structure {
		public byte[] sKeyTypeFlag = new byte[2];  // ��Կ���ͱ�־
		public byte[] sZmkKey = new byte[50];      // ZMK����
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sZmkKeyFlag = new byte[2];   // ��Կ��ʶ(ZMK) 
		public byte[] sLmkKeyFlag = new byte[2];   // ��Կ��ʶ(LMK)
		public byte[] sKCVType = new byte[2];      // ��ԿУ��ֵ����
	    
	    public static class ByReference extends RQ_MSG_FI 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_FI 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyTypeFlag", 
            		"sZmkKey", "sSeparator", "sZmkKeyFlag",
            		"sLmkKeyFlag", "sKCVType"});
        }

		public void setsKeyTypeFlag(byte[] keyTypeFlag) {
			System.arraycopy(keyTypeFlag, 0, sKeyTypeFlag, 0, keyTypeFlag.length);
		}

		public void setsZmkKey(byte[] zmkKey) {
			System.arraycopy(zmkKey, 0, sZmkKey, 0, zmkKey.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsZmkKeyFlag(byte[] zmkKeyFlag) {
			System.arraycopy(zmkKeyFlag, 0, sZmkKeyFlag, 0, zmkKeyFlag.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsKCVType(byte[] type) {
			System.arraycopy(type, 0, sKCVType, 0, type.length);
		}
	}

	public static class RP_MSG_FJ extends Structure {
	    public byte[] sKeybyZmk = new byte[50];    // ��Կ���ģ�ZMK��
	    public byte[] sKeybyLmk = new byte[50];    // ��Կ���ģ�LMK��
	    public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_FJ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_FJ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyZmk", "sKeybyLmk", 
            		"sKeyCV"});
        }

		public byte[] getsKeybyZmk() {
			return DJHsmUtils.getHexByte(sKeybyZmk);
		}

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_FK extends Structure {
		public byte[] sKeyTypeFlag = new byte[2];    // ��Կ���ͱ�־
	    public byte[] sZmkKey = new byte[50];        // ZMK����
	    public byte[] sKeybyZmk = new byte[50];      // ZEK/ZAK���ģ�ZMK��
	    public byte[] sSeparator = new byte[2];      // �ָ���
	    public byte[] sZmkKeyFlag = new byte[2];     // ��Կ��ʶ(ZMK)
	    public byte[] sLmkKeyFlag = new byte[2];     // ��Կ��ʶ(LMK)
	    public byte[] sKCVType = new byte[2];        // ��ԿУ��ֵ(KCV)
	    
	    public static class ByReference extends RQ_MSG_FK 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_FK 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyTypeFlag", 
            		"sZmkKey", "sKeybyZmk", "sSeparator",
            		"sZmkKeyFlag", "sLmkKeyFlag", "sKCVType"});
        }

		public void setsKeyTypeFlag(byte[] keyTypeFlag) {
			System.arraycopy(keyTypeFlag, 0, sKeyTypeFlag, 0, keyTypeFlag.length);
		}

		public void setsZmkKey(byte[] zmkKey) {
			System.arraycopy(zmkKey, 0, sZmkKey, 0, zmkKey.length);
		}

		public void setsKeybyZmk(byte[] keybyZmk) {
			System.arraycopy(keybyZmk, 0, sKeybyZmk, 0, keybyZmk.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsZmkKeyFlag(byte[] zmkKeyFlag) {
			System.arraycopy(zmkKeyFlag, 0, sZmkKeyFlag, 0, zmkKeyFlag.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsKCVType(byte[] type) {
			System.arraycopy(type, 0, sKCVType, 0, type.length);
		}
	}

	public static class RP_MSG_FL extends Structure {
		public byte[] sKeybyLmk = new byte[50];    // ZEK/ZAK���ģ�LMK��
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_FL 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_FL 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyLmk", "sKeyCV"});
        }

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}

	public static class RQ_MSG_FM extends Structure {
		public byte[] sKeyTypeFlag = new byte[2];     // ��Կ���ͱ�־
	    public byte[] sZmkKey = new byte[50];         // ZMK����
	    public byte[] sZekKey = new byte[50];         // ZEK���ģ�LMK��
	    public byte[] sZakKey = new byte[50];         // ZAK���ģ�LMK��
	    public byte[] sSeparator = new byte[2];       // �ָ���
	    public byte[] sZmkKeyFlag = new byte[2];      // ��Կ��ʶ ZMK
	    public byte[] sLmkKeyFlag = new byte[2];      // ��Կ��ʶ(LMK)
	    public byte[] sKCVType = new byte[2];         // ��ԿУ��ֵ(KCV)
	    
	    public static class ByReference extends RQ_MSG_FM 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_FM 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyTypeFlag", 
            		"sZmkKey", "sZekKey", "sZakKey",
            		"sSeparator", "sZmkKeyFlag", "sLmkKeyFlag",
            		"sKCVType"});
        }

		public void setsKeyTypeFlag(byte[] keyTypeFlag) {
			System.arraycopy(keyTypeFlag, 0, sKeyTypeFlag, 0, keyTypeFlag.length);
		}

		public void setsZmkKey(byte[] zmkKey) {
			System.arraycopy(zmkKey, 0, sZmkKey, 0, zmkKey.length);
		}

		public void setsZekKey(byte[] zekKey) {
			System.arraycopy(zekKey, 0, sZekKey, 0, zekKey.length);
		}

		public void setsZakKey(byte[] zakKey) {
			System.arraycopy(zakKey, 0, sZakKey, 0, zakKey.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsZmkKeyFlag(byte[] zmkKeyFlag) {
			System.arraycopy(zmkKeyFlag, 0, sZmkKeyFlag, 0, zmkKeyFlag.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsKCVType(byte[] type) {
			System.arraycopy(type, 0, sKCVType, 0, type.length);
		}
	}

	public static class RP_MSG_FN extends Structure {
		public byte[] sKeybyZmk = new byte[50];    // ZEK/ZAK���ģ�ZMK��
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_FN 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_FN 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyZmk", "sKeyCV"});
        }

		public byte[] getsKeybyZmk() {
			return DJHsmUtils.getHexByte(sKeybyZmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_HC extends Structure {
		public byte[] sTmkKey = new byte[50];      // TMK����
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sTmkKeyFlag = new byte[2];   // ����Կ��ʶ(TMK) 
		public byte[] sLmkKeyFlag = new byte[2];   // ����Կ��ʶ(LMK) 
		public byte[] sKCVType = new byte[2];      // ��ԿУ��ֵ����
	    
	    public static class ByReference extends RQ_MSG_HC 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_HC 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sTmkKey", 
            		"sSeparator", "sTmkKeyFlag", "sLmkKeyFlag",
            		"sKCVType"});
        }

		public void setsTmkKey(byte[] tmkKey) {
			System.arraycopy(tmkKey, 0, sTmkKey, 0, tmkKey.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsTmkKeyFlag(byte[] tmkKeyFlag) {
			System.arraycopy(tmkKeyFlag, 0, sTmkKeyFlag, 0, tmkKeyFlag.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsKCVType(byte[] type) {
			System.arraycopy(type, 0, sKCVType, 0, type.length);
		}
	}

	public static class RP_MSG_HD extends Structure {
		public byte[] sKeybyTmk = new byte[50];    // ��Կ����(TMK)
		public byte[] sKeybyLmk = new byte[50];    // ��Կ����(LMK) 
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_HD 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_HD 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyTmk", "sKeybyLmk", 
            		"sKeyCV"});
        }

		public byte[] getsKeybyTmk() {
			return DJHsmUtils.getHexByte(sKeybyTmk);
		}

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_HA extends Structure {
		public byte[] sTmkKey = new byte[50];        // TMK����
		public byte[] sSeparator = new byte[2];      // �ָ���
		public byte[] sTmkKeyFlag = new byte[2];     // ��Կ��ʶ(TMK)
		public byte[] sLmkKeyFlag = new byte[2];     // ��Կ��ʶ(LMK)
		public byte[] sKCVType = new byte[2];        // ��ԿУ��ֵ(KCV)
	    
	    public static class ByReference extends RQ_MSG_HA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_HA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sTmkKey","sSeparator", 
            		"sTmkKeyFlag", "sLmkKeyFlag", "sKCVType"});
        }

		public void setsTmkKey(byte[] tmkKey) {
			System.arraycopy(tmkKey, 0, sTmkKey, 0, tmkKey.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsTmkKeyFlag(byte[] tmkKeyFlag) {
			System.arraycopy(tmkKeyFlag, 0, sTmkKeyFlag, 0, tmkKeyFlag.length);
		}

		public void setsLmkKeyFlag(byte[] lmkKeyFlag) {
			System.arraycopy(lmkKeyFlag, 0, sLmkKeyFlag, 0, lmkKeyFlag.length);
		}

		public void setsKCVType(byte[] type) {
			System.arraycopy(type, 0, sKCVType, 0, type.length);
		}
	}

	public static class RP_MSG_HB extends Structure {
		public byte[] sKeybyTmk = new byte[50];    // ��Կ����(TMK)
		public byte[] sKeybyLmk = new byte[50];    // ��Կ����(LMK)
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ  
	    
	    public static class ByReference extends RP_MSG_HB 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_HB 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyTmk", "sKeybyLmk", 
            		"sKeyCV"});
        }

		public byte[] getsKeybyTmk() {
			return DJHsmUtils.getHexByte(sKeybyTmk);
		}

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
		
	public static class RQ_MSG_MA extends Structure {
		public byte[] sTakKey = new byte[34];      // TAK
		public byte[] sDataLen = new byte[5];      // ���ݳ�
		public byte[] sData = new byte[4096];      // ����
	    
	    public static class ByReference extends RQ_MSG_MA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_MA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sTakKey", "sDataLen", 
            		"sData"});
        }

		public void setsTakKey(byte[] takKey) {
			System.arraycopy(takKey, 0, sTakKey, 0, takKey.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}

	public static class RP_MSG_MB extends Structure {
		public byte[] sMac = new byte[33];         // MAC
	    
	    public static class ByReference extends RP_MSG_MB 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_MB 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}	

	public static class RQ_MSG_MC extends Structure {
		public byte[] sTakKey = new byte[34];      // TAK
		public byte[] sMac = new byte[9];          // MAC
		public byte[] sDataLen = new byte[5];      // ���ݳ�
	    public byte[] sData = new byte[4096];      // ����
	    
	    public static class ByReference extends RQ_MSG_MC 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_MC 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sTakKey", "sMac", 
            		"sDataLen", "sData"});
        }

		public void setsTakKey(byte[] takKey) {
			System.arraycopy(takKey, 0, sTakKey, 0, takKey.length);
		}

		public void setsMac(byte[] mac) {
			System.arraycopy(mac, 0, sMac, 0, mac.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}
	
	public static class RQ_MSG_MQ extends Structure {
		public byte[] sBlockFlag = new byte[2];    // ���Ŀ��ʶ
		public byte[] sZakKey = new byte[34];      // ZAK��Կ����
		public byte[] sIV = new byte[33];          // IV
		public byte[] sDataLen = new byte[4];      // ��Ϣ����
		public byte[] sData = new byte[2048];      // ����
	    
	    public static class ByReference extends RQ_MSG_MQ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_MQ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sBlockFlag", 
            		"sZakKey", "sIV", "sDataLen",
            		"sData"});
        }

		public void setsBlockFlag(byte[] blockFlag) {
			System.arraycopy(blockFlag, 0, sBlockFlag, 0, blockFlag.length);
		}

		public void setsZakKey(byte[] zakKey) {
			System.arraycopy(zakKey, 0, sZakKey, 0, zakKey.length);
		}

		public void setsIV(byte[] siv) {
			System.arraycopy(siv, 0, sIV, 0, siv.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}

	public static class RP_MSG_MR extends Structure {
		public byte[] sMac = new byte[33];         // MAC
	    
	    public static class ByReference extends RP_MSG_MR 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_MR 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}
	
	public static class RQ_MSG_UQ extends Structure {
		public byte[] sBlockFlag = new byte[2];    // ���Ŀ��ʶ
		public byte[] sZpkKey = new byte[34];      // ZPK��Կ����
		public byte[] sIV = new byte[33];          // IV
		public byte[] sDataLen = new byte[4];      // ��Ϣ����
		public byte[] sData = new byte[2048];      // ����
		public byte[] sMacLen = new byte[3];       // ���MAC����
	    
	    public static class ByReference extends RQ_MSG_UQ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_UQ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sBlockFlag", 
            		"sZpkKey", "sIV", "sDataLen",
            		"sData", "sMacLen"});
        }

		public void setsBlockFlag(byte[] blockFlag) {
			System.arraycopy(blockFlag, 0, sBlockFlag, 0, blockFlag.length);
		}

		public void setsZpkKey(byte[] zpkKey) {
			System.arraycopy(zpkKey, 0, sZpkKey, 0, zpkKey.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsMacLen(byte[] macLen) {
			System.arraycopy(macLen, 0, sMacLen, 0, macLen.length);
		}
	}

	public static class RP_MSG_UR extends Structure {
		public byte[] sMac = new byte[33];         // MAC
	    
	    public static class ByReference extends RP_MSG_UR 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_UR 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}
	
	public static class RQ_MSG_MU extends Structure {
		public byte[] sBlockFlag = new byte[2];    // ���Ŀ��ʶ
		public byte[] sKeyType = new byte[2];      // ��Կ����
		public byte[] sKeyLen = new byte[2];       // ��Կ����
		public byte[] sDataType = new byte[2];     // ��������
		public byte[] sKeybyLmk = new byte[34];    // TAK/ZAK
		public byte[] sIV = new byte[33];          // IV
		public byte[] sDataLen = new byte[5];      // ��Ϣ����
		public byte[] sData = new byte[4096];      // ����
	    
	    public static class ByReference extends RQ_MSG_MU 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_MU 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sBlockFlag", 
            		"sKeyType", "sKeyLen", "sDataType",
            		"sKeybyLmk", "sIV", "sDataLen",
            		"sData"});
        }

		public void setsBlockFlag(byte[] blockFlag) {
			System.arraycopy(blockFlag, 0, sBlockFlag, 0, blockFlag.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sKeyLen, 0, keyLen.length);
		}

		public void setsDataType(byte[] dataType) {
			System.arraycopy(dataType, 0, sDataType, 0, dataType.length);
		}

		public void setsKeybyLmk(byte[] keybyLmk) {
			System.arraycopy(keybyLmk, 0, sKeybyLmk, 0, keybyLmk.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}
	
	public static class RP_MSG_MV extends Structure {
		public byte[] sMac = new byte[33];         // MAC
	    
	    public static class ByReference extends RP_MSG_MV 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_MV 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}

	public static class RQ_MSG_MS extends Structure {
		public byte[] sBlockFlag = new byte[2];    // ���Ŀ��ʶ
		public byte[] sKeyType = new byte[2];      // ��Կ����
		public byte[] sKeyLen = new byte[2];       // ��Կ����
		public byte[] sDataType = new byte[2];     // ��������
		public byte[] sKeybyLmk = new byte[34];    // TAK/ZAK
		public byte[] sIV = new byte[33];          // IV
		public byte[] sDataLen = new byte[5];      // ��Ϣ����
		public byte[] sData = new byte[4096];      // ����
	    
	    public static class ByReference extends RQ_MSG_MS 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_MS 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sBlockFlag", 
            		"sKeyType", "sKeyLen", "sDataType",
            		"sKeybyLmk", "sIV", "sDataLen",
            		"sData"});
        }

		public void setsBlockFlag(byte[] blockFlag) {
			System.arraycopy(blockFlag, 0, sBlockFlag, 0, blockFlag.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sKeyLen, 0, keyLen.length);
		}

		public void setsDataType(byte[] dataType) {
			System.arraycopy(dataType, 0, sDataType, 0, dataType.length);
		}

		public void setsKeybyLmk(byte[] keybyLmk) {
			System.arraycopy(keybyLmk, 0, sKeybyLmk, 0, keybyLmk.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}

	public static class RP_MSG_MT extends Structure {
		public byte[] sMac = new byte[33];         // MAC
	    
	    public static class ByReference extends RP_MSG_MT 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_MT 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac"});
        }

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}
	
	//PIN���������	
	public static class RQ_MSG_JA extends Structure {
		public byte[] sAccNo = new byte[13];       // �˺�
		public byte[] sPinLen = new byte[3];       // PIN����
	    
	    public static class ByReference extends RQ_MSG_JA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_JA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAccNo", 
            		"sPinLen"});
        }

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsPinLen(byte[] pinLen) {
			System.arraycopy(pinLen, 0, sPinLen, 0, pinLen.length);
		}
	}

	public static class RP_MSG_JB extends Structure {
		public byte[] sPinByLmk = new byte[14];    // PIN
	    
	    public static class ByReference extends RP_MSG_JB 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_JB 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinByLmk"});
        }

		public byte[] getsPinByLmk() {
			return DJHsmUtils.getHexByte(sPinByLmk);
		}
	    
	    
	}
	
	public static class RQ_MSG_BA extends Structure {
		public byte[] sPin = new byte[14];         // PIN����
		public byte[] sAccNo = new byte[13];       // �˺�
	    
	    public static class ByReference extends RQ_MSG_BA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_BA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPin", 
            		"sAccNo"});
        }

		public void setsPin(byte[] pin) {
			System.arraycopy(pin, 0, sPin, 0, pin.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}
	}
	
	public static class RP_MSG_BB extends Structure {
		public byte[] sPinByLmk = new byte[14];    // PIN����
	    
	    public static class ByReference extends RP_MSG_BB 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_BB 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinByLmk"});
        }

		public byte[] getsPinByLmk() {
			return DJHsmUtils.getHexByte(sPinByLmk);
		}
	    
	    
	}

	public static class RQ_MSG_NG extends Structure {
		public byte[] sAccNo = new byte[13];       // �˺�
		public byte[] sPinByLmk = new byte[14];    // PIN����
	    
	    public static class ByReference extends RQ_MSG_NG 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_NG 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAccNo", 
            		"sPinByLmk"});
        }

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsPinByLmk(byte[] pinByLmk) {
			System.arraycopy(pinByLmk, 0, sPinByLmk, 0, pinByLmk.length);
		}
	}
		
	public static class RP_MSG_NH extends Structure {
		public byte[] sPin = new byte[13];         // PIN����
	    
	    public static class ByReference extends RP_MSG_NH 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_NH 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPin"});
        }

		public byte[] getsPin() {
			return DJHsmUtils.getHexByte(sPin);
		}
	    
	    
	}
	
	//PIN����ת��		
	public static class RQ_MSG_JC extends Structure {
		public byte[] sTpkKey = new byte[50];      // ԴTPK��Կ
		public byte[] sPinBlock = new byte[33];    // ԴPINBLOCK����
		public byte[] sPinFormat = new byte[3];    // PINBLOCK��ʽ 
		public byte[] sAccNo = new byte[19];       // �˺�
	    
	    public static class ByReference extends RQ_MSG_JC 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_JC 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sTpkKey", 
            		"sPinBlock", "sPinFormat", "sAccNo"});
        }

		public void setsTpkKey(byte[] tpkKey) {
			System.arraycopy(tpkKey, 0, sTpkKey, 0, tpkKey.length);
		}

		public void setsPinBlock(byte[] pinBlock) {
			System.arraycopy(pinBlock, 0, sPinBlock, 0, pinBlock.length);
		}

		public void setsPinFormat(byte[] pinFormat) {
			System.arraycopy(pinFormat, 0, sPinFormat, 0, pinFormat.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}
	}
	
	public static class RP_MSG_JD extends Structure {
		public byte[] sPinByLmk = new byte[14];    // PIN����
	    
	    public static class ByReference extends RP_MSG_JD 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_JD 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinByLmk"});
        }

		public byte[] getsPinByLmk() {
			return DJHsmUtils.getHexByte(sPinByLmk);
		}
	    
	    
	}
	
	public static class RQ_MSG_JE extends Structure {
		public byte[] sZpkKey = new byte[50];      // ԴZPK��Կ
		public byte[] sPinBlock = new byte[33];    // ԴPINBLOCK����
		public byte[] sPinFormat = new byte[3];    // PINBLOCK��ʽ
		public byte[] sAccNo = new byte[19];       // �˺�
	    
	    public static class ByReference extends RQ_MSG_JE 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_JE 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sZpkKey", 
            		"sPinBlock", "sPinFormat", "sAccNo"});
        }

		public void setsZpkKey(byte[] zpkKey) {
			System.arraycopy(zpkKey, 0, sZpkKey, 0, zpkKey.length);
		}

		public void setsPinBlock(byte[] pinBlock) {
			System.arraycopy(pinBlock, 0, sPinBlock, 0, pinBlock.length);
		}

		public void setsPinFormat(byte[] pinFormat) {
			System.arraycopy(pinFormat, 0, sPinFormat, 0, pinFormat.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}
	}

	public static class RP_MSG_JF extends Structure {
		public byte[] sPinByLmk = new byte[14];    // PIN����
	    
	    public static class ByReference extends RP_MSG_JF 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_JF 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinByLmk"});
        }

		public byte[] getsPinByLmk() {
			return DJHsmUtils.getHexByte(sPinByLmk);
		}
	    
	    
	}

	public static class RQ_MSG_JG extends Structure {
		public byte[] sZpkKey = new byte[50];      // Ŀ��ZPK��Կ
		public byte[] sPinFormat = new byte[3];    // PINBLOCK��ʽ
		public byte[] sAccNo = new byte[19];       // �˺�
		public byte[] sPinByLmk = new byte[14];    // PIN����
	    
	    public static class ByReference extends RQ_MSG_JG 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_JG 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sZpkKey", 
            		"sPinFormat", "sAccNo", "sPinByLmk"});
        }

		public void setsZpkKey(byte[] zpkKey) {
			System.arraycopy(zpkKey, 0, sZpkKey, 0, zpkKey.length);
		}

		public void setsPinFormat(byte[] pinFormat) {
			System.arraycopy(pinFormat, 0, sPinFormat, 0, pinFormat.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsPinByLmk(byte[] pinByLmk) {
			System.arraycopy(pinByLmk, 0, sPinByLmk, 0, pinByLmk.length);
		}
	}
	
	public static class RP_MSG_JH extends Structure {
		public byte[] sPinBlock = new byte[33];    // Ŀ��PINBLOCK����
	    
	    public static class ByReference extends RP_MSG_JH 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_JH 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinBlock"});
        }

		public byte[] getsPinBlock() {
			return DJHsmUtils.getHexByte(sPinBlock);
		}
	    
	    
	}
	
	public static class RQ_MSG_CA extends Structure {
		public byte[] sTpkKey = new byte[50];      // ԴTPK��Կ
		public byte[] sZpkKey = new byte[50];      // Ŀ��ZPK��Կ 
		public byte[] sPinLen = new byte[3];       // ���PIN���� 
		public byte[] sPinBlock = new byte[33];    // ԴPINBLOCK����
		public byte[] sSrcForm = new byte[3];      // ԴPINBLOCK��ʽ 
		public byte[] sDesForm = new byte[3];      // Ŀ��PINBLOCK��ʽ
		public byte[] sAccNo = new byte[19];       // �˺�
	    
	    public static class ByReference extends RQ_MSG_CA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_CA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sTpkKey", "sZpkKey", 
            		"sPinLen", "sPinBlock", "sSrcForm", "sDesForm", 
            		"sAccNo"});
        }

		public void setsTpkKey(byte[] tpkKey) {
			System.arraycopy(tpkKey, 0, sTpkKey, 0, tpkKey.length);
		}

		public void setsZpkKey(byte[] zpkKey) {
			System.arraycopy(zpkKey, 0, sZpkKey, 0, zpkKey.length);
		}

		public void setsPinLen(byte[] pinLen) {
			System.arraycopy(pinLen, 0, sPinLen, 0, pinLen.length);
		}

		public void setsPinBlock(byte[] pinBlock) {
			System.arraycopy(pinBlock, 0, sPinBlock, 0, pinBlock.length);
		}

		public void setsSrcForm(byte[] srcForm) {
			System.arraycopy(srcForm, 0, sSrcForm, 0, srcForm.length);
		}

		public void setsDesForm(byte[] desForm) {
			System.arraycopy(desForm, 0, sDesForm, 0, desForm.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}
	}
	
	public static class RP_MSG_CB extends Structure {
		public byte[] sPinLen = new byte[3];       // PIN����
		public byte[] sPinBlock = new byte[33];    // Ŀ��PINBLOCK����
		public byte[] sDesForm = new byte[3];      // Ŀ��PINBLOCK��ʽ 
	    
	    public static class ByReference extends RP_MSG_CB 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_CB 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinLen", "sPinBlock", 
            		"sDesForm"});
        }

		public byte[] getsPinLen() {
			return DJHsmUtils.getHexByte(sPinLen);
		}

		public byte[] getsPinBlock() {
			return DJHsmUtils.getHexByte(sPinBlock);
		}

		public byte[] getsDesForm() {
			return DJHsmUtils.getHexByte(sDesForm);
		}
	    
	    
	}
	
	public static class RQ_MSG_CC extends Structure {
		public byte[] sSrcZpkKey = new byte[50];   // ԴZPK��Կ
		public byte[] sDesZpkKey = new byte[50];   // Ŀ��ZPK��Կ 
		public byte[] sPinLen = new byte[3];       // ���PIN���� 
		public byte[] sPinBlock = new byte[33];    // ԴPINBLOCK����
		public byte[] sSrcForm = new byte[3];      // ԴPINBLOCK��ʽ
		public byte[] sDesForm = new byte[3];      // Ŀ��PINBLOCK��ʽ
		public byte[] sAccNo = new byte[19];       // �˺�
	    
	    public static class ByReference extends RQ_MSG_CC 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_CC 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSrcZpkKey", 
            		"sDesZpkKey", "sPinLen", "sPinBlock",
            		"sSrcForm", "sDesForm", "sAccNo"});
        }

		public void setsSrcZpkKey(byte[] srcZpkKey) {
			System.arraycopy(srcZpkKey, 0, sSrcZpkKey, 0, srcZpkKey.length);
		}

		public void setsDesZpkKey(byte[] desZpkKey) {
			System.arraycopy(desZpkKey, 0, sDesZpkKey, 0, desZpkKey.length);
		}

		public void setsPinLen(byte[] pinLen) {
			System.arraycopy(pinLen, 0, sPinLen, 0, pinLen.length);
		}

		public void setsPinBlock(byte[] pinBlock) {
			System.arraycopy(pinBlock, 0, sPinBlock, 0, pinBlock.length);
		}

		public void setsSrcForm(byte[] srcForm) {
			System.arraycopy(srcForm, 0, sSrcForm, 0, srcForm.length);
		}

		public void setsDesForm(byte[] desForm) {
			System.arraycopy(desForm, 0, sDesForm, 0, desForm.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}
	}
	
	public static class RP_MSG_CD extends Structure {
		public byte[] sPinLen = new byte[3];       // PIN����
		public byte[] sPinBlock = new byte[33];    // Ŀ��PINBLOCK����
		public byte[] sDesForm = new byte[3];      // Ŀ��PINBLOCK��ʽ
	    
	    public static class ByReference extends RP_MSG_CD 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_CD 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sErrCode", "sPinLen", 
            		"sPinBlock", "sDesForm"});
        }

		public byte[] getsPinLen() {
			return DJHsmUtils.getHexByte(sPinLen);
		}

		public byte[] getsPinBlock() {
			return DJHsmUtils.getHexByte(sPinBlock);
		}

		public byte[] getsDesForm() {
			return DJHsmUtils.getHexByte(sDesForm);
		}
	    
	    
	}
	
	public static class RQ_MSG_TI extends Structure {
		public byte[] sSrcKeyType = new byte[2];   // Դ��Կ����
		public byte[] sSrcKey = new byte[50];      // ԴTPK/ZPK��Կ
		public byte[] sDesKeyType = new byte[2];   // Ŀ����Կ����
		public byte[] sDesKey = new byte[50];      // Ŀ��TPK/ZPK��Կ
		public byte[] sPinLen = new byte[3];       // ���PIN���� 
		public byte[] sPinBlock = new byte[33];    // ԴPINBLOCK���� 
		public byte[] sSrcForm = new byte[3];      // ԴPINBLOCK��ʽ
		public byte[] sSrcAccNo = new byte[19];    // Դ�˺�
		public byte[] sDesForm = new byte[3];      // Ŀ��PINBLOCK��ʽ
		public byte[] sDesAccNo = new byte[19];    // Ŀ���˺�
	    
	    public static class ByReference extends RQ_MSG_TI 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TI 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSrcKeyType", 
            		"sSrcKey", "sDesKeyType", "sDesKey",
            		"sPinLen", "sPinBlock", "sSrcForm",
            		"sSrcAccNo", "sDesForm", "sDesAccNo"});
        }

		public void setsSrcKeyType(byte[] srcKeyType) {
			System.arraycopy(srcKeyType, 0, sSrcKeyType, 0, srcKeyType.length);
		}

		public void setsSrcKey(byte[] srcKey) {
			System.arraycopy(srcKey, 0, sSrcKey, 0, srcKey.length);
		}

		public void setsDesKeyType(byte[] desKeyType) {
			System.arraycopy(desKeyType, 0, sDesKeyType, 0, desKeyType.length);
		}

		public void setsDesKey(byte[] desKey) {
			System.arraycopy(desKey, 0, sDesKey, 0, desKey.length);
		}

		public void setsPinLen(byte[] pinLen) {
			System.arraycopy(pinLen, 0, sPinLen, 0, pinLen.length);
		}

		public void setsPinBlock(byte[] pinBlock) {
			System.arraycopy(pinBlock, 0, sPinBlock, 0, pinBlock.length);
		}

		public void setsSrcForm(byte[] srcForm) {
			System.arraycopy(srcForm, 0, sSrcForm, 0, srcForm.length);
		}

		public void setsSrcAccNo(byte[] srcAccNo) {
			System.arraycopy(srcAccNo, 0, sSrcAccNo, 0, srcAccNo.length);
		}

		public void setsDesForm(byte[] desForm) {
			System.arraycopy(desForm, 0, sDesForm, 0, desForm.length);
		}

		public void setsDesAccNo(byte[] desAccNo) {
			System.arraycopy(desAccNo, 0, sDesAccNo, 0, desAccNo.length);
		}
	}
	
	public static class RP_MSG_TJ extends Structure {
		public byte[] sPinBlock = new byte[33];    // Ŀ��PINBLOCK����
	    
	    public static class ByReference extends RP_MSG_TJ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TJ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinBlock"});
        }

		public byte[] getsPinBlock() {
			return DJHsmUtils.getHexByte(sPinBlock);
		}
	    
	    
	}
	
	public static class RQ_MSG_N6 extends Structure {
		public byte[] sPubAlgFlag = new byte[3];   // ��Կ�㷨��ʶ
		public byte[] sSIndex = new byte[5];       // ˽Կ����
		public byte[] sSKeyLen = new byte[5];      // ˽Կ����
		public byte[] sSKey = new byte[2500];      // ˽Կ
		public byte[] sPubKeyPinForm = new byte[3];// ��Կ����PIN��ɸ�ʽ 
	    public byte[] sPadMode = new byte[3];      // ��Կ���ܵ����ģʽ
	    public byte[] sZpkKey = new byte[50];      // ZPK��Կ
	    public byte[] sZpkPinForm = new byte[3];   // ZPK����PIN��ɸ�ʽ 
	    public byte[] sAccNoLen = new byte[3];     // �˺ų���
	    public byte[] sAccNo = new byte[25];       // �˺�PAN
	    public byte[] sPinBlockLen = new byte[5];  // ��Կ���ܵ�PIN���ĳ��� 
	    public byte[] sPinBlock = new byte[2048];  // ��Կ���ܵ�PIN����
	    public byte[] sExtMark = new byte[2];      // ��չ�����ʶ 
	    public byte[] sMacAlgMode = new byte[3];   // MAC�㷨ģʽ
	    public byte[] sMacPadMode = new byte[3];   // ����MAC�����ݿ�PAD��ʶ 
	    
	    public static class ByReference extends RQ_MSG_N6 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_N6 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPubAlgFlag", "sSIndex", 
            		"sSKeyLen", "sSKey", "sPubKeyPinForm", "sPadMode", 
            		"sZpkKey", "sZpkPinForm", "sAccNoLen", "sAccNo",
            		"sPinBlockLen", "sPinBlock", "sExtMark",
            		"sMacAlgMode", "sMacPadMode"});
        }

		public void setsPubAlgFlag(byte[] pubAlgFlag) {
			System.arraycopy(pubAlgFlag, 0, sPubAlgFlag, 0, pubAlgFlag.length);
		}

		public void setsSIndex(byte[] index) {
			System.arraycopy(index, 0, sSIndex, 0, index.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}

		public void setsPubKeyPinForm(byte[] pubKeyPinForm) {
			System.arraycopy(pubKeyPinForm, 0, sPubKeyPinForm, 0, pubKeyPinForm.length);
		}

		public void setsPadMode(byte[] padMode) {
			System.arraycopy(padMode, 0, sPadMode, 0, padMode.length);
		}

		public void setsZpkKey(byte[] zpkKey) {
			System.arraycopy(zpkKey, 0, sZpkKey, 0, zpkKey.length);
		}

		public void setsZpkPinForm(byte[] zpkPinForm) {
			System.arraycopy(zpkPinForm, 0, sZpkPinForm, 0, zpkPinForm.length);
		}

		public void setsAccNoLen(byte[] accNoLen) {
			System.arraycopy(accNoLen, 0, sAccNoLen, 0, accNoLen.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsPinBlockLen(byte[] pinBlockLen) {
			System.arraycopy(pinBlockLen, 0, sPinBlockLen, 0, pinBlockLen.length);
		}

		public void setsPinBlock(byte[] pinBlock) {
			System.arraycopy(pinBlock, 0, sPinBlock, 0, pinBlock.length);
		}

		public void setsExtMark(byte[] extMark) {
			System.arraycopy(extMark, 0, sExtMark, 0, extMark.length);
		}

		public void setsMacAlgMode(byte[] macAlgMode) {
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}

		public void setsMacPadMode(byte[] macPadMode) {
			System.arraycopy(macPadMode, 0, sMacPadMode, 0, macPadMode.length);
		}
	}
	
	public static class RP_MSG_N7 extends Structure {
		public byte[] sPinLen = new byte[3];       // ����PIN����
		public byte[] sPinBlockLen = new byte[3];  // ZPK���ܵ�PIN���ĳ��� 
		public byte[] sPinBlock = new byte[129];   // ZPK���ܵ�PIN����
		public byte[] sIDLen = new byte[3];        // ID�볤��
		public byte[] sID = new byte[65];          // ID��
		public byte[] sMac = new byte[33];         // ���ݿ�MACֵ
	    
	    public static class ByReference extends RP_MSG_N7 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_N7 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinLen", "sPinBlockLen", 
            		"sPinBlock", "sIDLen", "sID", "sMac"});
        }

		public byte[] getsPinLen() {
			return DJHsmUtils.getHexByte(sPinLen);
		}

		public byte[] getsPinBlockLen() {
			return DJHsmUtils.getHexByte(sPinBlockLen);
		}

		public byte[] getsPinBlock() {
			String lenStr = new String(getsPinBlockLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10) * 2;
			return DJHsmUtils.getData(sPinBlock, len);
		}

		public byte[] getsIDLen() {
			return DJHsmUtils.getHexByte(sIDLen);
		}

		public byte[] getsID() {
			String lenStr = new String(getsIDLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10) * 2;
			return DJHsmUtils.getData(sID, len);
		}

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	    
	}
	
	public static class RQ_MSG_N8 extends Structure {
		public byte[] sPubAlgFlag = new byte[3];     // ��Կ�㷨��ʶ
		public byte[] sSIndex = new byte[5];         // ˽Կ����
		public byte[] sSKeyLen = new byte[5];        // ˽Կ����
		public byte[] sSKey = new byte[2500];        // ˽Կ 
	    public byte[] sPubKeyPinForm = new byte[3];  // ��Կ����PIN��ɸ�ʽ
	    public byte[] sPadMode = new byte[3];        // ��Կ���ܵ����ģʽ
	    public byte[] sZpkKey = new byte[50];        // ZPK��Կ
	    public byte[] sZpkPinForm = new byte[3];     // PINBLOCK��ʽ
	    public byte[] sAccNo = new byte[25];         // �˺�PAN
	    public byte[] sPinBlockLen = new byte[5];    // ��Կ���ܵ�PIN���ĳ���
	    public byte[] sPinBlock = new byte[2048];    // ��Կ���ܵ�PIN����
	    
	    public static class ByReference extends RQ_MSG_N8 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_N8 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPubAlgFlag", 
            		"sSIndex", "sSKeyLen", "sSKey",
            		"sPubKeyPinForm", "sPadMode", "sZpkKey",
            		"sZpkPinForm", "sAccNo", "sPinBlockLen",
            		"sPinBlock"});
        }

		public void setsPubAlgFlag(byte[] pubAlgFlag) {
			System.arraycopy(pubAlgFlag, 0, sPubAlgFlag, 0, pubAlgFlag.length);
		}

		public void setsSIndex(byte[] index) {
			System.arraycopy(index, 0, sSIndex, 0, index.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}

		public void setsPubKeyPinForm(byte[] pubKeyPinForm) {
			System.arraycopy(pubKeyPinForm, 0, sPubKeyPinForm, 0, pubKeyPinForm.length);
		}

		public void setsPadMode(byte[] padMode) {
			System.arraycopy(padMode, 0, sPadMode, 0, padMode.length);
		}

		public void setsZpkKey(byte[] zpkKey) {
			System.arraycopy(zpkKey, 0, sZpkKey, 0, zpkKey.length);
		}

		public void setsZpkPinForm(byte[] zpkPinForm) {
			System.arraycopy(zpkPinForm, 0, sZpkPinForm, 0, zpkPinForm.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsPinBlockLen(byte[] pinBlockLen) {
			System.arraycopy(pinBlockLen, 0, sPinBlockLen, 0, pinBlockLen.length);
		}

		public void setsPinBlock(byte[] pinBlock) {
			System.arraycopy(pinBlock, 0, sPinBlock, 0, pinBlock.length);
		}
	}
	
	public static class RP_MSG_N9 extends Structure {
		public byte[] sPinLen = new byte[3];       // ����PIN����
		public byte[] sPinBlockLen = new byte[3];  // ZPK���ܵ�PIN���ĳ���
		public byte[] sPinBlock = new byte[33];    // ZPK���ܵ�PIN����
		public byte[] sIDLen = new byte[3];        // ID�볤��
		public byte[] sID = new byte[65];          // ID��
	    
	    public static class ByReference extends RP_MSG_N9 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_N9 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinLen", "sPinBlockLen", 
            		"sPinBlock", "sIDLen", "sID"});
        }

		public byte[] getsPinLen() {
			return DJHsmUtils.getHexByte(sPinLen);
		}

		public byte[] getsPinBlockLen() {
			return DJHsmUtils.getHexByte(sPinBlockLen);
		}

		public byte[] getsPinBlock() {
			String lenStr = new String(getsPinBlockLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10) * 2;
			return DJHsmUtils.getData(sPinBlock, len);
		}

		public byte[] getsIDLen() {
			return DJHsmUtils.getHexByte(sIDLen);
		}

		public byte[] getsID() {
			String lenStr = new String(getsIDLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10) * 2;
			return DJHsmUtils.getData(sID, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_CB extends Structure {
		public byte[] sSrcZpkKey = new byte[50];   // ԴZPK��Կ
		public byte[] sDesKeyType = new byte[3];   // Ŀ��PIN������Կ����
		public byte[] sDesKey = new byte[50];      // Ŀ��PIN������Կ
		public byte[] sPinLen = new byte[3];       // ���PIN����
		public byte[] sPinBlock = new byte[33];    // ԴPINBLOCK����
		public byte[] sPinForm = new byte[3];      // ԴPINBLOCK��ʽ
		public byte[] sAccNo = new byte[19];       // ԴPINBLOCK�����˺�
		public byte[] sAlgFlag = new byte[3];      // ˽��PIN�����㷨��ʶ
	    public byte[] sDate = new byte[9];         // ����
	    
	    public static class ByReference extends RQ_MSG_CB 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_CB 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSrcZpkKey", 
            		"sDesKeyType", "sDesKey", "sPinLen",
            		"sPinBlock", "sPinForm", "sAccNo",
            		"sAlgFlag", "sDate"});
        }

		public void setsSrcZpkKey(byte[] srcZpkKey) {
			System.arraycopy(srcZpkKey, 0, sSrcZpkKey, 0, srcZpkKey.length);
		}

		public void setsDesKeyType(byte[] desKeyType) {
			System.arraycopy(desKeyType, 0, sDesKeyType, 0, desKeyType.length);
		}

		public void setsDesKey(byte[] desKey) {
			System.arraycopy(desKey, 0, sDesKey, 0, desKey.length);
		}

		public void setsPinLen(byte[] pinLen) {
			System.arraycopy(pinLen, 0, sPinLen, 0, pinLen.length);
		}

		public void setsPinBlock(byte[] pinBlock) {
			System.arraycopy(pinBlock, 0, sPinBlock, 0, pinBlock.length);
		}

		public void setsPinForm(byte[] pinForm) {
			System.arraycopy(pinForm, 0, sPinForm, 0, pinForm.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsAlgFlag(byte[] algFlag) {
			System.arraycopy(algFlag, 0, sAlgFlag, 0, algFlag.length);
		}

		public void setsDate(byte[] date) {
			System.arraycopy(date, 0, sDate, 0, date.length);
		}
	}
		
	public static class RP_MSG_CC extends Structure {
		public byte[] sPinLen = new byte[3];       // PIN����
		public byte[] sPinBlock = new byte[33];    // Ŀ��PINBLOCK����
	    
	    public static class ByReference extends RP_MSG_CC 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_CC 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinLen", "sPinBlock"});
        }

		public byte[] getsPinLen() {
			return DJHsmUtils.getHexByte(sPinLen);
		}

		public byte[] getsPinBlock() {
			String lenStr = new String(getsPinLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sPinBlock, len);
		}
	}
	
	//PIN��֤
	public static class RQ_MSG_DE extends Structure {
		public byte[] sPVK = new byte[50];         // PVK����
		public byte[] sPinByLmk = new byte[14];    // PIN����
		public byte[] sPinCVLen = new byte[3];     // PINУ�鳤��
		public byte[] sAccNo = new byte[13];       // �˺�
		public byte[] sTable = new byte[17];       // ʮ����ת����
		public byte[] sPinCV = new byte[13];       // PINУ������
	    
	    public static class ByReference extends RQ_MSG_DE 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_DE 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPVK", 
            		"sPinByLmk", "sPinCVLen", "sAccNo",
            		"sTable", "sPinCV"});
        }

		public void setsPVK(byte[] pvk) {
			System.arraycopy(pvk, 0, sPVK, 0, pvk.length);
		}

		public void setsPinByLmk(byte[] pinByLmk) {
			System.arraycopy(pinByLmk, 0, sPinByLmk, 0, pinByLmk.length);
		}

		public void setsPinCVLen(byte[] pinCVLen) {
			System.arraycopy(pinCVLen, 0, sPinCVLen, 0, pinCVLen.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsTable(byte[] table) {
			System.arraycopy(table, 0, sTable, 0, table.length);
		}

		public void setsPinCV(byte[] pinCV) {
			System.arraycopy(pinCV, 0, sPinCV, 0, pinCV.length);
		}
	}
	
	public static class RP_MSG_DF extends Structure {
		public byte[] sPinOffset = new byte[13];   // ƫ����
	    
	    public static class ByReference extends RP_MSG_DF 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_DF 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinOffset"});
        }

		public byte[] getsPinOffset() {
			return DJHsmUtils.getHexByte(sPinOffset);
		}
	    
	    
	}
	
	public static class RQ_MSG_EE extends Structure {
		public byte[] sPVK = new byte[50];         // PVK����������
		public byte[] sPinOffset = new byte[13];   // Offset
		public byte[] sPinCVLen = new byte[3];     // ��鳤��
		public byte[] sAccNo = new byte[13];       // �˺�
		public byte[] sTable = new byte[17];       // ʮ����ת���� 
		public byte[] sPinCV = new byte[13];       // PINУ������
	    
	    public static class ByReference extends RQ_MSG_EE 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_EE 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPVK", 
            		"sPinOffset", "sPinCVLen", "sAccNo",
            		"sTable", "sPinCV"});
        }

		public void setsPVK(byte[] pvk) {
			System.arraycopy(pvk, 0, sPVK, 0, pvk.length);
		}

		public void setsPinOffset(byte[] pinOffset) {
			System.arraycopy(pinOffset, 0, sPinOffset, 0, pinOffset.length);
		}

		public void setsPinCVLen(byte[] pinCVLen) {
			System.arraycopy(pinCVLen, 0, sPinCVLen, 0, pinCVLen.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsTable(byte[] table) {
			System.arraycopy(table, 0, sTable, 0, table.length);
		}

		public void setsPinCV(byte[] pinCV) {
			System.arraycopy(pinCV, 0, sPinCV, 0, pinCV.length);
		}
	}
	
	public static class RP_MSG_EF extends Structure {
		public byte[] sPinByLmk = new byte[14];    // PIN
	    
	    public static class ByReference extends RP_MSG_EF 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_EF 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinByLmk"});
        }

		public byte[] getsPinByLmk() {
			return DJHsmUtils.getHexByte(sPinByLmk);
		}
	    
	    
	}
	
	public static class RQ_MSG_DA extends Structure {
		public byte[] sTPK = new byte[50];         // TPK
		public byte[] sPVK = new byte[50];         // PVK����
		public byte[] sPinLen = new byte[3];       // PIN��󳤶�
		public byte[] sPinByTpk = new byte[33];    // PIN����
		public byte[] sPinForm = new byte[3];      // PINBLOCK��ʽ����
		public byte[] sPinCVLen = new byte[3];     // ��鳤��
		public byte[] sAccNo = new byte[19];       // �˺�
		public byte[] sTable = new byte[17];       // ʮ����ת����
		public byte[] sPinCV = new byte[13];       // PINУ������
		public byte[] sPinOffset = new byte[13];   // PIN OFFSET
	    
	    public static class ByReference extends RQ_MSG_DA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_DA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sTPK", 
            		"sPVK", "sPinLen", "sPinByTpk",
            		"sPinForm", "sPinCVLen", "sAccNo",
            		"sTable", "sPinCV", "sPinOffset"});
        }

		public void setsTPK(byte[] tpk) {
			System.arraycopy(tpk, 0, sTPK, 0, tpk.length);
		}

		public void setsPVK(byte[] pvk) {
			System.arraycopy(pvk, 0, sPVK, 0, pvk.length);
		}

		public void setsPinLen(byte[] pinLen) {
			System.arraycopy(pinLen, 0, sPinLen, 0, pinLen.length);
		}

		public void setsPinByTpk(byte[] pinByTpk) {
			System.arraycopy(pinByTpk, 0, sPinByTpk, 0, pinByTpk.length);
		}

		public void setsPinForm(byte[] pinForm) {
			System.arraycopy(pinForm, 0, sPinForm, 0, pinForm.length);
		}

		public void setsPinCVLen(byte[] pinCVLen) {
			System.arraycopy(pinCVLen, 0, sPinCVLen, 0, pinCVLen.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsTable(byte[] table) {
			System.arraycopy(table, 0, sTable, 0, table.length);
		}

		public void setsPinCV(byte[] pinCV) {
			System.arraycopy(pinCV, 0, sPinCV, 0, pinCV.length);
		}

		public void setsPinOffset(byte[] pinOffset) {
			System.arraycopy(pinOffset, 0, sPinOffset, 0, pinOffset.length);
		}
	}
		
	public static class RQ_MSG_EA extends Structure {
		public byte[] sZPK = new byte[50];         // ZPK
		public byte[] sPVK = new byte[50];         // PVK����
		public byte[] sPinLen = new byte[3];       // PIN��󳤶�
		public byte[] sPinByZpk = new byte[33];    // PIN����
		public byte[] sPinForm = new byte[3];      // PIN��ʽ����
		public byte[] sPinCVLen = new byte[3];     // ��鳤��
		public byte[] sAccNo = new byte[19];       // �˺�
		public byte[] sTable = new byte[17];       // ʮ����ת����
		public byte[] sPinCV = new byte[13];       // PINУ������
		public byte[] sPinOffset = new byte[13];   // PIN OFFSET
	    
	    public static class ByReference extends RQ_MSG_EA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_EA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sZPK", 
            		"sPVK", "sPinLen", "sPinByZpk",
            		"sPinForm", "sPinCVLen", "sAccNo",
            		"sTable", "sPinCV", "sPinOffset"});
        }

		public void setsZPK(byte[] szpk) {
			System.arraycopy(szpk, 0, sZPK, 0, szpk.length);
		}

		public void setsPVK(byte[] pvk) {
			System.arraycopy(pvk, 0, sPVK, 0, pvk.length);
		}

		public void setsPinLen(byte[] pinLen) {
			System.arraycopy(pinLen, 0, sPinLen, 0, pinLen.length);
		}

		public void setsPinByZpk(byte[] pinByZpk) {
			System.arraycopy(pinByZpk, 0, sPinByZpk, 0, pinByZpk.length);
		}

		public void setsPinForm(byte[] pinForm) {
			System.arraycopy(pinForm, 0, sPinForm, 0, pinForm.length);
		}

		public void setsPinCVLen(byte[] pinCVLen) {
			System.arraycopy(pinCVLen, 0, sPinCVLen, 0, pinCVLen.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsTable(byte[] table) {
			System.arraycopy(table, 0, sTable, 0, table.length);
		}

		public void setsPinCV(byte[] pinCV) {
			System.arraycopy(pinCV, 0, sPinCV, 0, pinCV.length);
		}

		public void setsPinOffset(byte[] pinOffset) {
			System.arraycopy(pinOffset, 0, sPinOffset, 0, pinOffset.length);
		}
	}
		
	public static class RQ_MSG_DG extends Structure {
		public byte[] sPVK = new byte[50];         // PVK����
		public byte[] sPinByLmk = new byte[14];    // PIN����
		public byte[] sAccNo = new byte[13];       // �˺�
		public byte[] sPVKI = new byte[2];         // PVKI
	    
	    public static class ByReference extends RQ_MSG_DG 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_DG 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPVK", 
            		"sPinByLmk", "sAccNo", "sPVKI"});
        }

		public void setsPVK(byte[] pvk) {
			System.arraycopy(pvk, 0, sPVK, 0, pvk.length);
		}

		public void setsPinByLmk(byte[] pinByLmk) {
			System.arraycopy(pinByLmk, 0, sPinByLmk, 0, pinByLmk.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsPVKI(byte[] pvki) {
			System.arraycopy(pvki, 0, sPVKI, 0, pvki.length);
		}
	}

	public static class RP_MSG_DH extends Structure {
		public byte[] sPVV = new byte[5];          // PVV
	    
	    public static class ByReference extends RP_MSG_DH 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_DH 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPVV"});
        }

		public byte[] getsPVV() {
			return DJHsmUtils.getHexByte(sPVV);
		}
	    
	    
	}
	
	//CVV����		
	public static class RQ_MSG_CW extends Structure {
		public byte[] sCvkKey = new byte[34];      // CVK  A/B
		public byte[] sAccNo = new byte[26];       // ���˺�
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sValidDate = new byte[5];    // ����ʱ�� 9301����93��1��
		public byte[] sServeNo = new byte[4];      // ������
	    
	    public static class ByReference extends RQ_MSG_CW 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_CW 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCvkKey", 
            		"sAccNo", "sSeparator", "sValidDate",
            		"sServeNo"});
        }

		public void setsCvkKey(byte[] cvkKey) {
			System.arraycopy(cvkKey, 0, sCvkKey, 0, cvkKey.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsValidDate(byte[] validDate) {
			System.arraycopy(validDate, 0, sValidDate, 0, validDate.length);
		}

		public void setsServeNo(byte[] serveNo) {
			System.arraycopy(serveNo, 0, sServeNo, 0, serveNo.length);
		}
	}
	
	public static class RP_MSG_CX extends Structure {
		public byte[] sCVV = new byte[4];          // CVV
	    
	    public static class ByReference extends RP_MSG_CX 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_CX 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCVV"});
        }

		public byte[] getsCVV() {
			return DJHsmUtils.getHexByte(sCVV);
		}
	    
	    
	}
	
	public static class RQ_MSG_CY extends Structure {
		public byte[] sCvkKey = new byte[34];      // CVK  A/B
		public byte[] sCVV = new byte[4];          // CVV
		public byte[] sAccNo = new byte[26];       // ���˺�
		public byte[] sSeparator = new byte[2];    // �ָ���
		public byte[] sValidDate = new byte[5];    // ��Ч��
		public byte[] sServeNo = new byte[4];      // ������
	    
	    public static class ByReference extends RQ_MSG_CY 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_CY 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCvkKey", 
            		"sCVV", "sAccNo", "sSeparator",
            		"sValidDate", "sServeNo"});
        }

		public void setsCvkKey(byte[] cvkKey) {
			System.arraycopy(cvkKey, 0, sCvkKey, 0, cvkKey.length);
		}

		public void setsCVV(byte[] cvv) {
			System.arraycopy(cvv, 0, sCVV, 0, cvv.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsValidDate(byte[] validDate) {
			System.arraycopy(validDate, 0, sValidDate, 0, validDate.length);
		}

		public void setsServeNo(byte[] serveNo) {
			System.arraycopy(serveNo, 0, sServeNo, 0, serveNo.length);
		}
	}

	//���ݼӽ���	
	public static class RQ_MSG_E0 extends Structure {
		public byte[] sBlockFlag = new byte[2];      // ���Ŀ��ʶ
		public byte[] sAlgFlag = new byte[2];        // �����ʶ
		public byte[] sAlgMode = new byte[2];        // �㷨ģʽ
		public byte[] sKeyType = new byte[2];        // ��Կ����
		public byte[] sKey = new byte[50];           // ��Կ����
		public byte[] sInDataForm = new byte[2];     // �������ݸ�ʽ
		public byte[] sOutDataForm = new byte[2];    // ������ݸ�ʽ
		public byte[] sPadMode = new byte[2];        // Pad ģʽ
		public byte[] sPadChars = new byte[5];       // Pad �ַ� (Pad character)
		public byte[] sPadCountFlag = new byte[2];   // Pad ������ʶ ��Pad count flag��
		public byte[] sIV = new byte[33];            // IV
	    public byte[] sDataLen = new byte[4];        // ���ݳ���
	    public byte[] sData = new byte[4096];        // ���ݿ�
	    
	    public static class ByReference extends RQ_MSG_E0 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_E0 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sBlockFlag", 
            		"sAlgFlag", "sAlgMode", "sKeyType",
            		"sKey", "sInDataForm", "sOutDataForm",
            		"sPadMode", "sPadChars", "sPadCountFlag",
            		"sIV", "sDataLen", "sData"});
        }

		public void setsBlockFlag(byte[] blockFlag) {
			System.arraycopy(blockFlag, 0, sBlockFlag, 0, blockFlag.length);
		}

		public void setsAlgFlag(byte[] algFlag) {
			System.arraycopy(algFlag, 0, sAlgFlag, 0, algFlag.length);
		}

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsInDataForm(byte[] inDataForm) {
			System.arraycopy(inDataForm, 0, sInDataForm, 0, inDataForm.length);
		}

		public void setsOutDataForm(byte[] outDataForm) {
			System.arraycopy(outDataForm, 0, sOutDataForm, 0, outDataForm.length);
		}

		public void setsPadMode(byte[] padMode) {
			System.arraycopy(padMode, 0, sPadMode, 0, padMode.length);
		}

		public void setsPadChars(byte[] padChars) {
			System.arraycopy(padChars, 0, sPadChars, 0, padChars.length);
		}

		public void setsPadCountFlag(byte[] padCountFlag) {
			System.arraycopy(padCountFlag, 0, sPadCountFlag, 0, padCountFlag.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}
	}
	
	public static class RP_MSG_E1 extends Structure {
		public byte[] sOutDataForm = new byte[2];  // ������ݵĸ�ʽ
		public byte[] sOutDataLen = new byte[4];   // ������ݳ���
		public byte[] sOutData = new byte[4096];   // �������
		public byte[] sNextIv = new byte[33];      // Next IV
	    
	    public static class ByReference extends RP_MSG_E1 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_E1 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sOutDataForm", "sOutDataLen", 
            		"sOutData", "sNextIv"});
        }

		public byte[] getsOutDataForm() {
			return DJHsmUtils.getHexByte(sOutDataForm);
		}

		public byte[] getsOutDataLen() {
			return DJHsmUtils.getHexByte(sOutDataLen);
		}

		public byte[] getsOutData() {
			//���� ������ݵĸ�ʽ�жϳ���  0: binary:n  ;  1: hex:2n
			String formStr = new String(getsOutDataForm());
			if (formStr.equalsIgnoreCase("")) {
				return null;
			}
			int form = Integer.parseInt(DJHsmUtils.isNumeric(formStr, 10) ? formStr : "0", 10);
			int times = 1;
			if (form == 0) {
				times = 1;
			} else if (form == 1) {
				times = 2;
			}
			String lenStr = new String(getsOutDataLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 16) ? lenStr : "0", 16) * times;
			return DJHsmUtils.getData(sOutData, len);
		}

		public byte[] getsNextIv() {
			return DJHsmUtils.getHexByte(sNextIv);
		}
	    
	    
	}

	//�ź���ӡ		
	public static class RQ_MSG_PA extends Structure {
		public byte[] sFormatData = new byte[5120]; // ��ʽ����
	    
	    public static class ByReference extends RQ_MSG_PA 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_PA 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sFormatData"});
        }

		public void setsFormatData(byte[] formatData) {
			System.arraycopy(formatData, 0, sFormatData, 0, formatData.length);
		}
	}
		
	public static class PRINTFIELD extends Structure {
		public byte[] sPrintField = new byte[15];
	    
	    public static class ByReference extends PRINTFIELD 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends PRINTFIELD 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPrintField"});
        }

		public void setsPrintField(byte[] printField) {
			System.arraycopy(printField, 0, sPrintField, 0, printField.length);
		}
	}	
	
	public static class RQ_MSG_PE extends Structure {
		public byte[] sDocType = new byte[2];      // �ĵ�����
		public byte[] sAccNo = new byte[13];       // �˺�
		public byte[] sPinByLmk = new byte[14];    // PIN
	    public byte[] sSeparator = new byte[2];    // �ָ���
	    public PRINTFIELD.ByValue[] printField; // ��ӡ��
	    
	    public RQ_MSG_PE(){
	    	PRINTFIELD.ByValue tdata = new PRINTFIELD.ByValue();
	    	printField = (PRINTFIELD.ByValue[])tdata.toArray(5120);
	    }
	    
	    public static class ByReference extends RQ_MSG_PE 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_PE 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sDocType", 
            		"sAccNo", "sPinByLmk", "sSeparator",
            		"printField"});
        }

		public void setsDocType(byte[] docType) {
			System.arraycopy(docType, 0, sDocType, 0, docType.length);
		}

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsPinByLmk(byte[] pinByLmk) {
			System.arraycopy(pinByLmk, 0, sPinByLmk, 0, pinByLmk.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}
	}

	public static class RP_MSG_PF extends Structure {
		public byte[] sPinCV = new byte[26];       // PIN&�ο������ֵ
	    
	    public static class ByReference extends RP_MSG_PF 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_PF 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPinCV"});
        }

		public byte[] getsPinCV() {
			return DJHsmUtils.getHexByte(sPinCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_NE extends Structure {
		public byte[] sKeyType = new byte[4];            // ��Կ����
		public byte[] sKeyFlag = new byte[2];            // ��Կ��ʶ(LMK) 
		public byte[] sSeparator = new byte[2];          // �ָ���
		public PRINTFIELD.ByValue[] printField;          // ��ӡ��
	    
		public RQ_MSG_NE(){
		    PRINTFIELD.ByValue tdata = new PRINTFIELD.ByValue();
		    printField = (PRINTFIELD.ByValue[])tdata.toArray(5120);
		}
		 
	    public static class ByReference extends RQ_MSG_NE 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_NE 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", 
            		"sKeyFlag", "sSeparator", "printField"});
        }

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKeyFlag(byte[] keyFlag) {
			System.arraycopy(keyFlag, 0, sKeyFlag, 0, keyFlag.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}
	}
	
	public static class RP_MSG_NF extends Structure {
		public byte[] sKeybyLmk = new byte[50];    // ��Կ����(LMK)
		public byte[] sKeyCV = new byte[9];        // ��ԿУ��ֵ
	    
	    public static class ByReference extends RP_MSG_NF 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_NF 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyLmk", "sKeyCV"});
        }

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_A2 extends Structure {
		public byte[] sKeyType = new byte[4];           // ��Կ����
		public byte[] sKeyFlag = new byte[2];           // ��Կ��ʶ(LMK)
		public byte[] sSeparator = new byte[2];         // �ָ��� 
		public PRINTFIELD.ByValue[] printField;         // ��ӡ��
	    
		public RQ_MSG_A2(){
		    PRINTFIELD.ByValue tdata = new PRINTFIELD.ByValue();
		    printField = (PRINTFIELD.ByValue[])tdata.toArray(5120);
		}
		
	    public static class ByReference extends RQ_MSG_A2 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_A2 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", 
            		"sKeyFlag", "sSeparator", "printField"});
        }

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKeyFlag(byte[] keyFlag) {
			System.arraycopy(keyFlag, 0, sKeyFlag, 0, keyFlag.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}
	}
	
	public static class RP_MSG_A3 extends Structure {
		public byte[] sKeybyLmk = new byte[50];    // ��Կ����(LMK)
		public byte[] sKeyCV = new byte[9];        // ��ԿУ��ֵ
	    
	    public static class ByReference extends RP_MSG_A3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_A3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyLmk", "sKeyCV"});
        }

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}

	public static class RQ_MSG_PG extends Structure {
		public byte[] sAccNo = new byte[13];       // �˺�
		public byte[] sPinByLmk = new byte[14];    // PIN
		public byte[] sPinCV = new byte[26];       // PIN&�ο������ֵ
	    
	    public static class ByReference extends RQ_MSG_PG 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_PG 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAccNo", 
            		"sPinByLmk", "sPinCV"});
        }

		public void setsAccNo(byte[] accNo) {
			System.arraycopy(accNo, 0, sAccNo, 0, accNo.length);
		}

		public void setsPinByLmk(byte[] pinByLmk) {
			System.arraycopy(pinByLmk, 0, sPinByLmk, 0, pinByLmk.length);
		}

		public void setsPinCV(byte[] pinCV) {
			System.arraycopy(pinCV, 0, sPinCV, 0, pinCV.length);
		}
	}
	
	public static class RP_MSG_ND extends Structure {
		public byte[] sDMKCV = new byte[17];       // ��ԿУ��ֵ(KCV) 
		public byte[] sMainVerNo = new byte[21];   // ��������汾��Ϣ
		public byte[] sDevVerNo = new byte[21];    // �������汾��Ϣ
		public byte[] sAppVerNo = new byte[21];    // ����ģ��汾��Ϣ
		public byte[] sDevSerNo = new byte[21];    // �豸���к�
	    
	    public static class ByReference extends RP_MSG_ND 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_ND 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sDMKCV", "sMainVerNo", 
            		"sDevVerNo", "sAppVerNo", "sDevSerNo"});
        }

	    //TODO �ĵ���ֱ��ָ��Ϊ��16,7,7,7,13 B
		public byte[] getsDMKCV() {
			return DJHsmUtils.getHexByte(sDMKCV);
		}

		public byte[] getsMainVerNo() {
			if (DJHsmUtils.isEmpty(sMainVerNo)) {
				return null;
			}
			return DJHsmUtils.getData(sMainVerNo, 7);
		}

		public byte[] getsDevVerNo() {
			if (DJHsmUtils.isEmpty(sDevVerNo)) {
				return null;
			}
			return DJHsmUtils.getData(sDevVerNo, 7);
		}

		public byte[] getsAppVerNo() {
			if (DJHsmUtils.isEmpty(sAppVerNo)) {
				return null;
			}
			return DJHsmUtils.getData(sAppVerNo, 7);
		}

		public byte[] getsDevSerNo() {
			if (DJHsmUtils.isEmpty(sDevSerNo)) {
				return null;
			}
			return DJHsmUtils.getData(sDevSerNo, 13);
		}
	    
	}
	
	public static class RQ_MSG_F3 extends Structure {
		public byte[] sKeyType = new byte[4];         // ����Կ����
		public byte[] sKey = new byte[50];            // ����Կ
		public byte[] sScatterLvl1 = new byte[2+1];   // ��Կ��ɢ����
		public byte[] sFactor1 = new byte[8*32+1];    // ��Կ��ɢ����
		public byte[] sMacAlgMode1 = new byte[3];     // MAC�㷨ģʽ
		public byte[] sScatterLvl2 = new byte[2+1];   // ��Կ��ɢ����
		public byte[] sFactor2 = new byte[8*32+1];    // ��Կ��ɢ����
		public byte[] sMacAlgMode2 = new byte[3];     // MAC�㷨ģʽ
		public byte[] sIVGenType = new byte[2];       // IV������ʽ
		public byte[] sIV = new byte[33];             // IV
		public byte[] sSeedLen = new byte[4];         // ���ӳ���
		public byte[] sPadFlag = new byte[3];         // PAD��ʶ
	    
	    public static class ByReference extends RQ_MSG_F3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_F3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", 
            		"sKey", "sScatterLvl1", "sFactor1",
            		"sMacAlgMode1", "sScatterLvl2", "sFactor2",
            		"sMacAlgMode2", "sIVGenType", "sIV",
            		"sSeedLen", "sPadFlag"});
        }

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsScatterLvl1(byte[] scatterLvl1) {
			System.arraycopy(scatterLvl1, 0, sScatterLvl1, 0, scatterLvl1.length);
		}

		public void setsFactor1(byte[] factor1) {
			System.arraycopy(factor1, 0, sFactor1, 0, factor1.length);
		}

		public void setsMacAlgMode1(byte[] macAlgMode1) {
			System.arraycopy(macAlgMode1, 0, sMacAlgMode1, 0, macAlgMode1.length);
		}

		public void setsScatterLvl2(byte[] scatterLvl2) {
			System.arraycopy(scatterLvl2, 0, sScatterLvl2, 0, scatterLvl2.length);
		}

		public void setsFactor2(byte[] factor2) {
			System.arraycopy(factor2, 0, sFactor2, 0, factor2.length);
		}

		public void setsMacAlgMode2(byte[] macAlgMode2) {
			System.arraycopy(macAlgMode2, 0, sMacAlgMode2, 0, macAlgMode2.length);
		}

		public void setsIVGenType(byte[] genType) {
			System.arraycopy(genType, 0, sIVGenType, 0, genType.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsSeedLen(byte[] seedLen) {
			System.arraycopy(seedLen, 0, sSeedLen, 0, seedLen.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}
	}
	
	public static class RP_MSG_F4 extends Structure {
		public byte[] sIV = new byte[33];          // IV
		public byte[] sKeyLen1 = new byte[5];      // ��һ����Կ���ܵ��������ĳ���
		public byte[] sKey1 = new byte[2001];      // ��һ����Կ���ܵ���������
		public byte[] sMac1 = new byte[33];        // ��һ����MACֵ
		public byte[] sKeyLen2 = new byte[5];      // �ڶ�����Կ���ܵ��������ĳ���
		public byte[] sKey2 = new byte[2001];      // �ڶ�����Կ���ܵ���������
		public byte[] sMac2 = new byte[33];        // �ڶ�����ԿMACֵ
	    
	    public static class ByReference extends RP_MSG_F4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_F4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sIV", "sKeyLen1", 
            		"sKey1", "sMac1", "sKeyLen2", "sKey2", 
            		"sMac2"});
        }

		public byte[] getsIV() {
			return DJHsmUtils.getHexByte(sIV);
		}

		public byte[] getsKeyLen1() {
			return DJHsmUtils.getHexByte(sKeyLen1);
		}

		public byte[] getsKey1() {
			String lenStr = new String(getsKeyLen1());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10) * 2;
			return DJHsmUtils.getData(sKey1, len);
		}

		public byte[] getsMac1() {
			return DJHsmUtils.getHexByte(sMac1);
		}

		public byte[] getsKeyLen2() {
			return DJHsmUtils.getHexByte(sKeyLen2);
		}

		public byte[] getsKey2() {
			String lenStr = new String(getsKeyLen2());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10) * 2;
			return DJHsmUtils.getData(sKey2, len);
		}

		public byte[] getsMac2() {
			return DJHsmUtils.getHexByte(sMac2);
		}
	    
	    
	}
	
	public static class RQ_MSG_F4 extends Structure {
		public byte[] sKeyType = new byte[4];         // ����Կ����
		public byte[] sKey = new byte[50];            // ����Կ
		public byte[] sScatterLvl = new byte[2+1];    // ��ɢ����
		public byte[] sFactor = new byte[8*32+1];     // ��ɢ����
		public byte[] sMacAlgMode = new byte[3];      // MAC�㷨ģʽ
		public byte[] sIV = new byte[33];             // IV
		public byte[] sSeedLen = new byte[4];         // �������ӳ���
		public byte[] sSeed = new byte[2048];         // ��������
		public byte[] sMac = new byte[33];            // ����MAC
		public byte[] sPadFlag = new byte[3];         // PAD��ʶ
		public byte[] sPlainMacAlgMode = new byte[3]; // ��������MAC�㷨ģʽ
		public byte[] sPlainIV = new byte[33];        // IV
	    
	    public static class ByReference extends RQ_MSG_F4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_F4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", 
            		"sKey", "sScatterLvl", "sFactor",
            		"sMacAlgMode", "sIV", "sSeedLen",
            		"sSeed", "sMac", "sPadFlag",
            		"sPlainMacAlgMode", "sPlainIV"});
        }

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsMacAlgMode(byte[] macAlgMode) {
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsSeedLen(byte[] seedLen) {
			System.arraycopy(seedLen, 0, sSeedLen, 0, seedLen.length);
		}

		public void setsSeed(byte[] seed) {
			System.arraycopy(seed, 0, sSeed, 0, seed.length);
		}

		public void setsMac(byte[] mac) {
			System.arraycopy(mac, 0, sMac, 0, mac.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsPlainMacAlgMode(byte[] plainMacAlgMode) {
			System.arraycopy(plainMacAlgMode, 0, sPlainMacAlgMode, 0, plainMacAlgMode.length);
		}

		public void setsPlainIV(byte[] plainIV) {
			System.arraycopy(plainIV, 0, sPlainIV, 0, plainIV.length);
		}
	}
	
	public static class RP_MSG_F5 extends Structure {
		public byte[] sSeedLen = new byte[4];      // �������ӳ���
		public byte[] sSeed = new byte[2048];      // ��������
		public byte[] sMac = new byte[33];         // MACֵ
	    
	    public static class ByReference extends RP_MSG_F5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_F5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSeedLen", "sSeed", 
            		"sMac"});
        }

		public byte[] getsSeedLen() {
			return DJHsmUtils.getHexByte(sSeedLen);
		}

		public byte[] getsSeed() {
			String lenStr = new String(getsSeedLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10) * 2;
			return DJHsmUtils.getData(sSeed, len);
		}

		public byte[] getsMac() {
			return DJHsmUtils.getHexByte(sMac);
		}
	    
	}
	
	public static class RQ_MSG_F5 extends Structure {
		public byte[] sKeyType = new byte[4];      // ����Կ����
		public byte[] sKey = new byte[50];         // ����Կ
		public byte[] sScatterLvl = new byte[2+1]; // ��ɢ����
		public byte[] sFactor = new byte[8*32+1];  // ��ɢ����
		public byte[] sMacAlgMode = new byte[3];   // MAC�㷨ģʽ
	    public byte[] sIV = new byte[33];          // IV
	    public byte[] sSeedLen = new byte[4];      // �������ӳ���
	    public byte[] sSeed = new byte[2048];      // ��������
	    public byte[] sMac = new byte[33];         // ����MAC 
	    public byte[] sPadFlag = new byte[3];      // PAD��ʶ
	    public byte[] sIDLen = new byte[4];        // ID����
	    public byte[] sID = new byte[49];          // IDֵ
	    public byte[] sHashAlgFlag = new byte[3];  // OTP�Ӵ��㷨��ʶ
	    public byte[] sOTPLen = new byte[3];       // ���OTP����
	    
	    public static class ByReference extends RQ_MSG_F5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_F5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyType", 
            		"sKey", "sScatterLvl", "sFactor",
            		"sMacAlgMode", "sIV", "sSeedLen",
            		"sSeed", "sMac", "sPadFlag",
            		"sIDLen", "sID", "sHashAlgFlag",
            		"sOTPLen"});
        }

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKey(byte[] key) {
			System.arraycopy(key, 0, sKey, 0, key.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsMacAlgMode(byte[] macAlgMode) {
			System.arraycopy(macAlgMode, 0, sMacAlgMode, 0, macAlgMode.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsSeedLen(byte[] seedLen) {
			System.arraycopy(seedLen, 0, sSeedLen, 0, seedLen.length);
		}

		public void setsSeed(byte[] seed) {
			System.arraycopy(seed, 0, sSeed, 0, seed.length);
		}

		public void setsMac(byte[] mac) {
			System.arraycopy(mac, 0, sMac, 0, mac.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsIDLen(byte[] len) {
			System.arraycopy(len, 0, sIDLen, 0, len.length);
		}

		public void setsID(byte[] id) {
			System.arraycopy(id, 0, sID, 0, id.length);
		}

		public void setsHashAlgFlag(byte[] hashAlgFlag) {
			System.arraycopy(hashAlgFlag, 0, sHashAlgFlag, 0, hashAlgFlag.length);
		}

		public void setsOTPLen(byte[] len) {
			System.arraycopy(len, 0, sOTPLen, 0, len.length);
		}
	}
	
	public static class RP_MSG_F6 extends Structure {
		public byte[] sOTPLen = new byte[3];       // OTP����
		public byte[] sOTP = new byte[49];         // OTPֵ
		public byte[] sHash = new byte[129];       // �Ӵս��
	    
	    public static class ByReference extends RP_MSG_F6 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_F6 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sOTPLen", "sOTP", 
            		"sHash"});
        }

		public byte[] getsOTPLen() {
			return DJHsmUtils.getHexByte(sOTPLen);
		}

		public byte[] getsOTP() {
			String lenStr = new String(getsOTPLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sOTP, len);
		}

		public byte[] getsHash() {
			return DJHsmUtils.getHexByte(sHash);
		}
	    
	    
	}
	
	//�ǶԳ�ָ��
	//RSA
	public static class RQ_MSG_EI extends Structure {
		public byte[] sModuleLen = new byte[5];       // ��Կģ�����ֽ�:1024-2048 ͬʱ����8�ı���
		public byte[] sPKeyCodeType = new byte[3];    // ��Կ�����������ֽ�:01 ASN.1��ʽDER����
		public byte[] sPKeyExpLen = new byte[5];      // ��ѡ�� ��Կָ������
	    public byte[] sPKeyExp = new byte[7];         // ��ѡ�� ��Կָ��
	    public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
	    public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
	    public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
	    public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ                
	    
	    public static class ByReference extends RQ_MSG_EI 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_EI 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sModuleLen", 
            		"sPKeyCodeType", "sPKeyExpLen", "sPKeyExp",
            		"sKeyStoreFlag", "sKeyStoreIndex", "sKeyTagLen",
            		"sKeyTag"});
        }

		public void setsModuleLen(byte[] moduleLen) {
			System.arraycopy(moduleLen, 0, sModuleLen, 0, moduleLen.length);
		}

		public void setsPKeyCodeType(byte[] keyCodeType) {
			System.arraycopy(keyCodeType, 0, sPKeyCodeType, 0, keyCodeType.length);
		}

		public void setsPKeyExpLen(byte[] keyExpLen) {
			System.arraycopy(keyExpLen, 0, sPKeyExpLen, 0, keyExpLen.length);
		}

		public void setsPKeyExp(byte[] keyExp) {
			System.arraycopy(keyExp, 0, sPKeyExp, 0, keyExp.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}
	}
	
	public static class RP_MSG_EJ extends Structure {
		public byte[] sPKey = new byte[513];       // ��Կ
		public byte[] sSKeyLen = new byte[5];      // ˽Կ����
		public byte[] sSKey = new byte[2500];      // ˽Կ����
	    
	    public static class ByReference extends RP_MSG_EJ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_EJ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPKey", "sSKeyLen", 
            		"sSKey"});
        }

		public byte[] getsPKey() {
			return DJHsmUtils.getsPubRSAKey(sPKey);
		}

		public byte[] getsSKeyLen() {
			return DJHsmUtils.getHexByte(sSKeyLen);
		}

		public byte[] getsSKey() {
			String lenStr = new String(getsSKeyLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sSKey, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_EJ extends Structure {
		public byte[] sSKeyLen = new byte[5];         // ˽Կ����
		public byte[] sSKey = new byte[2500];         // ˽Կ����
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
		public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ                
	    
	    public static class ByReference extends RQ_MSG_EJ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_EJ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSKeyLen", 
            		"sSKey", "sKeyStoreIndex", "sKeyTagLen",
            		"sKeyTag"});
        }

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}
	}
		
	public static class RQ_MSG_ER extends Structure {
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ������ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ������
	    
	    public static class ByReference extends RQ_MSG_ER 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_ER 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyStoreFlag", 
            		"sKeyStoreIndex"});
        }

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}
	}

	public static class RP_MSG_ES extends Structure {
		public byte[] sPKey = new byte[513];       // ��Կ
	    
	    public static class ByReference extends RP_MSG_ES 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_ES 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPKey"});
        }

		public byte[] getsPKey() {
			return DJHsmUtils.getsPubRSAKey(sPKey);
		}
	    
	}

	public static class RQ_MSG_3A extends Structure {
		public byte[] sAlgFlag = new byte[3];      // �㷨��ʶ
		public byte[] sPadMode = new byte[3];      // ���ģʽ
		public byte[] sMGF = new byte[3];          // MGF
		public byte[] sMGFAlg = new byte[3];       // MGF�Ӵ��㷨
		public byte[] sEncodeParaLen = new byte[3];// OAEP�����������
		public byte[] sEncodePara = new byte[100]; // OAEP�������
		public byte[] sSeparator1 = new byte[2];   // OAEP ��������ָ���
		public byte[] sDataLen = new byte[5];      // ���ݿ鳤��
		public byte[] sData = new byte[257];       // ���ݿ�
		public byte[] sSeparator2 = new byte[2];   // �ָ���
		public byte[] sKeyStoreFlag = new byte[2]; // ��Կ������ʶ
		public byte[] sKeyStoreIndex = new byte[5];// RSA��Կ������
		public byte[] sPKey = new byte[513];       // ��Կ
	    
	    public static class ByReference extends RQ_MSG_3A 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_3A 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgFlag", 
            		"sPadMode", "sMGF", "sMGFAlg",
            		"sEncodeParaLen", "sEncodePara", "sSeparator1",
            		"sDataLen", "sData", "sSeparator2",
            		"sKeyStoreFlag", "sKeyStoreIndex", "sPKey"});
        }

		public void setsAlgFlag(byte[] algFlag) {
			System.arraycopy(algFlag, 0, sAlgFlag, 0, algFlag.length);
		}

		public void setsPadMode(byte[] padMode) {
			System.arraycopy(padMode, 0, sPadMode, 0, padMode.length);
		}

		public void setsMGF(byte[] smgf) {
			System.arraycopy(smgf, 0, sMGF, 0, smgf.length);
		}

		public void setsMGFAlg(byte[] alg) {
			System.arraycopy(alg, 0, sMGFAlg, 0, alg.length);
		}

		public void setsEncodeParaLen(byte[] encodeParaLen) {
			System.arraycopy(encodeParaLen, 0, sEncodeParaLen, 0, encodeParaLen.length);
		}

		public void setsEncodePara(byte[] encodePara) {
			System.arraycopy(encodePara, 0, sEncodePara, 0, encodePara.length);
		}

		public void setsSeparator1(byte[] separator1) {
			System.arraycopy(separator1, 0, sSeparator1, 0, separator1.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsSeparator2(byte[] separator2) {
			System.arraycopy(separator2, 0, sSeparator2, 0, separator2.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}
	}
	
	public static class RP_MSG_3B extends Structure {
		public byte[] sCipherTextLen = new byte[5];// ���ĳ���
		public byte[] sCipherText = new byte[2500];// ��������
	    
	    public static class ByReference extends RP_MSG_3B 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_3B 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCipherTextLen", "sCipherText"});
        }

		public byte[] getsCipherTextLen() {
			return DJHsmUtils.getHexByte(sCipherTextLen);
		}

		public byte[] getsCipherText() {
			String lenStr = new String(getsCipherTextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sCipherText, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_3B extends Structure {
		public byte[] sAlgFlag = new byte[3];         // �㷨��ʶ
		public byte[] sPadMode = new byte[3];         // ���ģʽ
		public byte[] sMGF = new byte[3];             // MGF
		public byte[] sMGFAlg = new byte[3];          // MGF�Ӵ��㷨
		public byte[] sEncodeParaLen = new byte[3];   // OAEP�����������
		public byte[] sEncodePara = new byte[100];    // OAEP�������
		public byte[] sSeparator1 = new byte[2];      // OAEP ��������ָ���
		public byte[] sCipherTextLen = new byte[5];   // ���ݿ����ĳ���
		public byte[] sCipherText = new byte[2500];   // ��������
	    public byte[] sSeparator2 = new byte[2];      // �ָ���
	    public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
	    public byte[] sKeyStoreIndex = new byte[5];   // ��Կ������
	    public byte[] sSKeyLen = new byte[5];         // ˽Կ����
	    public byte[] sSKey = new byte[2500];         // ˽Կ
	    
	    public static class ByReference extends RQ_MSG_3B 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_3B 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgFlag", 
            		"sPadMode", "sMGF", "sMGFAlg",
            		"sEncodeParaLen", "sEncodePara", "sSeparator1",
            		"sCipherTextLen", "sCipherText", "sSeparator2",
            		"sKeyStoreFlag", "sKeyStoreIndex", "sSKeyLen",
            		"sSKey"});
        }

		public void setsAlgFlag(byte[] algFlag) {
			System.arraycopy(algFlag, 0, sAlgFlag, 0, algFlag.length);
		}

		public void setsPadMode(byte[] padMode) {
			System.arraycopy(padMode, 0, sPadMode, 0, padMode.length);
		}

		public void setsMGF(byte[] mgf) {
			System.arraycopy(mgf, 0, sMGF, 0, mgf.length);
		}

		public void setsMGFAlg(byte[] alg) {
			System.arraycopy(alg, 0, sMGFAlg, 0, alg.length);
		}

		public void setsEncodeParaLen(byte[] encodeParaLen) {
			System.arraycopy(encodeParaLen, 0, sEncodeParaLen, 0, encodeParaLen.length);
		}

		public void setsEncodePara(byte[] encodePara) {
			System.arraycopy(encodePara, 0, sEncodePara, 0, encodePara.length);
		}

		public void setsSeparator1(byte[] separator1) {
			System.arraycopy(separator1, 0, sSeparator1, 0, separator1.length);
		}

		public void setsCipherTextLen(byte[] cipherTextLen) {
			System.arraycopy(cipherTextLen, 0, sCipherTextLen, 0, cipherTextLen.length);
		}

		public void setsCipherText(byte[] cipherText) {
			System.arraycopy(cipherText, 0, sCipherText, 0, cipherText.length);
		}

		public void setsSeparator2(byte[] separator2) {
			System.arraycopy(separator2, 0, sSeparator2, 0, separator2.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}
	}
	
	public static class RP_MSG_3C extends Structure {
		public byte[] sDataLen = new byte[5];      // ���ݳ���
		public byte[] sData = new byte[2500];      // ��������
	    
	    public static class ByReference extends RP_MSG_3C 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_3C 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sDataLen", "sData"});
        }

		public byte[] getsDataLen() {
			return DJHsmUtils.getHexByte(sDataLen);
		}

		public byte[] getsData() {
			String lenStr = new String(getsDataLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sData, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_EW extends Structure {
		public byte[] sHashAlgFlag = new byte[3];    // HASH�㷨��ʶ 
		public byte[] sSignAlgFlag = new byte[3];    // ǩ���㷨��ʶ
		public byte[] sPadMode = new byte[3];        // ���ģʽ
		public byte[] sMGF = new byte[3];            // MGF
		public byte[] sMGFAlg = new byte[3];         // MGF�Ӵ��㷨
		public byte[] sEncodeParaLen = new byte[3];  // OAEP�����������
		public byte[] sEncodePara = new byte[100];   // OAEP�������
		public byte[] sSeparator1 = new byte[2];     // OAEP ��������ָ���
		public byte[] sDataLen = new byte[5];        // ���ݿ鳤��
		public byte[] sData = new byte[2500];        // ���ݿ�
		public byte[] sSeparator2 = new byte[2];     // �ָ���
		public byte[] sKeyStoreFlag = new byte[2];   // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];  // ��Կ����
		public byte[] sSKeyLen = new byte[5];        // ˽Կ����
		public byte[] sSKey = new byte[2500];        // ˽Կ
	    
	    public static class ByReference extends RQ_MSG_EW 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_EW 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashAlgFlag", 
            		"sSignAlgFlag", "sPadMode", "sMGF",
            		"sMGFAlg", "sEncodeParaLen", "sEncodePara",
            		"sSeparator1", "sDataLen", "sData",
            		"sSeparator2", "sKeyStoreFlag", "sKeyStoreIndex",
            		"sSKeyLen", "sSKey"});
        }

		public void setsHashAlgFlag(byte[] hashAlgFlag) {
			System.arraycopy(hashAlgFlag, 0, sHashAlgFlag, 0, hashAlgFlag.length);
		}

		public void setsSignAlgFlag(byte[] signAlgFlag) {
			System.arraycopy(signAlgFlag, 0, sSignAlgFlag, 0, signAlgFlag.length);
		}

		public void setsPadMode(byte[] padMode) {
			System.arraycopy(padMode, 0, sPadMode, 0, padMode.length);
		}

		public void setsMGF(byte[] mgf) {
			System.arraycopy(mgf, 0, sMGF, 0, mgf.length);
		}

		public void setsMGFAlg(byte[] alg) {
			System.arraycopy(alg, 0, sMGFAlg, 0, alg.length);
		}

		public void setsEncodeParaLen(byte[] encodeParaLen) {
			System.arraycopy(encodeParaLen, 0, sEncodeParaLen, 0, encodeParaLen.length);
		}

		public void setsEncodePara(byte[] encodePara) {
			System.arraycopy(encodePara, 0, sEncodePara, 0, encodePara.length);
		}

		public void setsSeparator1(byte[] separator1) {
			System.arraycopy(separator1, 0, sSeparator1, 0, separator1.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsSeparator2(byte[] separator2) {
			System.arraycopy(separator2, 0, sSeparator2, 0, separator2.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}
	}
	
	public static class RP_MSG_EX extends Structure {
		public byte[] sSignLen = new byte[5];      // ǩ������
		public byte[] sSign = new byte[2500];      // ����ǩ��
	    
	    public static class ByReference extends RP_MSG_EX 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_EX 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSignLen", "sSign"});
        }

		public byte[] getsSignLen() {
			return DJHsmUtils.getHexByte(sSignLen);
		}

		public byte[] getsSign() {
			String lenStr = new String(getsSignLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sSign, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_EY extends Structure {
		public byte[] sHashAlgFlag = new byte[3];    // HASH�㷨��ʶ
		public byte[] sSignAlgFlag = new byte[3];    // ǩ���㷨��ʶ 
		public byte[] sPadMode = new byte[3];        // ���ģʽ
		public byte[] sMGF = new byte[3];            // MGF
		public byte[] sMGFAlg = new byte[3];         // MGF�Ӵ��㷨
		public byte[] sEncodeParaLen = new byte[3];  // OAEP�����������
		public byte[] sEncodePara = new byte[100];   // OAEP������� 
		public byte[] sSeparator = new byte[2];      // OAEP ��������ָ���
	    public byte[] sSignLen = new byte[5];        // ǩ������
	    public byte[] sSign = new byte[2500];        // ����֤��ǩ��
	    public byte[] sSeparator1 = new byte[2];     // �ָ���
	    public byte[] sDataLen = new byte[5];        // ���ݿ鳤��
	    public byte[] sData = new byte[2500];        // ���ݿ�
	    public byte[] sSeparator2 = new byte[2];     // �ָ���
	    public byte[] sKeyStoreFlag = new byte[2];   // ��Կ�洢��ʶ
	    public byte[] sKeyStoreIndex = new byte[5];  // ��Կ����
	    public byte[] sPKey = new byte[513];         // ��Կ
	    
	    public static class ByReference extends RQ_MSG_EY 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_EY 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashAlgFlag", 
            		"sSignAlgFlag", "sPadMode", "sMGF",
            		"sMGFAlg", "sEncodeParaLen", "sEncodePara",
            		"sSeparator", "sSignLen", "sSign",
            		"sSeparator1", "sDataLen", "sData",
            		"sSeparator2", "sKeyStoreFlag", "sKeyStoreIndex",
            		"sPKey"});
        }

		public void setsHashAlgFlag(byte[] hashAlgFlag) {
			System.arraycopy(hashAlgFlag, 0, sHashAlgFlag, 0, hashAlgFlag.length);
		}

		public void setsSignAlgFlag(byte[] signAlgFlag) {
			System.arraycopy(signAlgFlag, 0, sSignAlgFlag, 0, signAlgFlag.length);
		}

		public void setsPadMode(byte[] padMode) {
			System.arraycopy(padMode, 0, sPadMode, 0, padMode.length);
		}

		public void setsMGF(byte[] mgf) {
			System.arraycopy(mgf, 0, sMGF, 0, mgf.length);
		}

		public void setsMGFAlg(byte[] alg) {
			System.arraycopy(alg, 0, sMGFAlg, 0, alg.length);
		}

		public void setsEncodeParaLen(byte[] encodeParaLen) {
			System.arraycopy(encodeParaLen, 0, sEncodeParaLen, 0, encodeParaLen.length);
		}

		public void setsEncodePara(byte[] encodePara) {
			System.arraycopy(encodePara, 0, sEncodePara, 0, encodePara.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsSignLen(byte[] signLen) {
			System.arraycopy(signLen, 0, sSignLen, 0, signLen.length);
		}

		public void setsSign(byte[] sign) {
			System.arraycopy(sign, 0, sSign, 0, sign.length);
		}

		public void setsSeparator1(byte[] separator1) {
			System.arraycopy(separator1, 0, sSeparator1, 0, separator1.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsSeparator2(byte[] separator2) {
			System.arraycopy(separator2, 0, sSeparator2, 0, separator2.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}
	}
		
	public static class RQ_MSG_TR extends Structure {
		public byte[] sAlgMode = new byte[3];      // �����㷨ģʽ
		public byte[] sKeyType = new byte[4];      // ��Կ����;
		public byte[] sSafeKey = new byte[50];     // ������Կ
		public byte[] sScatterLvl = new byte[3];   // ������Կ��ɢ����
		public byte[] sFactor = new byte[257];     // ������Կ��ɢ����
		public byte[] sKeyStoreFlag = new byte[2]; // ��������Կ������ʶ 
		public byte[] sKeyStoreIndex = new byte[5];// ��������Կ������
		public byte[] sSKeyLen = new byte[5];      // ��������Կ˽Կ����
		public byte[] sSKey = new byte[2500];      // ��������Կ˽Կ����
		public byte[] sExtenFlag = new byte[2];    // ��չ��ʶ
		public byte[] sPadFlag = new byte[3];      // PAD��ʶ
		public byte[] sPKeyFormat = new byte[2];   // ��Կ�����ʽ 
		public byte[] sIV = new byte[33];          // IV
	    
	    public static class ByReference extends RQ_MSG_TR 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TR 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", 
            		"sKeyType", "sSafeKey", "sScatterLvl",
            		"sFactor", "sKeyStoreFlag", "sKeyStoreIndex",
            		"sSKeyLen", "sSKey", "sExtenFlag",
            		"sPadFlag", "sPKeyFormat", "sIV"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsSafeKey(byte[] safeKey) {
			System.arraycopy(safeKey, 0, sSafeKey, 0, safeKey.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}

		public void setsExtenFlag(byte[] extenFlag) {
			System.arraycopy(extenFlag, 0, sExtenFlag, 0, extenFlag.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsPKeyFormat(byte[] keyFormat) {
			System.arraycopy(keyFormat, 0, sPKeyFormat, 0, keyFormat.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}
	}
		
	public static class RP_MSG_TS extends Structure {
		public byte[] sPKey = new byte[513];       // ��Կ����
		public byte[] sMLen = new byte[5];         // ��Կģm���ĳ���
		public byte[] sM = new byte[513];          // ��Կģm����
		public byte[] sELen = new byte[5];         // ��Կָ��e���ĳ���
		public byte[] sE = new byte[513];          // ��Կָ��e����
		public byte[] sDLen = new byte[5];         // ˽Կָ��d���ĳ���
		public byte[] sD = new byte[513];          // ˽Կָ��d����
		public byte[] sPLen = new byte[5];         // ˽Կ����P���ĳ���
		public byte[] sP = new byte[513];          // ˽Կ����P����
		public byte[] sQLen = new byte[5];         // ˽Կ����Q���ĳ��� 
		public byte[] sQ = new byte[513];          // ˽Կ����Q����
		public byte[] sDPLen = new byte[5];        // ˽Կ����dP���ĳ��� 
		public byte[] sDP = new byte[513];         // ˽Կ����dP����
		public byte[] sDQLen = new byte[5];        // ˽Կ����dQ���ĳ���
		public byte[] sDQ = new byte[513];         // ˽Կ����dQ���� 
		public byte[] sQInvLen = new byte[5];      // ˽Կ����qInv���ĳ��� 
		public byte[] sQInv = new byte[513];       // ˽Կ����qInv����
	    
	    public static class ByReference extends RP_MSG_TS 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TS 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPKey", "sMLen", 
            		"sM", "sELen", "sE", "sDLen", "sD", "sPLen", 
            		"sP", "sQLen", "sQ", "sDPLen", "sDP", "sDQLen", 
            		"sDQ", "sQInvLen", "sQInv"});
        }

		public byte[] getsPKey() {
			return DJHsmUtils.getsPubRSAKey(sPKey);
		}

		public byte[] getsMLen() {
			return DJHsmUtils.getHexByte(sMLen);
		}

		public byte[] getsM() {
			String lenStr = new String(getsMLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sM, len);
		}

		public byte[] getsELen() {
			return DJHsmUtils.getHexByte(sELen);
		}

		public byte[] getsE() {
			String lenStr = new String(getsELen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sE, len);
		}

		public byte[] getsDLen() {
			return DJHsmUtils.getHexByte(sDLen);
		}

		public byte[] getsD() {
			String lenStr = new String(getsDLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sD, len);
		}

		public byte[] getsPLen() {
			return DJHsmUtils.getHexByte(sPLen);
		}

		public byte[] getsP() {
			String lenStr = new String(getsPLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sP, len);
		}

		public byte[] getsQLen() {
			return DJHsmUtils.getHexByte(sQLen);
		}

		public byte[] getsQ() {
			String lenStr = new String(getsQLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sQ, len);
		}

		public byte[] getsDPLen() {
			return DJHsmUtils.getHexByte(sDPLen);
		}

		public byte[] getsDP() {
			String lenStr = new String(getsDPLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sDP, len);
		}

		public byte[] getsDQLen() {
			return DJHsmUtils.getHexByte(sDQLen);
		}

		public byte[] getsDQ() {
			String lenStr = new String(getsDQLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sDQ, len);
		}

		public byte[] getsQInvLen() {
			return DJHsmUtils.getHexByte(sQInvLen);
		}

		public byte[] getsQInv() {
			String lenStr = new String(getsQInvLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sQInv, len);
		}
	    
	    
	}

	public static class RQ_MSG_TS extends Structure {
		public byte[] sAlgMode = new byte[3];         // �����㷨ģʽ
		public byte[] sKeyType = new byte[4];         // ��Կ����
		public byte[] sSafeKey = new byte[50];        // ������Կ
		public byte[] sScatterLvl = new byte[3];      // ������Կ��ɢ����
		public byte[] sFactor = new byte[257];        // ������Կ��ɢ����
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
	    public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ   
	    public byte[] sExtenFlag = new byte[2];       // ��չ��ʶ
	    public byte[] sPadFlag = new byte[3];         // PAD��ʶ
	    public byte[] sPKeyFormat = new byte[2];      // ��Կ�����ʽ
	    public byte[] sIV = new byte[33];             // IV	    
	    public byte[] sPKey = new byte[513];          // ��Կ����
	    public byte[] sMLen = new byte[5];            // ��Կģm���ĳ���
	    public byte[] sM = new byte[513];             // ��Կģm����
	    public byte[] sELen = new byte[5];            // ��Կָ��e���ĳ���
	    public byte[] sE = new byte[513];	          // ��Կָ��e����
	    public byte[] sDLen = new byte[5];            // ˽Կָ��d����
	    public byte[] sD = new byte[513];             // ˽Կָ��d 
	    public byte[] sPLen = new byte[5];            // ˽Կ����P����
	    public byte[] sP = new byte[513];             // ˽Կ����P
	    public byte[] sQLen = new byte[5];            // ˽Կ����Q����
	    public byte[] sQ = new byte[513];             // ˽Կ����Q
	    public byte[] sDPLen = new byte[5];           // ˽Կ����dP����
	    public byte[] sDP = new byte[513];            // ˽Կ����dP
	    public byte[] sDQLen = new byte[5];           // ˽Կ����dQ����
	    public byte[] sDQ = new byte[513];            // ˽Կ����dQ 
	    public byte[] sQInvLen = new byte[5];         // ˽Կ����qInv����
	    public byte[] sQInv = new byte[513];          // ˽Կ����qInv
	    
	    public static class ByReference extends RQ_MSG_TS 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TS 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", 
            		"sKeyType", "sSafeKey", "sScatterLvl",
            		"sFactor", "sKeyStoreFlag", "sKeyStoreIndex",
            		"sKeyTagLen", "sKeyTag", "sExtenFlag", 
            		"sPadFlag", "sPKeyFormat", "sIV",
            		"sPKey", "sMLen", "sM", "sELen", "sE",
            		"sDLen", "sD", "sPLen",
            		"sP", "sQLen", "sQ",
            		"sDPLen", "sDP", "sDQLen",
            		"sDQ", "sQInvLen", "sQInv"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsSafeKey(byte[] safeKey) {
			System.arraycopy(safeKey, 0, sSafeKey, 0, safeKey.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}

		public void setsExtenFlag(byte[] extenFlag) {
			System.arraycopy(extenFlag, 0, sExtenFlag, 0, extenFlag.length);
		}

		public void setsPadFlag(byte[] padFlag) {
			System.arraycopy(padFlag, 0, sPadFlag, 0, padFlag.length);
		}

		public void setsPKeyFormat(byte[] keyFormat) {
			System.arraycopy(keyFormat, 0, sPKeyFormat, 0, keyFormat.length);
		}

		public void setsIV(byte[] iv) {
			System.arraycopy(iv, 0, sIV, 0, iv.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsMLen(byte[] len) {
			System.arraycopy(len, 0, sMLen, 0, len.length);
		}

		public void setsM(byte[] m) {
			System.arraycopy(m, 0, sM, 0, m.length);
		}

		public void setsELen(byte[] len) {
			System.arraycopy(len, 0, sELen, 0, len.length);
		}

		public void setsE(byte[] e) {
			System.arraycopy(e, 0, sE, 0, e.length);
		}

		public void setsDLen(byte[] len) {
			System.arraycopy(len, 0, sDLen, 0, len.length);
		}

		public void setsD(byte[] d) {
			System.arraycopy(d, 0, sD, 0, d.length);
		}

		public void setsPLen(byte[] len) {
			System.arraycopy(len, 0, sPLen, 0, len.length);
		}

		public void setsP(byte[] p) {
			System.arraycopy(p, 0, sP, 0, p.length);
		}

		public void setsQLen(byte[] len) {
			System.arraycopy(len, 0, sQLen, 0, len.length);
		}

		public void setsQ(byte[] q) {
			System.arraycopy(q, 0, sQ, 0, q.length);
		}

		public void setsDPLen(byte[] len) {
			System.arraycopy(len, 0, sDPLen, 0, len.length);
		}

		public void setsDP(byte[] dp) {
			System.arraycopy(dp, 0, sDP, 0, dp.length);
		}

		public void setsDQLen(byte[] len) {
			System.arraycopy(len, 0, sDQLen, 0, len.length);
		}

		public void setsDQ(byte[] dq) {
			System.arraycopy(dq, 0, sDQ, 0, dq.length);
		}

		public void setsQInvLen(byte[] invLen) {
			System.arraycopy(invLen, 0, sQInvLen, 0, invLen.length);
		}

		public void setsQInv(byte[] inv) {
			System.arraycopy(inv, 0, sQInv, 0, inv.length);
		}
	}
	
	public static class RP_MSG_TT extends Structure {
		public byte[] sSKeyLen = new byte[5];      // ˽Կ����
		public byte[] sSKey = new byte[2500];      // ˽Կ����
	    
	    public static class ByReference extends RP_MSG_TT 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TT 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSKeyLen", "sSKey"});
        }

		public byte[] getsSKeyLen() {
			return DJHsmUtils.getHexByte(sSKeyLen);
		}

		public byte[] getsSKey() {
			String lenStr = new String(getsSKeyLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sSKey, len);
		}
	    
	}
	
	public static class RQ_MSG_TV extends Structure {
		public byte[] sPadMode = new byte[3];         // ���ģʽ
		public byte[] sKeyType = new byte[4];         // ��������Կ����
		public byte[] sExportKey = new byte[50];      // ��������Կ
		public byte[] sScatterLvl = new byte[3];      // ��������Կ��ɢ����
		public byte[] sFactor = new byte[8*32+1];     // ��������Կ��ɢ����
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sPKey = new byte[513];          // ��Կ
		public byte[] sAuthDataLen = new byte[5];     // ��֤���ݳ���
		public byte[] sAuthData = new byte[2500];     // ��֤����	    
	    public byte[] sSeparator = new byte[2];       // ��֤���ݷָ���
	    public byte[] sMac = new byte[5];             // ��ԿMAC
	    
	    public static class ByReference extends RQ_MSG_TV 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TV 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPadMode", 
            		"sKeyType", "sExportKey", "sScatterLvl",
            		"sFactor", "sKeyStoreFlag", "sKeyStoreIndex",
            		"sPKey", "sAuthDataLen", "sAuthData", 
            		"sSeparator", "sMac"});
        }

		public void setsPadMode(byte[] padMode) {
			System.arraycopy(padMode, 0, sPadMode, 0, padMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsExportKey(byte[] exportKey) {
			System.arraycopy(exportKey, 0, sExportKey, 0, exportKey.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsAuthDataLen(byte[] authDataLen) {
			System.arraycopy(authDataLen, 0, sAuthDataLen, 0, authDataLen.length);
		}

		public void setsAuthData(byte[] authData) {
			System.arraycopy(authData, 0, sAuthData, 0, authData.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsMac(byte[] mac) {
			System.arraycopy(mac, 0, sMac, 0, mac.length);
		}
	}
	
	public static class RP_MSG_TW extends Structure {
		public byte[] sCipherTextLen = new byte[5];   // ���ĳ���
		public byte[] sCipherText = new byte[2500];   // ��Կ���ݿ�����
		public byte[] sKeyCV = new byte[17];          // ��ԿУ��ֵ
	    
	    public static class ByReference extends RP_MSG_TW 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TW 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCipherTextLen", "sCipherText", 
            		"sKeyCV"});
        }

		public byte[] getsCipherTextLen() {
			return DJHsmUtils.getHexByte(sCipherTextLen);
		}

		public byte[] getsCipherText() {
			String lenStr = new String(getsCipherTextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 16) ? lenStr : "0", 16);
			return DJHsmUtils.getData(sCipherText, len);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_TW extends Structure {
		public byte[] sPadMode = new byte[3];         // ���ģʽ
		public byte[] sKeyType = new byte[4];         // ������Կ����
		public byte[] sKeyFlag = new byte[2];         // ������Կ��ʶ(LMK)
		public byte[] sKeyStoreFlag = new byte[2];    // ������Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
	    public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
	    public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ                
	    public byte[] sKeyCV = new byte[17];          // ��ԿУ��ֵ
	    public byte[] sCipherTextLen = new byte[5];   // ������Կ�����ĳ���
	    public byte[] sCipherText = new byte[2500];   // ������Կ�����ģ�PK��Կ��
	    public byte[] sRSAFlag = new byte[2];         // RSA��Կ������ʶ
	    public byte[] sRSAIndex = new byte[5];        // RSA��Կ������
	    public byte[] sSKeyLen = new byte[5];         // ˽Կ����
	    public byte[] sSKey = new byte[2500];         // ˽Կ����
	    
	    public static class ByReference extends RQ_MSG_TW 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TW 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPadMode", 
            		"sKeyType", "sKeyFlag", "sKeyStoreFlag",
            		"sKeyStoreIndex", "sKeyTagLen", "sKeyTag",
            		"sKeyCV", "sCipherTextLen", "sCipherText",
            		"sRSAFlag", "sRSAIndex", "sSKeyLen",
            		"sSKey"});
        }

		public void setsPadMode(byte[] padMode) {
			System.arraycopy(padMode, 0, sPadMode, 0, padMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKeyFlag(byte[] keyFlag) {
			System.arraycopy(keyFlag, 0, sKeyFlag, 0, keyFlag.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}

		public void setsKeyCV(byte[] keyCV) {
			System.arraycopy(keyCV, 0, sKeyCV, 0, keyCV.length);
		}

		public void setsCipherTextLen(byte[] cipherTextLen) {
			System.arraycopy(cipherTextLen, 0, sCipherTextLen, 0, cipherTextLen.length);
		}

		public void setsCipherText(byte[] cipherText) {
			System.arraycopy(cipherText, 0, sCipherText, 0, cipherText.length);
		}

		public void setsRSAFlag(byte[] flag) {
			System.arraycopy(flag, 0, sRSAFlag, 0, flag.length);
		}

		public void setsRSAIndex(byte[] index) {
			System.arraycopy(index, 0, sRSAIndex, 0, index.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}
	}

	public static class RP_MSG_TX extends Structure {
		public byte[] sKeybyLmk = new byte[50];    // ��������Կ�����ģ�LMK��
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ
	    
	    public static class ByReference extends RP_MSG_TX 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TX 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyLmk", "sKeyCV"});
        }

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_EO extends Structure {
		public byte[] sPKeyCodeType = new byte[3];   // ��Կ��������
		public byte[] sPKey = new byte[513];         // ��Կ
	    public byte[] sAuthDataLen = new byte[5];    // ��֤���ݳ�
	    public byte[] sAuthData = new byte[129];     // ��֤����
	    
	    public static class ByReference extends RQ_MSG_EO 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_EO 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPKeyCodeType", 
            		"sPKey", "sAuthDataLen", "sAuthData"});
        }

		public void setsPKeyCodeType(byte[] keyCodeType) {
			System.arraycopy(keyCodeType, 0, sPKeyCodeType, 0, keyCodeType.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsAuthDataLen(byte[] authDataLen) {
			System.arraycopy(authDataLen, 0, sAuthDataLen, 0, authDataLen.length);
		}

		public void setsAuthData(byte[] authData) {
			System.arraycopy(authData, 0, sAuthData, 0, authData.length);
		}
	}

	//TODO ��ôȥ�������ַ� MAC : 4B
	public static class RP_MSG_EP extends Structure {
		public byte[] sMac = new byte[5];          // MAC
		public byte[] sPKey = new byte[513];       // ��Կ
	    
	    public static class ByReference extends RP_MSG_EP 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_EP
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac", "sPKey"});
        }

		public byte[] getsMac() {
			if (DJHsmUtils.isEmpty(sMac)) {
				return null;
			}
			return DJHsmUtils.reduceTailBytes(sMac);
		}

		public byte[] getsPKey() {
			return DJHsmUtils.getsPubRSAKey(sPKey);
		}
	    
	}
	
	public static class RQ_MSG_E7 extends Structure {
		public byte[] sCurveFlag = new byte[3];       // ���߱�ʶ
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
		public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ                
	    
	    public static class ByReference extends RQ_MSG_E7 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_E7 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCurveFlag", 
            		"sKeyStoreFlag", "sKeyStoreIndex", "sKeyTagLen",
            		"sKeyTag"});
        }

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}
	}
	
	public static class RP_MSG_E8 extends Structure {
		public byte[] sPKey = new byte[257];       // ��Կ
		public byte[] sSKeyLen = new byte[5];      // ˽Կ����
		public byte[] sSKey = new byte[129];       // ˽Կ
	    
	    public static class ByReference extends RP_MSG_E8 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_E8 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPKey", "sSKeyLen", 
            		"sSKey"});
        }

		public byte[] getsPKey() {
			return DJHsmUtils.getsPubSM2Key(sPKey);
		}

		public byte[] getsSKeyLen() {
			return DJHsmUtils.getHexByte(sSKeyLen);
		}

		public byte[] getsSKey() {
			String lenStr = new String(getsSKeyLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sSKey, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_E1 extends Structure {
		public byte[] sCurveFlag = new byte[3];       // ���߱�ʶ
		public byte[] sPKey = new byte[257];          // ��Կ
		public byte[] sSKeyLen = new byte[5];         // ˽Կ����
		public byte[] sSKey = new byte[129];          // ˽Կ����
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
		public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ                
	    
	    public static class ByReference extends RQ_MSG_E1 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_E1 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCurveFlag", 
            		"sPKey", "sSKeyLen", "sSKey", 
            		"sKeyStoreIndex", "sKeyTagLen", "sKeyTag"});
        }

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}
	}
			
	public static class RQ_MSG_E2 extends Structure {
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
	    
	    public static class ByReference extends RQ_MSG_E2 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_E2 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeyStoreFlag", 
            		"sKeyStoreIndex"});
        }

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}
	}

	public static class RP_MSG_E3 extends Structure {
		public byte[] sPKey = new byte[257];       // ��Կ
	    
	    public static class ByReference extends RP_MSG_E3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_E3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPKey"});
        }

		public byte[] getsPKey() {
			return DJHsmUtils.getsPubSM2Key(sPKey);
		}
	    
	    
	}
	
	public static class RQ_MSG_E3 extends Structure {
		public byte[] sCurveFlag = new byte[3];       // ���߱�ʶ
		public byte[] sDataLen = new byte[5];         // ���ݿ鳤��
		public byte[] sData = new byte[1901];         // ���ݿ�
		public byte[] sSeparator = new byte[2];       // �ָ���
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // SM2��Կ����
		public byte[] sPKey = new byte[257];          // �ָ���
		public byte[] sEncodeFormat = new byte[2];    // ���ı����ʽ
	    
	    public static class ByReference extends RQ_MSG_E3 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_E3 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCurveFlag", 
            		"sDataLen", "sData", "sSeparator",
            		"sKeyStoreFlag", "sKeyStoreIndex", "sPKey",
            		"sEncodeFormat"});
        }

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsEncodeFormat(byte[] encodeFormat) {
			System.arraycopy(encodeFormat, 0, sEncodeFormat, 0, encodeFormat.length);
		}
	}

	public static class RP_MSG_E4 extends Structure {
		public byte[] sCipherTextLen = new byte[5];// ���ĳ���
		public byte[] sCipherText = new byte[2000];// ��������
	    
	    public static class ByReference extends RP_MSG_E4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_E4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCipherTextLen", "sCipherText"});
        }

		public byte[] getsCipherTextLen() {
			return DJHsmUtils.getHexByte(sCipherTextLen);
		}

		public byte[] getsCipherText() {
			String lenStr = new String(getsCipherTextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sCipherText, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_E4 extends Structure {
		public byte[] sCurveFlag = new byte[3];       // ���߱�ʶ
		public byte[] sEncodeFormat = new byte[2];    // ���ı����ʽ
		public byte[] sCipherTextLen = new byte[5];   // ���ݿ����ĳ���
		public byte[] sCipherText = new byte[2000];   // ��������
		public byte[] sSeparator = new byte[2];       // �ָ���
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sSKeyLen = new byte[5];         // ˽Կ����
		public byte[] sSKey = new byte[129];          // ˽Կ
	    
	    public static class ByReference extends RQ_MSG_E4 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_E4 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCurveFlag", 
            		"sEncodeFormat", "sCipherTextLen", "sCipherText",
            		"sSeparator", "sKeyStoreFlag", "sKeyStoreIndex",
            		"sSKeyLen", "sSKey"});
        }

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsEncodeFormat(byte[] encodeFormat) {
			System.arraycopy(encodeFormat, 0, sEncodeFormat, 0, encodeFormat.length);
		}

		public void setsCipherTextLen(byte[] cipherTextLen) {
			System.arraycopy(cipherTextLen, 0, sCipherTextLen, 0, cipherTextLen.length);
		}

		public void setsCipherText(byte[] cipherText) {
			System.arraycopy(cipherText, 0, sCipherText, 0, cipherText.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}
	}
		
	public static class RP_MSG_E5 extends Structure {
		public byte[] sDataLen = new byte[5];      // ���ݳ���
		public byte[] sData = new byte[2000];      // ��������
	    
	    public static class ByReference extends RP_MSG_E5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_E5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sDataLen", "sData"});
        }

		public byte[] getsDataLen() {
			return DJHsmUtils.getHexByte(sDataLen);
		}

		public byte[] getsData() {
			String lenStr = new String(getsDataLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sData, len);
		}
	    
	    
	}

	public static class RQ_MSG_E5 extends Structure {
		public byte[] sHashAlgFlag = new byte[3];  // HASH�㷨��ʶ
		public byte[] sCurveFlag = new byte[3];    // ���߱�ʶ
		public byte[] sUserIDLen = new byte[5];    // �û���ʶ����
		public byte[] sUserID = new byte[129];     // �û���ʶ
	    public byte[] sSeparator1 = new byte[2];   // �ָ���
	    public byte[] sDataLen = new byte[5];      // ���ݿ鳤��
	    public byte[] sData = new byte[2000];      // ���ݿ�
	    public byte[] sSeparator2 = new byte[2];   // �ָ���
	    public byte[] sKeyStoreFlag = new byte[2]; // ��Կ�洢��ʶ
	    public byte[] sKeyStoreIndex = new byte[5];// SM2��Կ����
	    public byte[] sPKey = new byte[257];       // SM2��Կ
	    public byte[] sSKeyLen = new byte[5];      // ˽Կ����
	    public byte[] sSKey = new byte[129];       // ˽Կ
	    public byte[] sEncodeFormat = new byte[2]; // ǩ�������ʽ
	    
	    public static class ByReference extends RQ_MSG_E5 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_E5 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashAlgFlag", 
            		"sCurveFlag", "sUserIDLen", "sUserID",
            		"sSeparator1", "sDataLen", "sData",
            		"sSeparator2", "sKeyStoreFlag", "sKeyStoreIndex",
            		"sPKey", "sSKeyLen", "sSKey",
            		"sEncodeFormat"});
        }

		public void setsHashAlgFlag(byte[] hashAlgFlag) {
			System.arraycopy(hashAlgFlag, 0, sHashAlgFlag, 0, hashAlgFlag.length);
		}

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsUserIDLen(byte[] userIDLen) {
			System.arraycopy(userIDLen, 0, sUserIDLen, 0, userIDLen.length);
		}

		public void setsUserID(byte[] userID) {
			System.arraycopy(userID, 0, sUserID, 0, userID.length);
		}

		public void setsSeparator1(byte[] separator1) {
			System.arraycopy(separator1, 0, sSeparator1, 0, separator1.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsSeparator2(byte[] separator2) {
			System.arraycopy(separator2, 0, sSeparator2, 0, separator2.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}

		public void setsEncodeFormat(byte[] encodeFormat) {
			System.arraycopy(encodeFormat, 0, sEncodeFormat, 0, encodeFormat.length);
		}
	}
	
	public static class RP_MSG_E6 extends Structure {
		public byte[] sSignLen = new byte[5];      // ǩ������
		public byte[] sSign = new byte[257];       // ����ǩ��
	    
	    public static class ByReference extends RP_MSG_E6 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_E6 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSignLen", "sSign"});
        }

		public byte[] getsSignLen() {
			return DJHsmUtils.getHexByte(sSignLen);
		}

		public byte[] getsSign() {
			String lenStr = new String(getsSignLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sSign, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_E6 extends Structure {
		public byte[] sHashAlgFlag = new byte[3];    // HASH�㷨��ʶ
		public byte[] sCurveFlag = new byte[3];      // ���߱�ʶ
		public byte[] sUserIDLen = new byte[5];      // �û���ʶ����
		public byte[] sUserID = new byte[129];       // �û���ʶ
		public byte[] sSeparator1 = new byte[2];     // �ָ���
		public byte[] sEncodeFormat = new byte[2];   // ǩ�������ʽ
		public byte[] sSignLen = new byte[5];        // ǩ������
		public byte[] sSign = new byte[257];         // ����֤��ǩ��
		public byte[] sSeparator2 = new byte[2];     // �ָ���
	    public byte[] sDataLen = new byte[5];        // ���ݿ鳤��
	    public byte[] sData = new byte[2000];        // ���ݿ�
	    public byte[] sSeparator3 = new byte[2];     // �ָ���
	    public byte[] sKeyStoreFlag = new byte[2];   // ��Կ�洢��ʶ
	    public byte[] sKeyStoreIndex = new byte[5];  // SM2��Կ����
	    public byte[] sPKey = new byte[257];         // SM2��Կ
	    
	    public static class ByReference extends RQ_MSG_E6 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_E6 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sHashAlgFlag", 
            		"sCurveFlag", "sUserIDLen", "sUserID",
            		"sSeparator1", "sEncodeFormat", "sSignLen",
            		"sSign", "sSeparator2", "sDataLen",
            		"sData", "sSeparator3", "sKeyStoreFlag",
            		"sKeyStoreIndex", "sPKey"});
        }

		public void setsHashAlgFlag(byte[] hashAlgFlag) {
			System.arraycopy(hashAlgFlag, 0, sHashAlgFlag, 0, hashAlgFlag.length);
		}

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsUserIDLen(byte[] userIDLen) {
			System.arraycopy(userIDLen, 0, sUserIDLen, 0, userIDLen.length);
		}

		public void setsUserID(byte[] userID) {
			System.arraycopy(userID, 0, sUserID, 0, userID.length);
		}

		public void setsSeparator1(byte[] separator1) {
			System.arraycopy(separator1, 0, sSeparator1, 0, separator1.length);
		}

		public void setsEncodeFormat(byte[] encodeFormat) {
			System.arraycopy(encodeFormat, 0, sEncodeFormat, 0, encodeFormat.length);
		}

		public void setsSignLen(byte[] signLen) {
			System.arraycopy(signLen, 0, sSignLen, 0, signLen.length);
		}

		public void setsSign(byte[] sign) {
			System.arraycopy(sign, 0, sSign, 0, sign.length);
		}

		public void setsSeparator2(byte[] separator2) {
			System.arraycopy(separator2, 0, sSeparator2, 0, separator2.length);
		}

		public void setsDataLen(byte[] dataLen) {
			System.arraycopy(dataLen, 0, sDataLen, 0, dataLen.length);
		}

		public void setsData(byte[] data) {
			System.arraycopy(data, 0, sData, 0, data.length);
		}

		public void setsSeparator3(byte[] separator3) {
			System.arraycopy(separator3, 0, sSeparator3, 0, separator3.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}
	}
	
	public static class RQ_MSG_ED extends Structure {
		public byte[] sCurveFlag = new byte[3];       // ���߱�ʶ
		public byte[] sDataAbstract = new byte[33];   // ���ݿ�ժҪֵ
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // SM2��Կ����
		public byte[] sSKeyLen = new byte[5];         // ˽Կ����
		public byte[] sSKey = new byte[129];          // ˽Կ
		public byte[] sEncodeFormat = new byte[2];    // ǩ�������ʽ
	    
	    public static class ByReference extends RQ_MSG_ED 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_ED 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCurveFlag", 
            		"sDataAbstract", "sKeyStoreFlag", "sKeyStoreIndex",
            		"sSKeyLen", "sSKey", "sEncodeFormat"});
        }

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsDataAbstract(byte[] dataAbstract) {
			System.arraycopy(dataAbstract, 0, sDataAbstract, 0, dataAbstract.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}

		public void setsEncodeFormat(byte[] encodeFormat) {
			System.arraycopy(encodeFormat, 0, sEncodeFormat, 0, encodeFormat.length);
		}
	}
		
	public static class RP_MSG_EE extends Structure {
		public byte[] sSignLen = new byte[5];      // ǩ������
		public byte[] sSign = new byte[257];       // ����ǩ��
	    
	    public static class ByReference extends RP_MSG_EE 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_EE 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSignLen", "sSign"});
        }

		public byte[] getsSignLen() {
			return DJHsmUtils.getHexByte(sSignLen);
		}

		public byte[] getsSign() {
			String lenStr = new String(getsSignLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sSign, len);
		}
	    
	    
	}
	
	public static class RQ_MSG_EF extends Structure {
		public byte[] sCurveFlag = new byte[3];       // ���߱�ʶ
		public byte[] sEncodeFormat = new byte[2];    // ǩ�������ʽ
		public byte[] sSignLen = new byte[5];         // ǩ������
		public byte[] sSign = new byte[129];          // ����֤��ǩ��
		public byte[] sSeparator = new byte[2];       // �ָ���
	    public byte[] sDataAbstract = new byte[33];   // ���ݿ�ժҪֵ
	    public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
	    public byte[] sKeyStoreIndex = new byte[5];   // SM2��Կ����
	    public byte[] sPKey = new byte[257];          // SM2��Կ
	    
	    public static class ByReference extends RQ_MSG_EF 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_EF 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCurveFlag", 
            		"sEncodeFormat", "sSignLen", "sSign",
            		"sSeparator", "sDataAbstract", "sKeyStoreFlag",
            		"sKeyStoreIndex", "sPKey"});
        }

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsEncodeFormat(byte[] encodeFormat) {
			System.arraycopy(encodeFormat, 0, sEncodeFormat, 0, encodeFormat.length);
		}

		public void setsSignLen(byte[] signLen) {
			System.arraycopy(signLen, 0, sSignLen, 0, signLen.length);
		}

		public void setsSign(byte[] sign) {
			System.arraycopy(sign, 0, sSign, 0, sign.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsDataAbstract(byte[] dataAbstract) {
			System.arraycopy(dataAbstract, 0, sDataAbstract, 0, dataAbstract.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}
	}

	public static class RQ_MSG_TT extends Structure {
		public byte[] sAlgMode = new byte[3];      // �����㷨ģʽ
		public byte[] sKeyType = new byte[4];      // ������Կ����
		public byte[] sSafeKey = new byte[50];     // ������Կ
		public byte[] sScatterLvl = new byte[3];   // ������Կ��ɢ����
		public byte[] sFactor = new byte[257];     // ������Կ��ɢ����
		public byte[] sCurveFlag = new byte[3];    // ���߱�ʶ
		public byte[] sKeyStoreIndex = new byte[5];// SM2��Կ������
		public byte[] sPKey = new byte[257];       // ��Կ
		public byte[] sSKeyLen = new byte[5];      // ˽Կ����
		public byte[] sSKey = new byte[129];       // ˽Կ����
	    
	    public static class ByReference extends RQ_MSG_TT 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TT 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", 
            		"sKeyType", "sSafeKey", "sScatterLvl",
            		"sFactor", "sCurveFlag", "sKeyStoreIndex",
            		"sPKey", "sSKeyLen", "sSKey"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsSafeKey(byte[] safeKey) {
			System.arraycopy(safeKey, 0, sSafeKey, 0, safeKey.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}
	}
	
	public static class RP_MSG_TU extends Structure {
		public byte[] sPKey = new byte[257];       // ��Կ
		public byte[] sDLen = new byte[5];         // ˽Կ����d���ܺ󳤶�
		public byte[] sD = new byte[257];          // ˽Կ����d����
	    
	    public static class ByReference extends RP_MSG_TU 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TU 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sPKey", "sDLen", 
            		"sD"});
        }

		public byte[] getsPKey() {
			return DJHsmUtils.getsPubSM2Key(sPKey);
		}

		public byte[] getsDLen() {
			return DJHsmUtils.getHexByte(sDLen);
		}

		public byte[] getsD() {
			String lenStr = new String(getsDLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sD, len);
		}
	    
	    
	}

	public static class RQ_MSG_TU extends Structure {
		public byte[] sAlgMode = new byte[3];      // �����㷨ģʽ
		public byte[] sKeyType = new byte[4];      // ������Կ����
		public byte[] sSafeKey = new byte[50];     // ������Կ
		public byte[] sScatterLvl = new byte[3];   // ������Կ��ɢ����
		public byte[] sFactor = new byte[257];     // ������Կ��ɢ����
		public byte[] sCurveFlag = new byte[3];    // ���߱�ʶ
		public byte[] sKeyStoreIndex = new byte[5];// ��Կ����
		public byte[] sKeyTagLen = new byte[5];    // ��Կ��ǩ���� 
		public byte[] sKeyTag = new byte[17];      // ��Կ��ǩ                
		public byte[] sPKey = new byte[257];       // ��Կ
		public byte[] sDLen = new byte[5];         // ˽Կ����d����
		public byte[] sD = new byte[257];          // ˽Կ����d
	    
	    public static class ByReference extends RQ_MSG_TU 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TU 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sAlgMode", "sKeyType", 
            		"sSafeKey", "sScatterLvl", "sFactor", "sCurveFlag", 
            		"sKeyStoreIndex", "sKeyTagLen", "sKeyTag", "sPKey",
            		"sDLen", "sD"});
        }

		public void setsAlgMode(byte[] algMode) {
			System.arraycopy(algMode, 0, sAlgMode, 0, algMode.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsSafeKey(byte[] safeKey) {
			System.arraycopy(safeKey, 0, sSafeKey, 0, safeKey.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsDLen(byte[] len) {
			System.arraycopy(len, 0, sDLen, 0, len.length);
		}

		public void setsD(byte[] d) {
			System.arraycopy(d, 0, sD, 0, d.length);
		}
	}
	
	public static class RP_MSG_TV extends Structure {
		public byte[] sSKeyLen = new byte[5];      // ˽Կ����
		public byte[] sSKey = new byte[129];       // ˽Կ����
	    
	    public static class ByReference extends RP_MSG_TV 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TV 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sSKeyLen", "sSKey"});
        }

		public byte[] getsSKeyLen() {
			return DJHsmUtils.getHexByte(sSKeyLen);
		}

		public byte[] getsSKey() {
			String lenStr = new String(getsSKeyLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 10) ? lenStr : "0", 10);
			return DJHsmUtils.getData(sSKey, len);
		}
	    
	    
	}

	public static class RQ_MSG_TX extends Structure {
		public byte[] sCurveFlag = new byte[3];      // ���߱�ʶ
		public byte[] sKeyType = new byte[4];        // ��������Կ����
		public byte[] sSafeKey = new byte[50];       // ��������Կ
		public byte[] sScatterLvl = new byte[3];     // ��������Կ��ɢ����
		public byte[] sFactor = new byte[8*32+1];    // ��������Կ��ɢ����
		public byte[] sKeyStoreIndex = new byte[5];  // SM2��Կ������
		public byte[] sPKey = new byte[257];         // SM2��Կ
		public byte[] sAuthDataLen = new byte[5];    // ��֤���ݳ�
	    public byte[] sAuthData = new byte[257];     // ��֤����
	    public byte[] sSeparator = new byte[2];      // ��֤���ݷָ���
	    public byte[] sMac = new byte[5];            // ��ԿMAC
	    
	    public static class ByReference extends RQ_MSG_TX 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TX 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCurveFlag", 
            		"sKeyType", "sSafeKey", "sScatterLvl",
            		"sFactor", "sKeyStoreIndex", "sPKey",
            		"sAuthDataLen", "sAuthData", "sSeparator",
            		"sMac"});
        }

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsSafeKey(byte[] safeKey) {
			System.arraycopy(safeKey, 0, sSafeKey, 0, safeKey.length);
		}

		public void setsScatterLvl(byte[] scatterLvl) {
			System.arraycopy(scatterLvl, 0, sScatterLvl, 0, scatterLvl.length);
		}

		public void setsFactor(byte[] factor) {
			System.arraycopy(factor, 0, sFactor, 0, factor.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsAuthDataLen(byte[] authDataLen) {
			System.arraycopy(authDataLen, 0, sAuthDataLen, 0, authDataLen.length);
		}

		public void setsAuthData(byte[] authData) {
			System.arraycopy(authData, 0, sAuthData, 0, authData.length);
		}

		public void setsSeparator(byte[] separator) {
			System.arraycopy(separator, 0, sSeparator, 0, separator.length);
		}

		public void setsMac(byte[] mac) {
			System.arraycopy(mac, 0, sMac, 0, mac.length);
		}
	}

	public static class RP_MSG_TY extends Structure {
		public byte[] sCipherTextLen = new byte[5];   // ���ĳ���
		public byte[] sCipherText = new byte[257];    // ��Կ���ݿ�����
		public byte[] sKeyCV = new byte[17];          // ��ԿУ��ֵ
	    
	    public static class ByReference extends RP_MSG_TY 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TY 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCipherTextLen", "sCipherText", 
            		"sKeyCV"});
        }

		public byte[] getsCipherTextLen() {
			return DJHsmUtils.getHexByte(sCipherTextLen);
		}

		public byte[] getsCipherText() {
			String lenStr = new String(getsCipherTextLen());
			int len = Integer.parseInt(DJHsmUtils.isNumeric(lenStr, 16) ? lenStr : "0", 16);
			return DJHsmUtils.getData(sCipherText, len);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_TY extends Structure {
		public byte[] sCurveFlag = new byte[3];       // ���߱�ʶ
		public byte[] sKeyType = new byte[4];         // ������Կ����
		public byte[] sKeyFlag = new byte[2];         // ������Կ��ʶ(LMK) 
		public byte[] sKeyStoreFlag = new byte[2];    // ��Կ�洢��ʶ
		public byte[] sKeyStoreIndex = new byte[5];   // ��Կ����
		public byte[] sKeyTagLen = new byte[3];       // ��Կ��ǩ����
		public byte[] sKeyTag = new byte[17];         // ��Կ��ǩ                
		public byte[] sKeyCV = new byte[17];          // ��ԿУ��ֵ
		public byte[] sCipherTextLen = new byte[5];   // ������Կ�����ĳ��� 
		public byte[] sCipherText = new byte[257];    // ������Կ�����ģ�PK��Կ��
		public byte[] sSM2Index = new byte[5];        // SM2��Կ������ 
		public byte[] sSKeyLen = new byte[5];         // ˽Կ����
		public byte[] sSKey = new byte[129];          // ˽Կ����
	    
	    public static class ByReference extends RQ_MSG_TY 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TY 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCurveFlag", 
            		"sKeyType", "sKeyFlag", "sKeyStoreFlag",
            		"sKeyStoreIndex", "sKeyTagLen", "sKeyTag",
            		"sKeyCV", "sCipherTextLen", "sCipherText",
            		"sSM2Index", "sSKeyLen", "sSKey"});
        }

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsKeyType(byte[] keyType) {
			System.arraycopy(keyType, 0, sKeyType, 0, keyType.length);
		}

		public void setsKeyFlag(byte[] keyFlag) {
			System.arraycopy(keyFlag, 0, sKeyFlag, 0, keyFlag.length);
		}

		public void setsKeyStoreFlag(byte[] keyStoreFlag) {
			System.arraycopy(keyStoreFlag, 0, sKeyStoreFlag, 0, keyStoreFlag.length);
		}

		public void setsKeyStoreIndex(byte[] keyStoreIndex) {
			System.arraycopy(keyStoreIndex, 0, sKeyStoreIndex, 0, keyStoreIndex.length);
		}

		public void setsKeyTagLen(byte[] keyTagLen) {
			System.arraycopy(keyTagLen, 0, sKeyTagLen, 0, keyTagLen.length);
		}

		public void setsKeyTag(byte[] keyTag) {
			System.arraycopy(keyTag, 0, sKeyTag, 0, keyTag.length);
		}

		public void setsKeyCV(byte[] keyCV) {
			System.arraycopy(keyCV, 0, sKeyCV, 0, keyCV.length);
		}

		public void setsCipherTextLen(byte[] cipherTextLen) {
			System.arraycopy(cipherTextLen, 0, sCipherTextLen, 0, cipherTextLen.length);
		}

		public void setsCipherText(byte[] cipherText) {
			System.arraycopy(cipherText, 0, sCipherText, 0, cipherText.length);
		}

		public void setsSM2Index(byte[] index) {
			System.arraycopy(index, 0, sSM2Index, 0, index.length);
		}

		public void setsSKeyLen(byte[] keyLen) {
			System.arraycopy(keyLen, 0, sSKeyLen, 0, keyLen.length);
		}

		public void setsSKey(byte[] key) {
			System.arraycopy(key, 0, sSKey, 0, key.length);
		}
	}
	
	public static class RP_MSG_TZ extends Structure {
		public byte[] sKeybyLmk = new byte[50];    // ��������Կ�����ģ�LMK��
		public byte[] sKeyCV = new byte[17];       // ��ԿУ��ֵ
	    
	    public static class ByReference extends RP_MSG_TZ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TZ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sKeybyLmk", "sKeyCV"});
        }

		public byte[] getsKeybyLmk() {
			return DJHsmUtils.getHexByte(sKeybyLmk);
		}

		public byte[] getsKeyCV() {
			return DJHsmUtils.getHexByte(sKeyCV);
		}
	    
	    
	}
	
	public static class RQ_MSG_TQ extends Structure {
		public byte[] sCurveFlag = new byte[3];    // ���߱�ʶ
		public byte[] sPKey = new byte[257];       // ��Կ
	    public byte[] sAuthDataLen = new byte[5];  // ��֤���ݳ�
	    public byte[] sAuthData = new byte[129];   // ��֤����
	    
	    public static class ByReference extends RQ_MSG_TQ 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RQ_MSG_TQ 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sCurveFlag", 
            		"sPKey", "sAuthDataLen", "sAuthData"});
        }

		public void setsCurveFlag(byte[] curveFlag) {
			System.arraycopy(curveFlag, 0, sCurveFlag, 0, curveFlag.length);
		}

		public void setsPKey(byte[] key) {
			System.arraycopy(key, 0, sPKey, 0, key.length);
		}

		public void setsAuthDataLen(byte[] authDataLen) {
			System.arraycopy(authDataLen, 0, sAuthDataLen, 0, authDataLen.length);
		}

		public void setsAuthData(byte[] authData) {
			System.arraycopy(authData, 0, sAuthData, 0, authData.length);
		}
	}

	//TODO macֱ���ƶ�Ϊ4,�޷�ֱ��ȥ�����ַ��� Mac 4B
	public static class RP_MSG_TR extends Structure {
		public byte[] sMac = new byte[9];          // MAC
		public byte[] sPKey = new byte[257];       // ��Կ
	    
	    public static class ByReference extends RP_MSG_TR 
	    	implements Structure.ByReference{}
	    
	    public static class ByValue extends RP_MSG_TR 
	    	implements Structure.ByValue{}
	    
	    @Override  
        protected List<String> getFieldOrder() {  
            return Arrays.asList(new String[]{"sMac", "sPKey"});
        }

		public byte[] getsMac() {
			if (DJHsmUtils.isEmpty(sMac)) {
				return null;
			}
			return DJHsmUtils.getData(sMac, 4);
		}

		public byte[] getsPKey() {
			return DJHsmUtils.getsPubSM2Key(sPKey);
		}
	    
	}
}
