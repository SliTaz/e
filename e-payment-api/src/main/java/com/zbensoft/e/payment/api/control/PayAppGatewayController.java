package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.PayAppGatewayService;
import com.zbensoft.e.payment.db.domain.PayAppGateway;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/payAppGateway")
@RestController
public class PayAppGatewayController {
	@Autowired
	PayAppGatewayService payAppGatewayService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_PAYMENT_A_Q')")
	@ApiOperation(value = "Query PayAppGateway，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<PayAppGateway>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		PayAppGateway payAppGateway = new PayAppGateway();
		List<PayAppGateway> list = payAppGatewayService.selectPage(payAppGateway);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<PayAppGateway>>(new ArrayList<PayAppGateway>(),
					HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<PayAppGateway>>(list, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_A_Q')")
	@ApiOperation(value = "Query PayAppGateway", notes = "")
	@RequestMapping(value = "/{payAppId}/{payGatewayId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<PayAppGateway> selectByPrimaryKey(@PathVariable("payAppId") String payAppId,
			@PathVariable("payGatewayId") String payGatewayId) {
		PayAppGateway bean = new PayAppGateway();
		bean.setPayAppId(payAppId);
		bean.setPayGatewayId(payGatewayId);
		PayAppGateway payAppGateway = payAppGatewayService.selectByPrimaryKey(bean);
		if (payAppGateway == null) {
			return new ResponseRestEntity<PayAppGateway>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<PayAppGateway>(payAppGateway, HttpRestStatus.OK);
	}
	

	@PreAuthorize("hasRole('R_PAYMENT_A_Q')")
	@ApiOperation(value = "Query PayAppGateway", notes = "")
	@RequestMapping(value = "/{payAppId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<PayAppGateway>> selectByPayAppId(@PathVariable("payAppId") String payAppId) {
		List<PayAppGateway> list = payAppGatewayService.selectByPayAppId(payAppId);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<PayAppGateway>>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<PayAppGateway>>(list, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "Add PayAppGateway", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createPayAppGateway(@Valid @RequestBody PayAppGateway payAppGateway,
			BindingResult result, UriComponentsBuilder ucBuilder) {
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		payAppGatewayService.insert(payAppGateway);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, payAppGateway,CommonLogImpl.PAYMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(
				ucBuilder.path("/payAppGateway/{id}").buildAndExpand(payAppGateway.getPayAppId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "Edit PayAppGateway", notes = "")
	@RequestMapping(value = "/{payAppId}/{payGatewayId}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayAppGateway> updatePayAppGateway(
			@PathVariable("payAppId") String payAppId, @PathVariable("payGatewayId") String payGatewayId,
			@Valid @RequestBody PayAppGateway payAppGateway, BindingResult result) {
		PayAppGateway bean = new PayAppGateway();
		bean.setPayAppId(payAppId);
		bean.setPayGatewayId(payGatewayId);
		PayAppGateway currentPayAppGateway = payAppGatewayService.selectByPrimaryKey(bean);

		if (currentPayAppGateway == null) {
			return new ResponseRestEntity<PayAppGateway>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentPayAppGateway.setPayAppId(payAppGateway.getPayAppId());
		currentPayAppGateway.setPayGatewayId(payAppGateway.getPayGatewayId());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<PayAppGateway>(currentPayAppGateway,
					HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		payAppGatewayService.updateByPrimaryKey(currentPayAppGateway);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentPayAppGateway,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayAppGateway>(currentPayAppGateway, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "Edit Part PayAppGateway", notes = "")
	@RequestMapping(value = "/{payAppId}/{payGatewayId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<PayAppGateway> updatePayAppGatewaySelective(@PathVariable("payAppId") String payAppId, 
			@PathVariable("payGatewayId") String payGatewayId,@RequestBody PayAppGateway payAppGateway) {
		PayAppGateway bean =new PayAppGateway();
		bean.setPayAppId(payAppId);
		bean.setPayGatewayId(payGatewayId);
		PayAppGateway currentPayAppGateway = payAppGatewayService.selectByPrimaryKey(bean);

		if (currentPayAppGateway == null) {
			return new ResponseRestEntity<PayAppGateway>(HttpRestStatus.NOT_FOUND);
		}
		currentPayAppGateway.setPayAppId(payAppGateway.getPayAppId());
		currentPayAppGateway.setPayGatewayId(payAppGateway.getPayGatewayId());
		payAppGatewayService.updateByPrimaryKeySelective(currentPayAppGateway);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentPayAppGateway,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayAppGateway>(currentPayAppGateway, HttpRestStatus.OK);
	}
	

	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "Delete PayAppGateway", notes = "")
	@RequestMapping(value = "/{payAppId}/{payGatewayId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<PayAppGateway> deletePayAppGateway(
			@PathVariable("payAppId") String payAppId,
			@PathVariable("payGatewayId") String payGatewayId) {
		PayAppGateway bean = new PayAppGateway();
		bean.setPayAppId(payAppId);
		bean.setPayGatewayId(payGatewayId);
		PayAppGateway payAppGateway = payAppGatewayService.selectByPrimaryKey(bean);
		if (payAppGateway == null) {
			return new ResponseRestEntity<PayAppGateway>(HttpRestStatus.NOT_FOUND);
		}
		payAppGatewayService.deleteByPrimaryKey(payAppGateway);
		//删除日志开始
		PayAppGateway delBean = new PayAppGateway();
		delBean.setPayAppId(payAppId);
		delBean.setPayGatewayId(payGatewayId);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.PAYMENT);
		//删除日志结束
		return new ResponseRestEntity<PayAppGateway>(HttpRestStatus.NO_CONTENT);
	}

}