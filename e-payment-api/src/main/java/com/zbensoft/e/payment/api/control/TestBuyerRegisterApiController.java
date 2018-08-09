package com.zbensoft.e.payment.api.control;

import java.util.Calendar;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.AlarmEmailManageService;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIGetClapResponse;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIGetpatriotResponse;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIRegisterUpdateWalletResponse;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIRequest;
import com.zbensoft.e.payment.common.util.DateUtil;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/TestBuyerRegisterApi")
@RestController
public class TestBuyerRegisterApiController {

	@Autowired
	AlarmEmailManageService alarmEmailManageService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/get_patriot", method = RequestMethod.POST)
	public ResponseRestEntity<APIGetpatriotResponse> getpatriot(@RequestParam String api_key, @RequestBody APIRequest apiRequest) {
		// apiRequest.getParams()[0];//vid
		// apiRequest.getParams()[1];//patrimony card code
		APIGetpatriotResponse getpatriotResponse = new APIGetpatriotResponse();
		getpatriotResponse.setR_cod(apiRequest.getParams()[1]);
		getpatriotResponse.setR_ser(getRand(10));
		getpatriotResponse.setR_stat(true);
		getpatriotResponse.setR_ced(apiRequest.getParams()[0]);
		getpatriotResponse.setR_n1(getRandStr(8));
		getpatriotResponse.setR_n2(getRandStr(8));
		getpatriotResponse.setR_ap1(getRandStr(8));
		getpatriotResponse.setR_ap2(getRandStr(8));
		if (new Random().nextInt(10) % 2 == 0) {
			getpatriotResponse.setR_gen("F");
		} else {
			getpatriotResponse.setR_gen("M");
		}

		getpatriotResponse.setR_fnac(DateUtil.convertDateToString(Calendar.getInstance().getTime(), DateUtil.DATE_FORMAT_ONE));
		getpatriotResponse.setR_mail("colin.hong@zbensoft.com");
		getpatriotResponse.setCod("1000");
		getpatriotResponse.setAccion("CONSULTA");
		getpatriotResponse.setCampo("N/A");
		getpatriotResponse.setSuccess("TRUE");
		getpatriotResponse.setMensaje("CONSULTA EXITOSA");

		return new ResponseRestEntity<APIGetpatriotResponse>(getpatriotResponse, HttpRestStatus.OK);
	}

	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/register_update_wallet", method = RequestMethod.POST)
	public ResponseRestEntity<APIRegisterUpdateWalletResponse> registerUpdateWallet(@RequestParam String api_key, @RequestBody APIRequest apiRequest) {
		// apiRequest.getParams()[0];//vid
		// apiRequest.getParams()[1];//patrimony card code
		// apiRequest.getParams()[2];//email
		// apiRequest.getParams()[3];//status
		APIRegisterUpdateWalletResponse registerUpdateWalletResponse = new APIRegisterUpdateWalletResponse();
		registerUpdateWalletResponse.setCod("3000");
		registerUpdateWalletResponse.setAccion("CONSULTA");
		registerUpdateWalletResponse.setCampo("N/A");
		registerUpdateWalletResponse.setSuccess("TRUE");
		registerUpdateWalletResponse.setMensaje("ACTUALIZADO CORRECTAMENTE");

		return new ResponseRestEntity<APIRegisterUpdateWalletResponse>(registerUpdateWalletResponse, HttpRestStatus.OK);
	}

	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/get_clap", method = RequestMethod.POST)
	public ResponseRestEntity<APIGetClapResponse> getClap(@RequestParam String api_key, @RequestBody APIRequest apiRequest) {
		// apiRequest.getParams()[0];//vid
		APIGetClapResponse getClapResponse = new APIGetClapResponse();
		getClapResponse.setR_ced(apiRequest.getParams()[0]);
		getClapResponse.setR_codcom("COM-AMA-020101-" + getRand(6));
		getClapResponse.setR_ncom(getRandStr(10));
		getClapResponse.setR_codclap("CLAPS-DIS-010101-" + getRand(6));
		getClapResponse.setR_codclap("CLAPS-DIS-010110-00090");
		getClapResponse.setR_nclap(getRandStr(100));
		getClapResponse.setR_codfam(Integer.valueOf(apiRequest.getParams()[0].substring(apiRequest.getParams()[0].length() - 4)));

		getClapResponse.setCod("1000");
		getClapResponse.setAccion("CONSULTA");
		getClapResponse.setCampo("N/A");
		getClapResponse.setSuccess("TRUE");
		getClapResponse.setMensaje("CONSULTA EXITOSA");

		return new ResponseRestEntity<APIGetClapResponse>(getClapResponse, HttpRestStatus.OK);
	}

	public String getRand(int len) {
		String code = "";
		for (int i = 0; i < len; i++) {
			code += new Random().nextInt(10);
		}
		return code;
	}

	public String getRandStr(int len) {
		String a = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String code = "";
		for (int i = 0; i < len; i++) {
			int index = new Random().nextInt(a.length());
			code += a.substring(index, index + 1);
		}
		return code;
	}
}
