package com.zbensoft.e.payment.api.control;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.vo.codeHelp.CodeHelp;
import com.zbensoft.e.payment.common.cxf.SOAPResultCode;

@RestController
@RequestMapping(value = "/codeHelp")
public class CodeHelpController {
	
	
	private static final Logger log = LoggerFactory.getLogger(CodeHelpController.class);


	@RequestMapping(value = "/SOAPResultCode", method = RequestMethod.GET)
	public ResponseEntity<List<CodeHelp>> selectPage() {

		return new ResponseEntity<List<CodeHelp>>(getList(SOAPResultCode.class), HttpStatus.OK);
	}

	private List<CodeHelp> getList(Class clazz) {
		List<CodeHelp> list = new ArrayList<CodeHelp>();
		if (clazz.isEnum()) {
			Object[] obs = clazz.getEnumConstants();
			for (Object ob : obs) {
				try {
					Method m = clazz.getMethod("getCode");
					Method m2 = clazz.getMethod("getKey");
					Method m3 = clazz.getMethod("getReason");
					Method m4 = clazz.getMethod("getReason_es");
					Object codeOb = m.invoke(ob);
					CodeHelp resultCodeVo = new CodeHelp();
					resultCodeVo.setCode(Integer.valueOf(m.invoke(ob).toString()));
					resultCodeVo.setKey(String.valueOf(m2.invoke(ob)));
					resultCodeVo.setReason(String.valueOf(m3.invoke(ob)));
					resultCodeVo.setReason_es(String.valueOf(m4.invoke(ob)));
					list.add(resultCodeVo);
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		return list;
	}

	private List<CodeHelp> getList(Class clazz, String code) {
		List<CodeHelp> list = new ArrayList<CodeHelp>();
		if (clazz.isEnum()) {
			Object[] obs = clazz.getEnumConstants();
			for (Object ob : obs) {
				try {
					Method m = clazz.getMethod("getCode");
					Method m2 = clazz.getMethod("getKey");
					Method m3 = clazz.getMethod("getReason");
					Method m4 = clazz.getMethod("getReason_es");
					Object codeOb = m.invoke(ob);
					CodeHelp resultCodeVo = new CodeHelp();
					resultCodeVo.setCode(Integer.valueOf(m.invoke(ob).toString()));
					resultCodeVo.setKey(String.valueOf(m2.invoke(ob)));
					resultCodeVo.setReason(String.valueOf(m3.invoke(ob)));
					resultCodeVo.setReason_es(String.valueOf(m4.invoke(ob)));
					if (code == null || "".equals(code)) {
						list.add(resultCodeVo);
					} else {
						if (String.valueOf(resultCodeVo.getCode()).contains(code)) {
							list.add(resultCodeVo);
						} else if (resultCodeVo.getKey().contains(code)) {
							list.add(resultCodeVo);
						} else if (resultCodeVo.getReason().contains(code)) {
							list.add(resultCodeVo);
						} else if (resultCodeVo.getReason_es().contains(code)) {
							list.add(resultCodeVo);
						}
					}
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		return list;
	}
}
