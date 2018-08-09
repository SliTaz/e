package com.zbensoft.e.payment.api.DJEncryption.hsm.api;

public enum DJHsmErrCode {
	ERR_OK("成功", 0), 
	ERR_VERIFY_FAILED("验证错误或密钥奇校验错", 1), 
	ERR_CIPHER_LEN_NOT_ENOUGH("算法的密钥长度不符合", 2),
	ERR_INVALID_CRYPT_MODE("无效的算法模式", 3), 
	ERR_INVALID_CIPHER_TYPE_CODE("无效的密钥类型代码", 4), 
	ERR_INVALID_CIPHER_LEN_FLAG("无效的密钥长度标识", 5),        
	ERR_OFFSET_INVALID("无效的密钥成份个数或非法的偏移量", 6),                 
	ERR_CRYPTOGRAPHIC_CHECK_FAILED("密钥校验值比对失败", 7),     
	ERR_INVALID_DATATYPE("输入数据类型无效", 8),               
	ERR_INVALID_CIPHER_NUM("导出的密钥个数无效", 9),             
	ERR_SRC_CIPHER_ODD_VERIFY_FAILED("源密钥奇校验错", 10),                        
	ERR_USER_DATA_MEM_INVALID("用户存储区内容无效。复位、重启或覆盖", 12),          
	ERR_INVALID_LMK("LMK 错误", 13),                    
	ERR_INVALID_PIN_UNDER_LMK("LMK 03-005 下加密的PIN无效", 14),          
	ERR_INVALID_INPUT_DATA("无效的输入数据 (无效的格式，无效的字符，或输入的数据长度不够)", 15),             
	ERR_PRINTER_OR_CONSOLE_OUTOFWORK("控制台或打印机没有准备好/没有连接", 16),   
	ERR_NOT_AUTHORIZE_OR_DISALLOW_PLPIN("加密机没有在授权状态，或不允许输出明文PIN", 17),
	ERR_DOC_FORMAT_NOT_LOADED("文档格式定义没有加载", 18),          
	ERR_INVALID_DIEBOLD("指定的 Diebold 表无效", 19),                
	ERR_INVALID_PINBLOCK("PIN数据块没有包含有效的值", 20),               
	ERR_INVALID_INDEX("无效的索引值，或索引/数据长度数溢出", 21),                  
	ERR_INVALID_ACCOUNT("无效的帐号", 22),                
	ERR_INVALID_PINBLOCK_FORMAT_CODE("无效的PIN数据块格式代码", 23),   
	ERR_INVALID_PIN_LEN("PIN 的长度不到4位或超过12位", 24),                
	ERR_DECIMAL_TRANS_TABLE_ERROR("十进制转换表不正确", 25),      
	ERR_CIPHER_FLAG_ERROR("密钥标识错", 26),              
	ERR_CIPHER_LEN_ERROR("密钥长度错", 27),               
	ERR_INVALID_CIPHER_TYPE("无效的密钥类型", 28),            
	ERR_CRYPTY_FUN_NOT_PERMITED("密码功能不允许", 29),        
	ERR_INVALID_USER_REFERENCE_ID("无效的用户参考号", 30),      
	ERR_APPLY_FOR_PIN_NOT_ENOUGH_MEM("PIN申请函批处理空间不足", 31),   
	ERR_INVALID_INPUT_TYPE("输入类型无效， RSA算法标识、 MAC报文块模式， ICV模式，交易模式等", 32),             
	ERR_LMK_STORE_MEM_ERROR("LMK密钥交换存储区有故障", 33),            
	ERR_INVALID_MAC_MODE("mac算法模式无效", 34),               
	ERR_INVALID_GAIN_MAC_TYPE("mac取值方式无效", 35),          
	ERR_INVALID_DISPERSE_STAGE("密钥分散级数无效", 36),         
	ERR_INVALID_SESSION_CIHPER_TYPE("会话密钥类型无效", 37),    
	ERR_SESSION_CIHPER_CRYPT_TYPE_ERROR("会话密钥算法类型错误", 38),
	ERR_INVALID_PAD_TYPE("非法的数据padding类型", 39),               
	ERR_INVALID_BIN_MAC("无效的固件校验值", 40),                
	ERR_INTERAL_ERROR("内在的硬件/软件错误。 RAM损坏，无效的错误代码等", 41),                  
	ERR_CRYPT_CALCULATE_FAILED("密码运算失败", 42),         
	ERR_DECODE_DER_DATA_FAILED("DER解码失败", 43),         
	ERR_CIPHER_NOT_EXIST("密钥不存在", 45),               
	ERR_INVALID_CIPHER("密钥错误", 49),                 
	ERR_FUNCTION_UNREALIZED("功能暂未实现", 50),            
	ERR_INVALID_MSG_HEADER("无效的消息头", 51),             
	ERR_ERROR_USE_ASYNC_CRYPT("非对称密钥密钥用法（签名、加密） 错误", 52),          
	ERR_INVALID_ASYNC_CIPHER_LEN("非对称密钥长度非法（ RSA 512-2048. ECC 256）", 53),       
	ERR_INVALID_DER_ENCODE_TYPE("DER编码类型非法", 54),        
	ERR_INVALID_INDEX_OF_CIPHER("密钥索引超限", 55),        
	ERR_INVALID_RSA_CIPHER_INDEX("RSA密钥指数非法", 56),       
	ERR_INVALID_ASYNC_CIPHER("非对称密钥数据非法", 57),           
	ERR_INVALID_ECC_CURVE_FLAG("ECC密钥曲线标识错误", 58),         
	ERR_TRANS_CIPHER_FLAG_IS_NULL("交易密钥标识设置为NULL", 65),      
	ERR_INVALID_COMMAND("非法指令", 66),                
	ERR_NOT_AUTHORIZED_CMD("命令码没有授权", 67),             
	ERR_CMD_FORBIDDEN("命令码禁用", 68),                  
	ERR_PINBLOCK_FORBIDDEN("PINBLOCK禁用", 69),             
	ERR_INVALID_CIPHERTEXT("无效的密文数据，解密后去PADDING失败；或密钥头验证失败", 70),             
	ERR_NOT_SUPPORTED_HASH_MODE("摘要hash模式不支持", 74),        
	ERR_USE_ERROR_CIPHER("单长度的密钥用作双长度或三长度", 75),               
	ERR_PUBLIC_KEY_ERROR("公钥长度错误", 76),               
	ERR_PLAINTEXT_DATA_ERROR("明文数据块错误", 77),           
	ERR_CIPHERTEXT_LEN_ERROR("密文密钥长度错误", 78),           
	ERR_HASH_ID_ERROR("哈希算法对象标识符错误", 79),                  
	ERR_MSG_LEN_ERROR("报文数据长度错误", 80),                  
	ERR_INVALID_CERTIFICATION_HEADER("无效的证书头", 81),   
	ERR_INVALID_MAC_LEN("无效的校验值长度", 82),                
	ERR_CIPHER_FORMAT_ERROR("密钥格式错误", 83),            
	ERR_CIPHER_MAC_ERROR("密钥校验值错误", 84),               
	ERR_INVALID_OAEP_ALGORITHM("无效的OAEP掩码产生算法", 85),         
	ERR_INVALID_OAEP_MAC_ALGORITHM("无效的OAEP掩码产生算法的摘要算法", 86),     
	ERR_OAEP_PARAM_ERROR("OAEP参数错", 87),
	ERR_KBK_NOT_EXIST("KBK不存在", 89),
	ERR_REQUEST_MSG_MAC_ERROR("密码机接受的请求数据校验错误", 90),          
	ERR_LRC_ERROR("纵向冗余校验(LRC) 字符和通过输入数据计算的值不匹配", 91),                      
	ERR_COUNTER_ERROR("命令/数据域中的计数值不正确或不在规定的范围内", 92),                  
	ERR_PUBLIC_KEY_VERIFY_FAILED("公钥标识验证失败", 93),       
	ERR_PUBLIC_KEY_MAC_VERIFY_FAILED("公钥摘要值验证失败", 94),   
	ERR_INNER_ROUTINE_ERROR("内部程序处理异常", 95),            
	ERR_INVALID_KEY_ID_LEN("密钥标签长度错", 96),             
	ERR_INTERAL_PARAM_ERROR("内部参数错，如会话密钥模式与源密钥类型冲突", 97),            
	ERR_MSG_PACK_ERROR("报文封装错", 98),                 
	ERR_INTERAL_CALC_FAILED("内部运算错误", 99),
	ERR_BASE("错误码基础值", 0x01000000),
	ERR_UNKNOWERR("未知错误", 0x01000000+1),
	ERR_NOTSUPPORT("不支持的接口调用", 0x01000000+2),
	ERR_COMMFAIL("与设备通信失败", 0x01000000+3),
	ERR_HARDFIAL("运算模块无响应", 0x01000000+4),
	ERR_OPENDEVICE("打开设备失败", 0x01000000+5),
	ERR_OPENSESSION("创建会话失败", 0x01000000+6),
	ERR_PARDENY("无私钥使用权限", 0x01000000+7),
	ERR_KEYNOTEXIST("不存在的密钥调用", 0x01000000+8),
	ERR_ALGNOTSUPPORT("不支持的算法调用", 0x01000000+9),
	ERR_ALGMODENOTSUPPORT("不支持的算法模式调用", 0x01000000+10),
	ERR_PKOPERR("公钥运算失败", 0x01000000+11),
	ERR_SKOPERR("私钥运算失败", 0x01000000+12),
	ERR_SIGNERR("签名运算失败", 0x01000000+13),
	ERR_VERIFYERR("验证签名失败", 0x01000000+14),
	ERR_SYMOPERR("对称算法运算失败", 0x01000000+15),
	ERR_STEPERR("多步运算失败", 0x01000000+16),
	ERR_FILESIZEERR("文件长度超出限制", 0x01000000+17),
	ERR_FILENOEXIST("指定的文件不存在", 0x01000000+18),
	ERR_FILEOFSERR("文件起始位置错误", 0x01000000+19),
	ERR_KEYTYPEERR("密钥类型错误", 0x01000000+20),
	ERR_KEYERR("密钥错误", 0x01000000+21),
	ERR_ENCDATAERR("ECC加密数据错误", 0x01000000+22),
	ERR_RANDERR("随机数产生失败", 0x01000000+23),
	ERR_PRKRERR("私钥使用权限获取失败", 0x01000000+24),
	ERR_MACERR("MAC运算失败", 0x01000000+25),
	ERR_FILEEXISTS("指定文件已存在", 0x01000000+26),
	ERR_FILEWERR("文件写入失败", 0x01000000+27),
	ERR_NOBUFFER("存储空间不足", 0x01000000+28),
	ERR_INARGERR("输入参数错误", 0x01000000+29),
	ERR_OUTARGERR("输出参数错误", 0x01000000+30),
	ERR_TIMEOUT("指令执行超时", 0x01000000+31),
	ERR_INVALIDDEVICEHANDLE("无效的设备句柄", 0x01000000+32),
	ERR_INVALIDSESSIONHANDLE("无效的会话句柄", 0x01000000+33),
	ERR_SENDERR("发送数据失败", 0x01000000+34),
	ERR_RECVERR("接收数据失败", 0x01000000+35),
	ERR_RECVCHECKERR("接收数据校验失败", 0x01000000+36),
	ERR_LOGINITFAIL("日志初始化失败", 0x01000000+37),
	ERR_DISPATCHERR("指令分发失败", 0x01000000+38),
	ERR_INICFGERR("INI配置文件错误", 0x01000000+39),
	ERR_LOGPATHERR("创建日志文件夹失败", 0x01000000+40),
	ERR_NOTEXIST_SYSKEY("对称密钥不存在", 0x01000000+41),
	ERR_NOTEXIST_ASYSKEY("非对称密钥不存在", 0x01000000+42),
	ERR_NOTEXIST_BOTHKEY("对称密钥、非对称密钥都不存在", 0x01000000+43),
	ERR_KEYTYPE_ERR("密钥类型错(对称、非对称、对称和非对称)", 0x01000000+44),
	ERR_INDEX_ILLEGAL("设备索引非法", 0x01000000+45),
	ERR_DEV_NOTWORK("设备工作状态异常", 0x01000000+46),
	ERR_NOTEXIST_DEV("设备不存在", 0x01000000+47),
	ERR_NOTEXIST_DESDEV("不存在可用的目的加密机进行密钥同步备份", 0x01000000+48),
	ERR_SYNC_SESSIONHANDLE("不能进行密钥同步", 0x01000000+49),
	ERR_SYNC_CUR("当前正在进行同步备份，稍后再试", 0x01000000+50),
	ERR_API_END("接口结束", 0x01000000+51);
	
	private String name;  
	private int value;

	private DJHsmErrCode(String name, int value) {  
		this.name = name;  
		this.value = value;  
	}

	public static String getName(int value) {  
		for (DJHsmErrCode c : DJHsmErrCode.values()) {  
			if (c.getValue() == value) {  
				return c.name;  
			}  
		}

		return value+"";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
